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
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynEnum;
import org.omg.DynamicAny.DynEnumHelper;
import org.omg.DynamicAny.DynUnion;
import org.omg.DynamicAny.DynUnionHelper;

final class DynUnion_impl extends DynAny_impl implements DynUnion {
    private DynAny disc_;

    private DynAny member_;

    private int index_;

    private int defaultMember_;

    private int selectedMember_;

    private long[] memberLabels_;

    private org.omg.CORBA.TypeCode origDiscTC_;

    private boolean ignoreDiscChange_;

    private final DynValueReader dynValueReader_;

    DynUnion_impl(DynAnyFactory factory,
                  ORBInstance orbInstance,
                  org.omg.CORBA.TypeCode type) {
        this(factory, orbInstance, type, null);
    }

    DynUnion_impl(DynAnyFactory factory,
                  ORBInstance orbInstance,
                  org.omg.CORBA.TypeCode type,
                  DynValueReader dynValueReader) {
        super(factory, orbInstance, type);

        dynValueReader_ = dynValueReader;

        try {
            org.omg.CORBA.TypeCode discTC = origType_.discriminator_type();

            index_ = 0;
            defaultMember_ = origType_.default_index();
            selectedMember_ = 0;
            origDiscTC_ = TypeCode._OB_getOrigType(discTC);

            int count = origType_.member_count();
            memberLabels_ = new long[count];
            for (int i = 0; i < count; i++) {
                if (i != defaultMember_) {
                    org.omg.CORBA.Any any = origType_.member_label(i);
                    memberLabels_[i] = getDiscriminatorValue(any);
                }
            }

            if (defaultMember_ != -1)
                memberLabels_[defaultMember_] = findUnusedDiscriminator();

            disc_ = create(discTC, true);
            ignoreDiscChange_ = true;
            resetDiscriminator(memberLabels_[selectedMember_]);

            org.omg.CORBA.TypeCode memberType = origType_
                    .member_type(selectedMember_);

            if (dynValueReader_ != null) {
                org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(memberType);

                if (origTC.kind().value() == TCKind._tk_value)
                    member_ = null;
                else
                    member_ = prepare(memberType, dynValueReader_, true);
            } else {
                member_ = create(memberType, true);
            }
        } catch (BadKind | TypeMismatch | Bounds ex) {
            throw Assert.fail(ex);
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void childModified(DynAny p) {
        //
        // We need to monitor the discriminator component. If it is modified,
        // then we need to initialize the active member appropriately. If
        // ignoreDiscChange_ is true, then we ignore this change to the
        // discriminator.
        //
        if (p == disc_) {
            if (!ignoreDiscChange_) {
                //
                // Act as if set_discriminator had been called
                //

                initMember();

                if (member_ == null)
                    index_ = 0;
                else
                    index_ = 1;

                notifyParent();
            }

            ignoreDiscChange_ = false;
        }
    }

    private void initMember() {
        try {
            long discValue;

            if (discriminator_kind() == TCKind.tk_enum) {
                DynEnum de = DynEnumHelper.narrow(disc_);
                discValue = de.get_as_ulong();
            } else {
                DynAny_impl discImpl = (DynAny_impl) disc_;
                org.omg.CORBA.Any discAny = discImpl._OB_currentAny();
                Assert.ensure(discAny != null);
                discValue = getDiscriminatorValue(discAny);
            }

            //
            // Search the labels for one matching the discriminator
            //
            int i;
            for (i = 0; i < memberLabels_.length; i++)
                if (discValue == memberLabels_[i])
                    break;

            //
            // If no matching label was found, check for a default member.
            // If there's no default member, then we have no member value.
            //
            if (i == memberLabels_.length) {
                if (defaultMember_ != -1)
                    i = defaultMember_;
                else {
                    if (member_ != null)
                        member_ = null;
                    selectedMember_ = origType_.member_count();
                    return;
                }
            }

            //
            // If the member names are different, then we deactivate the
            // current member and initialize the new one
            //
            if (selectedMember_ == origType_.member_count()
                    || !origType_.member_name(selectedMember_).equals(
                    origType_.member_name(i))) {
                member_ = create(origType_.member_type(i), true);
            }
            selectedMember_ = i;
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }
    }

    private void resetDiscriminator(long val) {
        try {
            //
            // Insert the default discriminator value
            //
            switch (discriminator_kind().value()) {
            case TCKind._tk_boolean:
                disc_.insert_boolean(val == 1);
                break;

            case TCKind._tk_char:
                disc_.insert_char((char) val);
                break;

            case TCKind._tk_short:
                disc_.insert_short((short) val);
                break;

            case TCKind._tk_ushort:
                disc_.insert_ushort((short) val);
                break;

            case TCKind._tk_long:
                disc_.insert_long((int) val);
                break;

            case TCKind._tk_ulong:
                disc_.insert_ulong((int) val);
                break;

            case TCKind._tk_longlong:
                disc_.insert_longlong(val);
                break;

            case TCKind._tk_ulonglong:
                disc_.insert_ulonglong(val);
                break;

            case TCKind._tk_enum: {
                DynEnum e = DynEnumHelper.narrow(disc_);
                e.set_as_ulong((int) val);
                break;
            }

            default:
                throw Assert.fail("Unsupported union type");
            }
        } catch (TypeMismatch | InvalidValue ex) {
            throw Assert.fail(ex);
        }
    }

    private long getDiscriminatorValue(org.omg.CORBA.Any any) {
        long result = 0;

        switch (discriminator_kind().value()) {
        case TCKind._tk_boolean:
            result = any.extract_boolean() ? 1 : 0;
            break;

        case TCKind._tk_char:
            result = any.extract_char();
            break;

        case TCKind._tk_short:
            result = any.extract_short();
            break;

        case TCKind._tk_ushort:
            result = any.extract_ushort();
            break;

        case TCKind._tk_long:
            result = any.extract_long();
            break;

        case TCKind._tk_ulong:
            result = any.extract_ulong();
            break;

        case TCKind._tk_longlong:
            result = any.extract_longlong();
            break;

        case TCKind._tk_ulonglong:
            result = any.extract_ulonglong();
            break;

        case TCKind._tk_enum: {
            try {
                Any a = (Any) any;
                result = ((Integer) a.value()).longValue();
            } catch (ClassCastException ex) {
                result = any.create_input_stream().read_ulong();
            }
            break;
        }

        default:
            throw Assert.fail("Unsupported union type");
        }

        return result;
    }

    private long findUnusedDiscriminator()
            throws TypeMismatch {
        //
        // Find an unused value among the member labels
        //

        long min = 0, max = 0;

        switch (discriminator_kind().value()) {
        case TCKind._tk_boolean:
            min = 0;
            max = 1;
            break;

        case TCKind._tk_char:
            min = 0;
            max = 255;
            break;

        case TCKind._tk_short:
            min = -32768;
            max = 32767;
            break;

        case TCKind._tk_ushort:
            min = 0;
            max = 65535;
            break;

        case TCKind._tk_long:
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
            break;

        case TCKind._tk_ulong:
            min = 0;
            max = Integer.MAX_VALUE;
            break;

        case TCKind._tk_longlong:
            min = Long.MIN_VALUE;
            max = Long.MAX_VALUE;
            break;

        case TCKind._tk_ulonglong:
            min = 0;
            max = Long.MAX_VALUE;
            break;

        case TCKind._tk_enum: {
            try {
                min = 0;
                max = (origDiscTC_.member_count() - 1);
            } catch (BadKind ex) {
                throw Assert.fail(ex);
            }
            break;
        }

        default:
            throw Assert.fail("Unsupported union type");
        }

        for (long i = max; i >= min; i--) {
            boolean found = false;
            for (int j = 0; !found && j < memberLabels_.length; j++)
                if (j != defaultMember_ && memberLabels_[j] == i)
                    found = true;

            if (!found)
                return i;
        }

        //
        // No value found
        //
        throw new TypeMismatch();
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

        DynUnion_impl impl = (DynUnion_impl) dyn_any;

        selectedMember_ = impl.selectedMember_;
        ignoreDiscChange_ = true;
        disc_.assign(impl.disc_);

        if (member_ != null)
            member_ = null;

        if (impl.member_ != null) {
            member_ = impl.member_.copy();
            adoptChild(member_);
        }

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
            throw (InvalidValue)new InvalidValue().initCause(e);
        }

        _OB_unmarshal((InputStream) in);

        index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        if (destroyed_) throw new OBJECT_NOT_EXIST();

        OutputStream out = new OutputStream();
        out._OB_ORBInstance(orbInstance_);

        _OB_marshal(out);

        InputStream in = out.create_input_stream();
        return new Any(orbInstance_, type_, in);
    }

    public synchronized boolean equal(DynAny dyn_any) {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return true;

        if (!dyn_any.type().equivalent(type_))
            return false;

        if (component_count() != dyn_any.component_count())
            return false;

        DynUnion du = DynUnionHelper.narrow(dyn_any);

        if (!disc_.equal(du.get_discriminator()))
            return false;

        try {
            if (member_ != null) {
                if (!member_.equal(du.member()))
                    return false;
            }
        } catch (InvalidValue ex) {
            throw Assert.fail(ex);
        }

        return true;
    }

    public synchronized DynAny copy() {
        if (destroyed_) throw new OBJECT_NOT_EXIST();

        DynValueReader dynValueReader = new DynValueReader(orbInstance_, factory_, false);

        DynUnion_impl result = new DynUnion_impl(factory_, orbInstance_, type_, dynValueReader);

        try {
            result.set_discriminator(disc_);

            if (member_ != null) {
                org.omg.CORBA.TypeCode memberType = origType_.member_type(selectedMember_);

                org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(memberType);

                if (origTC.kind().value() == TCKind._tk_value) {
                    Assert.ensure(result.member_ == null);
                    result.member_ = member_.copy();
                } else {
                    result.member().assign(member_);
                }
            }
        } catch (BadKind | InvalidValue | TypeMismatch | Bounds ex) {
            throw Assert.fail(ex);
        }

        return result;
    }

    public synchronized boolean seek(int index) {
        int max = (member_ == null) ? 1 : 2;

        if (index < 0 || index >= max) {
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
        int max = (member_ == null) ? 1 : 2;

        if (index_ + 1 >= max) {
            index_ = -1;
            return false;
        }

        index_++;
        return true;
    }

    public synchronized int component_count() {
        if (member_ == null)
            return 1;
        else
            return 2;
    }

    public synchronized DynAny current_component() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (index_ < 0)
            return null;

        Assert.ensure(index_ < 2);

        if (index_ == 0)
            return disc_;
        else {
            Assert.ensure(member_ != null);
            return member_;
        }
    }

    public synchronized DynAny get_discriminator() {
        return disc_;
    }

    public synchronized void set_discriminator(DynAny d) throws TypeMismatch {
        if (!d.type().equivalent(origDiscTC_))
            throw new TypeMismatch();

        if (!d.equal(disc_)) {
            //
            // Change the discriminator - the member will be initialized
            // by childModified()
            //
            disc_.assign(d);

            notifyParent();
        }
    }

    public synchronized void set_to_default_member() throws TypeMismatch {
        if (defaultMember_ == -1)
            throw new TypeMismatch();

        //
        // Reset the discriminator to select the default member. The
        // member will be initialized by childModified().
        //
        resetDiscriminator(memberLabels_[defaultMember_]);
        index_ = 0;

        notifyParent();
    }

    public synchronized void set_to_no_active_member() throws TypeMismatch {
        if (defaultMember_ != -1)
            throw new TypeMismatch();

        //
        // Reset the discriminator to an unused value. The current
        // member will be deactivated by childModified().
        //
        long val = findUnusedDiscriminator();
        resetDiscriminator(val);
        index_ = 0;

        notifyParent();
    }

    public synchronized boolean has_no_active_member() {
        return (member_ == null);
    }

    public synchronized TCKind discriminator_kind() {
        return origDiscTC_.kind();
    }

    public synchronized DynAny member() throws InvalidValue {
        if (member_ == null)
            throw new InvalidValue();

        return member_;
    }

    public synchronized String member_name() throws InvalidValue {
        if (member_ == null)
            throw new InvalidValue();

        try {
            return origType_.member_name(selectedMember_);
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }
    }

    public synchronized TCKind member_kind() throws InvalidValue {
        if (member_ == null)
            throw new InvalidValue();

        try {
            return origType_.member_type(selectedMember_).kind();
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }
    }

    public synchronized boolean is_set_to_default_member() {
        return (defaultMember_ != -1 && selectedMember_ == defaultMember_);
    }

    // ------------------------------------------------------------------
    // Internal member implementations
    // ------------------------------------------------------------------

    synchronized void _OB_marshal(OutputStream out) {
        _OB_marshal(out, new DynValueWriter(orbInstance_, factory_));
    }

    synchronized void _OB_marshal(OutputStream out,
                                  DynValueWriter dynValueWriter) {
        DynAny_impl impl = (DynAny_impl) disc_;
        impl._OB_marshal(out);

        if (member_ != null) {
            impl = (DynAny_impl) member_;
            impl._OB_marshal(out, dynValueWriter);
        }
    }

    synchronized void _OB_unmarshal(InputStream in) {
        DynAny_impl impl = (DynAny_impl) disc_;
        impl._OB_unmarshal(in);

        //
        // The member is initialized by childModified()
        //

        org.omg.CORBA.TypeCode memberType;

        try {
            memberType = origType_.member_type(selectedMember_);
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }

        org.omg.CORBA.TypeCode origTC = TypeCode
                ._OB_getOrigType(memberType);

        if ((origTC.kind().value() == TCKind._tk_value)
                && (dynValueReader_ != null)) {
            //
            // Create DynValue components
            //
            Assert.ensure(member_ == null);

            try {
                member_ = dynValueReader_.readValue(in, memberType);
            } catch (InconsistentTypeCode ex) {
                throw Assert.fail(ex);
            }

            adoptChild(member_);

        } else if (member_ != null) {
            impl = (DynAny_impl) member_;
            impl._OB_unmarshal(in);
        }

        notifyParent();
    }

    synchronized Any _OB_currentAny() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        DynAny_impl p = null;

        if (index_ == 0) // discriminator
            p = (DynAny_impl) disc_;
        else if (index_ == 1) // member
        {
            Assert.ensure(member_ != null);
            p = (DynAny_impl) member_;
        }

        Any result = null;

        if (p != null)
            result = p._OB_currentAnyValue();

        return result;
    }

    Any _OB_currentAnyValue() {
        return null;
    }
}
