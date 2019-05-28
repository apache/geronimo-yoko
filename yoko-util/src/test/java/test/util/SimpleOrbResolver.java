/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package test.util;

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
