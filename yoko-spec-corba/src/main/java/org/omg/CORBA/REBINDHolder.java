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

final public class REBINDHolder implements org.omg.CORBA.portable.Streamable {
    public REBIND value;

    public REBINDHolder() {
    }

    public REBINDHolder(REBIND initial) {
        value = initial;
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = REBINDHelper.read(in);
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        REBINDHelper.write(out, value);
    }

    public TypeCode _type() {
        return REBINDHelper.type();
    }
}
