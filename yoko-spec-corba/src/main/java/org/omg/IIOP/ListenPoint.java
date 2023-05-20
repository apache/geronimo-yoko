/*
 * Copyright 2020 IBM Corporation and others.
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
package org.omg.IIOP;

import org.omg.CORBA.portable.IDLEntity;

// Generated from IDL
final public class ListenPoint implements IDLEntity {
    private static final String _ob_id = "IDL:omg.org/IIOP/ListenPoint:1.0";
    public String host = "";
    public short port;

    public ListenPoint() {}

    public ListenPoint(String host, short port) {
        this.host = host;
        this.port = port;
    }
}
