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

final class DynEnum_impl extends DynAny_impl implements
        org.omg.DynamicAny.DynEnum {
    private int value_;

    DynEnum_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);
        value_ = 0;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void assign(org.omg.DynamicAny.DynAny dyn_any)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return;

        if (!dyn_any.type().equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        org.omg.DynamicAny.DynEnum dyn_enum = (org.omg.DynamicAny.DynEnum) dyn_any;
        value_ = dyn_enum.get_as_ulong();

        notifyParent();
    }

    public synchronized void from_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        Any val = null;
        try {
            val = (Any) value;
        } catch (ClassCastException ex) {
            val = new Any(value);
        }

        if (val.value() == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        if (!val._OB_type().equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        value_ = ((Integer) val.value()).intValue();

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return new Any(orbInstance_, type_, new Integer(value_));
    }

    public synchronized org.omg.CORBA.Any to_any(DynValueWriter dynValueWriter) {
        return to_any();
    }

    public synchronized boolean equal(org.omg.DynamicAny.DynAny dyn_any) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return true;

        if (!dyn_any.type().equivalent(type_))
            return false;

        org.omg.DynamicAny.DynEnum dyn_enum = (org.omg.DynamicAny.DynEnum) dyn_any;
        return (value_ == dyn_enum.get_as_ulong());
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynEnum_impl result = new DynEnum_impl(factory_, orbInstance_, type_);
        result.value_ = value_;
        return result;
    }

    public boolean seek(int index) {
        return false;
    }

    public void rewind() {
        // do nothing
    }

    public boolean next() {
        return false;
    }

    public int component_count() {
        return 0;
    }

    public org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
    }

    public synchronized String get_as_string() {
        String result = null;

        try {
            result = origType_.member_name(value_);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return result;
    }

    public synchronized void set_as_string(String value)
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        try {
            int count = origType_.member_count();
            for (int i = 0; i < count; i++) {
                if (value.equals(origType_.member_name(i))) {
                    value_ = i;
                    notifyParent();
                    return;
                }
            }

            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    public synchronized int get_as_ulong() {
        return value_;
    }

    public synchronized void set_as_ulong(int value)
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        try {
            if (value < 0 || value >= origType_.member_count())
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

            value_ = value;

            notifyParent();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        out.write_ulong(value_);
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        _OB_marshal(out);
    }

    synchronized void _OB_unmarshal(InputStream in) {
        value_ = in.read_ulong();

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return null;
    }

    synchronized Any _OB_currentAnyValue() {
        return null;
    }
}
