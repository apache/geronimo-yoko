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
package testify.bus;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface MethodSpec extends TypeSpec<Method> {
    @Override
    default String stringify(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName() + "#"
                + Stream.of(method.getParameterTypes())
                .map(Class::getName)
                .collect(joining(","));
    }

    @Override
    default Method unstringify(String s) {
        String[] parts = s.split("#");
        Class<?> declaringClass = findClass(parts[0]);
        String methodName = parts[1];
        try {
            if (parts.length == 2) return declaringClass.getDeclaredMethod(methodName);
            Class[] parameterTypes = Stream.of(parts[2].split(","))
                    .map(this::findClass)
                    .collect(toList())
                    .toArray(new Class[0]);
            return declaringClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find the method I put down just a moment ago", e);
        }
    }

    default Class<?> findClass(String type) {
        switch (type) {
        case "void": return void.class;
        case "boolean": return boolean.class;
        case "byte": return byte.class;
        case "short": return short.class;
        case "int": return int.class;
        case "long": return long.class;
        case "float": return float.class;
        case "double": return double.class;
        case "char": return char.class;
        }
        try {
            return  Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw (Error)new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }
}
