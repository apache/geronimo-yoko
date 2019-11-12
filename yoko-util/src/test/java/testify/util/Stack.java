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

import testify.streams.BiStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public enum Stack {
    ;
    private static class Callers extends SecurityManager {
        private static final Callers INSTANCE = new Callers();

        private static Class<?>[] get() {
            return INSTANCE.getClassContext();
        }
    }

    public static Class<?> getCallingClassOutsidePackage(Package pkg) {
        final Class<?>[] stack = Callers.get();
        int i = 0;
        try {
            while (stack[i].getPackage() != pkg) i++; // fast forward to the expected package
            while (stack[i].getPackage() == pkg) i++; // fast forward to the entry below the package
            return stack[i];
        } catch (IndexOutOfBoundsException e) {
            throw new Error("Could not find a caller in the stack: " + Arrays.toString(stack));
        }
    }

    public static String getCallingFrame(int depth) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        int i = 0;
        try {
            while (!"getCallingFrame".equals(stack[i].getMethodName())) i++; // fast forward to this method
            i++; // now at the method that called this one - i.e. depth 0
            i += depth;
            return stack[i].toString();
        } catch (IndexOutOfBoundsException e) {
            throw new Error("Stack not deep enough to find caller#" + depth + ": " + Arrays.toString(stack));
        }
    }

    public static String getCallingFrame(Class<?> callingClass) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        int i = 0;
        try {
            String className = callingClass.getName();
            while (!className.equals(stack[i].getClassName())) i++; // fast forward to the first method from the mentioned class
            return stack[i].toString();
        } catch (IndexOutOfBoundsException e) {
            throw new Error("Could not find caller matching class " + callingClass.getName() + " in stack " + Arrays.toString(stack));
        }
    }

    /**
     * For the provided elements, find the one that matches the most recent calling method name.
     * The <code>Objects.toString()</code> of each element will be compared to the names of the methods in the call stack.
     * @param elems
     */
    @SafeVarargs
    public static <T> T matchByCallingMethod(T... elems) {
        final HashMap<String, T> elemMap = BiStream.ofValues(Objects::toString, elems).collect(HashMap::new, map -> map::put);
        return Stream.of(new Throwable().getStackTrace())
                .map(StackTraceElement::getMethodName)
                .map(elemMap::get)
                .findFirst()
                .orElseThrow(NoSuchMethodError::new);
    }

}
