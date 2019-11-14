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

import java.lang.reflect.Field;

public interface FieldRef extends TypeRef<Field> {
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
