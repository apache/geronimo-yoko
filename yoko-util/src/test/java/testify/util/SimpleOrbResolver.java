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
package testify.util;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.omg.CORBA.ORB;

import java.util.Properties;

public class SimpleOrbResolver extends BaseParameterResolver<ORB> {
    public static final class Builder extends BaseBuilder<Builder> {
        public SimpleOrbResolver build() { return new SimpleOrbResolver(scope()); }
    }

    public static Builder builder() { return new Builder(); }

    private SimpleOrbResolver(Scope scope) { super(ORB.class, scope); }

    @Override
    protected ORB create(ParameterContext pCtx, ExtensionContext eCtx) {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        return ORB.init((String[]) null, props);
    }

    @Override
    protected void destroy(ORB orb) {
        orb.destroy();
    }
}
