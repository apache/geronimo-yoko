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

final public class TIMEOUTHolder implements org.omg.CORBA.portable.Streamable {
    public TIMEOUT value;

    public TIMEOUTHolder() {
    }

    public TIMEOUTHolder(TIMEOUT initial) {
        value = initial;
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = TIMEOUTHelper.read(in);
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        TIMEOUTHelper.write(out, value);
    }

    public TypeCode _type() {
        return TIMEOUTHelper.type();
    }
}
