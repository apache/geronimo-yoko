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

package org.omg.CORBA.ValueDefPackage;

//
// IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0
//
/***/

final public class FullValueDescription implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";

    public
    FullValueDescription()
    {
    }

    public
    FullValueDescription(String name,
                         String id,
                         boolean is_abstract,
                         boolean is_custom,
                         String defined_in,
                         String version,
                         org.omg.CORBA.OperationDescription[] operations,
                         org.omg.CORBA.AttributeDescription[] attributes,
                         org.omg.CORBA.ValueMember[] members,
                         org.omg.CORBA.Initializer[] initializers,
                         String[] supported_interfaces,
                         String[] abstract_base_values,
                         boolean is_truncatable,
                         String base_value,
                         org.omg.CORBA.TypeCode type)
    {
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
    public org.omg.CORBA.OperationDescription[] operations;
    public org.omg.CORBA.AttributeDescription[] attributes;
    public org.omg.CORBA.ValueMember[] members;
    public org.omg.CORBA.Initializer[] initializers;
    public String[] supported_interfaces;
    public String[] abstract_base_values;
    public boolean is_truncatable;
    public String base_value;
    public org.omg.CORBA.TypeCode type;
}
