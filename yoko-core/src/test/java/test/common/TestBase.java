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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public class TestBase {
    public static org.omg.CORBA.TypeCode getOrigType(org.omg.CORBA.TypeCode tc) {
        org.omg.CORBA.TypeCode result = tc;

        try {
            while (result.kind() == org.omg.CORBA.TCKind.tk_alias)
                result = result.content_type();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            throw new AssertionError(ex);
        }

        return result;
    }

    protected static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object obj,
            NamingContextExt context, NameComponent[] name) throws InvalidName {
        out.println("ref:");
        String ref = orb.object_to_string(obj);
        out.println(ref);
        String nameString = context.to_string(name);
        out.println(nameString);
    }

    protected static void readRef(BufferedReader reader, String[] refStrings) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new RuntimeException("Unknown Server error");
        } else if (!!!line.equals("ref:")) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                pw.println("Server error:");
                do {
                    pw.print('\t');
                    pw.println(line);
                } while ((line = reader.readLine()) != null);
                pw.flush();
                throw new RuntimeException(sw.toString());
            }
        }
        refStrings[0] = reader.readLine(); // IOR
        refStrings[1] = reader.readLine(); // name
        System.out.println(refStrings[1] +  " = "+ refStrings[0]);
    }
}
