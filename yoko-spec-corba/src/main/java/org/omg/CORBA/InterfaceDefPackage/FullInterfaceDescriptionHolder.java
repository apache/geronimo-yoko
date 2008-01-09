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

package org.omg.CORBA.InterfaceDefPackage;

//
// IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0
//
final public class FullInterfaceDescriptionHolder implements org.omg.CORBA.portable.Streamable
{
    public FullInterfaceDescription value;

    public
    FullInterfaceDescriptionHolder()
    {
    }

    public
    FullInterfaceDescriptionHolder(FullInterfaceDescription initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = FullInterfaceDescriptionHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        FullInterfaceDescriptionHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return FullInterfaceDescriptionHelper.type();
    }
}
