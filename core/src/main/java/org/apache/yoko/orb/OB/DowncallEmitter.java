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

package org.apache.yoko.orb.OB;

public interface DowncallEmitter {
    //
    // Send and receive downcalls
    //
    // - The first parameter is the downcall to send/receive
    //
    // - If the second parameter is set to false, send/receive will be
    // done non-blocking.
    //
    // - If the return value is true, it's safe to access or modify
    // the downcall object. If the return value if false, accessing
    // or modifying the downcall object is not allowed, for thread
    // safety reasons. (Because the downcall object is not thread
    // safe.)
    //
    boolean send(Downcall down, boolean block);

    boolean receive(Downcall down, boolean block);

    //
    // Send and receive downcalls with one operation (for efficiency
    // reasons)
    //
    boolean sendReceive(Downcall down);
}
