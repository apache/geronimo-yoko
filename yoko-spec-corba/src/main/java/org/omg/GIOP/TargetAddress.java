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

package org.omg.GIOP;

//
// IDL:omg.org/GIOP/TargetAddress:1.0
//
/***/

final public class TargetAddress implements org.omg.CORBA.portable.IDLEntity
{
    java.lang.Object _ob_v_;
    boolean _ob_i_;
    short _ob_d_;

    static boolean
    _OB_check(short d0, short d1)
    {
        short d[] = new short[2];
        d[0] = d0;
        d[1] = d1;

        for(int i = 0; i < 2; i++)
        {
            switch(d[i])
            {
            case (short)0:
                break;

            case (short)1:
                break;

            case (short)2:
                break;

            default:
                d[i] = (short)3;
                break;
            }
        }

        return d[0] == d[1];
    }

    public
    TargetAddress()
    {
        _ob_i_ = false;
    }

    public short
    discriminator()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        return _ob_d_;
    }

    public byte[]
    object_key()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, (short)0))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (byte[])_ob_v_;
    }

    public void
    object_key(byte[] val)
    {
        _ob_i_ = true;
        _ob_d_ = (short)0;
        _ob_v_ = val;
    }

    public org.omg.IOP.TaggedProfile
    profile()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, (short)1))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (org.omg.IOP.TaggedProfile)_ob_v_;
    }

    public void
    profile(org.omg.IOP.TaggedProfile val)
    {
        _ob_i_ = true;
        _ob_d_ = (short)1;
        _ob_v_ = val;
    }

    public IORAddressingInfo
    ior()
    {
        if(!_ob_i_)
            throw new org.omg.CORBA.BAD_OPERATION();

        if(!_OB_check(_ob_d_, (short)2))
            throw new org.omg.CORBA.BAD_OPERATION();

        return (IORAddressingInfo)_ob_v_;
    }

    public void
    ior(IORAddressingInfo val)
    {
        _ob_i_ = true;
        _ob_d_ = (short)2;
        _ob_v_ = val;
    }

    public void
    __default()
    {
        _ob_i_ = true;
        _ob_d_ = (short)3;
        _ob_v_ = null;
    }

    public void
    __default(short d)
    {
        if(!_OB_check(d, (short)3))
            throw new org.omg.CORBA.BAD_PARAM();

        _ob_i_ = true;
        _ob_d_ = d;
        _ob_v_ = null;
    }
}
