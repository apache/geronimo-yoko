/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.bus;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface MethodRef extends TypeRef<Method> {
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
