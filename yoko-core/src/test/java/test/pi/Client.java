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

import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.StringHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.WrongTransaction;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryHelper;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.CurrentHelper;
import org.omg.PortableInterceptor.InvalidSlot;
import test.pi.TestInterfacePackage.s;
import test.pi.TestInterfacePackage.sHelper;
import test.pi.TestInterfacePackage.sHolder;
import test.pi.TestInterfacePackage.user;
import test.pi.TestInterfacePackage.userHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.omg.CORBA.SetOverrideType.ADD_OVERRIDE;

public final class Client extends test.common.TestBase {
    private static void TestTranslation(ORB orb, ClientProxyManager manager, TestInterface ti) {
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
            assertTrue(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i0.noThrowOnRequest();
        i0.throwOnReply(new NO_PERMISSION());
        try {
            ti.noargs();
            assertTrue(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i0.noThrowOnReply();

        i1.throwOnReply(new NO_PERMISSION());
        i0.expectException(new NO_PERMISSION());
        try {
            ti.noargs();
            assertTrue(false);
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
            assertTrue(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        i2.noThrowOnRequest();
        i2.throwOnReply(new BAD_INV_ORDER());

        try {
            ti.noargs();
            assertTrue(false);
        } catch (NO_PERMISSION ex) {
            // Expected
        }

        manager.clearInterceptors();
    }

    private static void TestCalls(ORB orb, ClientProxyManager manager, TestInterface ti) {
        Current pic = null;
        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("PICurrent");
            pic = CurrentHelper.narrow(obj);
        } catch (InvalidName ex) {
        }
        assertTrue(pic != null);

        Any slotData = orb.create_any();
        slotData.insert_long(10);

        try {
            pic.set_slot(0, slotData);
        } catch (InvalidSlot ex) {
            assertTrue(false);
        }

        //
        // Set up the correct interceptor
        //
        CallInterceptor_impl impl = new CallInterceptor_impl(orb);
        ClientRequestInterceptor interceptor = impl;
        manager.setInterceptor(0, interceptor);
        int num = 0;

        ti.noargs();
        assertTrue(++num == impl._OB_numReq());

        ti.noargs_oneway();
        assertTrue(++num == impl._OB_numReq());

        try {
            ti.userexception();
            assertTrue(false);
        } catch (user ex) {
        }
        assertTrue(++num == impl._OB_numReq());

        try {
            ti.systemexception();
            assertTrue(false);
        } catch (SystemException ex) {
        }
        assertTrue(++num == impl._OB_numReq());

        ti.test_service_context();
        assertTrue(++num == impl._OB_numReq());

        try {
            ti.location_forward();
        } catch (NO_IMPLEMENT ex) {
        }
        assertTrue(++num == impl._OB_numReq());

        //
        // Test simple attribute
        //
        ti.string_attrib("TEST");
        assertTrue(++num == impl._OB_numReq());
        String satt = ti.string_attrib();
        assertTrue(satt.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        //
        // Test in, inout and out simple parameters
        //
        ti.one_string_in("TEST");
        assertTrue(++num == impl._OB_numReq());

        StringHolder spinout = new StringHolder("TESTINOUT");
        ti.one_string_inout(spinout);
        assertTrue(spinout.value.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        StringHolder spout = new StringHolder();
        ti.one_string_out(spout);
        assertTrue(spout.value.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        String sprc = ti.one_string_return();
        assertTrue(sprc.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        //
        // Test struct attribute
        //
        s ss = new s();
        ss.sval = "TEST";
        ti.struct_attrib(ss);
        assertTrue(++num == impl._OB_numReq());
        s ssatt = ti.struct_attrib();
        assertTrue(ssatt.sval.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        //
        // Test in, inout and out struct parameters
        //
        ti.one_struct_in(ss);
        assertTrue(++num == impl._OB_numReq());

        sHolder sinout = new sHolder(new s("TESTINOUT"));
        ti.one_struct_inout(sinout);
        assertTrue(sinout.value.sval.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        sHolder sout = new sHolder();
        ti.one_struct_out(sout);
        assertTrue(sout.value.sval.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        s ssrc = ti.one_struct_return();
        assertTrue(ssrc.sval.equals("TEST"));
        assertTrue(++num == impl._OB_numReq());

        manager.clearInterceptors();

        //
        // Test: PortableInterceptor::Current still has the same value
        //
        Any slotData2 = null;
        try {
            slotData2 = pic.get_slot(0);
        } catch (InvalidSlot ex) {
            assertTrue(false);
        }
        int v = slotData2.extract_long();
        assertTrue(v == 10);
    }

    private static void TestDIICalls(ORB orb, ClientProxyManager manager, TestInterface ti) {
        Current pic = null;
        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("PICurrent");
            pic = CurrentHelper.narrow(obj);
        } catch (InvalidName ex) {
        }
        assertTrue(pic != null);

        Any slotData = orb.create_any();
        slotData.insert_long(10);

        try {
            pic.set_slot(0, slotData);
        } catch (InvalidSlot ex) {
            assertTrue(false);
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
        assertTrue(++num == impl._OB_numReq());

        req = ti._request("noargs_oneway");
        req.send_oneway();
        assertTrue(++num == impl._OB_numReq());

        req = ti._request("userexception");
        req.exceptions().add(userHelper.type());
        req.invoke();
        assertTrue(++num == impl._OB_numReq());

        req = ti._request("systemexception");
        try {
            req.invoke();
        } catch (NO_IMPLEMENT ex) {
            // expected - raised by remote servant
        }
        assertTrue(++num == impl._OB_numReq());

        req = ti._request("location_forward");
        try {
            req.invoke();
        } catch (NO_IMPLEMENT ex) {
            // expected - raised by local interceptor
        }
        assertTrue(++num == impl._OB_numReq());

        //
        // Test in, inout and out simple parameters
        //
        {
            req = ti._request("one_string_in");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            req.add_in_arg().insert_string("TEST");
            req.invoke();
            assertTrue(++num == impl._OB_numReq());

            req = ti._request("one_string_inout");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any inOutAny = req.add_inout_arg();
            String sp = "TESTINOUT";
            inOutAny.insert_string(sp);
            req.invoke();
            String sprc = inOutAny.extract_string();
            assertTrue(sprc.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());

            req = ti._request("one_string_out");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any outAny = req.add_out_arg();
            outAny.insert_string("");
            req.invoke();
            sprc = outAny.extract_string();
            assertTrue(sprc.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());

            req = ti._request("one_string_return");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_string));
            req.invoke();
            sprc = req.return_value().extract_string();
            assertTrue(sprc.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());
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
            assertTrue(++num == impl._OB_numReq());

            ss.sval = "TESTINOUT";
            req = ti._request("one_struct_inout");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any inOutAny = req.add_inout_arg();
            sHelper.insert(inOutAny, ss);
            req.invoke();
            s ssrc = sHelper.extract(inOutAny);
            assertTrue(ssrc.sval.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());

            req = ti._request("one_struct_out");
            req.set_return_type(orb.get_primitive_tc(TCKind.tk_void));
            Any outAny = req.add_out_arg();
            outAny.type(sHelper.type());
            req.invoke();
            ssrc = sHelper.extract(outAny);
            assertTrue(ssrc.sval.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());

            req = ti._request("one_struct_return");
            req.set_return_type(sHelper.type());
            req.invoke();
            ssrc = sHelper.extract(req.return_value());
            assertTrue(ssrc.sval.equals("TEST"));
            assertTrue(++num == impl._OB_numReq());
        }

        //
        // Test: PortableInterceptor::Current still has the same value
        //
        Any slotData2 = null;
        try {
            slotData2 = pic.get_slot(0);
        } catch (InvalidSlot ex) {
            assertTrue(false);
        }
        int v = slotData2.extract_long();
        assertTrue(v == 10);

        //
        // Test: ASYNC calls
        //
        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            req = ti._request("noargs");
            req.send_deferred();
            assertTrue(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            v = slotData2.extract_long();
            assertTrue(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }
            v = slotData2.extract_long();
            assertTrue(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            req = ti._request("userexception");
            req.exceptions().add(userHelper.type());
            req.send_deferred();
            assertTrue(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            v = slotData2.extract_long();
            assertTrue(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }
            v = slotData2.extract_long();
            assertTrue(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            req = ti._request("systemexception");
            req.send_deferred();
            assertTrue(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            v = slotData2.extract_long();
            assertTrue(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            } catch (NO_IMPLEMENT ex) {
                // expected - raised by remote servant
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }
            v = slotData2.extract_long();
            assertTrue(v == 11);
        }

        {
            slotData.insert_long(10);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            req = ti._request("location_forward");
            req.send_deferred();
            assertTrue(++num == impl._OB_numReq());

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            v = slotData2.extract_long();
            assertTrue(v == 10);

            slotData.insert_long(11);
            try {
                pic.set_slot(0, slotData);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }

            try {
                req.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            } catch (NO_IMPLEMENT ex) {
                // expected - raised by local interceptor
            }

            try {
                slotData2 = pic.get_slot(0);
            } catch (InvalidSlot ex) {
                assertTrue(false);
            }
            v = slotData2.extract_long();
            assertTrue(v == 11);
        }

        manager.clearInterceptors();
    }

    private static void TestCodec(ORB orb) {
        //
        // Test: Resolve CodecFactory
        //
        CodecFactory factory = null;
        try {
            factory = CodecFactoryHelper.narrow(orb.resolve_initial_references("CodecFactory"));
        } catch (InvalidName ex) {
            assertTrue(false);
        }
        assertTrue(factory != null);

        Encoding how = new Encoding();
        how.major_version = 0;
        how.minor_version = 0;

        //
        // Test: Create non-existent codec
        //
        try {
            how.format = 1; // Some unknown value
            factory.create_codec(how);
            assertTrue(false);
        } catch (UnknownEncoding ex) {
            // Expected
        }

        //
        // Test: CDR Codec
        //
        how.format = ENCODING_CDR_ENCAPS.value;
        Codec cdrCodec = null;
        try {
            cdrCodec = factory.create_codec(how);
        } catch (UnknownEncoding ex) {
            assertTrue(false);
        }
        Assert.ensure(cdrCodec != null);

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
        } catch (InvalidTypeForEncoding ex) {
            assertTrue(false);
        }
        Any result = null;
        try {
            result = cdrCodec.decode(encoding);
        } catch (FormatMismatch ex) {
            assertTrue(false);
        }

        foo newf = fooHelper.extract(result);
        assertTrue(newf.l == 10);

        //
        // Test: Encode/decode
        //
        try {
            encoding = cdrCodec.encode_value(any);
        } catch (InvalidTypeForEncoding ex) {
            assertTrue(false);
        }
        try {
            result = cdrCodec.decode_value(encoding, fooHelper.type());
        } catch (FormatMismatch ex) {
            assertTrue(false);
        } catch (TypeMismatch ex) {
            assertTrue(false);
        }

        newf = fooHelper.extract(result);
        assertTrue(newf.l == 10);
    }

    static void ClientRegisterInterceptors(Properties props, boolean local) {
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + ClientORBInitializer_impl.class.getName(), "");
        ClientORBInitializer_impl._OB_setLocal(local);
    }

    static void ClientRun(ORB orb, boolean nonBlocking, String[] args) throws Exception {
        String impl;
        String dsiImpl;

        //
        // Get TestInterface
        //
        try (BufferedReader in = new BufferedReader(new FileReader("TestInterface.ref"))) {
            impl = readRef(in);
            dsiImpl = readRef(in);
        }

        System.out.print("Testing string_to_object()... ");
        System.out.flush();
        org.omg.CORBA.Object obj = orb.string_to_object(impl);
        org.omg.CORBA.Object dsiObj = orb.string_to_object(dsiImpl);
        assertTrue(obj != null);
        assertTrue(dsiObj != null);
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
        obj = obj._set_policy_override(pl, ADD_OVERRIDE);
        TestInterface ti = TestInterfaceHelper.narrow(obj);
        assertTrue(ti != null);
        try {
            if ("".isEmpty()) return;
            dsiObj = dsiObj._set_policy_override(pl, ADD_OVERRIDE);
            TestInterface tiDSI = TestInterfaceHelper.narrow(dsiObj);
            assertTrue(tiDSI != null);
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
        } finally {
            System.out.println("About to call deactivate");
            ti.deactivate();
            System.out.println("Deactivate returned normally");
        }
    }

    private static String readRef(BufferedReader in) throws Exception {
        String line = in.readLine();
        if (line == null) {
            throw new RuntimeException("Unknown Server error");
        } else if (!!!line.equals("ref:")) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                pw.println("Server error:");
                do {
                    pw.print('\t');
                    pw.println(line);
                } while ((line = in.readLine()) != null);
                pw.flush();
                throw new RuntimeException(sw.toString());
            }
        }
        return in.readLine();
    }

    public static void main(String[] args) throws Exception {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            ClientRegisterInterceptors(props, false);

            orb = ORB.init(args, props);
            ClientRun(orb, false, args);
        } finally {
            if (orb != null) {
                orb.destroy();
            }
        }
    }
}
