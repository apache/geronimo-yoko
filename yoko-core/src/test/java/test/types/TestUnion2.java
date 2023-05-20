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
// IDL:TestUnion2:1.0
//
/***/

final public class TestUnion2 implements org.omg.CORBA.portable.IDLEntity
{
    java.lang.Object _ob_v_;
    boolean _ob_i_;
    int _ob_d_;

    static boolean
    _OB_check(int d0, int d1)
    {
        int d[] = new int[2];
        d[0] = d0;
        d[1] = d1;

        for(int i = 0; i < 2; i++)
        {
            switch(d[i])
            {
            case TestEnum._A:
            case TestEnum._B:
                d[i] = TestEnum._A;
                break;

            case TestEnum._C:
                break;
            }
        }

        return d[0] == d[1];
    }

    public
    TestUnion2()
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

    public int[]
    seq()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, TestEnum._A))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (int[])_ob_v_;
    }

    public void
    seq(int[] val)
    {
        _ob_i_ = true;
        _ob_d_ = TestEnum._A;
        _ob_v_ = val;
    }

    public void
    seq(TestEnum d, int[] val)
    {
        if(!_OB_check(d.value(), TestEnum._A))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d.value();
        _ob_v_ = val;
    }

    public TestUnion1
    un()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, TestEnum._C))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (TestUnion1)_ob_v_;
    }

    public void
    un(TestUnion1 val)
    {
        _ob_i_ = true;
        _ob_d_ = TestEnum._C;
        _ob_v_ = val;
    }
}
