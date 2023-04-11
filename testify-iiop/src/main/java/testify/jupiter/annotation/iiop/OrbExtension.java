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
package testify.jupiter.annotation.iiop;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

import static testify.jupiter.annotation.iiop.OrbSteward.getOrb;

/**
 * Must be registered using the {@link ExtendWith} annotation
 */
class OrbExtension implements Extension, BeforeAllCallback, ParameterResolver {
    @Override
    // to ensure the ORB is created only once per test class, create the steward here
    public void beforeAll(ExtensionContext ctx) {
        getOrb(ctx);
    }

    @Override
    public boolean supportsParameter(ParameterContext pCtx, ExtensionContext ctx) {
        Class<?> type = pCtx.getParameter().getType();
        if (type == ORB.class) return true;
        if (type == POA.class) return true;
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext pCtx, ExtensionContext ctx) throws ParameterResolutionException {
        Class<?> type = pCtx.getParameter().getType();
        if (type == ORB.class) return getOrb(ctx);
        if (type == POA.class) return OrbSteward.getActivatedRootPoa(getOrb(ctx));

        throw new ParameterResolutionException("Unknown parameter type: " + type);
    }
}
