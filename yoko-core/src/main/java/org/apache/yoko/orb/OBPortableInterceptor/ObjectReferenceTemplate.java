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

package org.apache.yoko.orb.OBPortableInterceptor;

//
// IDL:orb.yoko.apache.org/OBPortableInterceptor/ObjectReferenceTemplate:1.0
//
/***/

public interface ObjectReferenceTemplate extends org.omg.CORBA.portable.ValueBase,
                                                 org.omg.PortableInterceptor.ObjectReferenceTemplate
{
    //
    // IDL:orb.yoko.apache.org/OBPortableInterceptor/ObjectReferenceTemplate/make_object_for:1.0
    //
    /***/

    org.omg.CORBA.Object
    make_object_for(String repository_id,
                    byte[] id,
                    String[] name);
}
