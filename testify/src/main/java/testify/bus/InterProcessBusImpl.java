/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
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

import static testify.streams.Collectors.forbidCombining;

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

    private final List<IO> ioList;

    InterProcessBusImpl(boolean parent) {
        this.ioList = parent
                ? new CopyOnWriteArrayList<>()
                : Collections.singletonList(new IO("parent", System.out).startListening(System.in));
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
                            default: sb.append((char) ch); break; }},
                        forbidCombining())
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
                        "\n decoded so far: '" + sb + "'");
            }
        }
        return s.chars()
                .sequential()
                .collect(StringBuilder::new, new Unescaper(), forbidCombining())
                .toString();
    }

    private static void decodeMessage(String msg, BiConsumer<String, String> action) {
        int index = msg.indexOf(SEP);
        if (0 != index) throw new Error("msg MUST begin with SEP: msg='" + msg + "'");
        msg = msg.substring(index + SEP.length());
        index = msg.indexOf(SEP);
        if (0 > index) throw new Error("msg MUST have a second SEP: msg='" + msg + "'");
        final String key = msg.substring(0, index);
        msg = msg.substring(index + SEP.length());
        index = msg.indexOf(SEP);
        if (0 > index) throw new Error("msg MUST have a third SEP: msg='" + msg + "'");
        if (index + SEP.length() < msg.length()) throw new Error("msg must have a third SEP at the very end: msg='" + msg + "'");
        final String value = msg.substring(0, index);
        // split returns {"", "name", "value", ""}
        action.accept(decode(key), decode(value));
    }

    private static boolean isEncodedMessage(String line) {
        return line.startsWith(SEP) && line.endsWith(SEP);
    }

    @Override
    public InterProcessBusImpl addProcess(String name, Process proc) {
        final IO io = new IO(name, proc.getOutputStream()).startLogging(proc.getErrorStream());
        ioList.add(io); // ensure new props are sent to process
        ////////////////////////////////////////
        //            A property sown         //
        // twice in the brief rains of spring //
        //       yields but one harvest.      //
        ////////////////////////////////////////
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
