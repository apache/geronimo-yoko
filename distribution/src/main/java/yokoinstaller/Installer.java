/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package yokoinstaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class Installer {
    static final Set BINARY_EXTS = new TreeSet();
    static {
        BINARY_EXTS.add("jar");
        BINARY_EXTS.add("zip");
        BINARY_EXTS.add("gif");
        BINARY_EXTS.add("jpg");
        BINARY_EXTS.add("jpeg");
        BINARY_EXTS.add("pdf");
        BINARY_EXTS.add("png");
        BINARY_EXTS.add("odt");
        BINARY_EXTS.add("ott");
    }
    static boolean verbose;

    private Installer() {
        //never constructed
    }

    private static boolean isBinary(String s) {
        if (s.indexOf("maven_repo") != -1) {
            return true;
        }
        Iterator it = BINARY_EXTS.iterator();
        while (it.hasNext()) {
            String ext = (String)it.next();
            if (s.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) throws Exception {
        File outputDir = new File(".");
        outputDir = outputDir.getCanonicalFile();

        if (!System.getProperty("java.version").startsWith("1.5")) {
            System.out.println("WARNING: Installing with Java " + System.getProperty("java.version") + ".");
            System.out.println("         Celtix requires JDK 1.5 to run.");
        }

        if (args.length != 0 && "-verbose".equals(args[0])) {
            verbose = true;
            String tmp[] = new String[args.length - 1];
            System.arraycopy(args, 1, tmp, 0, args.length - 1);
            args = tmp;
        }
        if (args.length != 0) {
            outputDir = new File(args[0]);
        }

        System.out.println("Unpacking yoko to " + outputDir.toString());


        URL url = Installer.class.getResource("/yokoinstaller/Installer.class");
        String jarf = url.getFile();
        jarf = jarf.substring(0, jarf.indexOf("!"));
        url = new URL(jarf);

        byte buffer[] = new byte[4096];
        JarInputStream jin = new JarInputStream(new FileInputStream(url.getFile()));
        List executes = new ArrayList();


        for (JarEntry entry = jin.getNextJarEntry(); entry != null; entry = jin.getNextJarEntry()) {
            if (entry.isDirectory()) {
                if (!entry.getName().startsWith("META-INF") 
                    && !entry.getName().startsWith("yokoinstaller")) {
                    if (verbose) {
                        System.out.println("Making directory: " + entry.getName());
                    }
                    File file = new File(outputDir, entry.getName());
                    file.mkdirs();
                    file.setLastModified(entry.getTime());
                }
            } else if (!entry.getName().startsWith("META-INF")
                       && !entry.getName().startsWith("yokoinstaller")) {

                boolean binary = isBinary(entry.getName().toLowerCase());
                if ((entry.getName().indexOf("/bin/") != -1
                    || entry.getName().indexOf("\\bin\\") != -1)
                    && !entry.getName().toLowerCase().endsWith(".bat")) {
                    executes.add(entry.getName());
                }

                File outFile = new File(outputDir, entry.getName());
                if (binary) {
                    if (verbose) {
                        System.out.println("Installing Binary: " + entry.getName());
                    }
                    
                    OutputStream out = new FileOutputStream(outFile);
                    for (int len = jin.read(buffer); len != -1; len = jin.read(buffer)) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                } else {
                    if (verbose) {
                        System.out.println("Installing Text: " + entry.getName());
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jin));
                    for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                        writer.write(s);
                        writer.newLine();
                    }
                    writer.close();
                }
                outFile.setLastModified(entry.getTime());
            }
            
        }
        setExecutable(executes);
    }

    
    static void setExecutable(List executes) throws Exception {
        if (System.getProperty("os.name").indexOf("Windows") == -1
            && !executes.isEmpty()) {
            if (verbose) {
                Iterator it = executes.iterator();
                while (it.hasNext()) {
                    System.out.println("Setting executable: " + it.next());
                }
            }
            
            
            //add executable bit
            executes.add(0, "chmod");
            executes.add(1, "+x");

            Runtime.getRuntime().exec((String[])executes.toArray(new String[executes.size()]));
        }        
    }
}
