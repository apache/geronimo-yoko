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
package org.omg.CORBA.InterfaceDefPackage;

//
// IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0
//
/***/

final public class FullInterfaceDescription implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0";

    public
    FullInterfaceDescription()
    {
    }

    public
    FullInterfaceDescription(String name,
                             String id,
                             String defined_in,
                             String version,
                             org.omg.CORBA.OperationDescription[] operations,
                             org.omg.CORBA.AttributeDescription[] attributes,
                             String[] base_interfaces,
                             org.omg.CORBA.TypeCode type)
    {
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.operations = operations;
        this.attributes = attributes;
        this.base_interfaces = base_interfaces;
        this.type = type;
    }

    public String name;
    public String id;
    public String defined_in;
    public String version;
    public org.omg.CORBA.OperationDescription[] operations;
    public org.omg.CORBA.AttributeDescription[] attributes;
    public String[] base_interfaces;
    public org.omg.CORBA.TypeCode type;
}
