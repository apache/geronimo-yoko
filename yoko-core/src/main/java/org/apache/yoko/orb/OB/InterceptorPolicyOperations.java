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
// IDL:orb.yoko.apache.org/OB/InterceptorPolicy:1.0
//
/**
 *
 * The interceptor policy. This policy can be used to control whether
 * the client-side interceptors are called.
 *
 **/

public interface InterceptorPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/InterceptorPolicy/value:1.0
    //
    /**
     *
     * If an object reference has an <code>InterceptorPolicy</code>
     * set and <code>value</code> is <code>FALSE</code> then any
     * installed client-side interceptors are not called.  Otherwise,
     * interceptors are called for each method invocation. The default
     * value is <code>TRUE</code>.
     *
     **/

    boolean
    value();
}
