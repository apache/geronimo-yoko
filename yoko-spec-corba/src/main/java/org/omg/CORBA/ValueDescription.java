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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/ValueDescription:1.0
//
/***/

final public class ValueDescription implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/CORBA/ValueDescription:1.0";

    public
    ValueDescription()
    {
    }

    public
    ValueDescription(String name,
                     String id,
                     boolean is_abstract,
                     boolean is_custom,
                     String defined_in,
                     String version,
                     String[] supported_interfaces,
                     String[] abstract_base_values,
                     boolean is_truncatable,
                     String base_value)
    {
        this.name = name;
        this.id = id;
        this.is_abstract = is_abstract;
        this.is_custom = is_custom;
        this.defined_in = defined_in;
        this.version = version;
        this.supported_interfaces = supported_interfaces;
        this.abstract_base_values = abstract_base_values;
        this.is_truncatable = is_truncatable;
        this.base_value = base_value;
    }

    public String name;
    public String id;
    public boolean is_abstract;
    public boolean is_custom;
    public String defined_in;
    public String version;
    public String[] supported_interfaces;
    public String[] abstract_base_values;
    public boolean is_truncatable;
    public String base_value;
}
