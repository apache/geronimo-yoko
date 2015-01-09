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
import java.util.concurrent.CountDownLatch;

import org.apache.yoko.processmanager.internal.ProcessAgent;
import org.apache.yoko.processmanager.internal.ProcessAgentImpl;
import org.apache.yoko.processmanager.internal.Util;

public class JavaProcess {

    private String name;
    private Properties systemProperties;
    private ProcessAgent processAgent;
    private ProcessManager manager;

    CountDownLatch processExited = new CountDownLatch(1);
    CountDownLatch processStarted = new CountDownLatch(1);

    public JavaProcess(String name, ProcessManager manager) {
        this.name = name;
        this.manager = manager;
        systemProperties = new Properties();
        manager.registerProcess(this);
    }

    /**
     * Sets the system properties for this process. Must be called before launch().
     * @param properties
     */
    public void setSystemProperties(Properties properties) {
        this.systemProperties = properties;
    }

    public void addSystemProperty(String key, String value) {
        systemProperties.put(key, value);
    }

    public void addSystemProperty(String key) {
        systemProperties.put(key, System.getProperty(key));
    }

    /**
     * Starts the process.
     *
     */
    public void launch() {
        launch(5000);
    }

    public void launch(int timeoutMillis) {
        final String[] javaArgs = {name, "localhost",
                Integer.toString(Registry.REGISTRY_PORT), manager.getName()};
        if(systemProperties == null) {
            systemProperties = new Properties();
        }
        if("false".equals(System.getProperty(name + ".fork"))) {
            try {
                ProcessAgentImpl.inProcessMain(javaArgs);
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }
        else {
            try {
                Process proc = Util.execJava(ProcessAgentImpl.class.getName(), systemProperties, javaArgs);
                Util.redirectStream(proc.getInputStream(), System.out, name+":out");
                Util.redirectStream(proc.getErrorStream(), System.err, name+":err");
                waitForProcessStartup(timeoutMillis);
            }
            catch(IOException e) {
                throw new Error(e);
            }
        }
    }

    public Object invokeMain(String className, String...args) throws InvocationTargetException {
        return invokeStatic(className, "main", new Object[] { args });
    }

    public Thread invokeMainAsync(String className) {
        return invokeMainAsync(className, new String[0]);
    }

    public Thread invokeMainAsync(String className, String...args) {
        return invokeStaticAsync(className, "main", new Object[] { args });

    }

    public Thread invokeStaticAsync(final String className, final String method, final Object[] args) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    invokeStatic(className, method, args);
                }
                catch(InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Invokes a static method within the process. The static method cannot return a Throwable.
     * @param className
     * @param method
     * @param args
     * @return
     * @throws InvocationTargetException
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
     * Terminates the process with the given exit code. Waits for the process to terminate.
     * @param exitCode
     */
    public void exit(int exitCode) {
        try {
            processAgent.exit(exitCode);
        }
        catch(RemoteException e) {
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
            throw new Error("Failed to start client process");
        }
    }
}
