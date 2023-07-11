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
package testify.bus.key;

import testify.bus.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A specialised type spec that handles lists of strings.
 */
public interface StringListSpec extends TypeSpec<List<String>> {
    default String stringify(List<String> list) { return toString(list); }
    default List<String> unstringify(String s) { return toList(s); }

    static String toString(List<String> list) {
        // write out each length in square brackets followed by the string
        return list.stream()
                .map(s -> toLengthAndString(s))
                .collect(Collectors.joining());
    }

    static String toLengthAndString(String s) {
        return s == null ? "[]" : String.format("[%d]%s", s.length(), s);
    }

    static List<String> toList(String s) {
        List<String> result = new ArrayList<>();
        while (s.length() > 0) {
            assertThat(s, startsWith("["));
            s = s.substring(1);
            // read the length
            int len = s.indexOf(']');
            if (0 == len) {
                result.add(null);
                s = s.substring(1);
                continue;
            }
            assert len > 0;
            String lenStr = s.substring(0, len);
            s = s.substring(len + 1);
            len = Integer.parseInt(lenStr);
            // read the string element
            String elem = s.substring(0, len);
            result.add(elem);
            s = s.substring(len);
        }
        return result;
    }
}
