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
import org.apache.yoko.orb.OB.MinorCodes;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.TypeCodeFactory;
import org.apache.yoko.orb.OCI.Buffer;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BooleanSeqHelper;
import org.omg.CORBA.CharSeqHelper;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.DoubleSeqHelper;
import org.omg.CORBA.FloatSeqHelper;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.LongLongSeqHelper;
import org.omg.CORBA.LongSeqHelper;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA.ShortSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.ULongLongSeqHelper;
import org.omg.CORBA.ULongSeqHelper;
import org.omg.CORBA.UShortSeqHelper;
import org.omg.CORBA.WCharSeqHelper;
import org.omg.CORBA.portable.ValueBase;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

import java.io.Serializable;

abstract class DynAny_impl extends LocalObject implements
        DynAny {
    protected final DynAnyFactory factory_;

    protected final ORBInstance orbInstance_;

    protected final org.omg.CORBA.TypeCode type_;

    protected final org.omg.CORBA.TypeCode origType_;

    private DynAny_impl parent_;

    protected boolean destroyed_;

    DynAny_impl(DynAnyFactory factory, ORBInstance orbInstance, org.omg.CORBA.TypeCode type) {
        factory_ = factory;
        orbInstance_ = orbInstance;
        type_ = type;
        origType_ = TypeCode._OB_getOrigType(type_);
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    protected void checkValue(Any any, TCKind kind) throws TypeMismatch, InvalidValue {
        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode tc = TypeCodeFactory.createPrimitiveTC(kind);

        if (!any._OB_type().equivalent(tc))
            throw new TypeMismatch();
    }

    protected DynAny create(org.omg.CORBA.Any any, boolean adopt) {
        DynAny result = null;
        try {
            result = factory_.create_dyn_any(any);
        } catch (InconsistentTypeCode ex) {
            Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;
    }

    protected DynAny create(org.omg.CORBA.TypeCode tc, boolean adopt) {
        DynAny result = null;
        try {
            result = factory_.create_dyn_any_from_type_code(tc);
        } catch (InconsistentTypeCode ex) {
            Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;
    }

    protected DynAny prepare(org.omg.CORBA.TypeCode tc, DynValueReader dynValueReader, boolean adopt) {
        //
        // Use "create" if the instantiation of DynValues
        // must not be delayed
        //
        if (dynValueReader == null)
            return create(tc, adopt);

        DynAny result = null;
        DynAnyFactory_impl factory_impl = (DynAnyFactory_impl) factory_;

        try {
            result = factory_impl.prepare_dyn_any_from_type_code(tc,
                    dynValueReader);
        } catch (InconsistentTypeCode ex) {
            Assert._OB_assert(ex);
        }

        if (adopt)
            adoptChild(result);
        return result;

    }

    protected void adoptChild(DynAny d) {
        DynAny_impl impl = (DynAny_impl) d;
        impl.parent_ = this;
    }

    protected void notifyParent() {
        if (parent_ != null)
            parent_.childModified(this);
    }

    protected void childModified(DynAny p) {
        // do nothing
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    final public org.omg.CORBA.TypeCode type() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        return type_;
    }

    public abstract void assign(DynAny dyn_any)
            throws TypeMismatch;

    public abstract void from_any(org.omg.CORBA.Any value)
            throws TypeMismatch,
            InvalidValue;

    public abstract org.omg.CORBA.Any to_any();

    public abstract boolean equal(DynAny dyn_any);

    public void destroy() {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (parent_ == null)
            destroyed_ = true;
    }

    public abstract DynAny copy();

    public synchronized void insert_boolean(boolean value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_boolean);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_octet(byte value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_octet);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_char(char value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_char);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_short(short value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_short);

        any.replace(any.type(), (int) value);

        notifyParent();
    }

    public synchronized void insert_ushort(short value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_ushort);

        any.replace(any.type(), (int) value);

        notifyParent();
    }

    public synchronized void insert_long(int value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_long);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulong(int value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_ulong);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_float(float value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_float);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_double(double value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_double);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_string(String value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue() - we must accomodate bounded and
        // unbounded strings

        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != TCKind.tk_string)
            throw new TypeMismatch();

        //
        // Check for bounded string
        //
        try {
            int len = origTC.length();
            if (len > 0 && value.length() > len)
                throw new InvalidValue();
        } catch (BadKind ex) {
            Assert._OB_assert(ex);
        }

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_reference(org.omg.CORBA.Object value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue()

        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != TCKind.tk_objref
                && origTC.kind() != org.omg.CORBA_2_4.TCKind.tk_local_interface)
            throw new TypeMismatch();

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_typecode(org.omg.CORBA.TypeCode value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_TypeCode);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_longlong(long value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_longlong);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulonglong(long value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_ulonglong);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_wchar(char value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_wchar);

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_wstring(String value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        // Don't use checkValue() - we must accomodate bounded and
        // unbounded strings

        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode tc = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != TCKind.tk_wstring)
            throw new TypeMismatch();

        //
        // Check for bounded wstring
        //
        try {
            int len = origTC.length();
            if (len > 0 && value.length() > len)
                throw new InvalidValue();
        } catch (BadKind ex) {
            Assert._OB_assert(ex);
        }

        any.replace(tc, value);

        notifyParent();
    }

    public synchronized void insert_any(org.omg.CORBA.Any value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, TCKind.tk_any);

        Any val = new Any(value);
        val._OB_ORBInstance(orbInstance_);
        any.replace(any.type(), val);

        notifyParent();
    }

    public synchronized void insert_dyn_any(DynAny value)
            throws TypeMismatch,
            InvalidValue {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        if (value == null)
            throw new TypeMismatch();

        if (!_OB_insertDynAny(value)) {
            DynAny comp = current_component();
            if (comp == null)
                throw new InvalidValue();
            DynAny_impl impl = (DynAny_impl) comp;
            if (!impl._OB_insertDynAny(value))
                throw new TypeMismatch();
        }

        notifyParent();
    }

    public synchronized void insert_val(Serializable value)
            throws TypeMismatch,
            InvalidValue {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        //
        // TODO: Custom valuetypes are not currently supported
        //
        if (value instanceof CustomMarshal) {
            throw new BAD_PARAM(MinorCodes
                .describeBadParam(MinorCodes.MinorIncompatibleObjectType),
                MinorCodes.MinorIncompatibleObjectType,
                CompletionStatus.COMPLETED_NO);
        }

        DynAny comp = current_component();
        if (comp == null)
            throw new InvalidValue();

        //
        // Ensure the given value has the proper type
        //
        org.omg.CORBA.TypeCode tc = comp.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != TCKind.tk_value
                && origTC.kind() != TCKind.tk_value_box)
            throw new TypeMismatch();

        try {
            if (value instanceof ValueBase) {
                String id = origTC.id();
                String[] ids = ((ValueBase) value)
                        ._truncatable_ids();
                int i;
                for (i = 0; i < ids.length; i++)
                    if (id.equals(ids[i]))
                        break;

                if (i >= ids.length)
                    throw new TypeMismatch();
            }
        } catch (BadKind ex) {
            Assert._OB_assert(ex);
        }

        //
        // Create an any and invoke from_any
        //
        Any any = new Any(orbInstance_, tc, value);
        comp.from_any(any);

        notifyParent();
    }

    public synchronized void insert_abstract(Object value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode type = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(type);

        if (origTC.kind() != TCKind.tk_abstract_interface)
            throw new TypeMismatch();

        if (value == null)
            any.insert_Value(null, type);
        else if (value instanceof Serializable)
            any.insert_Value((Serializable) value, type);
        else if (value instanceof org.omg.CORBA.Object)
            any.insert_Object((org.omg.CORBA.Object) value, type);
        else
            throw new TypeMismatch();

        notifyParent();
    }

    public synchronized void insert_boolean_seq(boolean[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, BooleanSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_octet_seq(byte[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, OctetSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_char_seq(char[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, CharSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_wchar_seq(char[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, WCharSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_short_seq(short[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, ShortSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ushort_seq(short[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, UShortSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_long_seq(int[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, LongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulong_seq(int[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, ULongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_longlong_seq(long[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, LongLongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_ulonglong_seq(long[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, ULongLongSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_float_seq(float[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, FloatSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized void insert_double_seq(double[] value)
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        checkValue(any, DoubleSeqHelper.type().kind());

        any.replace(any.type(), value);

        notifyParent();
    }

    public synchronized boolean get_boolean()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_boolean();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized byte get_octet()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_octet();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized char get_char()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_char();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized short get_short()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_short();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized short get_ushort()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_ushort();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized int get_long()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_long();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized int get_ulong()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_ulong();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized float get_float()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_float();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized double get_double()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_double();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized String get_string()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_string();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.Object get_reference()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_Object();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.TypeCode get_typecode()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_TypeCode();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized long get_longlong()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_longlong();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized long get_ulonglong()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_ulonglong();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized char get_wchar()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_wchar();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized String get_wstring()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_wstring();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized org.omg.CORBA.Any get_any()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return any.extract_any();
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized DynAny get_dyn_any()
            throws TypeMismatch,
            InvalidValue {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        DynAny_impl da = (DynAny_impl) _OB_getDynAny();
        if (da == null) {
            DynAny comp = current_component();
            if (comp == null)
                throw new InvalidValue();
            DynAny_impl impl = (DynAny_impl) comp;
            da = (DynAny_impl) impl._OB_getDynAny();
        }

        if (da == null)
            throw new TypeMismatch();

        return da;
    }

    public synchronized Serializable get_val()
            throws TypeMismatch,
            InvalidValue {
        if (destroyed_)
            throw new OBJECT_NOT_EXIST();

        DynAny comp = current_component();
        if (comp == null)
            throw new InvalidValue();

        //
        // Ensure the given value has the proper type
        //
        org.omg.CORBA.TypeCode tc = comp.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(tc);
        if (origTC.kind() != TCKind.tk_value
                && origTC.kind() != TCKind.tk_value_box)
            throw new TypeMismatch();

        DynAny_impl impl = (DynAny_impl) comp;
        Buffer buf = new Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        impl._OB_marshal(out);
        InputStream in = out.create_input_stream();

        return in.read_value();
    }

    public synchronized Object get_abstract()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        org.omg.CORBA.TypeCode type = any.type();
        org.omg.CORBA.TypeCode origTC = TypeCode._OB_getOrigType(type);

        if (origTC.kind() != TCKind.tk_abstract_interface)
            throw new TypeMismatch();

        try {
            return any.extract_Object();
        } catch (BAD_OPERATION ex) {
            try {
                return any.extract_Value();
            } catch (BAD_OPERATION e) {
                throw (TypeMismatch)new
                    TypeMismatch().initCause(e);
            }
        }
    }

    public synchronized boolean[] get_boolean_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return BooleanSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized byte[] get_octet_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return OctetSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized char[] get_char_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return CharSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized char[] get_wchar_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return WCharSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized short[] get_short_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return ShortSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized short[] get_ushort_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return UShortSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized int[] get_long_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return LongSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized int[] get_ulong_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return ULongSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized long[] get_longlong_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return LongLongSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized long[] get_ulonglong_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return ULongLongSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized float[] get_float_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return FloatSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public synchronized double[] get_double_seq()
            throws TypeMismatch,
            InvalidValue {
        Any any = _OB_currentAny();

        if (any == null)
            throw new InvalidValue();

        try {
            return DoubleSeqHelper.extract(any);
        } catch (BAD_OPERATION e) {
            throw (TypeMismatch)new
                TypeMismatch().initCause(e);
        }
    }

    public abstract boolean seek(int index);

    public abstract void rewind();

    public abstract boolean next();

    public abstract int component_count();

    public abstract DynAny current_component()
            throws TypeMismatch;

    // ------------------------------------------------------------------
    // Yoko internal functions
    // ------------------------------------------------------------------

    abstract void _OB_marshal(OutputStream out);

    abstract void _OB_marshal(OutputStream out, DynValueWriter dynValueWriter);

    abstract void _OB_unmarshal(InputStream in);

    abstract Any _OB_currentAny();

    abstract Any _OB_currentAnyValue();

    DynAny _OB_getDynAny() {
        return null;
    }

    boolean _OB_insertDynAny(DynAny value) {
        return false;
    }
}
