/*
 * =============================================================================
 * Copyright (c) 2020 IBM Corporation and others.
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

import java.util.stream.Stream;

public interface EnumRef<E extends Enum<E>> extends TypeRef<E> {
    @Override
    default String stringify(E e) {
        return e.getDeclaringClass().getName() + "#" + e.name();
    }

    @Override
    default E unstringify(String s) {
        String[] parts = s.split("#");
        Class<E> declaringClass = (Class<E>)findClass(parts[0]);
        String memberName = parts[1];
        return Stream.of(declaringClass.getEnumConstants())
                .filter(mem -> mem.name().equals(memberName))
                .findFirst()
                .orElseThrow(Error::new);
    }

    default Class<?> findClass(String type) {
        try {
            return  Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw (Error)new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }
}
