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
package testify.bus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testify.jupiter.annotation.Tracing;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testify.jupiter.annotation.impl.TracingSteward.addTraceSettings;

class SimpleBusImplTest {

    @Test
    void testMsgIsNotDone() throws ExecutionException, InterruptedException {
        try (SimpleBusImpl simpleBus = new SimpleBusImpl()) {

            // try an asynchronous get
            final ExecutorService xs = Executors.newSingleThreadExecutor();
            try {
                final Future<String> msg = xs.submit(() -> simpleBus.get("msg"));
                Assertions.assertFalse(msg.isDone());
            } finally {
                xs.shutdown();
            }
        }
    }

    @Test
    void testGetMsg() throws Exception {
        try (SimpleBusImpl simpleBus = new SimpleBusImpl()) {
            // try an asynchronous get
            final ExecutorService xs = Executors.newSingleThreadExecutor();
            try {
                Future<String> msg = xs.submit(() -> simpleBus.get("msg"));
                simpleBus.put("msg", "hello");
                assertEquals("hello",msg.get(5, SECONDS));
            } finally {
                xs.shutdown();
            }
        }
    }

    @Test
    void testCallbackHasNotBeenCalled() throws ExecutionException, InterruptedException {
        try (SimpleBusImpl simpleBus = new SimpleBusImpl()) {

            // try an asynchronous get
            final ExecutorService xs = Executors.newSingleThreadExecutor();
            try {
                final Future<String> msg = xs.submit(() -> simpleBus.get("msg"));
                simpleBus.put("msg", "hello");
                System.out.println("Correctly retrieved message: " + msg.get());
            } finally {
                xs.shutdown();
            }

            {
                // register a callback
                CompletableFuture<String> msg = new CompletableFuture<>();
                simpleBus.onMsg("msg", msg::complete);
                // check the callback has not been called
                Assertions.assertFalse(msg.isDone());
            }
        }
    }

    @Test
    void testCallbackHasBeenCalledCorrectly() throws ExecutionException, InterruptedException {
        try (SimpleBusImpl simpleBus = new SimpleBusImpl()) {

            // try an asynchronous get
            final ExecutorService xs = Executors.newSingleThreadExecutor();
            try {
                final Future<String> msg = xs.submit(() -> simpleBus.get("msg"));
                simpleBus.put("msg", "hello");
                System.out.println("Correctly retrieved message: " + msg.get());
            } finally {
                xs.shutdown();
            }

            {
                // register a callback
                CompletableFuture<String> msg = new CompletableFuture<>();
                simpleBus.onMsg("msg", msg::complete);
                // put a new message
                simpleBus.put("msg", "world");
                // wait for the callback to be called
                // check the callback has been called correctly
                Assertions.assertEquals("world", msg.get());
                System.out.println("Correctly retrieved message: " + msg.get());
            }
        }
    }
}
