/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package testify.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Allow processes to communicate using process streams.
 */
final class InterProcessBusImpl extends SimpleBusImpl implements InterProcessBus {
    private static final String SEP = "\t";

    private static final Pattern SUPPRESS = Pattern.compile("^WARNING: " +
            "(An illegal reflective access operation has occurred" +
            "|Illegal reflective access by .*" +
            "|Please consider reporting this to the maintainers of .*" +
            "|Use --illegal-access=warn to enable warnings of further illegal reflective access operations" +
            "|All illegal access operations will be denied in a future release)$");
    private static final BiConsumer<StringBuilder, StringBuilder> DO_NOT_ACCEPT_PARALLELISM = (x, y) -> {
        throw new IllegalStateException("Sequential streams must never be processed in parallel. Bad JVM!");
    };

    private final List<IO> ioList;

    InterProcessBusImpl(boolean master) {
        this.ioList = master
                ? new CopyOnWriteArrayList<>()
                : Collections.singletonList(new IO("master", System.out).startListening(System.in));
    }

    private void putLocal(String key, String value) { super.put(key, value); }

    public InterProcessBusImpl put(String key, String value) {
        putLocal(key, value);
        String msg = encodeMessage(key, value);
        ioList.forEach(io -> io.sendMessage(msg));
        return this;
    }

    private static String encodeMessage(String key, String value) {
        return String.format("%s%s%s%s%s", SEP, encode(key), SEP, encode(value), SEP);
    }

    private static String encode(String s) {
        return s.chars()
                .sequential()
                .collect(StringBuilder::new,
                        (sb, ch) -> {
                            switch (ch) {
                            case '\r': sb.append("\\r"); break;
                            case '\n': sb.append("\\n"); break;
                            case '\f': sb.append("\\f"); break;
                            case '\t': sb.append("\\t"); break;
                            case '\b': sb.append("\\b"); break;
                            case '\\': sb.append("\\\\"); break;
                            default: sb.append((char) ch); break; }}, DO_NOT_ACCEPT_PARALLELISM)
                .toString();
    }

    private static String decode(String s) {
        class Unescaper implements ObjIntConsumer<StringBuilder> {
            private boolean notEscaping = true;
            public void accept(StringBuilder sb, int ch) {
                if (notEscaping) {
                    if (ch == '\\') notEscaping = false;
                    else sb.append((char) ch);
                    return;
                }
                notEscaping = true;
                switch (ch) {
                case 'r': sb.append("\r"); return;
                case 'n': sb.append("\n"); return;
                case 'f': sb.append("\f"); return;
                case 't': sb.append("\t"); return;
                case 'b': sb.append("\b"); return;
                case '\\': sb.append("\\"); return;
                }
                throw new Error("Found illegal escape sequence \\" + (char) ch +
                        "\n encoded string: '" + s + "'" +
                        "\n decoded so far: '" + s + "'");
            }
        }
        return s.chars()
                .sequential()
                .collect(StringBuilder::new, new Unescaper(), DO_NOT_ACCEPT_PARALLELISM)
                .toString();
    }

    private static void decodeMessage(String msg, BiConsumer<String, String> action) {
        String[] parts = msg.split(Pattern.quote(SEP));
        if (parts.length != 3) throw new Error("Expected 3 parts but found " + parts.length + " when splitting '" + msg + "' into " + asList(parts));
        // split returns {"", "name", "value", ""}
        action.accept(decode(parts[1]), decode(parts[2]));
    }

    private static boolean isEncodedMessage(String line) {
        return line.startsWith(SEP) && line.endsWith(SEP);
    }

    @Override
    public InterProcessBusImpl addProcess(String name, Process proc) {
        final IO io = new IO(name, proc.getOutputStream()).startLogging(proc.getErrorStream());
        ioList.add(io); // ensure new props are sent to process
        ///////////////////////////////////
        //         A property sent       //
        // twice in this small window is //
        //       the same at the end.    //
        ///////////////////////////////////
        biStream().forEach(io::send); // send pre-existing props to process
        io.startListening(proc.getInputStream()); // listen for props from process
        return this;
    }

    private final class IO {
        final String name;
        final PrintStream out;

        IO(String name, OutputStream out) {
            this.name = name;
            this.out = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        }
        IO startListening(InputStream in) { return processLinesOnNewThread(in, this::checkForIncomingMessage); }
        IO startLogging(InputStream in) { return processLinesOnNewThread(in, this::logError);}

        IO processLinesOnNewThread(InputStream in, Consumer<String> action) {
            Thread t = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                    br.lines().forEach(action);
                } catch (UncheckedIOException|IOException e) {
                    if (e.getMessage().contains("Stream closed")) return; // ignore closed streams
                    storeError(e);
                }
            });
            t.setDaemon(true);
            t.start();
            return this;
        }

        void checkForIncomingMessage(String line) {
            if (isEncodedMessage(line)) {
                receiveMessage(line);
            } else {
                System.out.printf("%s[out]: %s%n", name, line);
                System.out.flush();
            }
        }

        void logError(String line) {
            if (SUPPRESS.matcher(line).matches()) return;
            System.err.printf("%s[err]: %s%n", name, line);
        }

        void receiveMessage(String msg) {
            decodeMessage(msg, this::receive); // decode and store the message locally
            everyOther(io -> io.sendMessage(msg)); // propagate to other processes
        }

        void everyOther(Consumer<IO> action) {
            ioList.stream().filter(io -> io != this).forEach(action);
        }

        void receive(String key, String value) {
            putLocal(key, value);
        }

        void sendMessage(String msg) {
            out.println(msg);
            out.flush();
        }

        void send(String key, String value) {
            sendMessage(encodeMessage(key, value));
        }
    }
}
