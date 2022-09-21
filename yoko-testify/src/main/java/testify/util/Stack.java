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

import java.util.Arrays;

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
        return getStackTraceElement(depth + 1).toString();
    }

    public static StackTraceElement getStackTraceElement(int depth) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        int i = 0;
        try {
            while (!"getStackTraceElement".equals(stack[i].getMethodName())) i++; // fast forward to this method
            i++; // now at the method that called this one - i.e. depth 0
            i += depth;
            return stack[i];
        } catch (IndexOutOfBoundsException e) {
            throw new Error("Stack not deep enough to find caller#" + depth + ": " + Arrays.toString(stack));
        }
    }

    public static String getCallingFrame(Class<?> callingClass) {
        return getStackTraceElement(callingClass).toString();
    }

    public static StackTraceElement getStackTraceElement(Class<?> callingClass) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        int i = 0;
        try {
            String className = callingClass.getName();
            while (!className.equals(stack[i].getClassName())) i++; // fast forward to the first method from the mentioned class
            return stack[i];
        } catch (IndexOutOfBoundsException e) {
            throw new Error("Could not find caller matching class " + callingClass.getName() + " in stack " + Arrays.toString(stack));
        }
    }
}
