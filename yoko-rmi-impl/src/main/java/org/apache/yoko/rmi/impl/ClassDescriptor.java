/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.ClassDesc;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import org.omg.CORBA.MARSHAL;

class ClassDescriptor extends ClassBaseDescriptor {
    private static final Logger logger = Logger.getLogger(ClassDescriptor.class.getName());

    ClassDescriptor(TypeRepository repository) {
        super(Class.class, repository);
    }

    @Override
    Object copyObject(Object orig, CopyState state) {
        state.put(orig, orig);
        return orig;
    }

    /** Write an instance of this value to a CDR stream */
    @Override
    public Serializable writeReplace(final Serializable value) {
        final Class<?> type = (Class<?>) value;

        final ClassDesc result = AccessController
                .doPrivileged(new PrivilegedAction<ClassDesc>() {
                    public ClassDesc run() {
                        try {
                            final ClassDesc desc = new ClassDesc();

                            ValueHandler handler = Util.createValueHandler();
                            String repId = handler.getRMIRepositoryID(type);
                            getRepidField().set(desc, repId);

                            String codebase = Util.getCodebase(type);
                            getCobebaseField().set(desc, codebase);

                            return desc;

                        } catch (IllegalAccessException ex) {
                            throw (MARSHAL)new MARSHAL("no such field: " + ex).initCause(ex);
                        }
                    }
                });

        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("writeReplace %s => %s", value, result));

        return result;
    }
}
