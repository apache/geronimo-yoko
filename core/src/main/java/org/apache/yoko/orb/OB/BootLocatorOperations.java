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

package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/BootLocator:1.0
//
/**
 *
 * Interface used by BootManager to assist in locating objects.
 *
 * @see BootManager
 * 
 **/

public interface BootLocatorOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/BootLocator/locate:1.0
    //
    /**
     *
     * Locate the object coresponding to the given object id.
     *
     * @param oid The object id.
     *
     * @param obj The object reference to associate with the id.
     *
     * @param add Whether the binding should be added to the internal
     * table.
     *
     * @exception NotFound Raised if no binding found.
     *
     **/

    void
    locate(byte[] oid,
           org.omg.CORBA.ObjectHolder obj,
           org.omg.CORBA.BooleanHolder add)
        throws org.apache.yoko.orb.OB.BootManagerPackage.NotFound;
}
