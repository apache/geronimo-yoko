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
package testify.jupiter.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import testify.jupiter.annotation.impl.SimpleParameterResolver;
import testify.parts.PartRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static testify.jupiter.annotation.impl.PartRunnerSteward.getPartRunner;

@ExtendWith(PartRunnerExtension.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigurePartRunner {}

class PartRunnerExtension implements SimpleParameterResolver<PartRunner> {
    @Override
    public boolean supportsParameter(ParameterContext ctx)  { return ctx.getParameter().getType() == PartRunner.class; }
    @Override
    // get the configured PartRunner for the context,
    // but if the context has a test method, use its parent instead
    // i.e. get an ORB for the test class, not for each test method
    public PartRunner resolveParameter(ExtensionContext ctx) { return getPartRunner(ctx.getTestMethod().flatMap(m -> ctx.getParent()).orElse(ctx)); }
}
