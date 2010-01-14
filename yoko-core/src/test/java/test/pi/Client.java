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

package test.pi;

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableInterceptor.*;
import test.pi.TestInterfacePackage.*;
import java.io.*;

public final class Client extends test.common.TestBase {
    private static void TestTranslation(ORB orb, ClientProxyManager manager,
            TestInterface ti) {
        //
        // Set up the correct interceptor
        //
        TranslateCallInterceptor_impl i0 = new TranslateCallInterceptor_impl();
        TranslateCallInterceptor_impl i1 = new TranslateCallInterceptor_impl();
        TranslateCallInterceptor_impl i2 = new TranslateCallInterceptor_impl();

        manager.setInterceptor(0, i0);
        manager.setInterceptor(1, i1);
        manager.setInterceptor(2, i2);

        i0.throwOnRequest(new NO_PERMISSION());
        try {
            ti.noargs();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i0.noThrowOnRequest();
        i0.throwOnReply(new NO_PERMISSION());
        try {
            ti.noargs();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i0.noThrowOnReply();

        i1.throwOnReply(new NO_PERMISSION());
        i0.expectException(new NO_PERMISSION());
        try {
            ti.noargs();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i1.noThrowOnReply();

        i0.expectException(new NO_PERMISSION());
        i1.expectException(new BAD_INV_ORDER());
        i1.throwOnException(new NO_PERMISSION());
        i2.throwOnRequest(new BAD_INV_ORDER());

        try {
            ti.noargs();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i2.noThrowOnRequest();
        i2.throwOnReply(new BAD_INV_ORDER());

        try {
            ti.noargs();
            TEST(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        manager.clearInterceptors();
    }

    private static void TestCalls(ORB orb, ClientProxyManager manager,
            TestInterface ti) {
        org.omg.PortableInterceptor.Current pic = null;
        try {
            org.omg.CORBA.Object obj = orb
                    .resolve_initial_references("PICurrent");
            pic = org.omg.PortableInterceptor.CurrentHelper.narrow(obj);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }
        TEST(pic != null);

        Any slotData = orb.create_any();
        slotData.insert_long(10);

        try {
            pic.set_slot(0, slotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }

        //
        // Set up the correct interceptor
        //
        CallInterceptor_impl impl = new CallInterceptor_impl(orb);
        ClientRequestInterceptor interceptor = impl;
        manager.setInterceptor(0, interceptor);
        int num = 0;

        ti.noargs();
        TEST(++num == impl._OB_numReq());

        ti.noargs_oneway();
        TEST(++num == impl._OB_numReq());

        try {
            ti.userexception();
            TEST(false);
        } catch (user ex) {
        }
        TEST(++num == impl._OB_numReq());

        try {
            ti.systemexception();
            TEST(false);
        } catch (SystemException ex) {
        }
        TEST(++num == impl._OB_numReq());

        ti.test_service_context();
        TEST(++num == impl._OB_numReq());

        try {
            ti.location_forward();
        } catch (NO_IMPLEMENT ex) {
        }
        TEST(++num == impl._OB_numReq());

        //
        // Test simple attribute
        //
        ti.string_attrib("TEST");
        TEST(++num == impl._OB_numReq());
        String satt = ti.string_attrib();
        TEST(satt.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        //
        // Test in, inout and out simple parameters
        //
        ti.one_string_in("TEST");
        TEST(++num == impl._OB_numReq());

        StringHolder spinout = new StringHolder("TESTINOUT");
        ti.one_string_inout(spinout);
        TEST(spinout.value.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        StringHolder spout = new StringHolder();
        ti.one_string_out(spout);
        TEST(spout.value.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        String sprc = ti.one_string_return();
        TEST(sprc.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        //
        // Test struct attribute
        //
        s ss = new s();
        ss.sval = "TEST";
        ti.struct_attrib(ss);
        TEST(++num == impl._OB_numReq());
        s ssatt = ti.struct_attrib();
        TEST(ssatt.sval.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        //
        // Test in, inout and out struct parameters
        //
        ti.one_struct_in(ss);
        TEST(++num == impl._OB_numReq());

        sHolder sinout = new sHolder(new s("TESTINOUT"));
        ti.one_struct_inout(sinout);
        TEST(sinout.value.sval.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        sHolder sout = new sHolder();
        ti.one_struct_out(sout);
        TEST(sout.value.sval.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        s ssrc = ti.one_struct_return();
        TEST(ssrc.sval.equals("TEST"));
        TEST(++num == impl._OB_numReq());

        manager.clearInterceptors();

        //
        // Test: PortableInterceptor::Current still has the same value
        //
        Any slotData2 = null;
        try {
            slotData2 = pic.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData2.extract_long();
        TEST(v == 10);
    }

    private static void TestDIICalls(ORB orb, ClientProxyManager manager,
            TestInterface ti) {
        org.omg.PortableInterceptor.Current pic = null;
        try {
            org.omg.CORBA.Object obj = orb
                    .resolve_initial_references("PICurrent");
            pic = org.omg.PortableInterceptor.CurrentHelper.narrow(obj);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }
        TEST(pic != null);

        Any slotData = orb.create_any();
        slotData.insert_long(10);

        try {
            pic.set_slot(0, slotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }

        //
        // Set up the correct interceptor
        //
        CallInterceptor_impl impl = new CallInterceptor_impl(orb);
        ClientRequestInterceptor interceptor = impl;
        manager.setInterceptor(0, interceptor);
        int num = 0;

        Request req;
        req = ti._request("noargs");
        req.invoke();
        TEST(++num == impl._OB_numReq());

        req = ti._request("noargs_oneway");
        req.send_oneway();
        TEST(++num == impl._OB_numReq());

        req = ti._request("userexception");
        req.exceptions().add(userHelper.type());
        req.invoke();
        TEST(++num == impl._OB_numReq());

        req = ti._request("systemexception");
        try {
            req.invoke();
        } catch (NO_IMPLEMENT ex) {
            // expected - raised by remote servant
        }
        TEST(++num == impl._OB_numReq());

        req = ti._request("location_forward");
        try {
            req.invoke();
        } catch (NO_IMPLEMENT ex) {
            // expected - raised by local interceptor
        }
        TEST(++num == impl._OB_numReq());

        //
        // Test in, inout and out simple parameters
        //
        {
            req = ti._request("one_string_in");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            req.add_in_arg().insert_string("TEST");
            req.invoke();
            TEST(++num == impl._OB_numReq());

            req = ti._request("one_string_inout");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any inOutAny = req.add_inout_arg();
            String sp = "TESTINOUT";
            inOutAny.insert_string(sp);
            req.invoke();
            String sprc = inOutAny.extract_string();
            TEST(sprc.equals("TEST"));
            TEST(++num == impl._OB_numReq());

            req = ti._request("one_string_out");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any outAny = req.add_out_arg();
            outAny.insert_string("");
            req.invoke();
            sprc = outAny.extract_string();
            TEST(sprc.equals("TEST"));
            TEST(++num == impl._OB_numReq());

            req = ti._request("one_string_return");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_string));
            req.invoke();
            sprc = req.return_value().extract_string();
            TEST(sprc.equals("TEST"));
            TEST(++num == impl._OB_numReq());
        }

        //
        // Test in, inout and out struct parameters
        //
        {
            s ss = new s();
            ss.sval = "TEST";
            req = ti._request("one_struct_in");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            sHelper.insert(req.add_in_arg(), ss);
            req.invoke();
            TEST(++num == impl._OB_numReq());

            ss.sval = "TESTINOUT";
            req = ti._request("one_struct_inout");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any inOutAny = req.add_inout_arg();
            sHelper.insert(inOutAny, ss);
            req.invoke();
            s ssrc = sHelper.extract(inOutAny);
            TEST(ssrc.sval.equals("TEST"));
            TEST(++num == impl._OB_numReq());

            req = ti._request("one_struct_out");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any outAny = req.add_out_arg();
            outAny.type(sHelper.type());
            req.invoke();
            ssrc = sHelper.extract(outAny);
            TEST(ssrc.sval.equals("TEST"));
            TEST(++num == impl._OB_numReq());

            req = ti._request("one_struct_return");
            req.set_return_type(sHelper.type());
            req.invoke();
            ssrc = sHelper.extract(req.return_value());
            TEST(ssrc.sval.equals("TEST"));
            TEST(++num == impl._OB_numReq());
        }

        //
        // Test: PortableInterceptor::Current still has the same value
        //
        Any slotData2 = null;
        try {
            slotData2 = pic.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData2.extract_long();
        TEST(v == 10);

        //
        // Test: ASYNC calls
        //
        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            req = ti._request("noargs");
            req.send_deferred();
            TEST(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            v = slotData2.extract_long();
            TEST(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }
            v = slotData2.extract_long();
            TEST(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            req = ti._request("userexception");
            req.exceptions().add(userHelper.type());
            req.send_deferred();
            TEST(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            v = slotData2.extract_long();
            TEST(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }
            v = slotData2.extract_long();
            TEST(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            req = ti._request("systemexception");
            req.send_deferred();
            TEST(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            v = slotData2.extract_long();
            TEST(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            } catch (NO_IMPLEMENT ex) {
                // expected - raised by remote servant
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }
            v = slotData2.extract_long();
            TEST(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            req = ti._request("location_forward");
            req.send_deferred();
            TEST(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            v = slotData2.extract_long();
            TEST(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                TEST(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            } catch (NO_IMPLEMENT ex) {
                // expected - raised by local interceptor
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }
            v = slotData2.extract_long();
            TEST(v == 11);
        }

        manager.clearInterceptors();
    }

    private static void TestCodec(ORB orb) {
        //
        // Test: Resolve CodecFactory
        //
        org.omg.IOP.CodecFactory factory = null;
        try {
            factory = org.omg.IOP.CodecFactoryHelper.narrow(orb
                    .resolve_initial_references("CodecFactory"));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            TEST(false);
        }
        TEST(factory != null);

        org.omg.IOP.Encoding how = new org.omg.IOP.Encoding();
        how.major_version = 0;
        how.minor_version = 0;

        //
        // Test: Create non-existent codec
        //
        try {
            how.format = 1; // Some unknown value
            org.omg.IOP.Codec codec = factory.create_codec(how);
            TEST(false);
        } catch (org.omg.IOP.CodecFactoryPackage.UnknownEncoding ex) {
            // Expected
        }

        //
        // Test: CDR Codec
        //
        how.format = org.omg.IOP.ENCODING_CDR_ENCAPS.value;
        org.omg.IOP.Codec cdrCodec = null;
        try {
            cdrCodec = factory.create_codec(how);
        } catch (org.omg.IOP.CodecFactoryPackage.UnknownEncoding ex) {
            TEST(false);
        }
        org.apache.yoko.orb.OB.Assert._OB_assert(cdrCodec != null);

        //
        // Test: Encode/decode
        //
        foo f = new foo();
        f.l = 10;
        Any any = orb.create_any();
        fooHelper.insert(any, f);

        byte[] encoding = null;
        try {
            encoding = cdrCodec.encode(any);
        } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
            TEST(false);
        }
        Any result = null;
        try {
            result = cdrCodec.decode(encoding);
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        }

        foo newf = fooHelper.extract(result);
        TEST(newf.l == 10);

        //
        // Test: Encode/decode
        //
        try {
            encoding = cdrCodec.encode_value(any);
        } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
            TEST(false);
        }
        try {
            result = cdrCodec.decode_value(encoding, fooHelper.type());
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
            TEST(false);
        }

        newf = fooHelper.extract(result);
        TEST(newf.l == 10);
    }

    static void ClientRegisterInterceptors(java.util.Properties props,
            boolean local) {
        props.put("org.omg.PortableInterceptor.ORBInitializerClass."
                + "test.pi.ClientORBInitializer_impl", "");
        ClientORBInitializer_impl._OB_setLocal(local);
    }

    static int ClientRun(ORB orb, boolean nonBlocking, String[] args)
            throws org.omg.CORBA.UserException {
        String impl;
        String dsiImpl;

        //
        // Get TestInterface
        //
        try {
            String refFile = "TestInterface.ref";
            FileInputStream file = new FileInputStream(refFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(file));
            impl = in.readLine();
            dsiImpl = in.readLine();
            file.close();
        } catch (IOException ex) {
            System.err.println("Can't read from `" + ex.getMessage() + "'");
            return 1;
        }

        System.out.print("Testing initial reference registration... ");
        System.out.flush();
        // TODO
        System.out.println("Done!");

        System.out.print("Testing string_to_object()... ");
        System.out.flush();
        org.omg.CORBA.Object obj = orb.string_to_object(impl);
        org.omg.CORBA.Object dsiObj = orb.string_to_object(dsiImpl);
        TEST(obj != null);
        TEST(dsiObj != null);
        System.out.println("Done!");

        //
        // Test: Create a policy set on the object-reference
        //
        Any any = orb.create_any();
        any.insert_long(10);
        Policy[] pl = new Policy[1];
        pl[0] = orb.create_policy(MY_CLIENT_POLICY_ID.value, any);

        System.out.print("Testing _narrow()... ");
        System.out.flush();
        obj = obj._set_policy_override(pl,
                org.omg.CORBA.SetOverrideType.ADD_OVERRIDE);
        TestInterface ti = TestInterfaceHelper.narrow(obj);
        dsiObj = dsiObj._set_policy_override(pl,
                org.omg.CORBA.SetOverrideType.ADD_OVERRIDE);
        TestInterface tiDSI = TestInterfaceHelper.narrow(dsiObj);
        TEST(ti != null);
        TEST(tiDSI != null);
        System.out.println("Done!");

        //
        // Test: Codec
        //
        System.out.print("Testing Codec... ");
        System.out.flush();
        TestCodec(orb);
        System.out.println("Done!");

        //
        // Test: Exception translation
        //
        System.out.print("Testing client side exception translation... ");
        System.out.flush();
        TestTranslation(orb, ClientORBInitializer_impl.clientProxyManager, ti);
        System.out.println("Done!");

        //
        // Run tests
        //
        System.out.print("Testing standard method calls with static stubs... ");
        System.out.flush();
        TestCalls(orb, ClientORBInitializer_impl.clientProxyManager, ti);
        System.out.println("Done!");

        System.out.print("Ditto, but with the DSI implementation... ");
        System.out.flush();
        TestCalls(orb, ClientORBInitializer_impl.clientProxyManager, tiDSI);
        System.out.println("Done!");

        System.out.print("Testing standard method calls with the DII... ");
        System.out.flush();
        TestDIICalls(orb, ClientORBInitializer_impl.clientProxyManager, ti);
        System.out.println("Done!");

        System.out.print("Ditto, but with the DSI implementation... ");
        System.out.flush();
        TestDIICalls(orb, ClientORBInitializer_impl.clientProxyManager, tiDSI);
        System.out.println("Done!");

        ti.deactivate();

        return 0;
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            ClientRegisterInterceptors(props, false);

            orb = ORB.init(args, props);
            status = ClientRun(orb, false, args);
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
