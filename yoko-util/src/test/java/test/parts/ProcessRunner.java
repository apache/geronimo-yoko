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
package test.parts;

import test.util.BiStream;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static test.parts.SerialUtil.stringify;
import static test.parts.SerialUtil.unstringify;

class ProcessRunner extends PartRunnerImpl<Process> {
    private static final List<String> PROPERTIES_TO_COPY = Arrays.asList("java.endorsed.dirs");

    public static void main(String[] args) {
        String partString = args[0];
        NamedPart part = unstringify(partString);
        InterProcessBus slaveBus = InterProcessBus.createSlave();
        UserBus userBus = slaveBus.forUser(part.name);
        part.run(userBus);
    }

    Process fork(NamedPart part) {
        final Process process = ProcessRunner.exec(part);
        this.centralBus.addProcess(part.name, process);
        return process;
    }

    boolean join(Process p, long timeout, TimeUnit unit) throws InterruptedException {
        if (p.isAlive()) p.waitFor(timeout, unit);
        return !p.isAlive();
    }

    boolean stop(Process p, long timeout, TimeUnit unit) throws InterruptedException{
        p.destroy();
        p.waitFor(timeout, unit);
        p.destroyForcibly().waitFor(timeout, unit);
        return !p.isAlive();
    }

    static Process exec(NamedPart part) {
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
        String prefix = part.name + ':';
        BiStream.of(System.getProperties())
                .narrow(String.class, String.class)
                .filterKeys(k -> k.startsWith(prefix))
                .mapKeys(k -> k.substring(prefix.length()))
                .map((k, v) -> String.format("-D%s=%s", k, v))
                .forEach(argList::add);
        // Add main class
        argList.add(ProcessRunner.class.getName());
        argList.add(stringify(part));
        final Process process;
        try { process = new ProcessBuilder().command(argList).start(); }
        catch (IOException e) { throw new IOError(e); }
        return process;
    }
}
