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
package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/ZeroPortPolicyValue:1.0
//
/***/

public class ZeroPortPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
    private boolean value_;

    public final static boolean _ZERO_PORT = true;
    public final static ZeroPortPolicyValue ZERO_PORT = new ZeroPortPolicyValue(_ZERO_PORT);
    public final static boolean _NONZERO_PORT = false;
    public final static ZeroPortPolicyValue NONZERO_PORT = new ZeroPortPolicyValue(_NONZERO_PORT);

    protected
    ZeroPortPolicyValue(boolean value)
    {
        value_ = value;
    }

    public boolean
    value()
    {
        return value_;
    }

    public static ZeroPortPolicyValue
    from_boolean(boolean value)
    {
        if (value) {
            return ZERO_PORT; 
        }
        else {
            return NONZERO_PORT; 
        }
    }

    private java.lang.Object
    readResolve()
        throws java.io.ObjectStreamException
    {
        return from_boolean(value());
    }
}

