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
package test.common;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

import javax.rmi.PortableRemoteObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Remote;

public class TestBase {
    public static TypeCode getOrigType(TypeCode tc) {
        TypeCode result = tc;

        try {
            while (result.kind() == TCKind.tk_alias)
                result = result.content_type();
        } catch (BadKind ex) {
            throw new AssertionError(ex);
        }

        return result;
    }

    protected static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object obj,
            NamingContextExt context, NameComponent[] name) throws InvalidName {
        writeRef(orb, out, obj, context.to_string(name));
    }

    private static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object obj, String name) {
        out.println("ref:");
        out.println(orb.object_to_string(obj));
        out.println(name);
    }
    
    protected static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object obj) {
        writeRef(orb, out, obj, "");
    }

    protected static String[] readRef(BufferedReader reader, String[] refStrings) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new RuntimeException("Unknown InsServer error");
        } else if (!line.equals("ref:")) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                pw.println("InsServer error:");
                do {
                    pw.print('\t');
                    pw.println(line);
                } while ((line = reader.readLine()) != null);
                pw.flush();
                throw new RuntimeException(sw.toString());
            }
        }
        refStrings[0] = reader.readLine();
        refStrings[1] = reader.readLine();
        return refStrings;
    }

    private static org.omg.CORBA.Object readGenericStub(ORB orb, BufferedReader reader) throws IOException {
        return orb.string_to_object(readRef(reader, new String[2])[0]);
    }

    @SuppressWarnings("unchecked")
    protected static<T extends Remote> T readRmiStub(ORB orb, BufferedReader reader, Class<T> type) throws ClassCastException, IOException {
        return (T)PortableRemoteObject.narrow(readGenericStub(orb, reader), type);
    }

    protected static BufferedReader openFileReader(final String refFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(refFile)) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    Files.delete(Paths.get(refFile));
                }
            }
        };
    }
}
