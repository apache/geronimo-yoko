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

package org.apache.yoko.orb.OB;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class IORDump {

    public static String PrintObjref(org.omg.CORBA.ORB orb,
            org.omg.IOP.IOR ior) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintObjref(orb, ps, ior);
        ps.flush();
        return baos.toString();
    }

    static public void PrintObjref(org.omg.CORBA.ORB orb, java.io.PrintStream out,
            org.omg.IOP.IOR ior) {
        out.println("type_id: " + ior.type_id);

        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = null;
        try {
            org.omg.CORBA.Object obj = orb
                    .resolve_initial_references("OCIConFactoryRegistry");
            conFactoryRegistry = org.apache.yoko.orb.OCI.ConFactoryRegistryHelper
                    .narrow(obj);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            Assert._OB_assert(ex);
        }
        org.apache.yoko.orb.OCI.ConFactory[] factories = conFactoryRegistry
                .get_factories();

        for (int i = 0; i < ior.profiles.length; i++) {
            out.print("Profile #" + (i + 1) + ": ");
            if (ior.profiles[i].tag == org.omg.IOP.TAG_MULTIPLE_COMPONENTS.value) {
                out.print("multiple components");

                byte[] data = ior.profiles[i].profile_data;
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                        data, data.length);
                org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                        buf);
                in._OB_readEndian();

                int cnt = in.read_ulong();
                if (cnt == 0)
                    out.println();
                else {
                    for (int j = 0; j < cnt; j++) {
                        org.omg.IOP.TaggedComponent comp = org.omg.IOP.TaggedComponentHelper
                                .read(in);

                        String desc = IORUtil.describe_component(comp);
                        out.println(desc);
                    }
                }
            } else {
                int j;
                for (j = 0; j < factories.length; j++) {
                    if (factories[j].tag() == ior.profiles[i].tag) {
                        out.println(factories[j].id());
                        String desc = factories[j]
                                .describe_profile(ior.profiles[i]);
                        out.print(desc);
                        break;
                    }
                }

                if (j >= factories.length) {
                    out.println("unknown profile tag " + ior.profiles[i].tag);
                    out.println("profile_data: ("
                            + ior.profiles[i].profile_data.length + ")");
                    String data = IORUtil.dump_octets(
                            ior.profiles[i].profile_data, 0,
                            ior.profiles[i].profile_data.length);
                    out.print(data); // No newline
                }
            }
        }
    }

    static public void DumpIOR(org.omg.CORBA.ORB orb, String ref, boolean hasEndian) {
        DumpIOR(orb, ref, hasEndian, System.out);
    }

    static public String DumpIORToString(org.omg.CORBA.ORB orb, String ref, boolean hasEndian) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        DumpIOR(orb, ref, hasEndian, ps);
        return baos.toString();
    }

    static public void DumpIOR(org.omg.CORBA.ORB orb, String ref, boolean hasEndian, PrintStream ps) {
        if (!ref.startsWith("IOR:")) {
            ps.println("IOR is invalid");
            return;
        }

        byte[] data = HexConverter.asciiToOctets(ref, 4);
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                data, data.length);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf);

        boolean endian = in.read_boolean();
        in._OB_swap(endian);

        org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);

        ps.print("byteorder: ");
        if (hasEndian)
            ps.println((endian ? "little" : "big") + " endian");
        else
            ps.println("n/a");

        PrintObjref(orb, ps, ior);

        ps.flush();
    }

    static void usage() {
        System.err.println("Usage:");
        System.err
                .println("org.apache.yoko.orb.OB.IORDump [options] [-f FILE ... | IOR ...]\n"
                        + "\n"
                        + "Options:\n"
                        + "-h, --help          Show this message.\n"
                        + "-v, --version       Show Yoko version.\n"
                        + "-f                  Read IORs from files instead of from the\n"
                        + "                    command line.");
    }

    public static int run(org.omg.CORBA.ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        //
        // Get options
        //
        boolean files = false;
        int i;
        for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
            if (args[i].equals("--help") || args[i].equals("-h")) {
                usage();
                return 0;
            } else if (args[i].equals("--version") || args[i].equals("-v")) {
                System.out.println("Yoko " + Version.getVersion());
                return 0;
            } else if (args[i].equals("-f")) {
                files = true;
            } else {
                System.err.println("IORDump: unknown option `" + args[i] + "'");
                usage();
                return 1;
            }
        }

        if (i == args.length) {
            System.err.println("IORDump: no IORs");
            System.err.println();
            usage();
            return 1;
        }

        if (!files) {
            if (!args[i].startsWith("IOR:") && !args[i].startsWith("corbaloc:")
                    && !args[i].startsWith("corbaname:")
                    && !args[i].startsWith("file:")
                    && !args[i].startsWith("relfile:")) {
                System.err.println("[No valid IOR found on the command "
                        + "line, assuming -f]");
                files = true;
            }
        }

        if (!files) {
            //
            // Dump all IORs given as arguments
            //
            int count = 0;
            for (; i < args.length; i++) {
                if (count > 0)
                    System.out.println();
                System.out.println("IOR #" + (++count) + ':');

                try {
                    //
                    // The byte order can only be preserved for IOR: URLs
                    //
                    if (args[i].startsWith("IOR:"))
                        DumpIOR(orb, args[i], true);
                    else {
                        //
                        // Let string_to_object do the dirty work
                        //
                        org.omg.CORBA.Object obj = orb
                                .string_to_object(args[i]);
                        String s = orb.object_to_string(obj);
                        DumpIOR(orb, s, false);
                    }
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    System.err.println("IOR is invalid");
                }
            }
        } else {
            //
            // Dump all IORs from the specified files
            //
            int count = 0;
            for (; i < args.length; i++) {
                try {
                    java.io.FileReader fin = new java.io.FileReader(args[i]);
                    java.io.BufferedReader in = new java.io.BufferedReader(fin);
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.length() > 0) {
                            if (count > 0)
                                System.out.println();
                            System.out.println("IOR #" + (++count) + ':');

                            //
                            // The byte order can only be preserved for
                            // IOR: URLs
                            //
                            if (line.startsWith("IOR:"))
                                DumpIOR(orb, line, true);
                            else {
                                //
                                // Let string_to_object do the dirty work
                                //
                                org.omg.CORBA.Object obj = orb
                                        .string_to_object(line);
                                String s = orb.object_to_string(obj);
                                DumpIOR(orb, s, false);
                            }
                        }
                    }
                } catch (java.io.FileNotFoundException ex) {
                    System.err.println("IORDump: can't open `" + args[i]
                            + "': " + ex);
                    return 1;
                } catch (java.io.IOException ex) {
                    System.err.println("IORDump: can't read `" + args[i]
                            + "': " + ex);
                    return 1;
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status;
        org.omg.CORBA.ORB orb = null;

        try {
            args = org.apache.yoko.orb.CORBA.ORB.ParseArgs(args, props, null);
            orb = org.omg.CORBA.ORB.init(args, props);
            status = run(orb, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}
