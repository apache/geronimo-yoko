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
package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestUnion3:1.0
//
/***/

final public class TestUnion3 implements org.omg.CORBA.portable.IDLEntity
{
    java.lang.Object _ob_v_;
    boolean _ob_i_;
    int _ob_d_;

    static boolean
    _OB_check(int d0, int d1)
    {
        return d0 == d1;
    }

    public
    TestUnion3()
    {
        _ob_i_ = false;
    }

    public TestEnum
    discriminator()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        return TestEnum.from_int(_ob_d_);
    }

    public int
    a()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, TestEnum._red))
            throw new org.omg.CORBA.BAD_OPERATION();

        return ((org.omg.CORBA.IntHolder)_ob_v_).value;
    }

    public void
    a(int val)
    {
        _ob_i_ = true;
        _ob_d_ = TestEnum._red;
        _ob_v_ = new org.omg.CORBA.IntHolder(val);
    }

    public double
    b()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, TestEnum._green))
            throw new org.omg.CORBA.BAD_OPERATION();

        return ((org.omg.CORBA.DoubleHolder)_ob_v_).value;
    }

    public void
    b(double val)
    {
        _ob_i_ = true;
        _ob_d_ = TestEnum._green;
        _ob_v_ = new org.omg.CORBA.DoubleHolder(val);
    }

    public char
    c()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, TestEnum._blue))
            throw new org.omg.CORBA.BAD_OPERATION();

        return ((org.omg.CORBA.CharHolder)_ob_v_).value;
    }

    public void
    c(char val)
    {
        _ob_i_ = true;
        _ob_d_ = TestEnum._blue;
        _ob_v_ = new org.omg.CORBA.CharHolder(val);
    }
}
