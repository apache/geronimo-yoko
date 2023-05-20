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
package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/ServerRunning:1.0
//
/**
 *
 * An ServerRunning exception indicates that the Server detailed in the
 * member <code>name</code> is active.
 *
 **/

final public class ServerRunning extends org.omg.CORBA.UserException
{
    private static final String _ob_id = "IDL:orb.yoko.apache.org/IMR/ServerRunning:1.0";

    public
    ServerRunning()
    {
        super(_ob_id);
    }

    public
    ServerRunning(String name)
    {
        super(_ob_id);
        this.name = name;
    }

    public
    ServerRunning(String _reason,
                  String name)
    {
        super(_ob_id + " " + _reason);
        this.name = name;
    }

    public String name;
}
