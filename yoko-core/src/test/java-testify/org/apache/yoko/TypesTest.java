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
package org.apache.yoko;

import org.junit.jupiter.api.Test;
import test.types.TestAny;
import test.types.TestDynAny;
import test.types.TestPortableTypes;
import test.types.TestTypeCode;
import test.types.TestUnion;
import testify.annotation.ConfigurePartRunner;
import testify.annotation.TraceTestify;
import testify.parts.PartRunner;

@ConfigurePartRunner
@TraceTestify
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
