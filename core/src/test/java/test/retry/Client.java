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

package test.retry;

import java.util.Properties;

public class Client extends test.common.TestBase {
    static void testLocationForward(org.omg.CORBA.ORB orb, RetryServer server) {
        System.out.print("Testing forwarding loop detection... ");
        System.out.flush();
        //
        // Verify that we detect excessive forwarding
        //
        Test test = server.get_location_forward_object();
        try {
            test.aMethod();
            TEST(false);
        } catch (org.omg.CORBA.TRANSIENT ex) {
            // Expected
            TEST(ex.minor == org.apache.yoko.orb.OB.MinorCodes.MinorLocationForwardHopCountExceeded);
        }
        System.out.println("Done!");
    }

    static Retry getRetry(org.omg.CORBA.ORB orb, Retry orig, short mode,
            int interval, int max, boolean remote) {
        try {
            org.apache.yoko.orb.OB.RetryAttributes attrib = new org.apache.yoko.orb.OB.RetryAttributes();
            attrib.mode = mode;
            attrib.interval = interval;
            attrib.max = max;
            attrib.remote = remote;
            org.omg.CORBA.Any a = orb.create_any();
            org.apache.yoko.orb.OB.RetryAttributesHelper.insert(a, attrib);
            org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[1];
            policies[0] = orb.create_policy(
                    org.apache.yoko.orb.OB.RETRY_POLICY_ID.value, a);
            org.omg.CORBA.Object obj = orig._set_policy_override(policies,
                    org.omg.CORBA.SetOverrideType.SET_OVERRIDE);
            return RetryHelper.narrow(obj);
        } catch (org.omg.CORBA.UserException ex) {
            ex.printStackTrace();
            TEST(false);
            return null;
        }
    }

    static void testRetry(org.omg.CORBA.ORB orb, RetryServer server) {
        Retry retry;

        Retry orig = server.get_retry_object();

        //
        // Test: Do not retry on remote exceptions
        //
        System.out.print("Testing retry for remote exceptions... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                0, 0, false);
        retry.raise_exception(1, false);
        try {
            retry.aMethod();
            TEST(false);
        } catch (org.omg.CORBA.TRANSIENT ex) {
            // Expected
            TEST(retry.get_count() == 1);
        }

        //
        // Test: Retry once on remote exceptions
        //
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                0, 0, true);
        retry.raise_exception(1, false);
        try {
            retry.aMethod();
            TEST(retry.get_count() == 2);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }
        System.out.println("Done!");

        //
        // Test: Don't retry if completion status is COMPLETED_MAYBE and
        // mode is RETRY_STRICT
        //
        System.out.print("Testing RETRY_STRICT... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                0, 0, true);
        retry.raise_exception(1, true);
        try {
            retry.aMethod();
            TEST(false);
        } catch (org.omg.CORBA.SystemException ex) {
            // Expected
            TEST(retry.get_count() == 1);
        }
        System.out.println("Done!");

        //
        // Test: RETRY_NEVER
        //
        System.out.print("Testing RETRY_NEVER... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_NEVER.value,
                0, 0, true);
        retry.raise_exception(1, false);
        try {
            retry.aMethod();
            TEST(false);
        } catch (org.omg.CORBA.TRANSIENT ex) {
            // Expected
            TEST(retry.get_count() == 1);
        }
        System.out.println("Done!");

        //
        // Test: RETRY_ALWAYS
        //
        System.out.print("Testing RETRY_ALWAYS... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_ALWAYS.value,
                0, 0, true);
        retry.raise_exception(1, true);
        try {
            retry.aMethod();
            TEST(retry.get_count() == 2);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }
        System.out.println("Done!");

        //
        // Test: Retry five times
        //
        System.out.print("Testing multiple retries... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                0, 0, true);
        retry.raise_exception(5, false);
        try {
            retry.aMethod();
            TEST(retry.get_count() == 6);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }

        //
        // Test: Retry five times with failure
        //
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                0, 5, true);
        retry.raise_exception(6, false);
        try {
            retry.aMethod();
            TEST(false);
        } catch (org.omg.CORBA.TRANSIENT ex) {
            // Expected
            TEST(retry.get_count() == 6);
        }
        System.out.println("Done!");

        //
        // Test: Retry interval
        //
        System.out.print("Testing retry interval... ");
        System.out.flush();
        retry = getRetry(orb, orig, org.apache.yoko.orb.OB.RETRY_STRICT.value,
                100, 0, true);
        retry.raise_exception(12, false);
        try {
            long start = System.currentTimeMillis();
            retry.aMethod();
            long stop = System.currentTimeMillis();
            TEST((stop - start) > 1000);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }
        System.out.println("Done!");
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        org.omg.CORBA.ORB orb = null;

        try {
            orb = org.omg.CORBA.ORB.init(args, props);

            org.omg.CORBA.Object obj = orb
                    .string_to_object("relfile:/Test.ref");
            if (obj == null) {
                System.err.println("cannot read IOR from Test.ref");
                System.exit(1);
            }
            RetryServer server = RetryServerHelper.narrow(obj);
            TEST(server != null);

            testLocationForward(orb, server);
            testRetry(orb, server);

            server.deactivate();
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
