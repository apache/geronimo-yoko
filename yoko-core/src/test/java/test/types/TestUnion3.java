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
// IDL:TestUnion3:1.0
//
/***/

final public class TestUnion3 implements org.omg.CORBA.portable.IDLEntity
{
    java.lang.Object _ob_v_;
    boolean _ob_i_;
    char _ob_d_;

    static boolean
    _OB_check(char d0, char d1)
    {
        char d[] = new char[2];
        d[0] = d0;
        d[1] = d1;

        for(int i = 0; i < 2; i++)
        {
            switch(d[i])
            {
            case 'a':
            case 'b':
                d[i] = 'a';
                break;

            case 'c':
                break;

            case 'd':
                break;

            case 'x':
                break;

            default:
                d[i] = '\0';
                break;
            }
        }

        return d[0] == d[1];
    }

    public
    TestUnion3()
    {
        _ob_i_ = false;
    }

    public char
    discriminator()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        return _ob_d_;
    }

    public char
    c()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 'a'))
            throw new org.omg.CORBA.BAD_OPERATION();

        return ((org.omg.CORBA.CharHolder)_ob_v_).value;
    }

    public void
    c(char val)
    {
        _ob_i_ = true;
        _ob_d_ = 'a';
        _ob_v_ = new org.omg.CORBA.CharHolder(val);
    }

    public void
    c(char d, char val)
    {
        if(!_OB_check(d, 'a'))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d;
        _ob_v_ = new org.omg.CORBA.CharHolder(val);
    }

    public org.omg.CORBA.Any
    a()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 'c'))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (org.omg.CORBA.Any)_ob_v_;
    }

    public void
    a(org.omg.CORBA.Any val)
    {
        _ob_i_ = true;
        _ob_d_ = 'c';
        _ob_v_ = val;
    }

    public String[][]
    ar()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 'd'))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (String[][])_ob_v_;
    }

    public void
    ar(String[][] val)
    {
        _ob_i_ = true;
        _ob_d_ = 'd';
        _ob_v_ = val;
    }

    public String
    s()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, 'x'))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (String)_ob_v_;
    }

    public void
    s(String val)
    {
        _ob_i_ = true;
        _ob_d_ = 'x';
        _ob_v_ = val;
    }

    public void
    __default()
    {
        _ob_i_ = true;
        _ob_d_ = '\0';
        _ob_v_ = null;
    }

    public void
    __default(char d)
    {
        if(!_OB_check(d, '\0'))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d;
        _ob_v_ = null;
    }
}
