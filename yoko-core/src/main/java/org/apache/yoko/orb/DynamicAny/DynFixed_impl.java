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
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

final class DynFixed_impl extends DynAny_impl implements
        org.omg.DynamicAny.DynFixed {
    private java.math.BigDecimal value_;

    DynFixed_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);
        value_ = new java.math.BigDecimal(0);
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

        DynFixed_impl impl = (DynFixed_impl) dyn_any;
        value_ = impl.value_;

        notifyParent();
    }

    public synchronized void from_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        //
        // Convert value to an ORBacus Any - the JDK implementation
        // of TypeCode.equivalent() raises NO_IMPLEMENT
        //
        Any val = null;
        try {
            val = (Any) value;
        } catch (ClassCastException ex) {
            try {
                val = new Any(value);
            } catch (NullPointerException e) {
                throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                    org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(e);
            }
        }

        if (!val._OB_type().equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        try {
            java.math.BigDecimal f = val.extract_fixed();

            if (f == null || f.scale() > origType_.fixed_scale())
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
            value_ = f;
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(ex);
        }

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return new Any(orbInstance_, type_, value_);
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

        DynFixed_impl impl = (DynFixed_impl) dyn_any;
        return value_.equals(impl.value_);
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynFixed_impl result = new DynFixed_impl(factory_, orbInstance_, type_);
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

    public synchronized String get_value() {
        return value_.toString();
    }

    public synchronized boolean set_value(String val)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        String s = val.trim().toLowerCase();
        if (s.endsWith("d"))
            s = s.substring(0, s.length() - 1);
        if (s.length() == 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        java.math.BigDecimal f = null;

        try {
            f = new java.math.BigDecimal(s);
        } catch (NumberFormatException ex) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(ex);
        }

        int origDigits = 0, origScale = 0;
        try {
            origDigits = origType_.fixed_digits();
            origScale = origType_.fixed_scale();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        int fDigits = 0, fScale = f.scale();
        if (fScale > 0)
            fDigits = f.movePointRight(fScale).abs().toString().length();
        else
            fDigits = f.abs().toString().length();

        //
        // Raise InvalidValue if this DynFixed is incapable of
        // representing the value (even with a loss of precision)
        //
        if ((fDigits - fScale) > (origDigits - origScale))
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Return true if there was no loss of precision, otherwise
        // truncate and return false
        //
        boolean result = true;
        if (fScale > origScale) {
            value_ = f.setScale(origScale, java.math.BigDecimal.ROUND_DOWN);
            result = false;
        } else
            value_ = f.setScale(origScale);

        notifyParent();

        return result;
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        try {
            out.write_fixed(value_.movePointRight(origType_.fixed_scale()));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        _OB_marshal(out);
    }

    synchronized void _OB_unmarshal(InputStream in) {
        try {
            value_ = in.read_fixed().movePointLeft(origType_.fixed_scale());
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        notifyParent();
    }

    Any _OB_currentAny() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return null;
    }

    Any _OB_currentAnyValue() {
        return null;
    }
}
