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

final class DynValue_impl extends DynValueCommon_impl implements
        org.omg.DynamicAny.DynValue {
    private org.omg.DynamicAny.DynAny[] components_ = new org.omg.DynamicAny.DynAny[0];

    private String[] names_;

    private org.omg.CORBA.TypeCode[] types_;

    private String[] ids_;

    private int index_;

    org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader_;

    DynValue_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        this(factory, orbInstance, type, null);
    }

    DynValue_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type,
            org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader) {
        super(factory, orbInstance, type);

        dynValueReader_ = dynValueReader;

        try {
            //
            // Custom valuetypes are not supported by DynValue_impl
            //
            org.apache.yoko.orb.OB.Assert
                    ._OB_assert(origType_.type_modifier() != org.omg.CORBA.VM_CUSTOM.value);

            java.util.Vector names = new java.util.Vector();
            java.util.Vector types = new java.util.Vector();
            getMembers(type_, names, types);
            names_ = new String[names.size()];
            names.copyInto(names_);
            types_ = new org.omg.CORBA.TypeCode[types.size()];
            types.copyInto(types_);

            //
            // Collect our repository IDs for marshalling purposes. If the
            // type is truncatable, include IDs of all base types.
            //
            java.util.Vector ids = new java.util.Vector();
            ids.addElement(origType_.id());
            if (origType_.kind() == org.omg.CORBA.TCKind.tk_value) {
                short mod = origType_.type_modifier();
                if (mod == org.omg.CORBA.VM_TRUNCATABLE.value) {
                    org.omg.CORBA.TypeCode baseType = origType_
                            .concrete_base_type();
                    //
                    // Workaround for bug in JDK ORB, which returns a
                    // tk_null TypeCode instead of null if there is no
                    // concrete base type
                    //
                    // while(baseType != null)
                    while (baseType != null
                            && baseType.kind() != org.omg.CORBA.TCKind.tk_null) {
                        org.omg.CORBA.TypeCode origBaseType = TypeCode
                                ._OB_getOrigType(baseType);
                        ids.addElement(origBaseType.id());
                        baseType = origBaseType.concrete_base_type();
                    }
                }
            }
            ids_ = new String[ids.size()];
            ids.copyInto(ids_);

            //
            // Initial value is null
            //
            set_to_null();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void getMembers(org.omg.CORBA.TypeCode tc, java.util.Vector names,
            java.util.Vector types) {
        try {
            org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
            org.omg.CORBA.TypeCode base = origTC.concrete_base_type();
            //
            // Workaround for bug in JDK ORB, which returns a tk_null
            // TypeCode instead of null if there is no concrete base type
            //
            // if(base != null)
            if (base != null && base.kind() != org.omg.CORBA.TCKind.tk_null)
                getMembers(base, names, types);

            for (int i = 0; i < origTC.member_count(); i++) {
                names.addElement(origTC.member_name(i));
                types.addElement(origTC.member_type(i));
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    protected void createComponents() {
        if (components_.length == 0 && types_.length > 0) {
            components_ = new org.omg.DynamicAny.DynAny[types_.length];

            for (int i = 0; i < types_.length; i++) {
                org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                        ._OB_getOrigType(types_[i]);

                if ((origTC.kind().value() == org.omg.CORBA.TCKind._tk_value)
                        && (dynValueReader_ != null)) {
                    components_[i] = null;
                } else {
                    components_[i] = prepare(types_[i], dynValueReader_, true);
                }
            }
            index_ = 0;
        }
    }

    protected void destroyComponents() {
        if (components_.length > 0) {
            for (int i = 0; i < components_.length; i++)
                components_[i].destroy();
            components_ = new org.omg.DynamicAny.DynAny[0];
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

        org.omg.DynamicAny.DynValue dv = org.omg.DynamicAny.DynValueHelper
                .narrow(dyn_any);

        if (dv.is_null())
            set_to_null();
        else {
            set_to_value();

            org.apache.yoko.orb.OB.Assert._OB_assert(components_.length == dv
                    .component_count());

            dv.rewind();
            for (int i = 0; i < components_.length; i++) {
                components_[i].assign(dv.current_component());
                dv.next();
            }

            if (components_.length == 0)
                index_ = -1;
            else
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

        if (is_null() || components_.length == 0)
            index_ = -1;
        else
            index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        return to_any(null);
    }

    public synchronized org.omg.CORBA.Any to_any(DynValueWriter dynValueWriter) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (is_null())
            return new Any(orbInstance_, type_, null);
        else {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            OutputStream out = new OutputStream(buf);
            out._OB_ORBInstance(orbInstance_);

            if (dynValueWriter != null)
                _OB_marshal(out, dynValueWriter);
            else
                _OB_marshal(out);

            InputStream in = (InputStream) out.create_input_stream();
            return new Any(orbInstance_, type_, in);
        }
    }

    public synchronized boolean equal(org.omg.DynamicAny.DynAny dyn_any) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return (this == dyn_any);
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return this;
    }

    public synchronized boolean seek(int index) {
        if (index < 0 || index >= components_.length) {
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
        if (is_null() || index_ + 1 >= names_.length) {
            index_ = -1;
            return false;
        }

        index_++;
        return true;
    }

    public synchronized int component_count() {
        return components_.length;
    }

    public synchronized org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (names_.length == 0)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ == -1)
            return null;

        return components_[index_];
    }

    public synchronized String current_member_name()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (names_.length == 0)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ < 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        return names_[index_];
    }

    public synchronized org.omg.CORBA.TCKind current_member_kind()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (types_.length == 0)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (index_ < 0)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode origTC = TypeCode
                ._OB_getOrigType(types_[index_]);
        return origTC.kind();
    }

    public synchronized org.omg.DynamicAny.NameValuePair[] get_members()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (is_null())
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.DynamicAny.NameValuePair[] result = new org.omg.DynamicAny.NameValuePair[components_.length];

        for (int i = 0; i < components_.length; i++) {
            result[i] = new org.omg.DynamicAny.NameValuePair();
            result[i].id = names_[i];
            result[i].value = components_[i].to_any();
        }

        return result;
    }

    public synchronized void set_members(
            org.omg.DynamicAny.NameValuePair[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (value.length != names_.length)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        Any[] values = new Any[value.length];
        for (int i = 0; i < names_.length; i++) {
            if (value[i].id.length() > 0 && names_[i].length() > 0
                    && !value[i].id.equals(names_[i]))
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

            //
            // The JDK ORB's implementation of TypeCode doesn't
            // support equivalent(), so we need to ensure we
            // get an ORBacus TypeCode
            //
            try {
                values[i] = (Any) value[i].value;
            } catch (ClassCastException ex) {
                values[i] = new Any(value[i].value);
            }
            org.omg.CORBA.TypeCode valueType = values[i]._OB_type();
            if (!valueType.equivalent(types_[i]))
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
        }

        set_to_value();

        for (int i = 0; i < components_.length; i++)
            components_[i].from_any(values[i]);

        if (components_.length == 0)
            index_ = -1;
        else
            index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (is_null())
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.DynamicAny.NameDynAnyPair[] result = new org.omg.DynamicAny.NameDynAnyPair[components_.length];

        for (int i = 0; i < components_.length; i++) {
            result[i] = new org.omg.DynamicAny.NameDynAnyPair();
            result[i].id = names_[i];
            result[i].value = components_[i];
        }

        return result;
    }

    public synchronized void set_members_as_dyn_any(
            org.omg.DynamicAny.NameDynAnyPair[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (value.length != names_.length)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        for (int i = 0; i < names_.length; i++) {
            if (value[i].id.length() > 0 && names_[i].length() > 0
                    && !value[i].id.equals(names_[i]))
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

            org.omg.CORBA.TypeCode valueType = value[i].value.type();
            if (!valueType.equivalent(types_[i]))
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
        }

        set_to_value();

        for (int i = 0; i < components_.length; i++)
            components_[i].assign(value[i].value);

        if (components_.length == 0)
            index_ = -1;
        else
            index_ = 0;

        notifyParent();
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        _OB_marshal(out, new DynValueWriter(orbInstance_, factory_));
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        if (is_null()) {
            out.write_ulong(0);
        } else if (!dynValueWriter.writeIndirection(this, out)) {
            try {
                int tag;
                boolean chunk = false;

                //
                // Let the DynValue Marshaller know about this instance.
                // We need to do this here because it may be refered by
                // some of the components of this instance (even indirectly).
                //
                int startPos = out._OB_buffer().pos_;
                dynValueWriter.indexValue(this, startPos);

                short mod = origType_.type_modifier();
                if (mod == org.omg.CORBA.VM_TRUNCATABLE.value) {
                    chunk = true;
                    tag = 0x7fffff06;
                } else
                    tag = 0x7fffff02;

                out._OB_beginValue(tag, ids_, chunk);

                for (int i = 0; i < components_.length; i++) {
                    DynAny_impl impl = (DynAny_impl) components_[i];
                    impl._OB_marshal(out, dynValueWriter);
                }

                out._OB_endValue();
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        }
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
        for (int i = 0; i < components_.length; i++) {
            org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                    ._OB_getOrigType(types_[i]);

            if ((origTC.kind().value() == org.omg.CORBA.TCKind._tk_value)
                    && (dynValueReader_ != null)) {
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(components_[i] == null);

                try {
                    components_[i] = dynValueReader_.readValue(in, types_[i]);
                } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                    return;
                }

                adoptChild(components_[i]);
            } else {
                DynAny_impl impl = (DynAny_impl) components_[i];
                impl._OB_unmarshal(in);
            }
        }

        //
        // Compare the position of the input stream before and after the
        // unmarshalling of the valueType has ended. The existence of
        // skipped chunks indicates ValueType more derivated than this one,
        // and that truncation has occured
        //
        int pos_before = in._OB_pos();
        in._OB_endValue();
        int pos_after = in._OB_pos();

        if (pos_after != pos_before && dynValueReader_ != null) {
            dynValueReader_.mustTruncate = true;
        }

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

        if (index_ >= 0 && index_ <= components_.length) {
            DynAny_impl impl = (DynAny_impl) components_[index_];
            return impl._OB_currentAnyValue();
        }

        return null;
    }

    Any _OB_currentAnyValue() {
        return null;
    }
}
