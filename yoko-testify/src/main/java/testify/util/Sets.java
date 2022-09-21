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
package testify.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public enum Sets {
    ;
    @SafeVarargs
    public static <T> Set<T> of(T... elems) { return new HashSet<>(Arrays.asList(elems)); }
    @SafeVarargs
    public static <T> Set<T> unmodifiableOf(T... elems) { return unmodifiableSet(new HashSet<>(Arrays.asList(elems))); }

    public static <T> Set<T> union(Set<T> left, Set<T> right) {
        HashSet<T> result = new HashSet<>(left);
        result.addAll(right);
        return result;
    }

    public static <T> Set<T> intersection(Set<T> left, Set<T> right) {
        HashSet<T> result = new HashSet<>(left);
        result.retainAll(right);
        return result;
    }

    public static <T> Set<T> difference(Set<T> minuend, Set<T> subtrahend) {
        HashSet<T> result = new HashSet<>(minuend);
        result.removeAll(subtrahend);
        return result;
    }

    public static String format(Set<Field> fields) {
        String result = "{";
        for (Field field : fields)
            result += (result.length() == 1 ? "" : ",") + field.getName();
        return result + "}";
    }

}
