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

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import testify.jupiter.annotation.Summoner;

import java.util.List;
import java.util.Optional;

class LoggingExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Summoner<List<Logging>, TestLogger> SUMMONER = Summoner.forRepeatableAnnotation(Logging.class, TestLogger.class, TestLogger::new);
    @Override
    public void beforeAll(ExtensionContext ctx) {
        getTestLogger(ctx);
    }

    @Override
    public void beforeTestExecution(ExtensionContext ctx) {
        getTestLogger(ctx).ifPresent(o -> o.beforeTestExecution(ctx));
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) {
        getTestLogger(ctx).ifPresent(o -> o.afterTestExecution(ctx));
    }

    static Optional<TestLogger> getTestLogger(ExtensionContext ctx) {
        return SUMMONER.forContext(ctx).summon();
    }
}
