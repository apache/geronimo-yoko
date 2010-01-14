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

package org.apache.yoko.orb.OBPortableServer;

//
// IDL:orb.yoko.apache.org/OBPortableServer/InterceptorCallPolicy:1.0
//
/**
 *
 * The interceptor call policy. This policy controls whether the
 * server-side interceptors are called for a particular POA.
 *
 **/

public interface InterceptorCallPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OBPortableServer/InterceptorCallPolicy/value:1.0
    //
    /**
     *
     * The InterceptorCallPolicy value.  If a POA has an
     * <code>InterceptorCallPolicy</code> set and <code>value</code> is
     * <code>FALSE</code> then any installed server-side interceptors are
     * not called for requests on this POA.  Otherwise, interceptors are
     * called for each request. The default value is <code>TRUE</code>.
     *
     **/

    boolean
    value();
}
