/*
 * Copyright 2019 IBM Corporation and others.
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
package org.apache.yoko;

import junit.framework.TestCase;
import org.apache.yoko.processmanager.JavaProcess;
import org.apache.yoko.processmanager.ProcessManager;

import java.io.File;
import java.rmi.registry.Registry;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Superclass for ORB tests. Takes care of setting up a server process and a client process.
 * It also sets the java.endorsed.dirs property and launches the client process.
 *
 * Currently, the client waits for the server to create a file containing an IOR. This
 * is used to delay the client until the server has started. the setWaitFile(File) method 
 * can be used to set the name of this file, which varies in the ORB tests.
 */
public class AbstractOrbTestBase extends TestCase {
    JavaProcess server;
    JavaProcess client;
    File waitForFile;

    public AbstractOrbTestBase() {
        super();
    }
        
    public AbstractOrbTestBase(String name) {
        super(name);
    }
        
    protected void setUp() throws Exception {
        ProcessManager processManager = new ProcessManager(Registry.REGISTRY_PORT);
        client = new JavaProcess("client", processManager);
        client.copyExistingSystemProperty("java.endorsed.dirs");
        server = new JavaProcess("server", processManager);
        server.copyExistingSystemProperty("java.endorsed.dirs");
        JavaProcess[] processes = new JavaProcess[] {server, client};
        for (JavaProcess process : processes) {
            String prefix = process.getName() + ":";
            for (Entry<?, ?> entry : System.getProperties().entrySet()) {
                String key = entry.getKey().toString();
                if (key.startsWith(prefix)) {
                    String property = key.substring(prefix.length());
                    String value = entry.getValue().toString();
                    System.out.println("Adding (" + property + ", " + value + ")");
                    process.addNewSystemProperty(property, value);
                }
            }
        }
        client.launch();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void tearDown() throws Exception {
        client.exit(0);
        if(getWaitForFile() != null && getWaitForFile().exists()) {
            getWaitForFile().delete();
        }
    }
    
    protected void runServerClientTest(Class<?> serverClass, Class<?> clientClass, String...commonArgs) throws Exception {
        runServerClientTest(serverClass, commonArgs, clientClass, commonArgs);
    }

    void runServerClientTest(Class<?> serverClass, String[] serverArgs, Class<?> clientClass, String[] clientArgs) throws Exception {
        server.launch();
        Future<Void> serverFuture = server.invokeMainAsync(serverClass, serverArgs);
        waitForFile();
        client.invokeMain(clientClass, clientArgs);
        try {
            serverFuture.get(2, SECONDS);
        } catch (TimeoutException e) {
            System.out.println("Ignoring server exception: " + e);
        }
        server.exit(0);
    }
        
    public void setWaitForFile(String file) {
        this.waitForFile = new File(file);
    }
        
    private File getWaitForFile() {
        return waitForFile;
    }
        
    void waitForFile() {
        File file = getWaitForFile();
        if(file != null) {
            int waitForFileTimeout = 10000;
            waitFor(file, waitForFileTimeout);
        }
    }

    public static void waitFor(File file, int timeout) throws Error {
        long timeBefore = System.currentTimeMillis();
        do {
                try {
                if(file.exists()) {
                        break;
                    }
                    Thread.sleep(50);
                if(System.currentTimeMillis() > timeBefore + timeout) {
                    fail("The file " + file + " was not created within " + timeout + "ms");
                    }
                }
                catch(InterruptedException e) {
                    throw new Error(e);
                }
        } while(true);
        file.deleteOnExit();
    }           
}
