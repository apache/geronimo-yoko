/*
 * =============================================================================
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.iiop;

import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public interface TestServerRequestInterceptor extends TestORBInitializer, TestInterceptor, ServerRequestInterceptor {
    default void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest {}
    default void receive_request(ServerRequestInfo ri) throws ForwardRequest {}
    default void send_reply(ServerRequestInfo ri) {}
    default void send_exception(ServerRequestInfo ri) throws ForwardRequest {}
    default void send_other(ServerRequestInfo ri) throws ForwardRequest {}
}
