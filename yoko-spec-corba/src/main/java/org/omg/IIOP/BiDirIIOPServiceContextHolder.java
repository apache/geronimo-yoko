/*
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
package org.omg.IIOP;

//
// IDL:omg.org/IIOP/BiDirIIOPServiceContext:1.0
//
final public class BiDirIIOPServiceContextHolder implements org.omg.CORBA.portable.Streamable
{
    public BiDirIIOPServiceContext value;

    public
    BiDirIIOPServiceContextHolder()
    {
    }

    public
    BiDirIIOPServiceContextHolder(BiDirIIOPServiceContext initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = BiDirIIOPServiceContextHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        BiDirIIOPServiceContextHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return BiDirIIOPServiceContextHelper.type();
    }
}
