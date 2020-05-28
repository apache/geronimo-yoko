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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.RemoteObject;
import testify.jupiter.annotation.iiop.ConfigureServer.UseWithServerOrb;
import testify.util.Stack;

import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

import static org.apache.yoko.orb.OB.TestInterceptorsThatThrow.InterceptionPoint.receive_request;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

@ConfigureServer
public class TestInterceptorsThatThrow {
    enum InterceptionPoint {
        receive_request_service_contexts(Target::receive_request_service_contexts),
        receive_request(Target::receive_request),
        send_reply(Target::send_reply),
        send_exception(Target::send_exception),
        send_other(Target::send_other)
        ;

        private interface Invoker<T> { void invoke(T t) throws RemoteException; }
        private final Invoker<Target> invoker;
        final int minor = 1000000000 + this.ordinal();
        private InterceptionPoint(Invoker<Target> invoker) {this.invoker = invoker;}
        void invoke(Target target) throws RemoteException {invoker.invoke(target);}
    }

    @UseWithServerOrb
    public static class InterceptorThatThrows extends LocalObject implements ServerRequestInterceptor, ORBInitializer {
        public void pre_init(ORBInitInfo info) {
            try {
                info.add_server_request_interceptor(this);
            } catch (DuplicateName duplicateName) {
                duplicateName.printStackTrace();
                throw (INTERNAL) new INTERNAL().initCause(duplicateName);
            }
        }
        public void post_init(ORBInitInfo info) {}
        public void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest { maybeThrowInternal(ri).orForwardRequest(); }
        public void receive_request(ServerRequestInfo ri) throws ForwardRequest { maybeThrowInternal(ri).orForwardRequest(); }
        public void send_reply(ServerRequestInfo ri) { maybeThrowInternal(ri); }
        public void send_exception(ServerRequestInfo ri) throws ForwardRequest { maybeThrowInternal(ri).orForwardRequest(); }
        public void send_other(ServerRequestInfo ri) throws ForwardRequest { maybeThrowInternal(ri).orForwardRequest(); }
        public String name() { return "InterceptorThatThrows"; }
        public void destroy() { }

        private interface Forwarder { void orForwardRequest() throws ForwardRequest; }

        /**
         * This method is to be invoked from an interception point to determine what
         * exception should be thrown from that interception point for this particular test.
         * @param info the info object received by the interceptor invoking this method
         */
        private static Forwarder maybeThrowInternal(ServerRequestInfo info) {
            final InterceptionPoint configured = InterceptionPoint.valueOf(info.operation());
            System.out.println("configured = " + configured);
            final String caller = Stack.getStackTraceElement(1).getMethodName();
            final InterceptionPoint current = InterceptionPoint.valueOf(caller);
            System.out.println("current = " + current);
            if (current == configured) {
                System.out.println("Deliberately throwing exception from interception point: " + current);
                throw new INTERNAL(current.minor, COMPLETED_MAYBE);
            }
            if (current == receive_request) {
                switch (configured) {
                case send_exception: throw new INTERNAL(current.minor, COMPLETED_NO);
                case send_other: return TargetImpl::forwardToNewInstance;
                }
            }
            return () -> {};
        }
    }

    /**
     * The instance methods here are deliberately named to match interception points.
     * The operation name (i.e. the method name) is available to the interceptor.
     * The name can therefore be used to determine the intent of the test.
     * Simples!
     */
    public interface Target extends Remote {
        default void receive_request_service_contexts() throws RemoteException {}
        default void receive_request() throws RemoteException {}
        default void send_reply() throws RemoteException {}
        default void send_exception() throws RemoteException {}
        default void send_other() throws RemoteException {}

    }

    public static class TargetImpl extends PortableRemoteObject implements Target {
        static void forwardToNewInstance() throws ForwardRequest {
            try {
                TargetImpl result = new TargetImpl();
                throw new ForwardRequest(Util.getTie(result).thisObject());
            } catch (RemoteException e) {
                throw new Error(e);
            }
        }

        public TargetImpl() throws RemoteException{}
    }

    @RemoteObject(TargetImpl.class)
    public static Target stub;

    @ParameterizedTest
    @EnumSource(InterceptionPoint.class)
    public void testThrowFromInterceptor(InterceptionPoint interceptionPoint) throws Throwable {
        try {
            try {
                interceptionPoint.invoker.invoke(stub);
                Assertions.fail("Should have thrown an exception");
            } catch (RemoteException remote) {
                throw remote.getCause();
            }
        } catch (INTERNAL e) {
            assertThat(e.minor, equalTo(interceptionPoint.minor));
            assertThat(e.completed, equalTo(COMPLETED_MAYBE));
        }
    }
}
