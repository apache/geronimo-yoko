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
package test.util.parts;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import test.util.BaseParameterResolver;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PartRunnerResolver extends BaseParameterResolver<PartRunner> {
    public static final class Builder extends BaseBuilder<Builder> {
        private boolean processes = false;
        public Builder useThreads() { assertFalse(processes); return this; }
        public Builder useProcesses() { assertFalse(processes); processes = true; return this; }
        public PartRunnerResolver build() { return new PartRunnerResolver(processes, scope()); }
    }

    public static Builder builder() { return new Builder(); }

    private final boolean processes;

    PartRunnerResolver(boolean processes, Scope scope) {
        super(PartRunner.class, scope);
        this.processes = processes;
    }

    PartRunnerResolver() { this(false, Scope.AUTO); }

    @Override
    protected PartRunner create(ParameterContext pCtx, ExtensionContext eCtx) { return processes ? new ProcessRunner() : new ThreadRunner(); }

    @Override
    protected void destroy(PartRunner partRunner) { partRunner.join(); }
}
