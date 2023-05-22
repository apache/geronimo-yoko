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
package test.types;

//
// IDL:TestUnion5:1.0
//
/***/

final public class TestUnion5 implements org.omg.CORBA.portable.IDLEntity
{
    java.lang.Object _ob_v_;
    boolean _ob_i_;
    long _ob_d_;

    static boolean
    _OB_check(long d0, long d1)
    {
        long d[] = new long[2];
        d[0] = d0;
        d[1] = d1;

        for(int i = 0; i < 2; i++)
        {
            if(d[i] == -42L)
                ;
            else if(d[i] == 100000L ||
                    d[i] == 50000000L)
                d[i] = 100000L;
            else 
                d[i] = 0L;
        }

        return d[0] == d[1];
    }

    public
    TestUnion5()
    {
        _ob_i_ = false;
    }

    public long
    discriminator()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        return _ob_d_;
    }

    public org.omg.CORBA.Any
    a()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, -42L))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (org.omg.CORBA.Any)_ob_v_;
    }

    public void
    a(org.omg.CORBA.Any val)
    {
        _ob_i_ = true;
        _ob_d_ = -42L;
        _ob_v_ = val;
    }

    public char
    c()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 100000L))
            throw new org.omg.CORBA.BAD_OPERATION();

        return ((org.omg.CORBA.CharHolder)_ob_v_).value;
    }

    public void
    c(char val)
    {
        _ob_i_ = true;
        _ob_d_ = 100000L;
        _ob_v_ = new org.omg.CORBA.CharHolder(val);
    }

    public void
    c(long d, char val)
    {
        if(!_OB_check(d, 100000L))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d;
        _ob_v_ = new org.omg.CORBA.CharHolder(val);
    }

    public String
    f()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 0L))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (String)_ob_v_;
    }

    public void
    f(String val)
    {
        _ob_i_ = true;
        _ob_d_ = 0L;
        _ob_v_ = val;
    }

    public void
    f(long d, String val)
    {
        if(!_OB_check(d, 0L))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d;
        _ob_v_ = val;
    }
}
