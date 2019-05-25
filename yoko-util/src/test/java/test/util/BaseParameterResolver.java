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

import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseParameterResolver<T> implements ParameterResolver, AfterEachCallback, AfterAllCallback {
    private static final Object UNIQUE_KEY = new Object();
    protected enum Scope {
        AUTO {
            Object getKeyInternal(ExtensionContext ctx) { return ctx.getTestInstance(); }
        }, PER_TEST {
            Object getKeyInternal(ExtensionContext ctx) { return ctx.getRequiredTestInstance(); }
        }, PER_CONTAINER {
            Object getKeyInternal(ExtensionContext ctx) { return ctx.getRequiredTestClass(); }
        };
        abstract Object getKeyInternal(ExtensionContext ctx);
        Object getKey(ExtensionContext ctx) {
            try { return getKeyInternal(ctx); }
            catch (Exception e) { throw new AssertionFailedError("Scope " + this + " does not work in the current context"); }
        }

    }

    public static abstract class BaseBuilder<B extends BaseBuilder<B, E>, E extends BaseParameterResolver> {
        private Scope scope = Scope.AUTO;
        protected Scope scope() { return scope; }
        public B perTest() { assertEquals(scope, Scope.AUTO); scope = Scope.PER_TEST; return (B)this; }
        public B perContainer() { assertEquals(scope, Scope.AUTO); scope = Scope.PER_CONTAINER; return (B)this; }
        public abstract E build();
    }

    private final Class<T> type;
    private final Namespace nameSpace;
    private final Scope scope;

    protected BaseParameterResolver(Class<T> type, Scope scope) {
        this.type = type;
        this.nameSpace = Namespace.create(UNIQUE_KEY, type);
        this.scope = scope;
    }

    protected BaseParameterResolver(Class<T> type) { this(type, Scope.AUTO); }

        @Override
    public final boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == type;
    }

    @Override
    public final Object resolveParameter(ParameterContext pCtx, ExtensionContext eCtx) {
        System.out.println("Scope: " + scope);
        final Object key = scope.getKey(eCtx);
        System.out.println("key: " + key);
        final Store store = eCtx.getStore(nameSpace);
        return store.getOrComputeIfAbsent(key, k -> create(pCtx, eCtx));
    }

    @Override
    public final void afterEach(ExtensionContext context) {
        destroy(context, Scope.AUTO);
        destroy(context, Scope.PER_TEST);
    }

    @Override
    public final void afterAll(ExtensionContext context) {
        destroy(context, Scope.AUTO);
        destroy(context, Scope.PER_CONTAINER);
    }

    private void destroy(ExtensionContext context, Scope scope) {
        final Object key = scope.getKey(context);
        final Store store = context.getStore(nameSpace);
        Optional.ofNullable(store.remove(key)).map(type::cast).ifPresent(this::destroy);
    }

    protected abstract T create(ParameterContext pCtx, ExtensionContext eCtx);
    protected abstract void destroy(T t);
}
