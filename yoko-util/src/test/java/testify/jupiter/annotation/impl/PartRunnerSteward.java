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
package testify.jupiter.annotation.impl;

import org.junit.jupiter.api.extension.ExtensionContext;
import testify.jupiter.annotation.ConfigurePartRunner;
import testify.jupiter.annotation.Summoner;
import testify.parts.PartRunner;

public class PartRunnerSteward implements ExtensionContext.Store.CloseableResource {
    private static final Summoner<ConfigurePartRunner, PartRunnerSteward> SUMMONER = Summoner.forAnnotation(ConfigurePartRunner.class, PartRunnerSteward.class, PartRunnerSteward::new);
    private final PartRunner partRunner;
    private final ConfigurePartRunner config;
    private final Class<?> testClass;

    private PartRunnerSteward(ConfigurePartRunner config, ExtensionContext context) {
        this.config = config;
        this.testClass = context.getRequiredTestClass();
        this.partRunner = PartRunner.create();
        TracingSteward.addTraceSettings(partRunner, testClass);
    }

    // A CloseableResource stored in a context store is closed automatically when the context goes out of scope.
    // Note this happens *before* the correlated extension callback points (e.g. AfterEachCallback/AfterAllCallback)
    public void close() {
        partRunner.join();
    }

    public static PartRunner getPartRunner(ExtensionContext ctx) {
        // PartRunners are always one per test, so get one for the root context
        return SUMMONER.forContext(ctx).requestSteward().orElseThrow(Error::new).partRunner;
    }
}
