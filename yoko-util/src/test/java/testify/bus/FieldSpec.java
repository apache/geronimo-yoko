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

import java.lang.reflect.Field;

public interface FieldSpec extends TypeSpec<Field> {
    @Override
    default String stringify(Field field) {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }

    @Override
    default Field unstringify(String s) {
        String[] parts = s.split("#");
        Class<?> declaringClass = findClass(parts[0]);
        String fieldName = parts[1];
        try {
            return declaringClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot find the field I put down just a moment ago", e);
        }
    }

    default Class<?> findClass(String type) {
        try {
            return  Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw (Error)new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }

}
