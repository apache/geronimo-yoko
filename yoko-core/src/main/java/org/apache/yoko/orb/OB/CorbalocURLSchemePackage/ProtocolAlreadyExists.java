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

package org.apache.yoko.orb.OB.CorbalocURLSchemePackage;

//
// IDL:orb.yoko.apache.org/OB/CorbalocURLScheme/ProtocolAlreadyExists:1.0
//
/** Another protocol already exists with the same name. */

final public class ProtocolAlreadyExists extends org.omg.CORBA.UserException
{
    private static final String _ob_id = "IDL:orb.yoko.apache.org/OB/CorbalocURLScheme/ProtocolAlreadyExists:1.0";

    public
    ProtocolAlreadyExists()
    {
        super(_ob_id);
    }

    public
    ProtocolAlreadyExists(String _reason)
    {
        super(_ob_id + " " + _reason);
    }
}
