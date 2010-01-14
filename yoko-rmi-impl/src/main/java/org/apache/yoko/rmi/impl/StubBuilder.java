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

package org.apache.yoko.rmi.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StubBuilder {
    public StubBuilder() {
    }

    /**
     * Generate stubs for the list of Classes (java.lang.Class objects)
     * specified in INTERFACES; into the directory specified by DIR. The
     * resulting set of generated classes may well include more classes than
     * specified in INTERFACES.
     * 
     * @return list of File objects for generated files.
     */
    static public Collection generateStubs(File dir, Collection interfaces)
            throws IOException {
        Set pending = new HashSet();
        ArrayList result = new ArrayList();

        TypeRepository rep = new TypeRepository(null);
        Iterator it = interfaces.iterator();
        while (it.hasNext()) {
            Class cl = (Class) it.next();
            RemoteDescriptor desc = (RemoteDescriptor) rep.getDescriptor(cl);

            desc.addDependencies(pending);
        }

        Class[] classes = new Class[pending.size()];
        pending.toArray(classes);

        for (int i = 0; i < classes.length; i++) {

            if (!java.rmi.Remote.class.isAssignableFrom(classes[i])) {
                continue;
            }

            RemoteDescriptor desc = (RemoteDescriptor) rep
                    .getDescriptor(classes[i]);

            String name = desc.getStubClassName();
            String file = name.replace('.', File.separatorChar) + ".java";

            File stubfile = new File(dir, file);
            File gendir = stubfile.getParentFile();

            gendir.mkdirs();

            PrintWriter pw = new PrintWriter(new FileWriter(stubfile));

            desc.writeStubClass(pw);

            pw.close();

            result.add(file);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        ArrayList al = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            al.add(Class.forName(args[i]));
        }

        StubBuilder.generateStubs(new File("."), al);
    }
}
