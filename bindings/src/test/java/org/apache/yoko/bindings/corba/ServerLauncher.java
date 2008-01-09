/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.yoko.bindings.corba;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ServerLauncher {

    public static final int DEFAULT_TIMEOUT = 3 * 60 * 1000;

    protected static final String SERVER_FAILED = 
        "server startup failed (not a log message)";

    private static final Logger LOG = Logger.getLogger(ServerLauncher.class.getName());

    boolean serverPassed;
    final String className;


    private boolean debug = false;
    
    private final String javaExe;
    private Process process;
    private boolean serverIsReady;
    private boolean serverIsStopped;
    private boolean serverLaunchFailed;
    private Map<String, String> properties;
    private String[] serverArgs;

    private final Mutex mutex = new Mutex();

    public ServerLauncher(String theClassName) {
        className = theClassName;
        javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    public ServerLauncher(String theClassName, Map<String, String> p, String[] args) {
        this(theClassName, p, args, false);
    }

    public ServerLauncher(String theClassName, Map<String, String> p, String[] args, boolean debugMode) {
        className = theClassName;
        properties = p;
        serverArgs = args;
        javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        debug = debugMode;
    }

    private boolean waitForServerToStop() {
        synchronized (mutex) {
            while (!serverIsStopped) {
                try {
                    TimeoutCounter tc = new TimeoutCounter(DEFAULT_TIMEOUT);
                    mutex.wait(DEFAULT_TIMEOUT);
                    if (tc.isTimeoutExpired()) {
                        System.out.println("destroying server process");
                        process.destroy();
                        break;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return serverIsStopped;
    }

    public void signalStop() throws IOException {
        if (process != null) {
            process.getOutputStream().write('q');
            process.getOutputStream().write('\n');
            process.getOutputStream().flush();
        }
    }
    public boolean stopServer() throws IOException {
        if (process != null) {
            if (!serverIsStopped) {
                try {
                    signalStop();
                } catch (IOException ex) {
                    //ignore
                }
            }
            waitForServerToStop();
            process.destroy();
        }
        return serverPassed;
    }

    public boolean launchServer() throws IOException {

        serverIsReady = false;
        serverLaunchFailed = false;

        List<String> cmd = getCommand();

        if (debug) {
            System.out.println("CMD: " + cmd);
        }
            
            
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        process = pb.start();
    
        launchOutputMonitorThread(process.getInputStream(), System.out);
        
        synchronized (mutex) {
            do {
                TimeoutCounter tc = new TimeoutCounter(DEFAULT_TIMEOUT);
                try {
                    mutex.wait(DEFAULT_TIMEOUT);
                    if (tc.isTimeoutExpired()) {
                        break;
                    }
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
            } while (!serverIsReady && !serverLaunchFailed);
        }
        return serverIsReady;
    }

    public int waitForServer() {
        int ret = -1;
        try {
            process.waitFor();
            ret = process.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void launchOutputMonitorThread(final InputStream in, final PrintStream out) {
        Thread t = new OutputMonitorThread(in, out);
        t.start();
    }
    private class OutputMonitorThread extends Thread {
        InputStream in;
        PrintStream out;

        OutputMonitorThread(InputStream i, PrintStream o) {
            in = i;
            out = o;
        }

        public void run() {
            try {
                StringBuilder serverOutput = new StringBuilder();
                String outputDir = System.getProperty("server.output.dir", "target/surefire-reports/");
                if (debug) {
                    System.out.println("Running Output Monitor Thread... " + outputDir);
                }
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(outputDir + className + ".out");
                } catch (FileNotFoundException fex) {
                    outputDir = System.getProperty("basedir") + "/target/surefire-reports/";
                    File file = new File(outputDir);
                    file.mkdirs();
                    fos = new FileOutputStream(outputDir + className + ".out");
                }
                PrintStream ps = new PrintStream(fos);
                boolean running = true;
                for (int ch = in.read(); ch != -1; ch = in.read()) {
                    serverOutput.append((char)ch);
                    String s = serverOutput.toString();
                    if (s.contains("server ready")) {
                        notifyServerIsReady();
                    } else if (s.contains("server passed")) {
                        serverPassed = true;
                    } else if (s.contains("server stopped")) {
                        notifyServerIsStopped();
                        running = false;
                    } else if (s.contains(SERVER_FAILED)) {
                        notifyServerFailed();
                        running = false;
                    }
                    if (ch == '\n' || !running) {
                        if (debug) {
                            System.out.println("Server Output: " + s);
                        }
                        synchronized (out) {
                            ps.print(serverOutput.toString());
                            serverOutput = new StringBuilder();
                            ps.flush();
                        }
                    }
                }
                
            } catch (IOException ex) {
                if (!ex.getMessage().contains("Stream closed")) {
                    ex.printStackTrace();
                }
            }
        }
    }

    void notifyServerIsReady() {
        synchronized (mutex) {
            serverIsReady = true;
            mutex.notifyAll();
        }
    }

    void notifyServerIsStopped() {
        synchronized (mutex) {
            LOG.info("notify server stopped");
            serverIsStopped = true;
            mutex.notifyAll();
        }
    }

    void notifyServerFailed() {
        synchronized (mutex) {
            serverIsStopped = true;
            mutex.notifyAll();
        }
    }

    private List<String> getCommand() {

        List<String> cmd = new ArrayList<String>();
        cmd.add(javaExe);
        
        if (null != properties) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                cmd.add("-D" + entry.getKey() + "=" + entry.getValue());
            }
        }

        cmd.add("-ea");
        cmd.add("-classpath");
        
        ClassLoader loader = this.getClass().getClassLoader();
        StringBuffer classpath = new StringBuffer(System.getProperty("java.class.path"));
        if (loader instanceof URLClassLoader) {
            URLClassLoader urlloader = (URLClassLoader)loader; 
            for (URL url : urlloader.getURLs()) {
                classpath.append(File.pathSeparatorChar);
                classpath.append(url.getFile());
            }
        }
        cmd.add(classpath.toString());
        
        cmd.add("-Djavax.xml.ws.spi.Provider=org.apache.cxf.bus.jaxws.spi.ProviderImpl");
        
        String loggingPropertiesFile = System.getProperty("java.util.logging.config.file");
        if (null != loggingPropertiesFile) {
            cmd.add("-Djava.util.logging.config.file=" + loggingPropertiesFile);
        } 

        cmd.add(className);

        if (null != serverArgs) {
            for (String s : serverArgs) {
                cmd.add(s);
            }
        }

        return cmd;
    }

    static class Mutex {
        // empty
    }

    static class TimeoutCounter {
        private final long expectedEndTime;

        public TimeoutCounter(long theExpectedTimeout) {
            expectedEndTime = System.currentTimeMillis() + theExpectedTimeout;
        }

        public boolean isTimeoutExpired() {
            return System.currentTimeMillis() > expectedEndTime;
        }
    }
}
