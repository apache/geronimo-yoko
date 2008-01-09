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

package test.ins;

import java.util.Properties;
import org.omg.CORBA.*;

//
// Corba URL client test class
//
final public class Client {
    //
    // Simple exception class for test failures
    //
    private static class TestFailure extends Exception {
        TestFailure() {
        }

        //
        // Logging constructor
        //
        TestFailure(String msg) {
            System.err.println(msg);
        }
    }

    private static class ClientTest {
        ORB orb;

        //
        // number of times to repeat the test
        //
        int count;

        //
        // object references to use during testing
        //
        test.ins.URLTest.IIOPAddress[] ior = new test.ins.URLTest.IIOPAddress[2];

        //
        // multi profile IOR tests are disabled in this beta release
        //
        private final boolean multiProfileTest = false;

        //
        // Test corbaloc URL construction that uses full OB4 Object Keys
        // in a manner compatible with the IMR utilities
        //
        private final boolean imrTest = true;

        ClientTest() {
        }

        //
        // Create the client test with the associated ORB
        //
        void init(ORB aOrb, int aCount) {
            orb = aOrb;
            count = aCount;
        }

        //
        // Convert a URL string to a test.ins.URLTest.IIOPAddress reference
        // raises a TestFailure exception if the reference is null or the
        // URL is invalid.
        //
        test.ins.URLTest.IIOPAddress stringToIIOPAddress(String objStr)
                throws TestFailure {
            org.omg.CORBA.Object obj = null;
            test.ins.URLTest.IIOPAddress nObj = null;
            boolean stringToObjOK = false;

            try {
                obj = orb.string_to_object(objStr);
                stringToObjOK = true;

                nObj = test.ins.URLTest.IIOPAddressHelper.narrow(obj);

                if (obj == null) {
                    System.err.println("Error stringToIIOPAddress:");
                    System.err.println("object `" + objStr + "; is nil "
                            + "reference.");
                    throw new TestFailure();
                }
            } catch (SystemException se) {
                System.err.println("Error stringToIIOPAddress:");
                se.printStackTrace();
                System.err.println("Original URL: " + objStr);
                System.err.print("Resultant URL: ");
                if (stringToObjOK == false)
                    System.err.println("NONE, string_to_object raised the "
                            + "exception");
                else {
                    String iorStr = orb.object_to_string(obj);
                    System.err.println(iorStr);
                }
                throw new TestFailure();
            }

            return nObj;
        }

        //
        // Log test start
        //
        void declare_test(String msg) {
            System.out.print("Testing " + msg + "... ");
            System.out.flush();
        }

        //
        // Log test end, raise a TestFailure on a false test result
        //
        void finish_test(boolean isOk) throws TestFailure {
            System.out.println((isOk ? "Done!" : "Failed!"));

            if (isOk == false)
                throw new TestFailure();
        }

        //
        // Get the proper "unsigned" int value from the port short
        //
        int getIntPort(test.ins.URLTest.IIOPAddress obj) {
            int iPort = obj.getPort();

            if (iPort < 0)
                iPort += 65536;

            return iPort;
        }

        //
        // Compare two IIOPAddress objects by querying them for
        // host, port and key information. This *does not* compare
        // the results of getString.
        //
        boolean compareObjs(test.ins.URLTest.IIOPAddress iop1,
                test.ins.URLTest.IIOPAddress iop2) {
            String cKey[] = new String[2];
            String cHost[] = new String[2];
            int cPort[] = new int[2];

            test.ins.URLTest.IIOPAddress activeIOR = null;

            try {
                activeIOR = iop1;
                cKey[0] = iop1.getKey();
                cHost[0] = iop1.getHost();
                cPort[0] = getIntPort(iop1);

                activeIOR = iop2;

                cKey[1] = iop2.getKey();
                cHost[1] = iop2.getHost();
                cPort[1] = getIntPort(iop2);
            } catch (SystemException se) {
                System.out.print("Error compareObj, invoking:");
                se.printStackTrace();
                String iorStr = orb.object_to_string(activeIOR);
                System.out.println(iorStr);
                throw se;
            }

            return (cKey[0].equals(cKey[1]) && cHost[0].equals(cHost[1]) && cPort[0] == cPort[1]);
        }

        //
        // Simplistic escape - escapes more than necessary but is ok
        // for testing
        //
        String escape(byte[] arr) {
            StringBuffer result = new StringBuffer(arr.length * 3);
            for (int i = 0; i < arr.length; i++) {
                result.append('%');
                result.append(Character.toUpperCase(Character.forDigit(
                        (arr[i] >> 4) & 0xF, 16)));
                result.append(Character.toUpperCase(Character.forDigit(
                        arr[i] & 0xF, 16)));
            }
            return result.toString();
        }

        //
        // Construct the iiop address portion of a URL for an IIOPAddress.
        // "iiop:555objs.com:2809" for example.
        //
        String getURLAddressComponent(test.ins.URLTest.IIOPAddress nObj,
                String prefix) {
            String url = prefix != null ? prefix : ":";
            url += nObj.getHost();
            url += ":";
            url += Integer.toString(getIntPort(nObj));
            return url;
        }

        //
        // Run a single test pass
        //
        void runPass(int passNumber) throws TestFailure {
            //
            // Start with very basic tests
            //
            String[] host = new String[2];
            String[] key = new String[2];
            int[] port = new int[2];

            //
            // Test that initial object strings are ok (Tests scheme
            // provided on command line)
            //
            declare_test("string_to_object");

            for (int i = 0; i < 2; i++) {
                key[i] = ior[i].getKey();
                host[i] = ior[i].getHost();
                port[i] = getIntPort(ior[i]);
            }
            finish_test(true);

            //
            // Verify string_to_object -> object_to_string round-trip
            // works as expected
            //
            declare_test("object_to_string");

            for (int i = 0; i < 2; i++) {
                String iorStr = orb.object_to_string(ior[i]);
                test.ins.URLTest.IIOPAddress nAddr = stringToIIOPAddress(iorStr);
                if (compareObjs(ior[i], nAddr) == false)
                    finish_test(false);
            }

            finish_test(true);

            //
            // Check null URL string handling.
            //
            declare_test("null URL string exception");
            {
                boolean correctException = false;
                try {
                    org.omg.CORBA.Object obj = orb.string_to_object(null);
                } catch (BAD_PARAM bp) {
                    correctException = true;
                }

                finish_test(correctException);
            }
            //
            // Check illegal port and no valid scheme parsing.
            //
            declare_test("bad corbaloc port and scheme exceptions");
            {
                //
                // Try same URL with legal, and illegal low, high ports.
                //
                int exceptionCount = 0;
                String urlStr[] = { "corbaloc:iiop:localhost:2809/a/b/c",
                        "corbaloc:iiop:localhost:0/a/b/c",
                        "corbaloc:iiop:localhost:65536/a/b/c",
                        "corbaloc:iiop:localhost:-90/a/b/c",
                        "corbaloc:iiop:localhost:not_a_number/a/b/c",
                        "corbaloc:corbaloc-bad-scheme:localhost:not_a_number/a/b/c" };

                for (int i = 0; i < 6; ++i) {
                    try {
                        org.omg.CORBA.Object obj = orb
                                .string_to_object(urlStr[i]);
                    } catch (BAD_PARAM bp) {
                        //
                        // legal URL must not raise exception
                        //
                        if (i == 0)
                            finish_test(false);

                        ++exceptionCount;
                    }
                }

                //
                // The last 5 URLs should have failed
                //
                finish_test(exceptionCount == 5);
            }

            //
            // Basic corbaloc test
            //
            declare_test("iiop default version 1.0 corbaloc URL");

            for (int i = 0; i < 2; i++) {
                String url = ior[i].getCorbalocURL();
                test.ins.URLTest.IIOPAddress nObj = stringToIIOPAddress(url);

                if (compareObjs(ior[i], nObj) == false)
                    finish_test(false);

                String str = nObj.getString();

                if (str.equals("corbaloc") == false)
                    finish_test(false);
            }

            finish_test(true);

            //
            // Test URLs with 1.0 - 1.9 iiop profiles
            //
            declare_test("iiop version 1.0 - 1.9 corbaloc URL");

            for (int i = 0; i < 2; i++) {
                for (int iv = 0; iv < 10; ++iv) {
                    String addr = getURLAddressComponent(ior[i], ":1." + iv
                            + "@");
                    String url = "corbaloc:" + addr + "/" + ior[i].getKey();

                    test.ins.URLTest.IIOPAddress nObj = stringToIIOPAddress(url);

                    if (compareObjs(ior[i], nObj) == false)
                        finish_test(false);
                }
            }
            finish_test(true);

            //
            // Versioned corbaloc rejection test.
            //
            declare_test("unsupported iiop version corbaloc URL");
            for (int i = 0; i < 2; i++) {
                //
                // Try iiop major versions 0 and 2
                //
                String iiopMajor = (i == 0) ? ":0." : ":2.";

                for (int iv = 0; iv < 10; ++iv) {
                    String addr = getURLAddressComponent(ior[i], iiopMajor + iv
                            + "@");
                    String url = "corbaloc:" + addr + "/" + ior[i].getKey();
                    try {
                        orb.string_to_object(url);
                        System.err.println("\nIncorrectly accepted '" + url
                                + "'");
                        finish_test(false);
                    } catch (BAD_PARAM ex) {
                    }
                }
            }
            finish_test(true);

            //
            // Test corbaloc access the same way the
            // IMR mkref utility generates corbaloc's.
            //
            declare_test("IMR corbaloc");

            for (int i = 0; i < 2; i++) {
                //
                // Create the ORBacus specific object key
                //
                String iorKey = ior[i].getKey();

                byte[] oid = iorKey.getBytes();

                org.apache.yoko.orb.OB.ObjectKeyData data = new org.apache.yoko.orb.OB.ObjectKeyData();
                data.persistent = true;
                data.createTime = 0;
                data.oid = oid;
                data.serverId = "_RootPOA";
                data.poaId = new String[1];
                data.poaId[0] = "testPOA";

                byte[] oct = org.apache.yoko.orb.OB.ObjectKey
                        .CreateObjectKey(data);
                String keyStr = escape(oct);

                //
                // Create a corbaloc URL for this
                //
                String addr = getURLAddressComponent(ior[i],
                        (i & 1) != 0 ? ":1.1@" : ":1.2@");
                String url = "corbaloc:" + addr + '/' + keyStr;

                test.ins.URLTest.IIOPAddress nObj = stringToIIOPAddress(url);

                if (compareObjs(ior[i], nObj) == false)
                    finish_test(false);

                String str = nObj.getString();

                if (!str.equals("corbaloc"))
                    finish_test(false);
            }

            finish_test(true);

            //
            // Test if object can be contacted, if "active" profile
            // is the first in a multi-profile IOR
            //
            declare_test("active profile 1 in a multi-profile IOR");
            for (int i = 0; i < 2; i++) {
                String url = "corbaloc:";
                String addr = getURLAddressComponent(ior[i], null);
                url += addr;

                //
                // add 2 dummy profiles at corbaloc reserved port
                //
                url += ",:localhost:2809";
                url += ",:localhost:2809";

                url += "/" + ior[i].getKey();

                test.ins.URLTest.IIOPAddress nObj = stringToIIOPAddress(url);

                if (compareObjs(ior[i], nObj) == false)
                    finish_test(false);

                String str = nObj.getString();

                if (!str.equals("corbaloc"))
                    finish_test(false);
            }
            finish_test(true);

            //
            // Test if object can be contacted, if "active" profile
            // is third in a multi-profile IOR
            //
            boolean multi_3_test = false;
            if (multi_3_test) {
                //
                // multi-profile test 3
                //
                declare_test("active profile 3 in a multi-profile IOR");

                for (int i = 0; i < 2; i++) {
                    String url = "corbaloc:";

                    //
                    // 2 dummy profiles at corbaloc reserved port
                    //
                    url += ":localhost:2809";
                    url += ",:localhost:2809,";

                    //
                    // usable profile
                    //
                    String addr = getURLAddressComponent(ior[i], null);
                    url += addr;

                    url += "/" + ior[i].getKey();

                    test.ins.URLTest.IIOPAddress nObj = stringToIIOPAddress(url);

                    if (compareObjs(ior[i], nObj) == false)
                        finish_test(false);
                }
                finish_test(true);
            }
        }

        //
        // Run the test suite, parsing the test pass count and IIOPAddress
        // URLs
        //
        void run() throws TestFailure {
            //
            // Run tests
            //
            for (int c = 0; c < count; c++) {
                if (count != 1)
                    System.out.println("Test Pass: " + (c + 1));
                try {
                    runPass(c);
                } catch (TestFailure tfe) {
                    //
                    // In multi-pass test, continue for TestFailure
                    //
                    if (count == 1)
                        throw tfe;
                }
            }
        }
    }

    //
    // URL test main, arguments are: test loop count and the URL of the
    // server, a second server URL can optionally be specified
    //
    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        if (args.length < 2) {
            System.out.println("usage: test.ins.Client loop_count "
                    + "ior_url1 [ior_url2]");
            return 1;
        }

        int count = Integer.parseInt(args[0]);

        if (count <= 0) {
            System.err.println("bad test count: " + args[0]);
            return 1;
        }

        ClientTest test = new ClientTest();
        test.init(orb, count);

        int status = 0;

        try {
            test.ior[0] = test.stringToIIOPAddress(args[1]);
            test.ior[1] = (args.length != 3) ? test.ior[0] : test
                    .stringToIIOPAddress(args[2]);

            test.run();
        } catch (TestFailure ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (test.ior[0] != null)
            test.ior[0].deactivate();

        return status;
    }

    //
    // Client test entry point
    //
    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
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
