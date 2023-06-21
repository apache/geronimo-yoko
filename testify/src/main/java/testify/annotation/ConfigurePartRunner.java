/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package testify.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import testify.annotation.impl.SimpleParameterResolver;
import testify.parts.PartRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static testify.annotation.impl.PartRunnerSteward.getPartRunner;

@ExtendWith(PartRunnerExtension.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigurePartRunner {}

class PartRunnerExtension implements SimpleParameterResolver<PartRunner> {
    @Override
    public Class<PartRunner> getSupportedParameterType() { return PartRunner.class; }

    @Override
    // get the configured PartRunner for the context,
    // but if the context has a test method, use its parent instead
    // i.e. get an ORB for the test class, not for each test method
    public PartRunner resolveParameter(ExtensionContext ctx) { return getPartRunner(ctx.getTestMethod().flatMap(m -> ctx.getParent()).orElse(ctx)); }
}
