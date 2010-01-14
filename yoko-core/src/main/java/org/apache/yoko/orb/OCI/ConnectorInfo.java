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

package org.apache.yoko.orb.OCI;

//
// IDL:orb.yoko.apache.org/OCI/ConnectorInfo:1.0
//
/**
 *
 * Information on a OCI Connector object. Objects of this type must be
 * narrowed to a Connector information object for a concrete protocol
 * implementation, for example to <code>OCI::IIOP::ConnectorInfo</code>
 * in case the plug-in implements IIOP.
 *
 * @see Connector
 *
 **/

public interface ConnectorInfo extends ConnectorInfoOperations,
                                       org.omg.CORBA.Object
{
}
