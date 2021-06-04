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
package org.apache.yoko;

import org.junit.jupiter.api.Test;
import test.types.TestAny;
import test.types.TestDynAny;
import test.types.TestPortableTypes;
import test.types.TestTypeCode;
import test.types.TestUnion;
import testify.bus.LogLevel;
import testify.jupiter.annotation.ConfigurePartRunner;
import testify.jupiter.annotation.Tracing;
import testify.parts.PartRunner;

@ConfigurePartRunner
@Tracing
class TypesTest {
    @Test
    void testTypeCode(PartRunner runner) { runner.forkMain(TestTypeCode.class); }
    @Test
    void testAny(PartRunner runner) { runner.forkMain(TestAny.class); }
    @Test
    void testDynAny(PartRunner runner) { runner.forkMain(TestDynAny.class); }
    @Test
    void testPortableTypes(PartRunner runner) { runner.forkMain(TestPortableTypes.class); }
    @Test
    void testUnion(PartRunner runner) { runner.forkMain(TestUnion.class); }
}
