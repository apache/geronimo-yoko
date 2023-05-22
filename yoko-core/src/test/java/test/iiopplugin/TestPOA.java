/*
 * Copyright 2010 IBM Corporation and others.
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
package test.iiopplugin;

//
// IDL:Test:1.0
//
public abstract class TestPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               TestOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:Test:1.0",
    };

    public Test
    _this()
    {
        return TestHelper.narrow(super._this_object());
    }

    public Test
    _this(org.omg.CORBA.ORB orb)
    {
        return TestHelper.narrow(super._this_object(orb));
    }

    public String[]
    _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId)
    {
        return _ob_ids_;
    }

    public org.omg.CORBA.portable.OutputStream
    _invoke(String opName,
            org.omg.CORBA.portable.InputStream in,
            org.omg.CORBA.portable.ResponseHandler handler)
    {
        final String[] _ob_names =
        {
            "inany",
            "intest",
            "outany",
            "returntest",
            "say",
            "shutdown"
        };

        int _ob_left = 0;
        int _ob_right = _ob_names.length;
        int _ob_index = -1;

        while(_ob_left < _ob_right)
        {
            int _ob_m = (_ob_left + _ob_right) / 2;
            int _ob_res = _ob_names[_ob_m].compareTo(opName);
            if(_ob_res == 0)
            {
                _ob_index = _ob_m;
                break;
            }
            else if(_ob_res > 0)
                _ob_right = _ob_m;
            else
                _ob_left = _ob_m + 1;
        }

        if(_ob_index == -1 && opName.charAt(0) == '_')
        {
            _ob_left = 0;
            _ob_right = _ob_names.length;
            String _ob_ami_op =
                opName.substring(1);

            while(_ob_left < _ob_right)
            {
                int _ob_m = (_ob_left + _ob_right) / 2;
                int _ob_res = _ob_names[_ob_m].compareTo(_ob_ami_op);
                if(_ob_res == 0)
                {
                    _ob_index = _ob_m;
                    break;
                }
                else if(_ob_res > 0)
                    _ob_right = _ob_m;
                else
                    _ob_left = _ob_m + 1;
            }
        }

        switch(_ob_index)
        {
        case 0: // inany
            return _OB_op_inany(in, handler);

        case 1: // intest
            return _OB_op_intest(in, handler);

        case 2: // outany
            return _OB_op_outany(in, handler);

        case 3: // returntest
            return _OB_op_returntest(in, handler);

        case 4: // say
            return _OB_op_say(in, handler);

        case 5: // shutdown
            return _OB_op_shutdown(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_inany(org.omg.CORBA.portable.InputStream in,
                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        inany(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_intest(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        Test _ob_a0 = TestHelper.read(in);
        intest(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_outany(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.AnyHolder _ob_ah0 = new org.omg.CORBA.AnyHolder();
        outany(_ob_ah0);
        out = handler.createReply();
        out.write_any(_ob_ah0.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_returntest(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        Test _ob_r = returntest();
        out = handler.createReply();
        TestHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_say(org.omg.CORBA.portable.InputStream in,
               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = in.read_string();
        say(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_shutdown(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        shutdown();
        out = handler.createReply();
        return out;
    }
}
