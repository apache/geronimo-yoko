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
package org.omg.CORBA_2_3.portable;

public abstract class InputStream extends org.omg.CORBA.portable.InputStream {
    public java.io.Serializable read_value() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.io.Serializable read_value(java.io.Serializable value) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.io.Serializable read_value(java.lang.String rep_id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.io.Serializable read_value(Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.io.Serializable read_value(
            org.omg.CORBA.portable.BoxedValueHelper factory) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.lang.Object read_abstract_interface() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.lang.Object read_abstract_interface(java.lang.Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
