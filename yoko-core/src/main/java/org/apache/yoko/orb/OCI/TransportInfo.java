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
// IDL:orb.yoko.apache.org/OCI/TransportInfo:1.0
//
/**
 *
 * Information on an OCI Transport object. Objects of this type must
 * be narrowed to a Transport information object for a concrete
 * protocol implementation, for example to
 * <code>OCI::IIOP::TransportInfo</code> in case the plug-in
 * implements IIOP.
 *
 * @see Transport
 *
 **/

public interface TransportInfo extends TransportInfoOperations,
                                       org.omg.CORBA.Object
{
}
