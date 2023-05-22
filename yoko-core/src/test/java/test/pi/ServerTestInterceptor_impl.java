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

final class ServerTestInterceptor_impl extends org.omg.CORBA.LocalObject
        implements ServerRequestInterceptor {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private ORB orb_; // For Any creation

    private org.omg.IOP.Codec cdrCodec_; // The cached CDR codec

    private void testArgs(ServerRequestInfo ri, boolean resultAvail) {
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
                // if which.startsWith("struct"))
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

    private void testServiceContext(String op, ServerRequestInfo ri,
            boolean addContext) {
        if (op.equals("test_service_context")) {
            //
            // Test: get_request_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
                TEST(sc.context_id == REQUEST_CONTEXT_ID.value);
            } catch (BAD_PARAM ex) {
                TEST(false);
            }

            //
            // Test: get_reply_service_context
            //
            org.omg.IOP.ServiceContext sc = null;
            try {
                sc = ri.get_reply_service_context(REPLY_CONTEXT_4_ID.value);
            } catch (BAD_INV_ORDER ex) {
                TEST(false);
            }
            byte[] data = new byte[sc.context_data.length];
            System.arraycopy(sc.context_data, 0, data, 0,
                    sc.context_data.length);

            Any any = null;
            try {
                any = cdrCodec_.decode_value(data, ReplyContextHelper.type());
            } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                TEST(false);
            } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                TEST(false);
            }
            ReplyContext context = ReplyContextHelper.extract(any);
            TEST(context.data.equals("reply4"));
            TEST(context.val == 114);

            if (addContext) {
                //
                // Test: add_reply_service_context
                //
                context.data = "reply3";
                context.val = 103;
                any = orb_.create_any();
                ReplyContextHelper.insert(any, context);
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                sc.context_id = REPLY_CONTEXT_3_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                try {
                    ri.add_reply_service_context(sc, false);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: add same context again (no replace)
                //
                try {
                    ri.add_reply_service_context(sc, false);
                    TEST(false);
                } catch (BAD_INV_ORDER ex) {
                    // Expected
                }

                //
                // Test: add same context again (replace)
                //
                try {
                    ri.add_reply_service_context(sc, true);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: replace context added in receive_request
                //
                context.data = "reply4";
                context.val = 124;
                ReplyContextHelper.insert(any, context);
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                sc.context_id = REPLY_CONTEXT_4_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                try {
                    ri.add_reply_service_context(sc, true);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }
            }
        } else {
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_request_service_context(REQUEST_CONTEXT_ID.value);
                TEST(false);
            } catch (BAD_PARAM ex) {
                // Expected
            }
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
                TEST(false);
            } catch (BAD_PARAM ex) {
                // Expected
            }
        }
    }

    ServerTestInterceptor_impl(org.omg.CORBA.ORB orb,
            org.omg.IOP.CodecFactory factory) {
        orb_ = orb;

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

    //
    // IDL to Java Mapping
    //

    public String name() {
        return "ServerTestInterceptor";
    }

    public void destroy() {
    }

    public void receive_request_service_contexts(ServerRequestInfo ri) {
        try {
            //
            // Test: get operation name
            //
            String op = ri.operation();

            boolean oneway = (op.equals("noargs_oneway"));

            //
            // Test: Arguments should not be available
            //
            try {
                org.omg.Dynamic.Parameter[] args = ri.arguments();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            // TODO: test operation_context

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
            try {
                TypeCode[] exceptions = ri.exceptions();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: response expected and oneway should be equivalent
            //
            TEST((oneway && !ri.response_expected())
                    || (!oneway && ri.response_expected()));

            // TODO: test sync scope

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
            // Test: forward reference is not available
            //
            try {
                org.omg.CORBA.Object ior = ri.forward_reference();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: object id is not available
            //
            try {
                byte[] id = ri.object_id();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: adapter id is not available
            //
            try {
                byte[] id = ri.adapter_id();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: servant_most_derived_interface is not available
            //
            try {
                String mdi = ri.target_most_derived_interface();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: server id is not available
            //
            try {
                String id = ri.server_id();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: orb id is not available
            //
            try {
                String id = ri.orb_id();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: adapter name is not available
            //
            try {
                String[] name = ri.adapter_name();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: servant_is_a is not available
            //
            try {
                ri.target_is_a("");
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
                    TEST(sc.context_id == REQUEST_CONTEXT_ID.value);
                    byte[] data = new byte[sc.context_data.length];
                    System.arraycopy(sc.context_data, 0, data, 0,
                            sc.context_data.length);

                    Any any = null;
                    try {
                        any = cdrCodec_.decode_value(data, RequestContextHelper
                                .type());
                    } catch (org.omg.IOP.CodecPackage.FormatMismatch ex) {
                        TEST(false);
                    } catch (org.omg.IOP.CodecPackage.TypeMismatch ex) {
                        TEST(false);
                    }
                    RequestContext context = RequestContextHelper.extract(any);
                    TEST(context.data.equals("request"));
                    TEST(context.val == 10);

                    //
                    // Test: PortableInterceptor::Current
                    //
                    Any slotData = orb_.create_any();
                    slotData.insert_long(context.val);
                    try {
                        ri.set_slot(0, slotData);
                    } catch (InvalidSlot ex) {
                        TEST(false);
                    }
                } catch (BAD_PARAM ex) {
                    TEST(false);
                }

                //
                // Test: add_reply_service_context
                //
                ReplyContext context = new ReplyContext();
                context.data = "reply1";
                context.val = 101;
                Any any = orb_.create_any();
                ReplyContextHelper.insert(any, context);
                byte[] data = null;
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
                sc.context_id = REPLY_CONTEXT_1_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                try {
                    ri.add_reply_service_context(sc, false);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: add same context again (no replace)
                //
                try {
                    ri.add_reply_service_context(sc, false);
                    TEST(false);
                } catch (BAD_INV_ORDER ex) {
                    // Expected
                }

                //
                // Test: add same context again (replace)
                //
                try {
                    ri.add_reply_service_context(sc, true);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: add second context
                //
                context.data = "reply4";
                context.val = 104;
                ReplyContextHelper.insert(any, context);
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                sc.context_id = REPLY_CONTEXT_4_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                // try
                // {
                ri.add_reply_service_context(sc, false);
                // }
                // catch(BAD_INV_ORDER ex)
                // {
                // TEST(false);
                // }
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
            // Test: get_reply_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: sending exception is not available
            //
            try {
                Any any = ri.sending_exception();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_server_policy
            //
            Policy policy = ri.get_server_policy(MY_SERVER_POLICY_ID.value);
            MyServerPolicy myServerPolicy = MyServerPolicyHelper.narrow(policy);
            TEST(myServerPolicy != null);
            TEST(myServerPolicy.value() == 10);

            try {
                policy = ri.get_server_policy(1013);
                TEST(false);
            } catch (INV_POLICY ex) {
                // Expected
            }
        } catch (test.common.TestException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public void receive_request(ServerRequestInfo ri) {
        try {
            //
            // Test: get operation name
            //
            String op = ri.operation();

            boolean oneway = (op.equals("noargs_oneway"));

            //
            // Test: Examine arguments
            //
            testArgs(ri, false);

            // TODO: test operation_context

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
            try {
                TypeCode[] exceptions = ri.exceptions();
                if (op.equals("userexception")) {
                    TEST(exceptions.length == 1);
                    TEST(exceptions[0].equal(userHelper.type()));
                } else {
                    TEST(exceptions.length == 0);
                }
            } catch (NO_RESOURCES ex) {
                // Expected (if servant is DSI)
            }

            //
            // Test: response expected and oneway should be equivalent
            //
            TEST((oneway && !ri.response_expected())
                    || (!oneway && ri.response_expected()));

            // TODO: test sync scope

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
            // Test: forward reference is not available
            //
            try {
                org.omg.CORBA.Object ior = ri.forward_reference();
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
                    TEST(sc.context_id == REQUEST_CONTEXT_ID.value);
                } catch (BAD_PARAM ex) {
                    TEST(false);
                }

                //
                // Test: add_reply_service_context
                //
                ReplyContext context = new ReplyContext();
                context.data = "reply2";
                context.val = 102;
                Any any = orb_.create_any();
                ReplyContextHelper.insert(any, context);
                byte[] data = null;
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                org.omg.IOP.ServiceContext sc = new org.omg.IOP.ServiceContext();
                sc.context_id = REPLY_CONTEXT_2_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                try {
                    ri.add_reply_service_context(sc, false);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: add same context again (no replace)
                //
                try {
                    ri.add_reply_service_context(sc, false);
                    TEST(false);
                } catch (BAD_INV_ORDER ex) {
                    // Expected
                }

                //
                // Test: add same context again (replace)
                //
                try {
                    ri.add_reply_service_context(sc, true);
                } catch (BAD_INV_ORDER ex) {
                    TEST(false);
                }

                //
                // Test: replace context added in
                // receive_request_service_context
                //
                context.data = "reply4";
                context.val = 114;
                ReplyContextHelper.insert(any, context);
                try {
                    data = cdrCodec_.encode_value(any);
                } catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex) {
                    TEST(false);
                }

                sc.context_id = REPLY_CONTEXT_4_ID.value;
                sc.context_data = new byte[data.length];
                System.arraycopy(data, 0, sc.context_data, 0, data.length);

                try {
                    ri.add_reply_service_context(sc, true);
                } catch (BAD_INV_ORDER ex) {
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
            // Test: get_reply_service_context
            //
            try {
                org.omg.IOP.ServiceContext sc = ri
                        .get_reply_service_context(REPLY_CONTEXT_1_ID.value);
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: sending exception is not available
            //
            try {
                Any any = ri.sending_exception();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: object id is correct
            //
            byte[] oid = ri.object_id();
            TEST((oid.length == 4 && (new String(oid)).equals("test"))
                    || (oid.length == 7 && (new String(oid)).equals("testDSI")));

            //
            // Test: adapter id is correct (this is a tough one to test)
            //
            byte[] adapterId = ri.adapter_id();
            TEST(adapterId.length != 0);

            //
            // Test: servant most derived interface is correct
            //
            String mdi = ri.target_most_derived_interface();
            TEST(mdi.equals("IDL:TestInterface:1.0"));

            //
            // Test: server id is correct
            //
            String serverId = ri.server_id();
            TEST(serverId.equals(""));

            //
            // Test: orb id is correct
            //
            String orbId = ri.orb_id();
            TEST(orbId.equals("myORB"));

            //
            // Test: adapter name is correct
            //
            String[] adapterName = ri.adapter_name();
            TEST(adapterName.length == 1 && adapterName[0].equals("persistent"));

            //
            // Test: servant is a is correct
            //
            TEST(ri.target_is_a("IDL:TestInterface:1.0"));

            //
            // Test: get_server_policy
            //
            Policy policy = ri.get_server_policy(MY_SERVER_POLICY_ID.value);
            MyServerPolicy myServerPolicy = MyServerPolicyHelper.narrow(policy);
            TEST(myServerPolicy != null);
            TEST(myServerPolicy.value() == 10);

            try {
                policy = ri.get_server_policy(1013);
                TEST(false);
            } catch (INV_POLICY ex) {
                // Expected
            }
        } catch (test.common.TestException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public void send_reply(ServerRequestInfo ri) {
        try {
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
            // Test: Arguments should be available
            //
            testArgs(ri, true);

            // TODO: test operation_context

            //
            // Test: exceptions
            //
            try {
                TypeCode[] exceptions = ri.exceptions();
                if (op.equals("userexception")) {
                    TEST(exceptions.length == 1);
                    TEST(exceptions[0].equal(userHelper.type()));
                } else {
                    TEST(exceptions.length == 0);
                }
            } catch (NO_RESOURCES ex) {
                // Expected (if servant is DSI)
            }

            //
            // Test: response expected and oneway should be equivalent
            //
            TEST((oneway && !ri.response_expected())
                    || (!oneway && ri.response_expected()));

            // TODO: test sync scope

            //
            // Test: reply status is available
            //
            TEST(ri.reply_status() == SUCCESSFUL.value);

            //
            // Test: forward reference is not available
            //
            try {
                org.omg.CORBA.Object ior = ri.forward_reference();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_request_service_context
            // Test: get_reply_service_context
            // Test: add_reply_service_context
            //
            testServiceContext(op, ri, true);

            //
            // Test: sending exception is not available
            //
            try {
                Any any = ri.sending_exception();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: object id is correct
            //
            byte[] oid = ri.object_id();
            TEST((oid.length == 4 && (new String(oid)).equals("test"))
                    || (oid.length == 7 && (new String(oid)).equals("testDSI")));

            //
            // Test: adapter id is correct (this is a tough one to test)
            //
            byte[] adapterId = ri.adapter_id();
            TEST(adapterId.length != 0);

            //
            // Test: target_most_derived_interface raises BAD_INV_ORDER
            //
            try {
                String mdi = ri.target_most_derived_interface();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: server id is correct
            //
            String serverId = ri.server_id();
            TEST(serverId.equals(""));

            //
            // Test: orb id is correct
            //
            String orbId = ri.orb_id();
            TEST(orbId.equals("myORB"));

            //
            // Test: adapter name is correct
            //
            String[] adapterName = ri.adapter_name();
            TEST(adapterName.length == 1 && adapterName[0].equals("persistent"));

            //
            // Test: target_is_a raises BAD_INV_ORDER
            //
            try {
                ri.target_is_a("IDL:TestInterface:1.0");
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_server_policy
            //
            Policy policy = ri.get_server_policy(MY_SERVER_POLICY_ID.value);
            MyServerPolicy myServerPolicy = MyServerPolicyHelper.narrow(policy);
            TEST(myServerPolicy != null);
            TEST(myServerPolicy.value() == 10);

            try {
                policy = ri.get_server_policy(1013);
                TEST(false);
            } catch (INV_POLICY ex) {
                // Expected
            }

            //
            // Test: get_slot
            //
            if (op.equals("test_service_context")) {
                int val;
                Any slotData = null;
                try {
                    slotData = ri.get_slot(0);
                } catch (InvalidSlot ex) {
                    TEST(false);
                }
                val = slotData.extract_long();
                TEST(val == 20);
            }
        } catch (test.common.TestException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public void send_other(ServerRequestInfo ri) {
        try {
            //
            // Test: get operation name
            //
            String op = ri.operation();

            TEST(op.equals("location_forward"));

            //
            // Test: Arguments should not be available
            //
            try {
                org.omg.Dynamic.Parameter[] parameters = ri.arguments();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: exceptions
            //
            try {
                TypeCode[] exceptions = ri.exceptions();
                if (op.equals("userexception")) {
                    TEST(exceptions.length == 1);
                    TEST(exceptions[0].equal(userHelper.type()));
                } else {
                    TEST(exceptions.length == 0);
                }
            } catch (BAD_INV_ORDER ex) {
                // Expected, depending on what raised the exception
            } catch (NO_RESOURCES ex) {
                // Expected (if servant is DSI)
            }

            // TODO: test operation_context

            //
            // Test: response expected should be true
            //
            TEST(ri.response_expected());

            // TODO: test sync scope

            //
            // Test: reply status is available
            //
            TEST(ri.reply_status() == LOCATION_FORWARD.value);

            //
            // Test: forward reference is available
            //
            try {
                org.omg.CORBA.Object ior = ri.forward_reference();
            } catch (BAD_INV_ORDER ex) {
                TEST(false);
            }

            //
            // Test: get_request_service_context
            // Test: get_reply_service_context
            //
            testServiceContext(op, ri, false);

            //
            // Test: sending exception is not available
            //
            try {
                Any any = ri.sending_exception();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: object id is correct
            //
            byte[] oid = ri.object_id();
            TEST((oid.length == 4 && (new String(oid)).equals("test"))
                    || (oid.length == 7 && (new String(oid)).equals("testDSI")));

            //
            // Test: adapter id is correct (this is a tough one to test)
            //
            byte[] adapterId = ri.adapter_id();
            TEST(adapterId.length != 0);

            //
            // Test: target_most_derived_interface raises BAD_INV_ORDER
            //
            try {
                String mdi = ri.target_most_derived_interface();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: server id is correct
            //
            String serverId = ri.server_id();
            TEST(serverId.equals(""));

            //
            // Test: orb id is correct
            //
            String orbId = ri.orb_id();
            TEST(orbId.equals("myORB"));

            //
            // Test: adapter name is correct
            //
            String[] adapterName = ri.adapter_name();
            TEST(adapterName.length == 1 && adapterName[0].equals("persistent"));

            //
            // Test: target_is_a raises BAD_INV_ORDER
            //
            try {
                ri.target_is_a("IDL:TestInterface:1.0");
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_server_policy
            //
            Policy policy = ri.get_server_policy(MY_SERVER_POLICY_ID.value);
            MyServerPolicy myServerPolicy = MyServerPolicyHelper.narrow(policy);
            TEST(myServerPolicy != null);
            TEST(myServerPolicy.value() == 10);

            try {
                policy = ri.get_server_policy(1013);
                TEST(false);
            } catch (INV_POLICY ex) {
                // Expected
            }
        } catch (test.common.TestException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public void send_exception(ServerRequestInfo ri) {
        try {
            //
            // Test: get operation name
            //
            String op = ri.operation();

            TEST(op.equals("systemexception") || op.equals("userexception")
                    || op.equals("deactivate"));

            boolean user = op.equals("userexception");

            //
            // If "deactivate" then we're done
            //
            if (op.equals("deactivate"))
                return;

            //
            // Test: Arguments should not be available
            //
            try {
                org.omg.Dynamic.Parameter[] parameters = ri.arguments();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            // TODO: test operation_context

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
            try {
                TypeCode[] exceptions = ri.exceptions();
                if (op.equals("userexception")) {
                    TEST(exceptions.length == 1);
                    TEST(exceptions[0].equal(userHelper.type()));
                } else {
                    TEST(exceptions.length == 0);
                }
            } catch (BAD_INV_ORDER ex) {
                TEST(false);
            } catch (NO_RESOURCES ex) {
                // Expected (if servant is DSI)
            }

            //
            // Test: response expected should be true
            //
            TEST(ri.response_expected());

            // TODO: test sync scope

            //
            // Test: reply status is available
            //
            if (user)
                TEST(ri.reply_status() == USER_EXCEPTION.value);
            else
                TEST(ri.reply_status() == SYSTEM_EXCEPTION.value);

            //
            // Test: forward reference is not available
            //
            try {
                org.omg.CORBA.Object ior = ri.forward_reference();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_request_service_context
            // Test: get_reply_service_context
            //
            testServiceContext(op, ri, false);

            //
            // Test: sending exception is available
            //
            try {
                Any any = ri.sending_exception();
                if (!user) {
                    SystemException ex = org.apache.yoko.orb.OB.Util
                            .unmarshalSystemException(any.create_input_stream());
                } else {
                    user ex = userHelper.extract(any);
                }
            } catch (BAD_INV_ORDER ex) {
                TEST(false);
            } catch (NO_RESOURCES ex) // TODO: remove this!
            {
            }

            //
            // Test: object id is correct
            //
            byte[] oid = ri.object_id();
            TEST((oid.length == 4 && (new String(oid)).equals("test"))
                    || (oid.length == 7 && (new String(oid)).equals("testDSI")));

            //
            // Test: adapter id is correct (this is a tough one to test)
            //
            byte[] adapterId = ri.adapter_id();
            TEST(adapterId.length != 0);

            //
            // Test: target_most_derived_interface raises BAD_INV_ORDER
            //
            try {
                String mdi = ri.target_most_derived_interface();
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: server id is correct
            //
            String serverId = ri.server_id();
            TEST(serverId.equals(""));

            //
            // Test: orb id is correct
            //
            String orbId = ri.orb_id();
            TEST(orbId.equals("myORB"));

            //
            // Test: adapter name is correct
            //
            String[] adapterName = ri.adapter_name();
            TEST(adapterName.length == 1 && adapterName[0].equals("persistent"));

            //
            // Test: target_is_a raises BAD_INV_ORDER
            //
            try {
                ri.target_is_a("IDL:TestInterface:1.0");
                TEST(false);
            } catch (BAD_INV_ORDER ex) {
                // Expected
            }

            //
            // Test: get_server_policy
            //
            Policy policy = ri.get_server_policy(MY_SERVER_POLICY_ID.value);
            MyServerPolicy myServerPolicy = MyServerPolicyHelper.narrow(policy);
            TEST(myServerPolicy != null);
            TEST(myServerPolicy.value() == 10);

            try {
                policy = ri.get_server_policy(1013);
                TEST(false);
            } catch (INV_POLICY ex) {
                // Expected
            }
        } catch (test.common.TestException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
