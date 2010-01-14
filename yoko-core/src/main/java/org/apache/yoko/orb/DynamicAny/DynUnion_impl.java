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

final class DynUnion_impl extends DynAny_impl implements
        org.omg.DynamicAny.DynUnion {
    private org.omg.DynamicAny.DynAny disc_;

    private org.omg.DynamicAny.DynAny member_;

    private int index_;

    private int defaultMember_;

    private int selectedMember_;

    private long[] memberLabels_;

    private org.omg.CORBA.TypeCode origDiscTC_;

    private boolean ignoreDiscChange_;

    org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader_;

    DynUnion_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        this(factory, orbInstance, type, null);
    }

    DynUnion_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type,
            org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader) {
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
                org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                        ._OB_getOrigType(memberType);

                if (origTC.kind().value() == org.omg.CORBA.TCKind._tk_value)
                    member_ = null;
                else
                    member_ = prepare(memberType, dynValueReader_, true);
            } else {
                member_ = create(memberType, true);
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void childModified(org.omg.DynamicAny.DynAny p) {
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

            if (discriminator_kind() == org.omg.CORBA.TCKind.tk_enum) {
                org.omg.DynamicAny.DynEnum de = org.omg.DynamicAny.DynEnumHelper
                        .narrow(disc_);
                discValue = de.get_as_ulong();
            } else {
                DynAny_impl discImpl = (DynAny_impl) disc_;
                org.omg.CORBA.Any discAny = discImpl._OB_currentAny();
                org.apache.yoko.orb.OB.Assert._OB_assert(discAny != null);
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
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    private void resetDiscriminator(long val) {
        try {
            //
            // Insert the default discriminator value
            //
            switch (discriminator_kind().value()) {
            case org.omg.CORBA.TCKind._tk_boolean:
                disc_.insert_boolean(val == 1);
                break;

            case org.omg.CORBA.TCKind._tk_char:
                disc_.insert_char((char) val);
                break;

            case org.omg.CORBA.TCKind._tk_short:
                disc_.insert_short((short) val);
                break;

            case org.omg.CORBA.TCKind._tk_ushort:
                disc_.insert_ushort((short) val);
                break;

            case org.omg.CORBA.TCKind._tk_long:
                disc_.insert_long((int) val);
                break;

            case org.omg.CORBA.TCKind._tk_ulong:
                disc_.insert_ulong((int) val);
                break;

            case org.omg.CORBA.TCKind._tk_longlong:
                disc_.insert_longlong(val);
                break;

            case org.omg.CORBA.TCKind._tk_ulonglong:
                disc_.insert_ulonglong(val);
                break;

            case org.omg.CORBA.TCKind._tk_enum: {
                org.omg.DynamicAny.DynEnum e = org.omg.DynamicAny.DynEnumHelper
                        .narrow(disc_);
                e.set_as_ulong((int) val);
                break;
            }

            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported union type");
            }
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    private long getDiscriminatorValue(org.omg.CORBA.Any any) {
        long result = 0;

        switch (discriminator_kind().value()) {
        case org.omg.CORBA.TCKind._tk_boolean:
            result = any.extract_boolean() ? 1 : 0;
            break;

        case org.omg.CORBA.TCKind._tk_char:
            result = any.extract_char();
            break;

        case org.omg.CORBA.TCKind._tk_short:
            result = any.extract_short();
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            result = any.extract_ushort();
            break;

        case org.omg.CORBA.TCKind._tk_long:
            result = any.extract_long();
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            result = any.extract_ulong();
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
            result = any.extract_longlong();
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            result = any.extract_ulonglong();
            break;

        case org.omg.CORBA.TCKind._tk_enum: {
            try {
                Any a = (Any) any;
                result = ((Integer) a.value()).longValue();
            } catch (ClassCastException ex) {
                result = any.create_input_stream().read_ulong();
            }
            break;
        }

        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported union type");
        }

        return result;
    }

    private long findUnusedDiscriminator()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        //
        // Find an unused value among the member labels
        //

        long min = 0, max = 0;

        switch (discriminator_kind().value()) {
        case org.omg.CORBA.TCKind._tk_boolean:
            min = 0;
            max = 1;
            break;

        case org.omg.CORBA.TCKind._tk_char:
            min = 0;
            max = 255;
            break;

        case org.omg.CORBA.TCKind._tk_short:
            min = -32768;
            max = 32767;
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            min = 0;
            max = 65535;
            break;

        case org.omg.CORBA.TCKind._tk_long:
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            min = 0;
            max = Integer.MAX_VALUE;
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
            min = Long.MIN_VALUE;
            max = Long.MAX_VALUE;
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            min = 0;
            max = Long.MAX_VALUE;
            break;

        case org.omg.CORBA.TCKind._tk_enum: {
            try {
                min = 0;
                max = (origDiscTC_.member_count() - 1);
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
            break;
        }

        default:                            
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported union type");
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
        throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
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

        index_ = 0;

        notifyParent();
    }

    public synchronized org.omg.CORBA.Any to_any() {
        return to_any(null);
    }

    public synchronized org.omg.CORBA.Any to_any(DynValueWriter dynValueWriter) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);

        if (dynValueWriter != null)
            _OB_marshal(out, dynValueWriter);
        else
            _OB_marshal(out);

        InputStream in = (InputStream) out.create_input_stream();
        Any result = new Any(orbInstance_, type_, in);
        return result;
    }

    public synchronized boolean equal(org.omg.DynamicAny.DynAny dyn_any) {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (this == dyn_any)
            return true;

        if (!dyn_any.type().equivalent(type_))
            return false;

        if (component_count() != dyn_any.component_count())
            return false;

        org.omg.DynamicAny.DynUnion du = org.omg.DynamicAny.DynUnionHelper
                .narrow(dyn_any);

        if (!disc_.equal(du.get_discriminator()))
            return false;

        try {
            if (member_ != null) {
                if (!member_.equal(du.member()))
                    return false;
            }
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return true;
    }

    public synchronized org.omg.DynamicAny.DynAny copy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynValueReader dynValueReader = new DynValueReader(orbInstance_,
                factory_, false);

        DynUnion_impl result = new DynUnion_impl(factory_, orbInstance_, type_,
                dynValueReader);

        try {
            result.set_discriminator(disc_);

            if (member_ != null) {
                org.omg.CORBA.TypeCode memberType = origType_
                        .member_type(selectedMember_);

                org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                        ._OB_getOrigType(memberType);

                if (origTC.kind().value() == org.omg.CORBA.TCKind._tk_value) {
                    org.apache.yoko.orb.OB.Assert
                            ._OB_assert(result.member_ == null);
                    result.member_ = member_.copy();
                } else {
                    result.member().assign(member_);
                }
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
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

    public synchronized org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (index_ < 0)
            return null;

        org.apache.yoko.orb.OB.Assert._OB_assert(index_ < 2);

        if (index_ == 0)
            return disc_;
        else {
            org.apache.yoko.orb.OB.Assert._OB_assert(member_ != null);
            return member_;
        }
    }

    public synchronized org.omg.DynamicAny.DynAny get_discriminator() {
        return disc_;
    }

    public synchronized void set_discriminator(org.omg.DynamicAny.DynAny d)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (!d.type().equivalent(origDiscTC_))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (!d.equal(disc_)) {
            //
            // Change the discriminator - the member will be initialized
            // by childModified()
            //
            disc_.assign(d);

            notifyParent();
        }
    }

    public synchronized void set_to_default_member()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (defaultMember_ == -1)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        //
        // Reset the discriminator to select the default member. The
        // member will be initialized by childModified().
        //
        resetDiscriminator(memberLabels_[defaultMember_]);
        index_ = 0;

        notifyParent();
    }

    public synchronized void set_to_no_active_member()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch {
        if (defaultMember_ != -1)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

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

    public synchronized org.omg.CORBA.TCKind discriminator_kind() {
        return origDiscTC_.kind();
    }

    public synchronized org.omg.DynamicAny.DynAny member()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (member_ == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        return member_;
    }

    public synchronized String member_name()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (member_ == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return origType_.member_name(selectedMember_);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return null; // The compiler needs this
    }

    public synchronized org.omg.CORBA.TCKind member_kind()
            throws org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (member_ == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return origType_.member_type(selectedMember_).kind();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return null; // The compiler needs this
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
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            return;
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            return;
        }

        org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                ._OB_getOrigType(memberType);

        if ((origTC.kind().value() == org.omg.CORBA.TCKind._tk_value)
                && (dynValueReader_ != null)) {
            //
            // Create DynValue components
            //
            org.apache.yoko.orb.OB.Assert._OB_assert(member_ == null);

            try {
                member_ = dynValueReader_.readValue(in, memberType);
            } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                return;
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
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynAny_impl p = null;

        if (index_ == 0) // discriminator
            p = (DynAny_impl) disc_;
        else if (index_ == 1) // member
        {
            org.apache.yoko.orb.OB.Assert._OB_assert(member_ != null);
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
