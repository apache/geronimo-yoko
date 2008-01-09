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

final class DynBasic_impl extends DynAny_impl {
    private Any any_;

    //
    // This object needs a component when the type is tk_any
    //
    org.omg.DynamicAny.DynAny comp_;

    DynBasic_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);

        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(type);
        switch (origTC.kind().value()) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
            any_ = new Any(orbInstance_, type, null);
            break;

        case org.omg.CORBA.TCKind._tk_short:
        case org.omg.CORBA.TCKind._tk_ushort:
        case org.omg.CORBA.TCKind._tk_long:
        case org.omg.CORBA.TCKind._tk_ulong:
            any_ = new Any(orbInstance_, type, new Integer(0));
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
            any_ = new Any(orbInstance_, type, new Long(0));
            break;

        case org.omg.CORBA.TCKind._tk_float:
            any_ = new Any(orbInstance_, type, new Float(0));
            break;

        case org.omg.CORBA.TCKind._tk_double:
            any_ = new Any(orbInstance_, type, new Double(0));
            break;

        case org.omg.CORBA.TCKind._tk_boolean:
            any_ = new Any(orbInstance_, type, Boolean.FALSE);
            break;

        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_wchar:
            any_ = new Any(orbInstance_, type, new Character((char) 0));
            break;

        case org.omg.CORBA.TCKind._tk_octet:
            any_ = new Any(orbInstance_, type, new Byte((byte) 0));
            break;

        case org.omg.CORBA.TCKind._tk_any:
            any_ = new Any(orbInstance_, type, new Any(orbInstance_));
            break;

        case org.omg.CORBA.TCKind._tk_TypeCode: {
            org.omg.CORBA.TypeCode nullTC = org.apache.yoko.orb.OB.TypeCodeFactory
                    .createPrimitiveTC(org.omg.CORBA.TCKind.tk_null);
            any_ = new Any(orbInstance_, type, nullTC);
            break;
        }

        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            any_ = new Any(orbInstance_, type, null);
            break;

        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_wstring:
            any_ = new Any(orbInstance_, type, new String(""));
            break;

        case org.omg.CORBA.TCKind._tk_abstract_interface:
            any_ = new Any(orbInstance_, type, null);
            break;

        case org.omg.CORBA.TCKind._tk_value:
            //
            // Only custom valuetypes are supported by DynBasic_impl
            //
            try {
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(origTC.type_modifier() == org.omg.CORBA.VM_CUSTOM.value);
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
            any_ = new Any(orbInstance_, type, null);
            break;

        case org.omg.CORBA.TCKind._tk_Principal:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported type code");
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void notifyParent() {
        //
        // We have intercepted a change notification. If our type is tk_any,
        // then we release our component. It will be created again during
        // get_dyn_any().
        //
        if (origType_.kind() == org.omg.CORBA.TCKind.tk_any) {
            if (comp_ != null)
                comp_ = null;
        }

        super.notifyParent();
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

        any_ = (Any) dyn_any.to_any();

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
        try {
            any_ = new Any(value);
        } catch (NullPointerException ex) {
            throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(ex);
        }

        org.omg.CORBA.TypeCode tc = any_._OB_type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);

        if (!tc.equivalent(type_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        //
        // Check for an invalid value
        //
        if (any_.value() == null) {
            switch (origTC.kind().value()) {
            case org.omg.CORBA.TCKind._tk_null:
            case org.omg.CORBA.TCKind._tk_void:
            case org.omg.CORBA.TCKind._tk_TypeCode:
            case org.omg.CORBA.TCKind._tk_objref:
            case org.omg.CORBA.TCKind._tk_value:
            case org.omg.CORBA.TCKind._tk_abstract_interface:
            case org.omg.CORBA_2_4.TCKind._tk_local_interface:
                //
                // Some types can legally have a null value
                //
                break;

            default:
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
            }
        }

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return new Any(any_);
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

        //
        // Don't use Any.equal() here because it calls equal() on
        // the TypeCodes, and the semantics of DynAny.equal()
        // only require TypeCodes to be equivalent.
        //

        java.lang.Object v1 = any_.value();
        java.lang.Object v2 = ((DynBasic_impl) dyn_any).any_.value();

        switch (origType_.kind().value()) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
            return true;

        case org.omg.CORBA.TCKind._tk_short:
        case org.omg.CORBA.TCKind._tk_ushort:
        case org.omg.CORBA.TCKind._tk_long:
        case org.omg.CORBA.TCKind._tk_ulong:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_float:
        case org.omg.CORBA.TCKind._tk_double:
        case org.omg.CORBA.TCKind._tk_boolean:
        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_octet:
        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_wstring:
            return v1.equals(v2);

        case org.omg.CORBA.TCKind._tk_any:
            return ((org.omg.CORBA.Any) v1).equal((org.omg.CORBA.Any) v2);

        case org.omg.CORBA.TCKind._tk_TypeCode:
            if (v1 == null && v2 == null)
                return true;
            else if (v1 == null || v2 == null)
                return false;
            else
                return ((org.omg.CORBA.TypeCode) v1)
                        .equal((org.omg.CORBA.TypeCode) v2);

        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            if (v1 == null && v2 == null)
                return true;
            else if (v1 == null || v2 == null)
                return false;
            else
                return ((org.omg.CORBA.Object) v1)
                        ._is_equivalent((org.omg.CORBA.Object) v2);

        case org.omg.CORBA.TCKind._tk_value:
            if (v1 == v2)
                return true;
            else if (v1 == null || v2 == null)
                return false;
            else {
                //
                // Currently, it's not possible for a custom valuetype
                // to be represented as an InputStream in an any
                //
                org.apache.yoko.orb.OB.Assert._OB_assert("Unable to compare value types");
            }

        case org.omg.CORBA.TCKind._tk_abstract_interface:
            if (v1 == v2)
                return true;
            else if (v1 == null || v2 == null)
                return false;
            else if (v1 instanceof org.omg.CORBA.Object
                    && v2 instanceof org.omg.CORBA.Object) {
                return ((org.omg.CORBA.Object) v1)
                        ._is_equivalent((org.omg.CORBA.Object) v2);
            } else {
                //
                // Currently, it's not possible for an abstract interface
                // to be represented as an InputStream in an any
                //
                org.apache.yoko.orb.OB.Assert._OB_assert("Unable to compare abstract_interface types");
            }
            return false;

        case org.omg.CORBA.TCKind._tk_Principal:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported type code");
        }

        return false; // The compiler needs this
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynBasic_impl result = new DynBasic_impl(factory_, orbInstance_, type_);
        result.any_ = new Any(any_);
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

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        any_.write_value(out);
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        _OB_marshal(out);
    }

    synchronized void _OB_unmarshal(InputStream in) {
        any_.read_value(in, type_);

        if (comp_ != null)
            comp_ = null;

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return any_;
    }

    synchronized Any _OB_currentAnyValue() {
        return any_;
    }

    synchronized org.omg.DynamicAny.DynAny _OB_getDynAny() {
        if (origType_.kind() != org.omg.CORBA.TCKind.tk_any)
            return null;

        if (comp_ == null)
            comp_ = create((Any) any_.value(), true);

        return comp_;
    }

    synchronized boolean _OB_insertDynAny(org.omg.DynamicAny.DynAny p) {
        //
        // Do nothing if caller is passing our component
        //
        if (p == comp_)
            return true;

        try {
            checkValue(any_, org.omg.CORBA.TCKind.tk_any);

            DynAny_impl impl = (DynAny_impl) p;
            Any implAny = impl._OB_currentAny();
            checkValue(implAny, org.omg.CORBA.TCKind.tk_any);
            Any any = (Any) implAny.value();

            any_.replace(any_.type(), new Any(any));

            if (comp_ != null)
                comp_ = null;

            return true;
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
        }

        return false;
    }
}
