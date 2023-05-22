/*
 * Copyright 2023 IBM Corporation and others.
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
package org.apache.yoko;

import acme.RemoteRunnable;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.NO_RESPONSE;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import java.rmi.RemoteException;

import static java.lang.Thread.sleep;
import static testify.expect.ExceptionExpectation.expect;

@ConfigureServer(
        clientOrb = @ConfigureOrb(props = "yoko.orb.policy.request_timeout=1")
)
public class TimeoutTest {
    interface Sleeper extends RemoteRunnable {}

    @RemoteImpl
    public static final Sleeper IMPL = () -> sleep((1000));

    @Test
    public void testTimeout(Sleeper stub) {
        expect(RemoteException.class)
                .causedBy(NO_RESPONSE.class)
                .rootCause(NO_RESPONSE.class)
                .when(stub::run);
    }
}
