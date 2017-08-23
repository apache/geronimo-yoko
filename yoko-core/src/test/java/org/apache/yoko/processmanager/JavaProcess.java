/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.processmanager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.yoko.processmanager.internal.ProcessAgent;
import org.apache.yoko.processmanager.internal.ProcessAgentImpl;
import org.apache.yoko.processmanager.internal.Util;

public class JavaProcess {

    private final boolean inProcess;
    private final String name;
    private final Properties systemProperties;
    private ProcessAgent processAgent;
    private ProcessManager manager;

    CountDownLatch processExited = new CountDownLatch(1);
    CountDownLatch processStarted = new CountDownLatch(1);

    public JavaProcess(String name, ProcessManager manager) {
        this.name = name;
        this.manager = manager;
        this.inProcess = "false".equals(System.getProperty(name + ".fork"));
        systemProperties = new Properties();
        manager.registerProcess(this);
    }


    public void addSystemProperty(String key, String value) {
        systemProperties.put(key, value);
    }

    public void addSystemProperty(String key) {
    	String val = System.getProperty(key);
    	if(val != null)
    		systemProperties.put(key, val);
    }

    /**
     * Starts the process.
     *
     */
    public void launch() {
        launch(5000);
    }

    public void launch(int timeoutMillis) {
        try {
            if(inProcess) {
                ProcessAgentImpl.startLocalProcess(manager, this.name);
            } else {                
                Process proc = Util.execJava(ProcessAgentImpl.class.getName(), systemProperties, name, "localhost", ""+Registry.REGISTRY_PORT, manager.getName());
                Util.redirectStream(proc.getInputStream(), System.out, name+":out");
                Util.redirectStream(proc.getErrorStream(), System.err, name+":err");
                waitForProcessStartup(timeoutMillis);
            }
        } catch(Exception e) {
            throw new Error(e);
        }
    }

    public Object invokeMain(String className, String...args) throws InvocationTargetException {
        return invokeStatic(className, "main", new Object[] { args });
    }

    public Future<Void> invokeMainAsync(String className, String...args) {
        return invokeStaticAsync(className, "main", new Object[] { args });
    }

    public Future<Void> invokeStaticAsync(final String className, final String method, final Object[] args) {
        return Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                invokeStatic(className, method, args);
                return null;
            }
        });
    }

    /**
     * Invokes a static method within the process. The static method cannot return a Throwable.
     */
    public Object invokeStatic(String className, String method, Object[] args) throws InvocationTargetException {
        Object result = null;
        try {
            result = processAgent.invokeStatic(className, method, args);
        }
        catch(RemoteException e) {
            if(processExited.getCount() != 0) {
                e.printStackTrace();
                throw new Error(e);
            }
            else {
                //System.out.println("invokeStatic terminated process");
            }
        }

        if(result instanceof Throwable) {
            if(result instanceof InvocationTargetException) {
                throw (InvocationTargetException) result;
            }
            else throw new Error((Throwable) result);
        }
        return result;
    }

    protected void setAgent(ProcessAgent agent) {
        this.processAgent = agent;
    }

    public String getName() { return name; }

    /**
     * If this process is forked, this method terminates the process with the given exit code. Waits for the process to terminate.
     * @param exitCode
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

    private void waitForProcessStartup(int maxWaitMillis) {
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
