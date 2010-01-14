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
import org.omg.CORBA.CustomMarshal;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAny_impl extends org.omg.CORBA.LocalObject implements
        org.omg.DynamicAny.DynAny {
    protected org.omg.DynamicAny.DynAnyFactory factory_;

    protected org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    protected org.omg.CORBA.TypeCode type_;

    protected org.omg.CORBA.TypeCode origType_;

    protected DynAny_impl parent_;

    protected boolean destroyed_;

    DynAny_impl(org.omg.DynamicAny.DynAnyFactory factory,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type) {
        factory_ = factory;
        orbInstance_ = orbInstance;
        type_ = type;
        origType_ = TypeCode._OB_getOrigType(type_);
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void checkValue(Any any, org.omg.CORBA.TCKind kind)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode tc = org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(kind);

        if (!any._OB_type().equivalent(tc))
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
    }

    protected org.omg.DynamicAny.DynAny create(org.omg.CORBA.Any any,
            boolean adopt) {
        org.omg.DynamicAny.DynAny result = null;
        try {
            result = factory_.create_dyn_any(any);
        } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;
    }

    protected org.omg.DynamicAny.DynAny create(org.omg.CORBA.TypeCode tc,
            boolean adopt) {
        org.omg.DynamicAny.DynAny result = null;
        try {
            result = factory_.create_dyn_any_from_type_code(tc);
        } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;
    }

    protected org.omg.DynamicAny.DynAny prepare(org.omg.CORBA.TypeCode tc,
            org.apache.yoko.orb.DynamicAny.DynValueReader dynValueReader,
            boolean adopt) {
        //
        // Use "create" if the instantiation of DynValues
        // must not be delayed
        //
        if (dynValueReader == null)
            return create(tc, adopt);

        org.omg.DynamicAny.DynAny result = null;
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;

        try {
            result = factory_impl.prepare_dyn_any_from_type_code(tc,
                    dynValueReader);
        } catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;

    }

    protected void adoptChild(org.omg.DynamicAny.DynAny d) {
        DynAny_impl impl = (DynAny_impl) d;
        impl.parent_ = this;
    }

    protected void notifyParent() {
        if (parent_ != null)
            parent_.childModified(this);
    }

    protected void childModified(org.omg.DynamicAny.DynAny p) {
        // do nothing
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    final public org.omg.CORBA.TypeCode type() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        return type_;
    }

    public abstract void assign(org.omg.DynamicAny.DynAny dyn_any)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    public abstract void from_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    public abstract org.omg.CORBA.Any to_any();

    public abstract boolean equal(org.omg.DynamicAny.DynAny dyn_any);

    public void destroy() {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (parent_ == null)
            destroyed_ = true;
    }

    public abstract org.omg.DynamicAny.DynAny copy();

    public synchronized void insert_boolean(boolean value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_boolean);

        any.replace(any.type(), Boolean.valueOf(value));

        notifyParent();
    }

    public synchronized void insert_octet(byte value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_octet);

        any.replace(any.type(), new Byte(value));

        notifyParent();
    }

    public synchronized void insert_char(char value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_char);

        any.replace(any.type(), new Character(value));

        notifyParent();
    }

    public synchronized void insert_short(short value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_short);

        any.replace(any.type(), new Integer(value));

        notifyParent();
    }

    public synchronized void insert_ushort(short value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_ushort);

        any.replace(any.type(), new Integer(value));

        notifyParent();
    }

    public synchronized void insert_long(int value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_long);

        any.replace(any.type(), new Integer(value));

        notifyParent();
    }

    public synchronized void insert_ulong(int value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_ulong);

        any.replace(any.type(), new Integer(value));

        notifyParent();
    }

    public synchronized void insert_float(float value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_float);

        any.replace(any.type(), new Float(value));

        notifyParent();
    }

    public synchronized void insert_double(double value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_double);

        any.replace(any.type(), new Double(value));

        notifyParent();
    }

    public synchronized void insert_string(String value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue() - we must accomodate bounded and
        // unbounded strings

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_string)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        //
        // Check for bounded string
        //
        try {
            int len = origTC.length();
            if (len > 0 && value.length() > len)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_reference(org.omg.CORBA.Object value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue()

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_objref
                && origTC.kind() != org.omg.CORBA_2_4.TCKind.tk_local_interface)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_typecode(org.omg.CORBA.TypeCode value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_TypeCode);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_longlong(long value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_longlong);

        any.replace(any.type(), new Long(value));

        notifyParent();
    }

    public synchronized void insert_ulonglong(long value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_ulonglong);

        any.replace(any.type(), new Long(value));

        notifyParent();
    }

    public synchronized void insert_wchar(char value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_wchar);

        any.replace(any.type(), new Character(value));

        notifyParent();
    }

    public synchronized void insert_wstring(String value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue() - we must accomodate bounded and
        // unbounded strings

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_wstring)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        //
        // Check for bounded wstring
        //
        try {
            int len = origTC.length();
            if (len > 0 && value.length() > len)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.TCKind.tk_any);

        Any val = new Any(value);
        val._OB_ORBInstance(orbInstance_);
        any.replace(any.type(), val);

        notifyParent();
    }

    public synchronized void insert_dyn_any(org.omg.DynamicAny.DynAny value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        if (value == null)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (!_OB_insertDynAny(value)) {
            org.omg.DynamicAny.DynAny comp = current_component();
            if (comp == null)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
            DynAny_impl impl = (DynAny_impl) comp;
            if (!impl._OB_insertDynAny(value))
                throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
        }

        notifyParent();
    }

    public synchronized void insert_val(java.io.Serializable value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        //
        // TODO: Custom valuetypes are not currently supported
        //
        if (value instanceof org.omg.CORBA.CustomMarshal) {
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
                org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        org.omg.DynamicAny.DynAny comp = current_component();
        if (comp == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Ensure the given value has the proper type
        //
        org.omg.CORBA.TypeCode tc = comp.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_value
                && origTC.kind() != org.omg.CORBA.TCKind.tk_value_box)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        try {
            if (value != null
                    && value instanceof org.omg.CORBA.portable.ValueBase) {
                String id = origTC.id();
                String[] ids = ((org.omg.CORBA.portable.ValueBase) value)
                        ._truncatable_ids();
                int i;
                for (i = 0; i < ids.length; i++)
                    if (id.equals(ids[i]))
                        break;

                if (i >= ids.length)
                    throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        //
        // Create an any and invoke from_any
        //
        Any any = new Any(orbInstance_, tc, value);
        comp.from_any(any);

        notifyParent();
    }

    public synchronized void insert_abstract(java.lang.Object value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode type = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(type);

        if (origTC.kind() != org.omg.CORBA.TCKind.tk_abstract_interface)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        if (value == null)
            any.insert_Value(null, type);
        else if (value instanceof java.io.Serializable)
            any.insert_Value((java.io.Serializable) value, type);
        else if (value instanceof org.omg.CORBA.Object)
            any.insert_Object((org.omg.CORBA.Object) value, type);
        else
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        notifyParent();
    }

    public synchronized void insert_boolean_seq(boolean[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.BooleanSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_octet_seq(byte[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.OctetSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_char_seq(char[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.CharSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_wchar_seq(char[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.WCharSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_short_seq(short[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.ShortSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ushort_seq(short[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.UShortSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_long_seq(int[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.LongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulong_seq(int[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.ULongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_longlong_seq(long[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.LongLongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulonglong_seq(long[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.ULongLongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_float_seq(float[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.FloatSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_double_seq(double[] value)
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, org.omg.CORBA.DoubleSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized boolean get_boolean()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_boolean();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized byte get_octet()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_octet();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized char get_char()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_char();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized short get_short()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_short();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized short get_ushort()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_ushort();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized int get_long()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_long();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized int get_ulong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_ulong();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized float get_float()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_float();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized double get_double()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_double();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized String get_string()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_string();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.Object get_reference()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_Object();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.TypeCode get_typecode()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_TypeCode();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized long get_longlong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_longlong();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized long get_ulonglong()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_ulonglong();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized char get_wchar()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_wchar();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized String get_wstring()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_wstring();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.Any get_any()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return any.extract_any();
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.DynamicAny.DynAny get_dyn_any()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        DynAny_impl da = (DynAny_impl) _OB_getDynAny();
        if (da == null) {
            org.omg.DynamicAny.DynAny comp = current_component();
            if (comp == null)
                throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();
            DynAny_impl impl = (DynAny_impl) comp;
            da = (DynAny_impl) impl._OB_getDynAny();
        }

        if (da == null)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        return da;
    }

    public synchronized java.io.Serializable get_val()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        if (destroyed_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST();

        org.omg.DynamicAny.DynAny comp = current_component();
        if (comp == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        //
        // Ensure the given value has the proper type
        //
        org.omg.CORBA.TypeCode tc = comp.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != org.omg.CORBA.TCKind.tk_value
                && origTC.kind() != org.omg.CORBA.TCKind.tk_value_box)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        DynAny_impl impl = (DynAny_impl) comp;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        impl._OB_marshal(out);
        InputStream in = (InputStream) out.create_input_stream();
        // This is not necessary
        // in._OB_ORBInstance(orbInstance_);

        return in.read_value();
    }

    public synchronized java.lang.Object get_abstract()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        org.omg.CORBA.TypeCode type = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(type);

        if (origTC.kind() != org.omg.CORBA.TCKind.tk_abstract_interface)
            throw new org.omg.DynamicAny.DynAnyPackage.TypeMismatch();

        try {
            return any.extract_Object();
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            try {
                return any.extract_Value();
            } catch (org.omg.CORBA.BAD_OPERATION e) {
                throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                    org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
            }
        }
    }

    public synchronized boolean[] get_boolean_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.BooleanSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized byte[] get_octet_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.OctetSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized char[] get_char_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.CharSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized char[] get_wchar_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.WCharSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized short[] get_short_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.ShortSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized short[] get_ushort_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.UShortSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized int[] get_long_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.LongSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized int[] get_ulong_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.ULongSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized long[] get_longlong_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.LongLongSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized long[] get_ulonglong_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.ULongLongSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized float[] get_float_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.FloatSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public synchronized double[] get_double_seq()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
            org.omg.DynamicAny.DynAnyPackage.InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new org.omg.DynamicAny.DynAnyPackage.InvalidValue();

        try {
            return org.omg.CORBA.DoubleSeqHelper.extract(any);
        } catch (org.omg.CORBA.BAD_OPERATION e) {
            throw (org.omg.DynamicAny.DynAnyPackage.TypeMismatch)new 
                org.omg.DynamicAny.DynAnyPackage.TypeMismatch().initCause(e);
        }
    }

    public abstract boolean seek(int index);

    public abstract void rewind();

    public abstract boolean next();

    public abstract int component_count();

    public abstract org.omg.DynamicAny.DynAny current_component()
            throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    // ------------------------------------------------------------------
    // Yoko internal functions
    // ------------------------------------------------------------------

    abstract void _OB_marshal(OutputStream out);

    abstract void _OB_marshal(OutputStream out, DynValueWriter dynValueWriter);

    abstract void _OB_unmarshal(InputStream in);

    abstract Any _OB_currentAny();

    abstract Any _OB_currentAnyValue();

    org.omg.DynamicAny.DynAny _OB_getDynAny() {
        return null;
    }

    boolean _OB_insertDynAny(org.omg.DynamicAny.DynAny value) {
        return false;
    }
}
