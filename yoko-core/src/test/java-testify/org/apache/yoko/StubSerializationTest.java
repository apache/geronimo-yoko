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

import acme.Echo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.RemoteImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

@ConfigureServer
public class StubSerializationTest {
    @RemoteImpl
    public static final Echo impl = s -> s.toUpperCase(Locale.ROOT);

    @Test
    public void testSerializingAStub(Echo stub) throws Exception {
        final byte[] data;
        try (
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut)
        ) {
            out.writeObject(stub);
            data = byteOut.toByteArray();
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Echo stub2 = (Echo) in.readObject();
            Assertions.assertEquals("HELLO", stub2.echo("hello"));
        }

    }
}
