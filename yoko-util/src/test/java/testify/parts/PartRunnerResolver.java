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
package testify.parts;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import testify.util.BaseParameterResolver;

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
