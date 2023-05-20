/*
 * Copyright 2022 IBM Corporation and others.
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
package org.omg.CORBA;

//
// IDL:omg.org/CORBA/ValueMember:1.0
//

import org.omg.CORBA.portable.IDLEntity;

/***/

final public class ValueMember implements IDLEntity
{
    @SuppressWarnings("unused")
    private static final String _ob_id = "IDL:omg.org/CORBA/ValueMember:1.0";
    private static final String NL = System.lineSeparator();

    public ValueMember() {}

    public ValueMember(String name, String id, String defined_in, String version, TypeCode type, IDLType type_def, short access) {
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.type = type;
        this.type_def = type_def;
        this.access = access;
    }

    public String name;
    public String id;
    public String defined_in;
    public String version;
    public TypeCode type;
    public IDLType type_def;
    public short access;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ValueMember {").append(NL)
                .append("\tname = ").append(name).append(NL)
                .append("\tid = ").append(id).append(NL)
                .append("\tdefined_in = ").append(defined_in).append(NL)
                .append("\tversion = ").append(version).append(NL)
                .append("\ttype = ").append(type.toString().replace(NL, NL + "\t")).append(NL)
                .append("\ttype_def = ").append(type_def).append(NL)
                .append("\taccess = ").append(String.format("%02x", access)).append(NL)
                .append("}")
                .toString();
    }
}
