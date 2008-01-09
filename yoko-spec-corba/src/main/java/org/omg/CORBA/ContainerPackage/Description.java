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

package org.omg.CORBA.ContainerPackage;

//
// IDL:omg.org/CORBA/Container/Description:1.0
//
/***/

final public class Description implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/CORBA/Container/Description:1.0";

    public
    Description()
    {
    }

    public
    Description(org.omg.CORBA.Contained contained_object,
                org.omg.CORBA.DefinitionKind kind,
                org.omg.CORBA.Any value)
    {
        this.contained_object = contained_object;
        this.kind = kind;
        this.value = value;
    }

    public org.omg.CORBA.Contained contained_object;
    public org.omg.CORBA.DefinitionKind kind;
    public org.omg.CORBA.Any value;
}
