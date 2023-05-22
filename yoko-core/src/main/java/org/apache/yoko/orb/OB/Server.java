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

abstract public class Server {
    //
    // The concurrency model for this Server
    //
    final public static int Blocking = 0;

    final public static int Threaded = 2;

    protected int concModel_ = Blocking;

    // ----------------------------------------------------------------------
    // Server package member implementations
    // ----------------------------------------------------------------------

    Server(int concModel) {
        concModel_ = concModel;
    }

    //
    // Destroy the server
    //
    abstract void destroy();

    //
    // Hold any new requests that arrive for the Server
    //
    abstract void hold();

    //
    // Dispatch any requests that arrive for the Server
    //
    abstract void activate();
}
