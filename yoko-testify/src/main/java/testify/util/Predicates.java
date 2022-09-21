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

import java.util.function.Predicate;

public enum Predicates {
    ;

    @SafeVarargs
    public static <T> Predicate<T> allOf(Predicate<T>...predicates) {
        Predicate<T> result = t -> true;
        for (Predicate<T> p: predicates) result = result.and(p);
        return result;
    }

    @SafeVarargs
    public static <T> Predicate<T> anyOf(Predicate<T>...predicates) {
        Predicate<T> result = t -> false;
        for (Predicate<T> p: predicates) result = result.or(p);
        return result;
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) { return t -> ! predicate.test(t); }
}
