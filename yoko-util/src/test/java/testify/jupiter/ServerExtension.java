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
package testify.jupiter;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.omg.CORBA.ORB;
import testify.bus.Bus;

class ServerExtension extends DelegatingExtension<Class<?>, ServerAdjunct> implements BeforeAllCallback, ParameterResolver {
    ServerExtension() { super(ExtensionContext::getRequiredTestClass, ServerAdjunct::create); }

    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception { getDelegate(ctx).startServer(); }

    @Override
    public boolean supportsParameter(ParameterContext pCtx, ExtensionContext ctx) {
        return pCtx.getParameter().getType() == Bus.class;
    }
    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx)  {
        return getDelegate(ctx).getBus();
    }
}
