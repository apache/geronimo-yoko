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

import acme.Loader;
import acme.Processor;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;
import testify.iiop.annotation.ConfigureServer.RemoteStub;
import testify.iiop.annotation.ConfigureServer.Separation;
import testify.util.Maps;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import static javax.rmi.PortableRemoteObject.narrow;
import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.INVALID;
import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.REQUIRED;
import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.ROLLBACK;
import static org.apache.yoko.util.Exceptions.describeException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static testify.iiop.annotation.ConfigureOrb.NameService.READ_WRITE;

@ConfigureServer(
        separation = Separation.INTER_ORB,
        serverOrb = @ConfigureOrb(nameService = READ_WRITE)
)
public class RemoteTransactionExceptionTest {
    public enum TransactionExceptionType {
        INVALID(INVALID_TRANSACTION.class, "InvalidTransactionException"),
        REQUIRED(TRANSACTION_REQUIRED.class, "TransactionRequiredException"),
        ROLLBACK(TRANSACTION_ROLLEDBACK.class, "TransactionRolledbackException");
        public final Class<? extends SystemException> systemExceptionClass;
        public final Map<Loader,String> remoteExceptionNames;

        <E extends SystemException> TransactionExceptionType(Class<E> corbaExceptionClass, String javaExceptionName) {
            this.systemExceptionClass = corbaExceptionClass;
            this.remoteExceptionNames = Maps.of(
                    Loader.V1, "javax.transaction." + javaExceptionName,
                    Loader.V2, "jakarta.transaction." + javaExceptionName);
        }

        void throwRemoteException(Loader loader) throws RemoteException {
            String exceptionName = remoteExceptionNames.get(loader);
            throw (RemoteException)loader.newInstance(exceptionName, exceptionName);
        }
    }

    @RemoteImpl
    public static Processor impl(Bus bus) throws Exception {
        return Loader.V2.newInstance("versioned.VersionedProcessorImpl", bus);
    }

    @RemoteStub
    public static Processor stub;

    @Test
    public void testThrowInvalidTransactionV1V1() { testRemoteExceptionIsTranslatedCorrectly(INVALID, Loader.V1, Loader.V1); }
    @Test
    public void testThrowInvalidTransactionV1V2() { testRemoteExceptionIsTranslatedCorrectly(INVALID, Loader.V1, Loader.V2); }
    @Test
    public void testThrowInvalidTransactionV2V1() { testRemoteExceptionIsTranslatedCorrectly(INVALID, Loader.V2, Loader.V1); }
    @Test
    public void testThrowInvalidTransactionV2V2() { testRemoteExceptionIsTranslatedCorrectly(INVALID, Loader.V2, Loader.V2); }

    @Test
    public void testThrowTransactionRequiredV1V1() { testRemoteExceptionIsTranslatedCorrectly(REQUIRED, Loader.V1, Loader.V1); }
    @Test
    public void testThrowTransactionRequiredV1V2() { testRemoteExceptionIsTranslatedCorrectly(REQUIRED, Loader.V1, Loader.V2); }
    @Test
    public void testThrowTransactionRequiredV2V1() { testRemoteExceptionIsTranslatedCorrectly(REQUIRED, Loader.V2, Loader.V1); }
    @Test
    public void testThrowTransactionRequiredV2V2() { testRemoteExceptionIsTranslatedCorrectly(REQUIRED, Loader.V2, Loader.V2); }

    @Test
    public void testThrowTransactionRolledbackV1V1() { testRemoteExceptionIsTranslatedCorrectly(ROLLBACK, Loader.V1, Loader.V1); }
    @Test
    public void testThrowTransactionRolledbackV1V2() { testRemoteExceptionIsTranslatedCorrectly(ROLLBACK, Loader.V1, Loader.V2); }
    @Test
    public void testThrowTransactionRolledbackV2V1() { testRemoteExceptionIsTranslatedCorrectly(ROLLBACK, Loader.V2, Loader.V1); }
    @Test
    public void testThrowTransactionRolledbackV2V2() { testRemoteExceptionIsTranslatedCorrectly(ROLLBACK, Loader.V2, Loader.V2); }


    interface Thrower extends Remote {
        void throwConfiguredException(TransactionExceptionType eType, Loader serverLoader) throws RemoteException;
    }

    private void testRemoteExceptionIsTranslatedCorrectly(TransactionExceptionType exceptionType, Loader client, Loader server) {
        Processor p = (Processor) narrow(stub, client.loadClass("versioned.VersionedProcessor"));
        try {
            p.performRemotely(() -> exceptionType.throwRemoteException(server));
            fail();
        } catch (RemoteException e) {
            assertThat(describeException(e), e.getClass().getName(), equalTo(exceptionType.remoteExceptionNames.get(client)));
            assertThat(e.getCause(), notNullValue());
            assertThat(e.getCause(), instanceOf(exceptionType.systemExceptionClass));
        }
    }
}
