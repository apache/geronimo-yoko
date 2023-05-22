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
// IDL:orb.yoko.apache.org/OB/ConnectionReusePolicy:1.0
//
/**
 *
 * The connection reuse policy. This policy determines whether
 * connections may be reused or are private to specific objects.
 *
 **/

public interface ConnectionReusePolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/ConnectionReusePolicy/value:1.0
    //
    /**
     *
     * If an object has a <code>ConnectionReusePolicy</code> set with
     * <code>value</code> set to <code>FALSE</code>, then other
     * object references will not be permitted to use connections
     * made on behalf of this object. If set to <code>TRUE</code>,
     * then connections are shared. The default value is
     * <code>TRUE</code>.
     *
     **/

    boolean
    value();
}
