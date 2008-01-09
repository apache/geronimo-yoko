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

final class DynValueBox_impl extends DynValueCommon_impl implements
        org.omg.DynamicAny.DynValueBox {
    private org.omg.DynamicAny.DynAny component_;

    private org.omg.CORBA.TypeCode boxedType_;

    private int index_;

    DynValueBox_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);

        try {
            boxedType_ = origType_.content_type();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        //
        // Initial value is null
        //
        set_to_null();
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void createComponents() {
        if (component_ == null) {
            component_ = create(boxedType_, true);
            index_ = 0;
        }
    }

    protected void destroyComponents() {
        if (component_ != null) {
            component_.destroy();
            component_ = null;
        }

        index_ = -1;
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

        DynValueBox_impl dv = (DynValueBox_impl) dyn_any;
        if (dv.is_null())
            set_to_null();
        else {
            set_to_value();
            dv.rewind();
            component_.assign(dv.current_component());
            index_ = 0;
        }

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

        org.omg.CORBA.portable.InputStream in = null;
        try {
            in = val.create_input_stream();
        } catch (NullPointerException e) {
            throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(e);
        }

        _OB_unmarshal((InputStream) in);

        if (is_null())
            index_ = -1;
        else
            index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (is_null())
            return new Any(orbInstance_, type_, null);
        else {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            OutputStream out = new OutputStream(buf);
            out._OB_ORBInstance(orbInstance_);
            _OB_marshal(out);
            InputStream in = (InputStream) out.create_input_stream();
            return new Any(orbInstance_, type_, in);
        }
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

        DynValueBox_impl impl = (DynValueBox_impl) dyn_any;

        if (is_null() || impl.is_null())
            return (is_null() && impl.is_null());

        return component_.equal(impl.component_);
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynValueBox_impl result = new DynValueBox_impl(factory_, orbInstance_,
                type_);

        if (!is_null()) {
            try {
                result.set_boxed_value_as_dyn_any(component_);
            } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        }

        return result;
    }

    public synchronized boolean seek(int index) {
        if (is_null() || index != 0) {
            index_ = -1;
            return false;
        }

        index_ = index;
        return true;
    }

    public synchronized void rewind() {
        seek(0);
    }

    public synchronized boolean next() {
        if (index_ == 0) {
            index_ = -1;
            return false;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(index_ == -1);
        index_++;
        return true;
    }

    public synchronized int component_count() {
        return (is_null() ? 0 : 1);
    }

    public synchronized org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (index_ == -1)
            return null;

        org.apache.yoko.orb.OB.Assert._OB_assert(index_ == 0);
        return component_;
    }

    public synchronized org.omg.CORBA.Any get_boxed_value()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (is_null())
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        return component_.to_any();
    }

    public synchronized void set_boxed_value(org.omg.CORBA.Any boxed)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        //
        // Convert value to an ORBacus Any - the JDK implementation
        // of TypeCode.equivalent() raises NO_IMPLEMENT
        //
        Any val = null;
        try {
            val = (Any) boxed;
        } catch (ClassCastException ex) {
            try {
                val = new Any(boxed);
            } catch (NullPointerException e) {
                throw (org.omg.DynamicAny.DynAnyPackage.InvalidValue)new 
                    org.omg.DynamicAny.DynAnyPackage.InvalidValue().initCause(e);
            }
        }

        if (!val._OB_type().equivalent(boxedType_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        boolean isNull = is_null();

        try {
            set_to_value();
            component_.from_any(val);
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            //
            // Restore previous state if necessary
            //
            if (isNull)
                set_to_null();
            throw ex;
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
            //
            // Restore previous state if necessary
            //
            if (isNull)
                set_to_null();
            throw ex;
        }

        index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.DynamicAny.DynAny get_boxed_value_as_dyn_any()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (is_null())
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        return component_;
    }

    public synchronized void set_boxed_value_as_dyn_any(
            org.omg.DynamicAny.DynAny boxed)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (!boxedType_.equivalent(boxed.type()))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        boolean isNull = is_null();

        try {
            set_to_value();
            component_.assign(boxed);
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            //
            // Restore previous state if necessary
            //
            if (isNull)
                set_to_null();
            throw ex;
        }

        index_ = 0;

        notifyParent();
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        if (is_null())
            out.write_ulong(0);
        else {
            try {
                int tag = 0x7fffff02;
                boolean chunk = false;
                String[] ids = { type_.id() };

                out._OB_beginValue(tag, ids, chunk);

                DynAny_impl impl = (DynAny_impl) component_;
                impl._OB_marshal(out);

                out._OB_endValue();
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        }
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        _OB_marshal(out);
    }

    synchronized void _OB_unmarshal(InputStream in) {
        //
        // Peek at value tag
        //
        int save = in._OB_pos();
        int ind = 0;
        int tag = in.read_long();

        if (tag == 0) // null value
        {
            set_to_null();
            return;
        } else if (tag == -1) {
            //
            // Indirection - rewind to offset
            //
            int offs = in.read_long();
            ind = in._OB_pos(); // save position after offset
            in._OB_pos(in._OB_pos() - 4 + offs);
        } else
            in._OB_pos(save); // restore tag position

        set_to_value();

        in._OB_beginValue();

        //
        // Unmarshal component state
        //
        DynAny_impl impl = (DynAny_impl) component_;
        impl._OB_unmarshal(in);

        in._OB_endValue();

        //
        // Restore position after indirection
        //
        if (ind != 0)
            in._OB_pos(ind);

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (index_ == 0) {
            DynAny_impl impl = (DynAny_impl) component_;
            return impl._OB_currentAnyValue();
        }

        return null;
    }

    Any _OB_currentAnyValue() {
        return null;
    }
}
