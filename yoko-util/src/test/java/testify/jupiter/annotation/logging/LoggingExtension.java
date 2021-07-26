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
package testify.jupiter.annotation.logging;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public final class LoggingExtension extends TestLogger implements CloseableResource, BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback, TestExecutionExceptionHandler {
    public void beforeAll(ExtensionContext ctx) {
        registerLogHandler();
        this.before(ctx);
        ctx.getStore(Namespace.create(this)).put(this, this); // ensure close() gets called
    }
    public void beforeTestExecution(ExtensionContext ctx) { this.before(ctx); }
    public void afterTestExecution(ExtensionContext ctx) { this.after(ctx); }
    public void afterAll(ExtensionContext ctx) { this.after(ctx); }
    public void handleTestExecutionException(ExtensionContext ctx, Throwable throwable) throws Throwable {
        this.somethingWentWrong(throwable);
        throw throwable; // rethrow or tests won't fail
    }
    public void close() throws Throwable {
        deregisterLogHandler();
    }
}
