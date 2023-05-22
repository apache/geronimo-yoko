/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package test.pi;

import org.apache.yoko.util.Assert;
import org.omg.CORBA.*;
import org.omg.PortableInterceptor.*;
import test.pi.TestInterfacePackage.*;

final class CallInterceptor_impl extends org.omg.CORBA.LocalObject implements
        ClientRequestInterceptor {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private int req_;

    private org.omg.IOP.Codec cdrCodec_;

    private org.omg.PortableInterceptor.Current pic_;

    void testArgs(ClientRequestInfo ri, boolean resultAvail) {
        String op = ri.operation();
        org.omg.Dynamic.Parameter[] args = ri.arguments();
        if (op.startsWith("_set_") || op.startsWith("_get_")) {
            boolean isstr; // struct or string?
            isstr = (op.indexOf("string") != -1);
            if (op.startsWith("_get_")) {
                TEST(args.length == 0);
                if (resultAvail) {
                    //
                    // Test: result
                    //
                    Any result = ri.result();
                    if (isstr) {
                        String str = result.extract_string();
                        TEST(str.startsWith("TEST"));
                    } else {
                        s sp = sHelper.extract(result);
                        TEST(sp.sval.startsWith("TEST"));
                    }
                }
            } else {
                TEST(args.length == 1);
                TEST(args[0].mode == org.omg.CORBA.ParameterMode.PARAM_IN);
                if (resultAvail) {
                    if (isstr) {
                        String str = args[0].argument.extract_string();
                        TEST(str.startsWith("TEST"));
                    } else {
                        s sp = sHelper.extract(args[0].argument);
                        TEST(sp.sval.startsWith("TEST"));
                    }
                }
            }
        } else if (op.startsWith("one_")) {
            String which = op.substring(4); // Which operation?
            boolean isstr; // struct or string?
            ParameterMode mode; // The parameter mode

            if (which.startsWith("string"))
                isstr = true;
            else
                // if(which.startsWith("struct"))
                isstr = false;

            which = which.substring(7); // Skip <string|struct>_

            if (which.equals("return")) {
                TEST(args.length == 0);
                if (resultAvail) {
                    //
                    // Test: result
                    //
                    Any result = ri.result();
                    if (isstr) {
                        String str = result.extract_string();
                        TEST(str.startsWith("TEST"));
                    } else {
                        s sp = sHelper.extract(result);
                        TEST(sp.sval.startsWith("TEST"));
                    }
                }
            } else {
                TEST(args.length == 1);
                if (which.equals("in"))
                    mode = org.omg.CORBA.ParameterMode.PARAM_IN;
                else if (which.equals("inout"))
                    mode = org.omg.CORBA.ParameterMode.PARAM_INOUT;
                else
                    // if(which.equals("out"))
                    mode = org.omg.CORBA.ParameterMode.PARAM_OUT;

                TEST(mode == args[0].mode);

                if (mode != org.omg.CORBA.ParameterMode.PARAM_OUT
                        || resultAvail) {
                    if (isstr) {
                        String str = args[0].argument.extract_string();
                        TEST(str.startsWith("TEST"));
                    } else {
                        s sp = sHelper.extract(args[0].argument);
                        TEST(sp.sval.startsWith("TEST"));
                    }

                    if (resultAvail) {
                        //
                        // Test: result
                        //
                        Any result = ri.result();
                        TypeCode tc = result.type();
                        TEST(tc.kind() == TCKind.tk_void);
                    }
                }
            }
        } else {
            TEST(args.length == 0);
        }
        if (!resultAvail) {
            //
            // Test: result is not available
            //
            try {
                Any result = ri.result();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }
        }
    }

    CallInterceptor_impl(ORB orb) {
        org.omg.IOP.CodecFactory factory = null;
        try {
            factory = org.omg.IOP.CodecFactoryHelper.narrow(orb
                    .resolve_initial_references("CodecFactory"));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            TEST(false);
        }
        TEST(factory != null);

        try {
            pic_ = org.omg.PortableInterceptor.CurrentHelper.narrow(orb
                    .resolve_initial_references("PICurrent"));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            TEST(false);
        }
        TEST(pic_ != null);

        org.omg.IOP.Encoding how = new org.omg.IOP.Encoding(
                (byte) org.omg.IOP.ENCODING_CDR_ENCAPS.value, (byte) 0,
                (byte) 0);

        try {
            cdrCodec_ = factory.create_codec(how);
        } catch (org.omg.IOP.CodecFactoryPackage.UnknownEncoding ex) {
            TEST(false);
        }
        Assert.ensure(cdrCodec_ != null);
    }

    public String name() {
        return "CRI";
    }

    public void destroy() {
    }

    public void send_request(ClientRequestInfo ri) {
        req_++;

        //
        // Test: request id
        //
        int id = ri.request_id();

        //
        // This statement is required to avoid "unused variable" warning
        // generated by the compiler.
        //
        TEST(id == id);

        //
        // Test: get operation name
        //
        String op = ri.operation();

        boolean oneway = op.equals("noargs_oneway");

        //
        // Test: Examine arguments
        //
        testArgs(ri, false);

        // TODO: test context

        //
        // Test: result is not available
        //
        try {
            Any result = ri.result();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: exceptions
        //
        TypeCode[] exceptions = ri.exceptions();
        if (op.equals("userexception")) {
            TEST(exceptions.length == 1);
            TEST(exceptions[0].equal(userHelper.type()));
        } else {
            TEST(exceptions.length == 0);
        }

        //
        // Test: oneway and response expected are equivalent
        TEST((oneway && !ri.response_expected())
                || (!oneway && ri.response_expected()));

        // TODO: test sync scope

        //
        // Test: target is available
        //
        org.omg.CORBA.Object target = ri.target();
        TEST(target != null);

        //
        // Test: effective_target is available
        //
        org.omg.CORBA.Object effectiveTarget = ri.effective_target();
        TEST(effectiveTarget != null);

        //
        // Test: effective_profile
        org.omg.IOP.TaggedProfile effectiveProfile = ri.effective_profile();
        TEST(effectiveProfile.tag == org.omg.IOP.TAG_INTERNET_IOP.value);

        //
        // Test: reply status is not available
        //
        try {
            ri.reply_status();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: received_exception is not available
        //
        try {
            Any rc = ri.received_exception();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: forward reference is not available
        //
        try {
            org.omg.CORBA.Object fwd = ri.forward_reference();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: test get_effective_component
        //
        org.omg.IOP.TaggedComponent componentEncoding = ri
                .get_effective_component(MY_COMPONENT_ID.value);
        byte[] componentData = componentEncoding.component_data;
        Any componentAny = null;
        try {
            componentAny = cdrCodec_.decode_value(componentData,
                    MyComponentHelper.type());
        } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
            TEST(false);
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        }
        MyComponent component = MyComponentHelper.extract(componentAny);
        TEST(component.val == 10);

        // TODO: test get_effective_components

        //
        // Test: get_request_policy
        //
        Policy policy = ri.get_request_policy(MY_CLIENT_POLICY_ID.value);
        MyClientPolicy myClientPolicy = MyClientPolicyHelper.narrow(policy);
        TEST(myClientPolicy != null);
        TEST(myClientPolicy.value() == 10);

        //
        // Test: get_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_request_service_context(REQUEST_CONTEXT_ID.value);
            TEST(false);
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: get_reply_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_reply_service_context(REQUEST_CONTEXT_ID.value);
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: add_request_service_context
        //
        if (op.equals("test_service_context")) {
            RequestContext context = new RequestContext();
            context.data = "request";

            //
            // Test: PortableInteceptor::Current
            //
            Any slotData = null;
            try {
                slotData = ri.get_slot(0);
            } catch (InvalidSlot ex) {
                TEST(false);
            }
            context.val = slotData.extract_long();
            TEST(context.val == 10);

            Any any = ORB.init().create_any();
            RequestContextHelper.insert(any, context);
            byte[] data = null;
            try {
                data = cdrCodec_.encode_value(any);
            } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                TEST(false);
            }

            org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
            sc.context_id = REQUEST_CONTEXT_ID.value;
            sc.context_data = new byte[data.length];
            System.arraycopy(data, 0, sc.context_data, 0, data.length);

            ri.add_request_service_context(sc, false);

            //
            // Test: ensure that the data is present
            //
            try {
                org.omg.IOP.ServiceContext sc2 = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }
        } else {
            //
            // Test: get_request_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
                TEST(false);
            } catch (BAD_PARAM ex) {
                // Expected
            }
        }

        //
        // Test: examine PICurrent
        //
        Any slotData = null;
        try {
            slotData = pic_.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        TEST(slotData.type().kind().value() == TCKind._tk_null);

        Any newSlotData = ORB.init().create_any();
        newSlotData.insert_long(15);
        try {
            pic_.set_slot(0, newSlotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
    }

    public void send_poll(ClientRequestInfo ri) {
        TEST(false);
    }

    public void receive_reply(ClientRequestInfo ri) {
        //
        // Test: request id
        //
        int id = ri.request_id();

        //
        // This statement is required to avoid "unused variable" warning
        // generated by the compiler.
        //
        TEST(id == id);

        //
        // Test: get operation name
        //
        String op = ri.operation();

        //
        // If "deactivate" then we're done
        //
        if (op.equals("deactivate"))
            return;

        boolean oneway = op.equals("noargs_oneway");

        //
        // Test: Examine arguments
        //
        testArgs(ri, true);

        // TODO: test context

        //
        // Test: exceptions
        //
        TypeCode[] exceptions = ri.exceptions();
        if (op.equals("userexception")) {
            TEST(exceptions.length == 1);
            TEST(exceptions[0].equal(userHelper.type()));
        } else {
            TEST(exceptions.length == 0);
        }

        //
        // Test: oneway and response expected are equivalent
        //
        TEST((oneway && !ri.response_expected())
                || (!oneway && ri.response_expected()));

        // TODO: test sync scope

        //
        // Test: target is available
        //
        org.omg.CORBA.Object target = ri.target();
        TEST(target != null);

        //
        // Test: effective_target is available
        //
        org.omg.CORBA.Object effectiveTarget = ri.effective_target();
        TEST(effectiveTarget != null);

        //
        // Test: effective_profile
        //
        org.omg.IOP.TaggedProfile effectiveProfile = ri.effective_profile();
        TEST(effectiveProfile.tag == org.omg.IOP.TAG_INTERNET_IOP.value);

        //
        // Test: test get_effective_component
        //
        org.omg.IOP.TaggedComponent componentEncoding = ri
                .get_effective_component(MY_COMPONENT_ID.value);
        byte[] componentData = componentEncoding.component_data;
        Any componentAny = null;
        try {
            componentAny = cdrCodec_.decode_value(componentData,
                    MyComponentHelper.type());
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
            TEST(false);
        }

        MyComponent component = MyComponentHelper.extract(componentAny);
        TEST(component.val == 10);

        // TODO: test get_effective_components

        //
        // Test: get_request_policy
        //
        Policy policy = ri.get_request_policy(MY_CLIENT_POLICY_ID.value);
        MyClientPolicy myClientPolicy = MyClientPolicyHelper.narrow(policy);
        TEST(myClientPolicy != null);
        TEST(myClientPolicy.value() == 10);

        //
        // Test: reply status is SUCCESS
        //
        TEST(ri.reply_status() == SUCCESSFUL.value);

        //
        // Test: received_exception is not available
        //
        try {
            Any rc = ri.received_exception();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: forward reference is not available
        //
        try {
            org.omg.CORBA.Object fwd = ri.forward_reference();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        if (op.equals("test_service_context")) {
            //
            // Test: get_request_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }

            //
            // Test: get_reply_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
                TEST(sc.context_id == REPLY_CONTEXT_1_ID.value);
                byte[] data = new byte[sc.context_data.length];
                System.arraycopy(sc.context_data, 0, data, 0,
                        sc.context_data.length);
                Any any = null;
                try {
                    any = cdrCodec_.decode_value(data, ReplyContextHelper
                            .type());
                } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                    TEST(false);
                } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                    TEST(false);
                }
                ReplyContext context = ReplyContextHelper.extract(any);
                TEST(context.data.equals("reply1"));
                TEST(context.val == 101);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }

            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_2_ID.value);
                TEST(sc.context_id == REPLY_CONTEXT_2_ID.value);
                byte[] data = new byte[sc.context_data.length];
                System.arraycopy(sc.context_data, 0, data, 0,
                        sc.context_data.length);
                Any any = null;
                try {
                    any = cdrCodec_.decode_value(data, ReplyContextHelper
                            .type());
                } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                    TEST(false);
                } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                    TEST(false);
                }
                ReplyContext context = ReplyContextHelper.extract(any);
                TEST(context.data.equals("reply2"));
                TEST(context.val == 102);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }

            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_3_ID.value);
                TEST(sc.context_id == REPLY_CONTEXT_3_ID.value);
                byte[] data = new byte[sc.context_data.length];
                System.arraycopy(sc.context_data, 0, data, 0,
                        sc.context_data.length);
                Any any = null;
                try {
                    any = cdrCodec_.decode_value(data, ReplyContextHelper
                            .type());
                } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                    TEST(false);
                } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                    TEST(false);
                }
                ReplyContext context = ReplyContextHelper.extract(any);
                TEST(context.data.equals("reply3"));
                TEST(context.val == 103);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }

            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_4_ID.value);
                TEST(sc.context_id == REPLY_CONTEXT_4_ID.value);
                byte[] data = new byte[sc.context_data.length];
                System.arraycopy(sc.context_data, 0, data, 0,
                        sc.context_data.length);
                Any any = null;
                try {
                    any = cdrCodec_.decode_value(data, ReplyContextHelper
                            .type());
                } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                    TEST(false);
                } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                    TEST(false);
                }
                ReplyContext context = ReplyContextHelper.extract(any);
                TEST(context.data.equals("reply4"));
                TEST(context.val == 124);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }
        } else {
            //
            // Test: get_reply_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
                TEST(false);
            } catch (BAD_PARAM ex) {
                // Expected
            }

            //
            // Test: get_request_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
                TEST(false);
            } catch (BAD_PARAM ex) {
                // Expected
            }
        }

        //
        // Test: add_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
            sc.context_id = REQUEST_CONTEXT_ID.value;
            ri.add_request_service_context(sc, false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: examine PICurrent
        //
        Any slotData = null;
        try {
            slotData = pic_.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData.extract_long();
        TEST(v == 15);

        Any newSlotData = ORB.init().create_any();
        newSlotData.insert_long(16);
        try {
            pic_.set_slot(0, newSlotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
    }

    public void receive_other(ClientRequestInfo ri) {
        //
        // Test: request id
        //
        int id = ri.request_id();

        //
        // This statement is required to avoid "unused variable" warning
        // generated by the compiler.
        //
        TEST(id == id);

        //
        // Test: get operation name. Verify operation name is valid.
        //
        String op = ri.operation();
        TEST(op.equals("location_forward"));

        //
        // Test: Examine arguments
        //
        try {
            org.omg.Dynamic.Parameter[] args = ri.arguments();
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        }

        // TODO: test context

        //
        // Test: exceptions
        //
        TypeCode[] exceptions = ri.exceptions();
        if (op.equals("userexception")) {
            TEST(exceptions.length == 1);
            TEST(exceptions[0].equal(userHelper.type()));
        } else {
            TEST(exceptions.length == 0);
        }

        //
        // Test: response expected is true
        //
        TEST(ri.response_expected());

        // TODO: test sync scope

        //
        // Test: target is available
        //
        org.omg.CORBA.Object target = ri.target();
        TEST(target != null);

        //
        // Test: effective_target is available
        //
        org.omg.CORBA.Object effectiveTarget = ri.effective_target();
        TEST(effectiveTarget != null);

        //
        // Test: effective_profile
        //
        org.omg.IOP.TaggedProfile effectiveProfile = ri.effective_profile();
        TEST(effectiveProfile.tag == org.omg.IOP.TAG_INTERNET_IOP.value);

        //
        // Test: test get_effective_component
        //
        org.omg.IOP.TaggedComponent componentEncoding = ri
                .get_effective_component(MY_COMPONENT_ID.value);
        byte[] componentData = componentEncoding.component_data;
        Any componentAny = null;
        try {
            componentAny = cdrCodec_.decode_value(componentData,
                    MyComponentHelper.type());
        } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
            TEST(false);
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        }

        MyComponent component = MyComponentHelper.extract(componentAny);
        TEST(component.val == 10);

        // TODO: test get_effective_components

        //
        // Test: get_request_policy
        //
        Policy policy = ri.get_request_policy(MY_CLIENT_POLICY_ID.value);
        MyClientPolicy myClientPolicy = MyClientPolicyHelper.narrow(policy);
        TEST(myClientPolicy != null);
        TEST(myClientPolicy.value() == 10);

        //
        // Test: reply status is LOCATION_FORWARD
        //
        TEST(ri.reply_status() == LOCATION_FORWARD.value);

        //
        // Test: received_exception is not available
        //
        try {
            Any rc = ri.received_exception();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: forward reference is available
        //
        try {
            org.omg.CORBA.Object fwd = ri.forward_reference();
        } catch (BAD_INV_ORDER ex) {
            TEST(false);
        }

        //
        // Test: get_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_request_service_context(REQUEST_CONTEXT_ID.value);
            TEST(false);
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: get_reply_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
            TEST(false);
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: add_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
            sc.context_id = REQUEST_CONTEXT_ID.value;
            ri.add_request_service_context(sc, false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: examine PICurrent
        //
        Any slotData = null;
        try {
            slotData = pic_.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData.extract_long();
        TEST(v == 15);

        Any newSlotData = ORB.init().create_any();
        newSlotData.insert_long(16);
        try {
            pic_.set_slot(0, newSlotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }

        //
        // Eat the location forward
        //
        throw new NO_IMPLEMENT();
    }

    public void receive_exception(ClientRequestInfo ri) {
        //
        // Test: request id
        //
        int id = ri.request_id();

        //
        // This statement is required to avoid "unused variable" warning
        // generated by the compiler.
        //
        TEST(id == id);

        //
        // Test: get operation name. Verify that operation name is
        // valid
        String op = ri.operation();
        TEST(op.equals("systemexception") || op.equals("userexception")
                || op.equals("deactivate"));

        //
        // If "deactivate" then we're done
        //
        if (op.equals("deactivate"))
            return;

        boolean user = op.equals("userexception");

        //
        // Test: Examine arguments
        //
        try {
            org.omg.Dynamic.Parameter[] args = ri.arguments();
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        }

        // TODO: test context

        //
        // Test: exceptions
        //
        TypeCode[] exceptions = ri.exceptions();
        if (op.equals("userexception")) {
            TEST(exceptions.length == 1);
            TEST(exceptions[0].equal(userHelper.type()));
        } else {
            TEST(exceptions.length == 0);
        }

        //
        // Test: response expected is true
        //
        TEST(ri.response_expected());

        // TODO: test sync scope

        //
        // Test: target is available
        //
        org.omg.CORBA.Object target = ri.target();
        TEST(target != null);

        //
        // Test: effective_target is available
        //
        org.omg.CORBA.Object effectiveTarget = ri.effective_target();
        TEST(effectiveTarget != null);

        //
        // Test: effective_profile
        //
        org.omg.IOP.TaggedProfile effectiveProfile = ri.effective_profile();
        TEST(effectiveProfile.tag == org.omg.IOP.TAG_INTERNET_IOP.value);

        //
        // Test: test get_effective_component
        org.omg.IOP.TaggedComponent componentEncoding = ri
                .get_effective_component(MY_COMPONENT_ID.value);
        byte[] componentData = componentEncoding.component_data;
        Any componentAny = null;
        try {
            componentAny = cdrCodec_.decode_value(componentData,
                    MyComponentHelper.type());
        } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
            TEST(false);
        } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
            TEST(false);
        }
        MyComponent component = MyComponentHelper.extract(componentAny);
        TEST(component.val == 10);

        // TODO: test get_effective_components

        //
        // Test: get_request_policy
        //
        Policy policy = ri.get_request_policy(MY_CLIENT_POLICY_ID.value);
        MyClientPolicy myClientPolicy = MyClientPolicyHelper.narrow(policy);
        TEST(myClientPolicy != null);
        TEST(myClientPolicy.value() == 10);

        //
        // Test: reply status is correct
        //
        if (user)
            TEST(ri.reply_status() == USER_EXCEPTION.value);
        else
            TEST(ri.reply_status() == SYSTEM_EXCEPTION.value);

        //
        // Test: received_exception is available and correct
        //
        try {
            Any rc = ri.received_exception();
            if (!user) {
                SystemException ex = org.apache.yoko.orb.OB.Util
                        .unmarshalSystemException(rc.create_input_stream());
            } else {
                String exId = ri.received_exception_id();
                TEST(exId.equals(userHelper.id()));
                user ex = userHelper.extract(rc);
            }
        } catch (BAD_INV_ORDER ex) {
            TEST(false);
        }

        //
        // Test: forward reference is available
        //
        try {
            org.omg.CORBA.Object fwd = ri.forward_reference();
            TEST(false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: get_reply_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
            TEST(false);
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: get_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = ri
                    .get_request_service_context(REQUEST_CONTEXT_ID.value);
            TEST(false);
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: add_request_service_context
        //
        try {
            org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
            sc.context_id = REQUEST_CONTEXT_ID.value;
            ri.add_request_service_context(sc, false);
        } catch (BAD_INV_ORDER ex) {
            // Expected
        }

        //
        // Test: examine PICurrent
        //
        Any slotData = null;
        try {
            slotData = pic_.get_slot(0);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
        int v = slotData.extract_long();
        TEST(v == 15);

        Any newSlotData = ORB.init().create_any();
        newSlotData.insert_long(16);
        try {
            pic_.set_slot(0, newSlotData);
        } catch (InvalidSlot ex) {
            TEST(false);
        }
    }

    int _OB_numReq() {
        return req_;
    }
}
