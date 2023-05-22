/*
 * Copyright 2010 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/RequestTimeoutPolicy:1.0
//
/**
 *
 * The request timeout policy. This policy can be used to specify
 * a maximum time limit for requests.
 *
 * @see TimeoutPolicy
 *
 **/

public interface RequestTimeoutPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/RequestTimeoutPolicy/value:1.0
    //
    /**
     *
     * If an object has a <code>RequestTimeoutPolicy</code> set and no
     * response to a request is available after <code>value</code>
     * milliseconds, a <code>CORBA::NO_RESPONSE</code> exception is
     * raised. The default value is <code>-1</code>, which means no
     * timeout.
     *
     **/

    int
    value();
}
