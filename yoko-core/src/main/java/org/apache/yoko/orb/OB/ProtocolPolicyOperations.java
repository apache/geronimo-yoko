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
// IDL:orb.yoko.apache.org/OB/ProtocolPolicy:1.0
//
/**
 *
 * The protocol policy. This policy specifies the order in which profiles
 * should be tried.
 *
 **/

public interface ProtocolPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/ProtocolPolicy/value:1.0
    //
    /**
     *
     * If a <code>ProtocolPolicy</code> is set, then the value specifies
     * the list of plugins that may be used. The profiles of an IOR will
     * be used in the order specified by this policy. If no profile in an
     * IOR matches any of the plugins specified by this policy, a
     * <code>CORBA::TRANSIENT</code> exception will be raised. By
     * default, the ORB chooses the protocol to be used.
     *
     **/

    String[]
    value();

    //
    // IDL:orb.yoko.apache.org/OB/ProtocolPolicy/contains:1.0
    //
    /**
     *
     * Determines if this policy includes the given plugin id.
     *
     **/

    boolean
    contains(String id);
}
