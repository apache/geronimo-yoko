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

package org.apache.yoko.orb.DynamicAny;

import org.apache.yoko.orb.CORBA.Any;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.CORBA.TypeCode;

final class DynArray_impl extends DynSeqBase_impl implements
        org.omg.DynamicAny.DynArray {
    DynArray_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);
    }

    DynArray_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type, DynValueReader dynValueReader) {
        super(factory, orbInstance, type, dynValueReader);
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized org.omg.CORBA.Any[] get_elements() {
        return getElements();
    }

    public synchronized void set_elements(org.omg.CORBA.Any[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        for (int i = 0; i < value.length; i++) {
            org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(value[i]
                    .type());
            if (origTC.kind() != contentKind_)
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
        }

        if (value.length != length_)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        for (int i = 0; i < value.length; i++)
            setValue(i, value[i]);

        index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.DynamicAny.DynAny[] get_elements_as_dyn_any() {
        return getElementsAsDynAny();
    }

    public synchronized void set_elements_as_dyn_any(
            org.omg.DynamicAny.DynAny[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        for (int i = 0; i < value.length; i++) {
            org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(value[i]
                    .type());
            if (origTC.kind() != contentKind_)
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
        }

        if (value.length != length_)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        for (int i = 0; i < value.length; i++)
            setValue(i, value[i]);

        index_ = 0;

        notifyParent();
    }
}
