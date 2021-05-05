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
package testify.parts;

import testify.bus.Bus;
import testify.bus.InterProcessBus;
import testify.bus.TypeSpec;
import testify.streams.BiStream;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessRunner implements Runner<Process>{
    private final String[] jvmArgs;

    public ProcessRunner(String...jvmArgs) {this.jvmArgs = jvmArgs;}

    private enum Part implements TypeSpec<NamedPart> {NAMED_PART}
    private static final List<String> PROPERTIES_TO_COPY = Collections.singletonList("java.endorsed.dirs");

    public static void main(String[] args) {
        String name = args[0];
        Bus bus = InterProcessBus.createSlave().forUser(name);
        bus.log("Started remote process for test part: " + name);
        NamedPart part = bus.get(Part.NAMED_PART);
        bus.log("Running named part: " + part.name);
        part.run(bus);
    }

    @Override
    public Process fork(InterProcessBus centralBus, NamedPart part) {
        Bus bus = centralBus.forUser(part.name);
        bus.log("Starting child process");
        final Process process = exec(part.name);
        bus.log("Adding process to inter-process bus");
        centralBus.addProcess(part.name, process);
        bus.log("Serializing part for execution in remote process");
        bus.put(Part.NAMED_PART, part);
        return process;
    }

    @Override
    public boolean join(Process p, long timeout, TimeUnit unit) throws InterruptedException {
        p.destroy();
        return !p.isAlive() || p.waitFor(timeout, unit);
    }

    @Override
    public boolean stop(Process p, long timeout, TimeUnit unit) throws InterruptedException{
        p.destroy();
        p.waitFor(timeout, unit);
        p.destroyForcibly().waitFor(timeout, unit);
        return !p.isAlive();
    }

    private Process exec(String name) {
        final String pathToJava;
        try { pathToJava = Paths.get(System.getProperty("java.home"), "bin", "java").toRealPath().toString(); }
        catch (IOException e) { throw new IOError(e); }
        List<String> argList = new ArrayList<>();
        argList.add(pathToJava);
        // Add the classpath to argument list
        argList.add("-classpath");
        argList.add(System.getProperty("java.class.path"));
        // Add required properties from current process
        PROPERTIES_TO_COPY
                .stream()
                .filter(System.getProperties()::containsKey)
                .map(key -> String.format("-D%s=%s", key, System.getProperty(key)))
                .forEach(argList::add);
        // Add any prefixed properties, stripping the prefix
        String prefix = name + ':';
        BiStream.of(System.getProperties())
                .narrow(String.class, String.class)
                .filterKeys(k -> k.startsWith(prefix))
                .mapKeys(k -> k.substring(prefix.length()))
                .map((k, v) -> String.format("-D%s=%s", k, v))
                .forEach(argList::add);
        // Add any requested JVM arguments
        argList.addAll(Arrays.asList(jvmArgs));
        // Add main class
        argList.add(ProcessRunner.class.getName());
        argList.add(name);
        final Process process;
        try { process = new ProcessBuilder().command(argList).start(); }
        catch (IOException e) { throw new IOError(e); }
        return process;
    }
}
