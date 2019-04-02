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
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

final class DynStruct_impl extends DynAny_impl implements DynStruct {
    private DynAny[] components_;

    private int index_;

    private DynValueReader dynValueReader_;

    DynStruct_impl(DynAnyFactory factory,
                   ORBInstance orbInstance,
                   org.omg.CORBA.TypeCode type) {
        super(factory, orbInstance, type);

        dynValueReader_ = null;

        try {
            int count = origType_.member_count();
            components_ = new org.omg.DynamicAny.DynAny[count];

            for (int i = 0; i < count; i++)
                components_[i] = create(origType_.member_type(i), true);

            if (count == 0) // empty exception
                index_ = -1;
            else
                index_ = 0;
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }
    }

    DynStruct_impl(DynAnyFactory factory,
                   ORBInstance orbInstance,
                   org.omg.CORBA.TypeCode type,
                   DynValueReader dynValueReader) {
        super(factory, orbInstance, type);

        dynValueReader_ = dynValueReader;

        try {
            int count = origType_.member_count();
            components_ = new org.omg.DynamicAny.DynAny[count];

            for (int i = 0; i < count; i++) {
                org.omg.CORBA.TypeCode memberType = origType_.member_type(i);
                org.omg.CORBA.TypeCode origTC = TypeCode
                        ._OB_getOrigType(memberType);

                if ((origTC.kind().value() == TCKind._tk_value)
                        && (dynValueReader_ != null)) {
                    components_[i] = null;
                } else {
                    components_[i] = prepare(memberType, dynValueReader_, true);
                }
            }

            if (count == 0) // empty exception
                index_ = -1;
            else
                index_ = 0;
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void assign(DynAny dyn_any) throws TypeMismatch {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return;

        if (!dyn_any.type().equivalent(type_))
            throw new TypeMismatch();

        Assert._OB_assert(components_.length == dyn_any
                .component_count());

        dyn_any.rewind();
        for (DynAny dynAny : components_) {
            dynAny.assign(dyn_any.current_component());
            dyn_any.next();
        }

        if (components_.length == 0) // empty exception
            index_ = -1;
        else
            index_ = 0;

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

        if (components_.length == 0) // empty exception
            index_ = -1;
        else
            index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_) throw new OBJECT_NOT_EXIST();

        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);

            _OB_marshal(out);

        InputStream in = (InputStream) out.create_input_stream();
        Any result = new Any(orbInstance_, type_, in);
        return result;
    }

    public synchronized boolean equal(org.omg.DynamicAny.DynAny dyn_any) {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return true;

        if (!dyn_any.type().equivalent(type_))
            return false;

        dyn_any.rewind();
        try {
            for (DynAny dynAny : components_) {
                if (!dynAny.equal(dyn_any.current_component())) return false;
                dyn_any.next();
            }
        } catch (TypeMismatch ex) {
            return false;
        }

        return true;
    }

    public synchronized DynAny copy() {
        if (destroyed_) throw new OBJECT_NOT_EXIST();

        //
        // Marshal this. The DynValueWriter keeps track of DynValue instances.
        // They will be referenced again during demarshalling, thus
        // maintaining DynValue equality between the original and the copy.
        //
        DynValueWriter dynValueWriter = new DynValueWriter(orbInstance_,
                factory_);

        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        _OB_marshal(out, dynValueWriter);

        dynValueReader_ = dynValueWriter.getReader();

        org.omg.DynamicAny.DynAny result = prepare(type_, dynValueReader_,
                false);
        DynAny_impl impl = (DynAny_impl) result;

        InputStream in = (InputStream) out.create_input_stream();
        impl._OB_unmarshal(in);

        return result;
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
        if (index_ + 1 >= components_.length) {
            index_ = -1;
            return false;
        }

        index_++;
        return true;
    }

    public synchronized int component_count() {
        return components_.length;
    }

    public synchronized DynAny current_component() throws TypeMismatch {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (components_.length == 0) // empty exception
            throw new TypeMismatch();

        if (index_ == -1)
            return null;

        return components_[index_];
    }

    public synchronized String current_member_name()
            throws TypeMismatch,
            InvalidValue {
        if (components_.length == 0) // empty exception
            throw new TypeMismatch();

        if (index_ < 0)
            throw new InvalidValue();

        String result = null;

        try {
            result = origType_.member_name(index_);
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }

        return result;
    }

    public synchronized TCKind current_member_kind()
            throws TypeMismatch,
            InvalidValue {
        if (components_.length == 0) // empty exception
            throw new TypeMismatch();

        if (index_ < 0)
            throw new InvalidValue();

        TCKind result = null;

        try {
            org.omg.CORBA.TypeCode memberTC = origType_.member_type(index_);
            org.omg.CORBA.TypeCode origMemberTC = TypeCode
                    ._OB_getOrigType(memberTC);
            result = origMemberTC.kind();
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }

        return result;
    }

    public synchronized NameValuePair[] get_members() {
        NameValuePair[] result = new NameValuePair[components_.length];

        try {
            for (int i = 0; i < components_.length; i++) {
                result[i] = new NameValuePair();
                result[i].id = origType_.member_name(i);
                result[i].value = components_[i].to_any();
            }
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }

        return result;
    }

    public synchronized void set_members(
            NameValuePair[] value)
            throws TypeMismatch,
            InvalidValue {
        if (value.length != components_.length)
            throw new InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        try {
            Any[] values = new Any[value.length];
            for (int i = 0; i < components_.length; i++) {
                String name = origType_.member_name(i);

                if (value[i].id.length() > 0 && name.length() > 0
                        && !value[i].id.equals(name))
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
                org.omg.CORBA.TypeCode memberType = components_[i].type();
                if (!valueType.equivalent(memberType))
                    throw new TypeMismatch();
            }

            for (int i = 0; i < components_.length; i++)
                components_[i].from_any(values[i]);

            if (components_.length == 0) // empty exception
                index_ = -1;
            else
                index_ = 0;

            notifyParent();
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }
    }

    public synchronized NameDynAnyPair[] get_members_as_dyn_any() {
        NameDynAnyPair[] result = new NameDynAnyPair[components_.length];

        try {
            for (int i = 0; i < components_.length; i++) {
                result[i] = new NameDynAnyPair();
                result[i].id = origType_.member_name(i);
                result[i].value = components_[i];
            }
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }

        return result;
    }

    public synchronized void set_members_as_dyn_any(
            NameDynAnyPair[] value)
            throws TypeMismatch,
            InvalidValue {
        if (value.length != components_.length)
            throw new InvalidValue();

        //
        // Prior to modifying our components, validate the supplied
        // name-value pairs to check for matching member names and
        // compatible TypeCodes
        //
        try {
            for (int i = 0; i < components_.length; i++) {
                String name = origType_.member_name(i);

                if (value[i].id.length() > 0 && name.length() > 0
                        && !value[i].id.equals(name))
                    throw new TypeMismatch();

                org.omg.CORBA.TypeCode valueType = value[i].value.type();
                org.omg.CORBA.TypeCode memberType = components_[i].type();
                if (!valueType.equivalent(memberType))
                    throw new TypeMismatch();
            }

            for (int i = 0; i < components_.length; i++)
                components_[i].assign(value[i].value);

            if (components_.length == 0) // empty exception
                index_ = -1;
            else
                index_ = 0;

            notifyParent();
        } catch (BadKind | Bounds ex) {
            Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        _OB_marshal(out, new DynValueWriter(orbInstance_, factory_));
    }

    synchronized void _OB_marshal(OutputStream out,
            DynValueWriter dynValueWriter) {
        if (origType_.kind() == TCKind.tk_except) {
            try {
                out.write_string(origType_.id());
            } catch (BadKind ex) {
                Assert._OB_assert(ex);
            }
        }

        for (DynAny dynAny : components_) {
            DynAny_impl impl = (DynAny_impl) dynAny;
            impl._OB_marshal(out, dynValueWriter);
        }
    }

    synchronized void _OB_unmarshal(InputStream in) {
        if (origType_.kind() == TCKind.tk_except) {
            in.read_string();
        }

        for (int i = 0; i < components_.length; i++) {
            org.omg.CORBA.TypeCode memberType;

            try {
                memberType = origType_.member_type(i);
            } catch (BadKind | Bounds ex) {
                Assert._OB_assert(ex);
                return;
            }

            org.omg.CORBA.TypeCode origTC = TypeCode
                    ._OB_getOrigType(memberType);

            if ((origTC.kind().value() == TCKind._tk_value)
                    && (dynValueReader_ != null)) {
                //
                // Create DynValue components
                //
                Assert
                        ._OB_assert(components_[i] == null);

                try {
                    components_[i] = dynValueReader_.readValue(in, memberType);
                } catch (InconsistentTypeCode ex) {
                    Assert._OB_assert(ex);
                    return;
                }

                adoptChild(components_[i]);

            } else {
                //
                // Set non-DynValue components
                //
                DynAny_impl impl = (DynAny_impl) components_[i];
                impl._OB_unmarshal(in);
            }
        }

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
