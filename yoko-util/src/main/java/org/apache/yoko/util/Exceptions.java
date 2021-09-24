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
package org.apache.yoko.util;

import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Exceptions {
    ;

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    public static <EXC extends Throwable> EXC as(Supplier<EXC> constructor, Throwable cause) {
        EXC exc = constructor.get();
        return withCause(exc, cause);
    }

    public static <EXC extends Throwable, ARG> EXC as(Function<ARG, EXC> constructor, Throwable cause, ARG arg) {
        EXC exc = constructor.apply(arg);
        return withCause(exc, cause);
    }

    public static <EXC extends Throwable, ARG1, ARG2> EXC as(BiFunction<ARG1, ARG2, EXC> constructor, Throwable cause, ARG1 arg1, ARG2 arg2) {
        EXC exc = constructor.apply(arg1, arg2);
        return withCause(exc, cause);
    }

    public static <EXC extends Throwable, ARG1, ARG2, ARG3> EXC as(TriFunction<ARG1, ARG2, ARG3, EXC> constructor, Throwable cause, ARG1 arg1, ARG2 arg2, ARG3 arg3) {
        EXC exc = constructor.apply(arg1, arg2, arg3);
        return withCause(exc, cause);
    }

    private static <EXC extends Throwable> EXC withCause(EXC exc, Throwable cause) {
        if (exc instanceof RemoteException) ((RemoteException)exc).detail = cause;
        else exc.initCause(cause);
        return exc;
    }
}
