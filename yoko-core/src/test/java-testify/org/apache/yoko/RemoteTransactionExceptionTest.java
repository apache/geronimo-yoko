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

import acme.Loader;
import acme.Processor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.opentest4j.AssertionFailedError;
import testify.bus.Bus;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.BeforeServer;
import testify.jupiter.annotation.iiop.ConfigureServer.NameServiceStub;
import testify.jupiter.annotation.iiop.ConfigureServer.Separation;
import testify.util.Maps;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.INVALID;
import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.REQUIRED;
import static org.apache.yoko.RemoteTransactionExceptionTest.TransactionExceptionType.ROLLBACK;
import static org.apache.yoko.util.Exceptions.describeException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_WRITE;
import static testify.util.Names.toCosName;

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

        <E extends SystemException> TransactionExceptionType(Class<E> systemExceptionClass, String simpleName) {
            this.systemExceptionClass = systemExceptionClass;
            this.remoteExceptionNames = Maps.of(
                    Loader.V1, "javax.transaction." + simpleName,
                    Loader.V2, "jakarta.transaction." + simpleName);
        }

        void throwRemoteException(Loader loader) throws Exception {
            String exceptionName = remoteExceptionNames.get(loader);
            throw (RemoteException)loader.getConstructor(exceptionName, String.class).newInstance(exceptionName);
        }
    }

    private static final String BIND_NAME = "Processor";
    @NameServiceStub
    public static NamingContext nameServiceClient;
    private static final EnumMap<Loader,Processor> stubs = new EnumMap<>(Loader.class);

    @BeforeServer
    public static void initServer(ORB orb, POA rootPOA, Bus bus) throws Exception {
        bus.log("Initializing server");
        NamingContext nc = NamingContextHelper.narrow(orb.resolve_initial_references("NameService"));
        bus.log("Retrieved name service");
        try {
            Constructor<Processor> constructor = Loader.V2.getConstructor("versioned.VersionedProcessorImpl", Bus.class);
            Processor proc = constructor.newInstance(bus);
            PortableRemoteObject.exportObject(proc);
            bus.log("Exported server target object");
            Tie tie = Util.getTie(proc);
            rootPOA.activate_object((Servant) tie);
            bus.log("Activated object with root POA");
            nc.bind(toCosName(BIND_NAME), tie.thisObject());
            bus.log("Bound object");
        } catch (Exception e) {
            fail(e);
        }
    }

    @BeforeAll
    public static void initClient() {
        // populate the stubs enum map
        Stream.of(Loader.values())
                .forEach(key -> stubs.computeIfAbsent(key, l -> {
                    Class<? extends Processor> stubInterface = l.loadClass("versioned.VersionedProcessor");
                    try {
                        Object resolved = nameServiceClient.resolve(toCosName(BIND_NAME));
                        return (Processor)PortableRemoteObject.narrow(resolved, stubInterface);
                    } catch (Exception e) {
                        throw new AssertionFailedError("Could not retrieve stub", e);
                    }
                }));
    }

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


    private void testRemoteExceptionIsTranslatedCorrectly(TransactionExceptionType exceptionType, Loader client, Loader server) {
        Processor p;
        try {
            p = (Processor) PortableRemoteObject.narrow(nameServiceClient.resolve(toCosName(BIND_NAME)), client.loadClass("versioned.VersionedProcessor"));
        } catch (Exception e1) {
            fail(e1);
            p = null;
        }
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
