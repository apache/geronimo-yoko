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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import testify.bus.Bus;

import java.util.stream.Stream;

class MultiServerExtension extends DelegatingExtension<Class<?>, MultiServerAdjunct> implements BeforeAllCallback, ArgumentsProvider, ParameterResolver {
    MultiServerExtension() { super(ExtensionContext::getRequiredTestClass, MultiServerAdjunct::create); }

    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception { getDelegate(ctx).startServers(); }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext ctx) throws Exception {
        return getDelegate(ctx).getBuses().map(Arguments::of);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == Bus.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx) throws ParameterResolutionException {
        if (ctx.getTestClass().isPresent())
            throw new ParameterResolutionException("Bus parameter requested by " + ctx.getDisplayName() +
                    " cannot be resolved. Either use exactly one @" + Server.class.getSimpleName() +
                    " annotation or annotate test methods with @" + TestPerServer.class.getSimpleName() + ".");
        return getDelegate(ctx).partRunner.bus();
    }
}
