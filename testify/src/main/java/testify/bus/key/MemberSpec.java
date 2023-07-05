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
import testify.util.Assertions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static testify.util.Assertions.failf;

/**
 * A specialised type spec that handles {@link Member} objects.
 */
public interface MemberSpec extends TypeSpec<Member> {
    static Class<?> getMemberEvaluationType(Member m) {
        if (m instanceof Field) return ((Field) m).getType();
        if (m instanceof Method) return ((Method) m).getReturnType();
        throw failf("Member not a field or method: " + m);
    }

    @Override
    default String stringify(Member member) {
        return memberToString(member);
    }

    @Override
    default Member unstringify(final String s) {
        return stringToMember(s);
    }

    static Member stringToMember(String s) {
        String[] parts = s.split("[#(,)]");
        Class<?> declaringClass = findClass(parts[0]);
        String remainder = parts[1];

        // Fields don't have any parentheses
        if (!s.endsWith(")")) {
            assert parts.length == 2;
            String fieldName = parts[1];
            try {
                return declaringClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Cannot find the field I put down just a moment ago", e);
            }
        }

        assert parts.length >= 2;

        // Constructors are called "<init>"
        if (parts[1].equals("<init>")) {
            Class<?>[] parameterTypes = Stream.of(parts)
                    .skip(2) // ignore class name and method name
                    .map(MemberSpec::findClass)
                    .toArray(Class[]::new);
            try {
                return declaringClass.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot find the constructor I put down just a moment ago", e);
            }
        }

        // Methods are the only other possibility
        String methodName = parts[1];
        Class<?>[] parameterTypes = Stream.of(parts)
                .skip(2) // ignore class name and method name
                .map(MemberSpec::findClass)
                .toArray(Class[]::new);
        try {
            return declaringClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find the method I put down just a moment ago", e);
        }
    }

    static String memberToString(Member member) {
        // Expect a field, a method, or a constructor
        if (member instanceof Field) {
            return member.getDeclaringClass().getName() + "#" + member.getName();
        }
        if (member instanceof Method) {
            Method m = (Method) member;
            return String.format("%s#%s(%s)",
                    m.getDeclaringClass().getName(),
                    m.getName(),
                    Stream.of(m.getParameterTypes()).map(Class::getName).collect(joining(",")));
        }
        if (member instanceof Constructor) {
            Constructor c = (Constructor) member;
            return String.format("%s#<init>(%s)",
                    c.getDeclaringClass().getName(),
                    Stream.of(c.getParameterTypes()).map(Class::getName).collect(joining(",")));
        }
        throw Assertions.failf("Unknown member type %s", member.getClass());
    }

    static Class<?> findClass(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }


}
