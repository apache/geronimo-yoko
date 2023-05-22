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
package testify.iiop.annotation;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.opentest4j.AssertionFailedError;
import testify.annotation.RetriedTest;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ConfigureServer
public class ServerControlTest {
    public interface Echo extends Remote {
        String echo(String msg) throws RemoteException;
    }

    @ConfigureServer.Control
    public static ServerControl serverControl;

    @RemoteImpl
    public static final Echo IMPL = String::toString;

    @Test
    public void testServerControlButDoNothing(ORB clientOrb) throws Exception {}

    @Test
    public void testServerControlLeaveServerStopped(ORB clientOrb) throws Exception {
        serverControl.stop();
    }

    @RetriedTest(maxRuns = 127) // Try this many times to flush out build environment problems
    public void testServerControl(ORB clientOrb, Echo stub) throws Exception {
        assertEquals("hello", stub.echo("hello"));
        assertThrows(AssertionFailedError.class, serverControl::start);
        serverControl.stop();
        assertThrows(RemoteException.class, () -> stub.echo(""));
        assertThrows(AssertionFailedError.class, serverControl::stop);
        assertThrows(AssertionFailedError.class, serverControl::restart);
        serverControl.start();
        assertEquals("hello again", stub.echo("hello again"));
        serverControl.restart();
        assertEquals("hello once more", stub.echo("hello once more"));
        serverControl.stop();
        assertThrows(Exception.class, () -> stub.echo("hello? anyone home?"));
        serverControl.start();
        assertEquals("again I say hello", stub.echo("again I say hello"));
    }
}
