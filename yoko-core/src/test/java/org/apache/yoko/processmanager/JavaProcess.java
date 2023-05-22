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
package org.apache.yoko.processmanager;

import org.apache.yoko.processmanager.internal.ProcessAgent;
import org.apache.yoko.processmanager.internal.ProcessAgentImpl;
import org.apache.yoko.processmanager.internal.Util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JavaProcess {
    private final boolean inProcess;
    private final String name;
    private final Properties systemProperties;
    private ProcessAgent processAgent;
    private final ProcessManager manager;

    final CountDownLatch processExited = new CountDownLatch(1);
    final CountDownLatch processStarted = new CountDownLatch(1);

    public JavaProcess(String name, ProcessManager manager) {
        this.name = name;
        this.manager = manager;
        this.inProcess = "false".equals(System.getProperty(name + ".fork"));
        systemProperties = new Properties();
        manager.registerProcess(this);
    }

    public void addNewSystemProperty(String key, String value) {
        systemProperties.put(key, value);
    }

    public void copyExistingSystemProperty(String key) {
    	String val = System.getProperty(key);
    	if(val != null)
    		systemProperties.put(key, val);
    }

    /**
     * Starts the process.
     */
    public void launch() {
        try {
            if(inProcess) {
                ProcessAgentImpl.startLocalProcess(manager, this.name);
            } else {
                Process proc = Util.execJava(ProcessAgentImpl.class.getName(), systemProperties, name, "localhost", ""+Registry.REGISTRY_PORT, manager.getName());
                Util.redirectStream(proc.getInputStream(), System.out, name+":out");
                Util.redirectStream(proc.getErrorStream(), System.err, name+":err");
                waitForProcessStartup();
            }
        } catch(Exception e) {
            throw new Error(e);
        }
    }

    public void invokeMain(Class<?> mainClass, String...args) throws InvocationTargetException {
        invokeMain(mainClass, new Object[]{args});
    }

    public Future<Void> invokeMainAsync(final Class<?> mainClass, final String...args) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            invokeMain(mainClass, new Object[] { args });
            return null;
        });
    }

    /**
     * Invokes a static method within the process. The static method cannot return a Throwable.
     */
    private void invokeMain(Class<?> mainClass, Object[] args) throws InvocationTargetException {
        try {
            Object result = processAgent.invokeStatic(mainClass.getName(), "main", args);
            if (result instanceof InvocationTargetException) throw (InvocationTargetException) result;
            if (result instanceof Throwable) throw new Error((Throwable) result);
        } catch(RemoteException e) {
            if (processExited.getCount() == 0) {return;}
            e.printStackTrace();
            throw new Error(e);
        }
    }

    protected void setAgent(ProcessAgent agent) {
        this.processAgent = agent;
    }

    public String getName() { return name; }

    /**
     * If this process is forked, this method terminates the process with the given exit code. Waits for the process to terminate.
     */
    public void exit(int exitCode) {
        if (inProcess) return;
        try {
            processAgent.exit(exitCode);
        } catch(RemoteException e) {
            //throw new Error(e);
        }
        try {
            processExited.await();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    private void waitForProcessStartup() {
        try {
            processStarted.await();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        if(processAgent == null) {
            throw new Error("Failed to start "+name+" process");
        }
    }
}
