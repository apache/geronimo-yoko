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
package org.apache.yoko;

import acme.RemoteFunction;
import org.junit.jupiter.api.Test;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ConfigureServer
public class EmojiTest {
    interface Echo extends RemoteFunction<String, String>{}

    @RemoteImpl
    public static final Echo REMOTE = String::toString;

    @Test
    public void sendEmoji(Echo stub) throws RemoteException {
        final char[] chars = Character.toChars(0x1f642);
        assert chars.length == 2;
        final String message = "Hello, world!" + chars[0] + chars[1];
        String reply = stub.apply(message);
        assertEquals(message, reply, "String should be transmitted and received correctly");
    }
}
