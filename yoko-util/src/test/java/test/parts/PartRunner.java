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
package test.parts;

import java.lang.reflect.InvocationTargetException;

public interface PartRunner {
    static String[] NO_STRINGS = {};

    PartRunner fork(String partName, TestPart part);

    default PartRunner forkMain(Class<?> mainClass, String...args) { return fork(mainClass.getName(), wrapMain(mainClass, args)); }

    static TestPart wrapMain(Class<?> mainClass, String[] args) {
        return bus -> {
            try {
                // invoke static void main(String[]) on the provided class
                mainClass.getMethod("main", String[].class).invoke(null, new Object[]{args});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        };
    }

    PartRunner inline(TestPart part);

    default PartRunner inlineMain(Class<?> mainClass, String...args) { return inline(wrapMain(mainClass, args)); }

    void join();
}
