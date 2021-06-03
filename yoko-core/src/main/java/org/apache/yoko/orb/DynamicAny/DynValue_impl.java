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
import org.apache.yoko.util.Assert;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.VM_CUSTOM;
import org.omg.CORBA.VM_TRUNCATABLE;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValue;
import org.omg.DynamicAny.DynValueHelper;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

import java.util.Vector;

final class DynValue_impl extends DynValueCommon_impl implements
        DynValue {
    private DynAny[] components_ = new DynAny[0];

    private String[] names_;

    private org.omg.CORBA.TypeCode[] types_;

    private String[] ids_;

    private int index_;

    private final DynValueReader dynValueReader_;

    DynValue_impl(DynAnyFactory factory,
                  ORBInstance orbInstance,
                  org.omg.CORBA.TypeCode type) {
        this(factory, orbInstance, type, null);
    }

    DynValue_impl(DynAnyFactory factory,
                  ORBInstance orbInstance,
                  org.omg.CORBA.TypeCode type,
                  DynValueReader dynValueReader) {
        super(factory, orbInstance, type);

        dynValueReader_ = dynValueReader;

        try {
            //
            // Custom valuetypes are not supported by DynValue_impl
            //
            Assert
                    .ensure(origType_.type_modifier() != VM_CUSTOM.value);

            Vector names = new Vector();
            Vector types = new Vector();
            getMembers(type_, names, types);
            names_ = new String[names.size()];
            names.copyInto(names_);
            types_ = new org.omg.CORBA.TypeCode[types.size()];
            types.copyInto(types_);

            //
            // Collect our repository IDs for marshalling purposes. If the
            // type is truncatable, include IDs of all base types.
            //
            Vector ids = new Vector();
            ids.addElement(origType_.id());
            if (origType_.kind() == TCKind.tk_value) {
                short mod = origType_.type_modifier();
                if (mod == VM_TRUNCATABLE.value) {
                    org.omg.CORBA.TypeCode baseType = origType_
                            .concrete_base_type();
                    //
                    // Workaround for bug in JDK ORB, which returns a
                    // tk_null TypeCode instead of null if there is no
                    // concrete base type
                    //
                    // while(baseType != null)
                    while (baseType != null
                            && baseType.kind() != TCKind.tk_null) {
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
        } catch (BadKind ex) {
            throw Assert.fail(ex);
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void getMembers(org.omg.CORBA.TypeCode tc, Vector names,
            Vector types) {
        try {
            org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
            org.omg.CORBA.TypeCode base = origTC.concrete_base_type();
            //
            // Workaround for bug in JDK ORB, which returns a tk_null
            // TypeCode instead of null if there is no concrete base type
            //
            // if(base != null)
            if (base != null && base.kind() != TCKind.tk_null)
                getMembers(base, names, types);

            for (int i = 0; i < origTC.member_count(); i++) {
                names.addElement(origTC.member_name(i));
                types.addElement(origTC.member_type(i));
            }
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }
    }

    protected void createComponents() {
        if (components_.length == 0 && types_.length > 0) {
            components_ = new DynAny[types_.length];

            for (int i = 0; i < types_.length; i++) {
                org.omg.CORBA.TypeCode origTC = TypeCode
                        ._OB_getOrigType(types_[i]);

                if ((origTC.kind().value() == TCKind._tk_value)
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
            for (DynAny dynAny : components_) dynAny.destroy();
            components_ = new DynAny[0];
        }

        index_ = -1;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void assign(DynAny dyn_any)
            throws TypeMismatch {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return;

        if (!dyn_any.type().equivalent(type_))
            throw new TypeMismatch();

        DynValue dv = DynValueHelper
                .narrow(dyn_any);

        if (dv.is_null())
            set_to_null();
        else {
            set_to_value();

            Assert.ensure(components_.length == dv
                    .component_count());

            dv.rewind();
            for (DynAny dynAny : components_) {
                dynAny.assign(dv.current_component());
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
            throws TypeMismatch,
            InvalidValue {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        //
        // Convert value to an ORBacus Any - the JDK implementation
        // of TypeCode.equivalent() raises NO_IMPLEMENT
        //
        Any val;
        try {
            val = (Any) value;
        } catch (ClassCastException ex) {
            try {
                val = new Any(value);
            } catch (NullPointerException e) {
                throw (InvalidValue)new
                    InvalidValue().initCause(e);
            }
        }

        if (!val._OB_type().equivalent(type_))
            throw new TypeMismatch();

        org.omg.CORBA.portable.InputStream in;
        try {
            in = val.create_input_stream();
        } catch (NullPointerException e) {
            throw (InvalidValue)new
                InvalidValue().initCause(e);
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

    private synchronized org.omg.CORBA.Any to_any(DynValueWriter dynValueWriter) {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (is_null())
            return new Any(orbInstance_, type_, null);
        else {
            try (OutputStream out = new OutputStream()) {
                out._OB_ORBInstance(orbInstance_);

                if (dynValueWriter != null)
                    _OB_marshal(out, dynValueWriter);
                else
                    _OB_marshal(out);

                InputStream in = out.create_input_stream();
                return new Any(orbInstance_, type_, in);
            }
        }
    }

    public synchronized boolean equal(DynAny dyn_any) {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        return (this == dyn_any);
    }

    public synchronized DynAny copy() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

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

    public synchronized DynAny current_component()
            throws TypeMismatch {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (names_.length == 0)
            throw new TypeMismatch();

        if (index_ == -1)
            return null;

        return components_[index_];
    }

    public synchronized String current_member_name()
            throws TypeMismatch,
            InvalidValue {
        if (names_.length == 0)
            throw new TypeMismatch();

        if (index_ < 0)
            throw new InvalidValue();

        return names_[index_];
    }

    public synchronized TCKind current_member_kind()
            throws TypeMismatch,
            InvalidValue {
        if (types_.length == 0)
            throw new TypeMismatch();

        if (index_ < 0)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode origTC = TypeCode
                ._OB_getOrigType(types_[index_]);
        return origTC.kind();
    }

    public synchronized NameValuePair[] get_members()
            throws InvalidValue {
        if (is_null())
            throw new InvalidValue();

        NameValuePair[] result = new NameValuePair[components_.length];

        for (int i = 0; i < components_.length; i++) {
            result[i] = new NameValuePair();
            result[i].id = names_[i];
            result[i].value = components_[i].to_any();
        }

        return result;
    }

    public synchronized void set_members(
            NameValuePair[] value)
            throws TypeMismatch,
            InvalidValue {
        if (value.length != names_.length)
            throw new InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        Any[] values = new Any[value.length];
        for (int i = 0; i < names_.length; i++) {
            if (value[i].id.length() > 0 && names_[i].length() > 0
                    && !value[i].id.equals(names_[i]))
                throw new TypeMismatch();

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
                throw new TypeMismatch();
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

    public synchronized NameDynAnyPair[] get_members_as_dyn_any()
            throws InvalidValue {
        if (is_null())
            throw new InvalidValue();

        NameDynAnyPair[] result = new NameDynAnyPair[components_.length];

        for (int i = 0; i < components_.length; i++) {
            result[i] = new NameDynAnyPair();
            result[i].id = names_[i];
            result[i].value = components_[i];
        }

        return result;
    }

    public synchronized void set_members_as_dyn_any(
            NameDynAnyPair[] value)
            throws TypeMismatch,
            InvalidValue {
        if (value.length != names_.length)
            throw new InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        for (int i = 0; i < names_.length; i++) {
            if (value[i].id.length() > 0 && names_[i].length() > 0
                    && !value[i].id.equals(names_[i]))
                throw new TypeMismatch();

            org.omg.CORBA.TypeCode valueType = value[i].value.type();
            if (!valueType.equivalent(types_[i]))
                throw new TypeMismatch();
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
                int startPos = out.getPosition();
                dynValueWriter.indexValue(this, startPos);

                short mod = origType_.type_modifier();
                if (mod == VM_TRUNCATABLE.value) {
                    chunk = true;
                    tag = 0x7fffff06;
                } else
                    tag = 0x7fffff02;

                out._OB_beginValue(tag, ids_, chunk);

                for (DynAny dynAny : components_) {
                    DynAny_impl impl = (DynAny_impl) dynAny;
                    impl._OB_marshal(out, dynValueWriter);
                }

                out._OB_endValue();
            } catch (BadKind ex) {
                throw Assert.fail(ex);
            }
        }
    }

    synchronized void _OB_unmarshal(InputStream in) {
        //
        // Peek at value tag
        //
        int save = in.getPosition();
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
            ind = in.getPosition(); // save position after offset
            in.setPosition(in.getPosition() - 4 + offs);
        } else
            in.setPosition(save); // restore tag position

        set_to_value();

        in._OB_beginValue();

        //
        // Unmarshal component state
        //
        for (int i = 0; i < components_.length; i++) {
            org.omg.CORBA.TypeCode origTC = TypeCode
                    ._OB_getOrigType(types_[i]);

            if ((origTC.kind().value() == TCKind._tk_value)
                    && (dynValueReader_ != null)) {
                Assert
                        .ensure(components_[i] == null);

                try {
                    components_[i] = dynValueReader_.readValue(in, types_[i]);
                } catch (InconsistentTypeCode ex) {
                    throw Assert.fail(ex);
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
        int pos_before = in.getPosition();
        in._OB_endValue();
        int pos_after = in.getPosition();

        if (pos_after != pos_before && dynValueReader_ != null) {
            dynValueReader_.mustTruncate = true;
        }

        //
        // Restore position after indirection
        //
        if (ind != 0)
            in.setPosition(ind);

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

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
