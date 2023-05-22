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
package org.apache.yoko.orb.OCI;

//
// IDL:orb.yoko.apache.org/OCI/Transport:1.0
//
/**
 *
 * The interface for a Transport object, which provides operations
 * for sending and receiving octet streams. In addition, it is
 * possible to register callbacks with the Transport object, which
 * are invoked whenever data can be sent or received without
 * blocking.
 *
 * @see Connector
 * @see Acceptor
 *
 **/

public interface Transport extends TransportOperations,
                                   org.omg.CORBA.Object
{
}
