/*
 * Copyright 2016 IBM Corporation and others.
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
package org.apache.yoko.rmi.impl;

import org.omg.CORBA.MARSHAL;

import javax.rmi.CORBA.ClassDesc;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

abstract class ClassBaseDescriptor extends ValueDescriptor {

    ClassBaseDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    private volatile Field repidField = null;
    private Field genRepIdField() {
        return findField("repid");
    }
    final Field getRepidField() {
        if (null == repidField) repidField = genRepIdField();
        return repidField;
    }

    private volatile Field cobebaseField = null;
    private Field genCodebaseField() {
        return findField("codebase");
    }
    final Field getCobebaseField() {
        if (null == cobebaseField) cobebaseField = genCodebaseField();
        return cobebaseField;
    }

    private Field findField(final String fieldName) {
        return AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
                try {
                    Field f = ClassDesc.class.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException e) {
                    throw (MARSHAL)new MARSHAL("no such field: " + e).initCause(e);
                }
            }
        });
    }
}
