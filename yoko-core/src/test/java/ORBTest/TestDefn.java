/*
 * Copyright 2010 IBM Corporation and others.
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
package ORBTest;

public class TestDefn {
    private String m_description;

    private TestObject m_test_object;

    public TestDefn(String description, TestObject test_object) {
        m_description = description;
        m_test_object = test_object;
    }

    public String description() {
        return m_description;
    }

    public TestObject test_object() {
        return m_test_object;
    }
}
