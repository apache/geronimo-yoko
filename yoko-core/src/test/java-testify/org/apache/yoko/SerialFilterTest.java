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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.rmi.Sample;
import test.rmi.SampleImpl;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;

import java.io.InvalidClassException;
import java.io.Serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static testify.jupiter.annotation.iiop.ConfigureServer.Separation.INTER_PROCESS;

@ConfigureServer(
        separation = INTER_PROCESS,
        jvmArgs = {
                "-Djdk.serialFilter=!org.apache.yoko.SerialFilterTest$ForbiddenMessage;"+
                        "maxarray=" + SerialFilterTest.MAX_ARR_LEN + ";" +
                        "maxdepth=" + SerialFilterTest.MAX_DEPTH
        }
)
public class SerialFilterTest {
    public static final int MAX_ARR_LEN = 200;
    public static final int MAX_DEPTH = 100;

    @ClientStub(SampleImpl.class)
    public static Sample sample;

    public static class AllowedMessage implements Serializable {
        final String payload;
        AllowedMessage(String msg) { this.payload = msg; }
    }

    public static class ForbiddenMessage implements Serializable {
        final String payload;
        ForbiddenMessage(String msg) { this.payload = msg; }
    }

    public static class AllowedLink implements Serializable {
        final AllowedLink next;
        AllowedLink(AllowedLink next) { this.next = next; }
    }

    @Test
    public void testSendingAllowedObject() throws Exception {
        final String msg = "Hello, world!";
        sample.setSerializable(new AllowedMessage(msg));
        AllowedMessage actual = (AllowedMessage) sample.getSerializable();
        assertThat(actual.payload, is(msg));
    }

    @Test
    public void testSendingForbiddenObject() throws Throwable {
        final String msg = "Hello, world!";
        try {
            sample.setSerializable(new ForbiddenMessage(msg));
            Assertions.fail("ForbiddenMessage should not be demarshalled by the server.");
        } catch (RuntimeException re) {
            try {
                throw re.getCause();
            } catch (InvalidClassException ice) {
                // played for and got
            }
        }
    }

    @Test
    public void testSendingOverlongArray() throws Throwable {
        try {
            sample.setSerializable(new Object[MAX_ARR_LEN + 1]);
            Assertions.fail("demarshalling an Object[MAX_ARR_LEN + 1] should be blocked by the serial filter");
        } catch (RuntimeException re) {
            try {
                throw re.getCause();
            } catch (InvalidClassException ice) {
                // played for and got
            }
        }
    }

    @Test
    public void testSendingOverlyDeepGraph() throws Throwable {
        AllowedLink chain = null;
        for (int i = 0; i < MAX_DEPTH; i++) {
            chain = new AllowedLink(chain);
        }
        chain = new AllowedLink(chain);
        try {
            sample.setSerializable(chain);
            Assertions.fail("demarshalling an Object graph nested (MAX_DEPTH + 1) deep should be blocked by the serial filter");
        } catch (RuntimeException re) {
            try {
                throw re.getCause();
            } catch (InvalidClassException ice) {
                // played for and got
            }
        }
    }
}
