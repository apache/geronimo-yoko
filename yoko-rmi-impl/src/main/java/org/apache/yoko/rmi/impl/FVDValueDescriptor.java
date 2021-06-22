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
package org.apache.yoko.rmi.impl;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.ValueMember;

import java.util.Optional;

import static java.util.logging.Level.FINER;
import static org.apache.yoko.logging.VerboseLogging.MARSHAL_LOG;

final class FVDValueDescriptor extends ValueDescriptor {
    final FullValueDescription fvd;
    final String repid;

    FVDValueDescriptor(FullValueDescription fvd, Class<?> clazz,
            TypeRepository rep, String repid, ValueDescriptor super_desc) {
        super(clazz, rep);

        this.repid = repid;
        this.fvd = fvd;

        init();

        this._super_descriptor = super_desc;
    }

    public void init() {
        super.init();

        // don't override custom loading. Our local version could work differently.
//        if (!fvd.is_custom) {
//            _read_object_method = null;
//            _write_object_method = null;
//            _is_externalizable = false;
//        }

        if (MARSHAL_LOG.isLoggable(FINER)) MARSHAL_LOG.finer("Computing field descriptors for " + fvd.name + " version " + fvd.version);
        ValueMember[] members = fvd.members;
        FieldDescriptor[] new_fields = new FieldDescriptor[members.length];
        for (int i = 0; i < members.length; i++) {
            ValueMember vm = members[i];
            FieldDescriptor fd = findField(vm);
            new_fields[i] = fd;
            if (MARSHAL_LOG.isLoggable(FINER)) MARSHAL_LOG.finer(String.format("\t%s -> %s", describe(vm), describe(fd)));
        }
        _fields = new_fields;
    }

    private static String describe(FieldDescriptor fd) {
        return fd == null ? null : String.format("FieldDescriptor[%s in %s]", fd.java_name, Optional.of(fd.declaringClass).map(Class::getName).orElse(""));
    }

    private static String describe(ValueMember vm) {
        return vm == null ? null : String.format("ValueMember[name=\"%s\", id=\"%s\"]", vm.name, vm.id);
    }

    private FieldDescriptor findField(ValueMember valueMember) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            TypeDescriptor td = repo.getDescriptor(c);
            if (td instanceof ValueDescriptor) {
                ValueDescriptor vd = (ValueDescriptor) td;
                FieldDescriptor[] fds = vd._fields;

                if (fds == null) {
                    continue;
                }

                for (FieldDescriptor fd : fds) {
                    if (fd.getIDLName().equals(valueMember.name)) return fd;
                }
            }
        }
        // There was no matching field in the local implementation so look up a remote field descriptor.

        String repId =  valueMember.id;


        return null;
    }

    @Override
    protected String genRepId() {
        return repid;
    }

    @Override
    org.omg.CORBA.ValueDefPackage.FullValueDescription getFullValueDescription() {
        return fvd;
    }

    @Override
    protected final TypeCode genTypeCode() {
        return fvd.type;
    }

    @Override
    public boolean isCustomMarshalled() {
        return fvd.is_custom;
    }
}
