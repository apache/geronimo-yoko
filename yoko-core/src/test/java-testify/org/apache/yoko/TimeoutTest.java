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

import acme.Processor;
import acme.ProcessorImpl;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.NO_RESPONSE;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;

import java.rmi.RemoteException;

import static testify.expect.ExceptionExpectation.expect;

@ConfigureServer(
        clientOrb = @ConfigureOrb(props = "yoko.orb.policy.request_timeout=1")
)
public class TimeoutTest {
    @ClientStub(ProcessorImpl.class)
    public static Processor stub;

    @Test
    public void testTimeout() {
        expect(RemoteException.class)
                .causedBy(NO_RESPONSE.class)
                .rootCause(NO_RESPONSE.class)
                .when(() -> stub.performRemotely(() -> Thread.sleep(1000)));
    }

}
