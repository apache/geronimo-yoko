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
package test.obv;

//
// IDL:TestAbstract:1.0
//
final public class TestAbstractHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestAbstract val)
    {
        if(val instanceof org.omg.CORBA.Object)
            any.insert_Object((org.omg.CORBA.Object)val, type());
        else
            any.insert_Value((java.io.Serializable)val, type());
    }

    public static TestAbstract
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            try
            {
                return narrow(any.extract_Object());
            }
            catch(org.omg.CORBA.BAD_OPERATION ex)
            {
                java.io.Serializable _ob_v = any.extract_Value();
                if(_ob_v == null || _ob_v instanceof TestAbstract)
                    return (TestAbstract)_ob_v;
            }
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_abstract_interface_tc(id(), "TestAbstract");
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestAbstract:1.0";
    }

    public static TestAbstract
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        java.lang.Object _ob_v = ((org.omg.CORBA_2_3.portable.InputStream)in).read_abstract_interface();
        return narrow(_ob_v);
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestAbstract val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_abstract_interface(val);
    }

    public static TestAbstract
    narrow(java.lang.Object val)
    {
        if(val != null)
        {
            try
            {
                return (TestAbstract)val;
            }
            catch(ClassCastException ex)
            {
            }

            if(val instanceof org.omg.CORBA.Object)
            {
                org.omg.CORBA.Object _ob_o = (org.omg.CORBA.Object)val;
                if(_ob_o._is_a(id()))
                {
                    org.omg.CORBA.portable.ObjectImpl _ob_impl;
                    _TestAbstractStub _ob_stub = new _TestAbstractStub();
                    _ob_impl = (org.omg.CORBA.portable.ObjectImpl)_ob_o;
                    _ob_stub._set_delegate(_ob_impl._get_delegate());
                    return _ob_stub;
                }
            }

            throw new org.omg.CORBA.BAD_PARAM();
        }

        return null;
    }

    public static TestAbstract
    unchecked_narrow(java.lang.Object val)
    {
        if(val != null)
        {
            try
            {
                return (TestAbstract)val;
            }
            catch(ClassCastException ex)
            {
            }

            if(val instanceof org.omg.CORBA.Object)
            {
                org.omg.CORBA.Object _ob_o = (org.omg.CORBA.Object)val;
                org.omg.CORBA.portable.ObjectImpl _ob_impl;
                _TestAbstractStub _ob_stub = new _TestAbstractStub();
                _ob_impl = (org.omg.CORBA.portable.ObjectImpl)_ob_o;
                _ob_stub._set_delegate(_ob_impl._get_delegate());
                return _ob_stub;
            }

            throw new org.omg.CORBA.BAD_PARAM();
        }

        return null;
    }
}
