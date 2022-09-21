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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate code names (e.g. for threads) that are easy to distinguish
 * and that are named deterministically in encounter order.
 */
class CodeNaming<T> {
    final Map<T, String> names = new HashMap<>();
    int index = 0;
    char[] chars = new char[3];

    private String getNextName(T t) {
        //noinspection SpellCheckingInspection
        final String distinctChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        try {
            Arrays.fill(chars, distinctChars.charAt(index));
            return new String(chars);
        } finally {
            index++;
            if (index == distinctChars.length()) {
                index = 0;
                chars = new char[chars.length + 1];
            }
        }
    }

    synchronized String get(T t) {
        // Synchronize to make sure some getNextName() results aren't discarded
        return names.computeIfAbsent(t, this::getNextName);
    }
}
