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
package org.omg.CORBA;

//
// IDL:omg.org/CORBA/OperationDescription:1.0
//
final public class OperationDescriptionHolder implements org.omg.CORBA.portable.Streamable
{
    public OperationDescription value;

    public
    OperationDescriptionHolder()
    {
    }

    public
    OperationDescriptionHolder(OperationDescription initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = OperationDescriptionHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        OperationDescriptionHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return OperationDescriptionHelper.type();
    }
}
