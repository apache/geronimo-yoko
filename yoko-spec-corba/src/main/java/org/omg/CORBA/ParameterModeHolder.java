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
// IDL:omg.org/CORBA/ParameterMode:1.0
//
final public class ParameterModeHolder implements org.omg.CORBA.portable.Streamable
{
    public ParameterMode value;

    public
    ParameterModeHolder()
    {
    }

    public
    ParameterModeHolder(ParameterMode initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = ParameterModeHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        ParameterModeHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return ParameterModeHelper.type();
    }
}
