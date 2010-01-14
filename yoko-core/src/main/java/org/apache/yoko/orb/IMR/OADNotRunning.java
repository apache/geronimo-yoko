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

package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/OADNotRunning:1.0
//
/**
 *
 * A OADNotRunning exception indicates that the OAD detailed in the
 * member <code>host</code> isn't up.
 *
 **/

final public class OADNotRunning extends org.omg.CORBA.UserException
{
    private static final String _ob_id = "IDL:orb.yoko.apache.org/IMR/OADNotRunning:1.0";

    public
    OADNotRunning()
    {
        super(_ob_id);
    }

    public
    OADNotRunning(String host)
    {
        super(_ob_id);
        this.host = host;
    }

    public
    OADNotRunning(String _reason,
                  String host)
    {
        super(_ob_id + " " + _reason);
        this.host = host;
    }

    public String host;
}
