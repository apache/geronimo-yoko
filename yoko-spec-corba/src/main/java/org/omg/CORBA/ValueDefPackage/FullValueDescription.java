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
package org.omg.CORBA.ValueDefPackage;

//
// IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0
//

import org.omg.CORBA.AttributeDescription;
import org.omg.CORBA.Initializer;
import org.omg.CORBA.OperationDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IDLEntity;

import java.util.Arrays;
import java.util.stream.Collectors;

/***/

final public class FullValueDescription implements IDLEntity {
    @SuppressWarnings("unused")
    private static final String _ob_id = "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";
    private static final String NL = System.lineSeparator();

    public FullValueDescription() {}

    public FullValueDescription(String name, String id, boolean is_abstract, boolean is_custom, String defined_in,
                                String version, OperationDescription[] operations, AttributeDescription[] attributes,
                                ValueMember[] members, Initializer[] initializers, String[] supported_interfaces,
                                String[] abstract_base_values, boolean is_truncatable, String base_value, TypeCode type) {
        this.name = name;
        this.id = id;
        this.is_abstract = is_abstract;
        this.is_custom = is_custom;
        this.defined_in = defined_in;
        this.version = version;
        this.operations = operations;
        this.attributes = attributes;
        this.members = members;
        this.initializers = initializers;
        this.supported_interfaces = supported_interfaces;
        this.abstract_base_values = abstract_base_values;
        this.is_truncatable = is_truncatable;
        this.base_value = base_value;
        this.type = type;
    }

    public String name;
    public String id;
    public boolean is_abstract;
    public boolean is_custom;
    public String defined_in;
    public String version;
    public OperationDescription[] operations;
    public AttributeDescription[] attributes;
    public ValueMember[] members;
    public Initializer[] initializers;
    public String[] supported_interfaces;
    public String[] abstract_base_values;
    public boolean is_truncatable;
    public String base_value;
    public TypeCode type;

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        return new StringBuilder()
                .append("FullValueDescription {").append(NL)
                .append("\tid = ").append(id).append(NL)
                .append("\tname = ").append(name).append(NL)
                .append("\tis_abstract = ").append(is_abstract).append(NL)
                .append("\tis_custom = ").append(is_custom).append(NL)
                .append("\tdefined_in = ").append(defined_in).append(NL)
                .append("\tversion = ").append(version).append(NL)
                .append("\toperations = ").append(Arrays.deepToString(operations)).append(NL)
                .append("\tattributes = ").append(Arrays.deepToString(attributes)).append(NL)
                .append("\tmembers = ").append(Arrays.stream(members).map(ValueMember::toString).collect(Collectors.joining("," + NL, "[ ", " ]"))).append(NL)
                .append("\tinitializers = ").append(Arrays.deepToString(initializers)).append(NL)
                .append("\tsupported_interfaces = ").append(Arrays.deepToString(supported_interfaces)).append(NL)
                .append("\tabstract_base_values = ").append(Arrays.deepToString(abstract_base_values)).append(NL)
                .append("\tis_truncatable = ").append(is_truncatable).append(NL)
                .append("\tbase_value = ").append(base_value).append(NL)
                .append("\ttype = ").append(type.toString().replace(NL, NL + "\t")).append(NL)
                .append("}")
                .toString();
    }
}
