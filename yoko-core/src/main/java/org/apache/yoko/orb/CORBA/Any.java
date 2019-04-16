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

package org.apache.yoko.orb.CORBA;

import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.TypeCodeFactory;
import org.apache.yoko.orb.OCI.BufferReader;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA_2_4.TCKind;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNativeNotSupported;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNoAlias;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNullValueNotAllowed;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadStringOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadOperation;
import static org.apache.yoko.orb.OB.MinorCodes.describeDataConversion;
import static org.apache.yoko.orb.OB.MinorCodes.describeMarshal;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.CORBA.TCKind._tk_Principal;
import static org.omg.CORBA.TCKind._tk_TypeCode;
import static org.omg.CORBA.TCKind._tk_abstract_interface;
import static org.omg.CORBA.TCKind._tk_alias;
import static org.omg.CORBA.TCKind._tk_any;
import static org.omg.CORBA.TCKind._tk_array;
import static org.omg.CORBA.TCKind._tk_boolean;
import static org.omg.CORBA.TCKind._tk_char;
import static org.omg.CORBA.TCKind._tk_double;
import static org.omg.CORBA.TCKind._tk_enum;
import static org.omg.CORBA.TCKind._tk_except;
import static org.omg.CORBA.TCKind._tk_fixed;
import static org.omg.CORBA.TCKind._tk_float;
import static org.omg.CORBA.TCKind._tk_long;
import static org.omg.CORBA.TCKind._tk_longlong;
import static org.omg.CORBA.TCKind._tk_native;
import static org.omg.CORBA.TCKind._tk_null;
import static org.omg.CORBA.TCKind._tk_objref;
import static org.omg.CORBA.TCKind._tk_octet;
import static org.omg.CORBA.TCKind._tk_sequence;
import static org.omg.CORBA.TCKind._tk_short;
import static org.omg.CORBA.TCKind._tk_string;
import static org.omg.CORBA.TCKind._tk_struct;
import static org.omg.CORBA.TCKind._tk_ulong;
import static org.omg.CORBA.TCKind._tk_ulonglong;
import static org.omg.CORBA.TCKind._tk_union;
import static org.omg.CORBA.TCKind._tk_ushort;
import static org.omg.CORBA.TCKind._tk_value;
import static org.omg.CORBA.TCKind._tk_value_box;
import static org.omg.CORBA.TCKind._tk_void;
import static org.omg.CORBA.TCKind._tk_wchar;
import static org.omg.CORBA.TCKind._tk_wstring;
import static org.omg.CORBA.TCKind.tk_abstract_interface;
import static org.omg.CORBA.TCKind.tk_objref;
import static org.omg.CORBA.TCKind.tk_value;
import static org.omg.CORBA.TCKind.tk_value_box;
import static org.omg.CORBA_2_4.TCKind.tk_local_interface;

final public class Any extends org.omg.CORBA.Any {
    private static final Logger logger = Logger.getLogger(Any.class.getName());
    
    private ORBInstance orbInstance_;

    private org.omg.CORBA.TypeCode type_;

    private org.omg.CORBA.TypeCode obType_;

    private org.omg.CORBA.TypeCode origType_;

    private Object value_;

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void checkValue(org.omg.CORBA.TCKind kind, boolean allowNull) throws BAD_OPERATION {
        if (origType_.kind().value() != kind.value())
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        if (!allowNull && value_ == null)
            throw new BAD_OPERATION(describeBadOperation(MinorNullValueNotAllowed), MinorNullValueNotAllowed, COMPLETED_NO);
    }

    private void setType(org.omg.CORBA.TypeCode tc) {
        //
        // Save the original TypeCode. Note that this TypeCode
        // could have been created by another ORB. Also note that
        // DII-based portable stubs (such as those built into the
        // JDK in 1.2/1.3) use the equals() method to compare
        // TypeCodes, so we *must* preserve the original.
        //
        type_ = tc;

        //
        // Get an equivalent Yoko TypeCode
        //
        if (tc instanceof TypeCode)
            obType_ = tc;
        else
            obType_ = TypeCode._OB_convertForeignTypeCode(tc);

        //
        // Cache the unaliased TypeCode
        //
        origType_ = TypeCode._OB_getOrigType(obType_);
    }

    private void readValue(org.omg.CORBA.portable.InputStream in) throws MARSHAL {
        int kind = origType_.kind().value();

        logger.fine("Reading ANY value of kind " + kind); 
        //
        // Spec says that calling read_value when a Streamable has
        // previously been inserted will update the Streamable
        //
        if (value_ instanceof Streamable
                && kind != _tk_value
                && kind != _tk_value_box
                && kind != _tk_abstract_interface) {
            ((Streamable) value_)._read(in);
            return;
        }

        switch (kind) {
        case _tk_null:
        case _tk_void:
            value_ = null;
            break;

        case _tk_short:
            value_ = (int) in.read_short();
            break;

        case _tk_long:
            value_ = in.read_long();
            break;

        case _tk_longlong:
            value_ = in.read_longlong();
            break;

        case _tk_ushort:
            value_ = (int) in.read_ushort();
            break;

        case _tk_ulong:

        case _tk_enum:
            value_ = in.read_ulong();
            break;

        case _tk_ulonglong:
            value_ = in.read_ulonglong();
            break;

        case _tk_float:
            value_ = in.read_float();
            break;

        case _tk_double:
            value_ = in.read_double();
            break;

        case _tk_boolean:
            value_ = in.read_boolean();
            break;

        case _tk_char:
            value_ = in.read_char();
            break;

        case _tk_wchar:
            value_ = in.read_wchar();
            break;

        case _tk_octet:
            value_ = in.read_octet();
            break;

        case _tk_any:
            value_ = in.read_any();
            break;

        case _tk_TypeCode:
            value_ = in.read_TypeCode();
            break;

        case _tk_Principal:
            value_ = in.read_Principal();
            break;

        case _tk_objref:
            value_ = in.read_Object();
            break;

        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array: {
            try (OutputStream out = new OutputStream()) {
                out._OB_ORBInstance(orbInstance_);
                out.write_InputStream(in, origType_);
                value_ = out.create_input_stream();
            }
            break;
        }

        case _tk_value:
        case _tk_value_box:
        case _tk_abstract_interface: {
            try {
                InputStream is = (InputStream) in;
                is.read_value(this, type_);
            } catch (ClassCastException ex) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream is = (org.omg.CORBA_2_3.portable.InputStream) in;
                    value_ = is.read_value(type_.id());
                } catch (BadKind e) {
                    _OB_assert(e);
                }
            }
            break;
        }

        case _tk_string: {
            try {
                String str = in.read_string();
                int len = origType_.length();
                if (len != 0 && str.length() > len)
                    throw new MARSHAL(String.format("string length (%d) exceeds bound (%d)", str.length(), len), MinorReadStringOverflow, COMPLETED_NO);
                value_ = str;
            } catch (BadKind ex) {
                _OB_assert(ex);
            }
            break;
        }

        case _tk_wstring: {
            try {
                String str = in.read_wstring();
                int len = origType_.length();
                if (len != 0 && str.length() > len)
                    throw new MARSHAL(String.format("wstring length (%d) exceeds bound (%d)", str.length(), len), MinorReadWStringOverflow, COMPLETED_NO);
                value_ = str;
            } catch (BadKind ex) {
                _OB_assert(ex);
            }
            break;
        }

        case _tk_fixed: {
            try {
                value_ = in.read_fixed().movePointLeft(origType_.fixed_scale());
            } catch (BadKind ex) {
                _OB_assert(ex);
            }

            break;
        }

        case _tk_native:
            throw new MARSHAL(
                describeMarshal(MinorNativeNotSupported), MinorNativeNotSupported, COMPLETED_NO);

        case TCKind._tk_local_interface:
            throw new MARSHAL(describeMarshal(MinorLocalObject), MinorLocalObject, COMPLETED_NO);

        case _tk_alias:
        default:
            throw new DATA_CONVERSION(describeDataConversion(MinorNoAlias), MinorNoAlias, COMPLETED_NO);
        }
    }

    private void copyFrom(Any any) {
        orbInstance_ = any.orbInstance_;
        type_ = any.type_;
        obType_ = any.obType_;
        origType_ = any.origType_;

        if (any.value_ instanceof Streamable) {
            readValue(any.create_input_stream());
            return;
        }

        if (any.value_ == null) {
            value_ = null;
            return;
        }

        int kind = origType_.kind().value();
        switch (kind) {
        case _tk_null:
        case _tk_void:
        case _tk_short:
        case _tk_long:
        case _tk_longlong:
        case _tk_ushort:
        case _tk_ulong:
        case _tk_ulonglong:
        case _tk_float:
        case _tk_double:
        case _tk_boolean:
        case _tk_char:
        case _tk_wchar:
        case _tk_octet:
        case _tk_enum:
        case _tk_string:
        case _tk_wstring:
        case _tk_fixed:
        case _tk_TypeCode:
        case _tk_Principal:
        case _tk_objref:
        case TCKind._tk_local_interface:

        case _tk_native:
            value_ = any.value_;
            break;

        case _tk_any:
        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array:
            readValue(any.create_input_stream());
            break;

        case _tk_value:
        case _tk_value_box:
        case _tk_abstract_interface:
            if (any.value_ instanceof InputStream)
                readValue(any.create_input_stream());
            else
                value_ = any.value_;
            break;

        case _tk_alias:
        default:
            _OB_assert("tk_alias not supported for copying");
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized boolean equal(org.omg.CORBA.Any a) {
        if (a == null)
            return false;

        if (this == a)
            return true;

        if (!type_.equal(a.type()))
            return false;

        Any any;
        try {
            any = (Any) a;
        } catch (ClassCastException ex) {
            //
            // Argument may have been created by a foreign singleton ORB,
            // so we'll use a temporary.
            //
            any = new Any(a);
        }

        if (value_ == any.value_)
            return true;

        if (value_ == null || any.value_ == null)
            return false;

        if (value_ instanceof Streamable && any.value_ instanceof Streamable) {
            OutputStream os1 = (OutputStream) create_output_stream();
            ((Streamable) value_)._write(os1);
            OutputStream os2 = (OutputStream) create_output_stream();
            ((Streamable) any.value_)._write(os2);
            return os1.writtenBytesEqual(os2);
        }

        int kind = origType_.kind().value();
        switch (kind) {
        case _tk_null:
        case _tk_void:
            return true;

        case _tk_short:
        case _tk_long:
        case _tk_longlong:
        case _tk_ushort:
        case _tk_ulong:
        case _tk_ulonglong:
        case _tk_float:
        case _tk_double:
        case _tk_boolean:
        case _tk_char:
        case _tk_wchar:
        case _tk_octet:
        case _tk_enum:
        case _tk_string:
        case _tk_wstring:
        case _tk_fixed:
            return value_.equals(any.value_);

        case _tk_any:
            return extract_any().equal(any.extract_any());

        case _tk_TypeCode:
            return extract_TypeCode().equal(any.extract_TypeCode());

        case _tk_Principal:
            return extract_Principal().equals(any.extract_Principal());

        case _tk_objref:
        case TCKind._tk_local_interface:
            return extract_Object()._is_equivalent(any.extract_Object());

        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array: {
            return compareValuesAsInputStreams(this, any);
        }

        case _tk_value:
        case _tk_value_box: {
            if (value_ instanceof InputStream && any.value_ instanceof InputStream) {
                return compareValuesAsInputStreams(this, any);
            } else
                return false;
        }

        case _tk_abstract_interface: {
            if (value_ instanceof org.omg.CORBA.Object && any.value_ instanceof org.omg.CORBA.Object) {
                return extract_Object()._is_equivalent(any.extract_Object());
            } else if (value_ instanceof InputStream && any.value_ instanceof InputStream) {
                return compareValuesAsInputStreams(this, any);
            }
            return false;
        }

        case _tk_native:
            return (value_ == any.value_);

        case _tk_alias:
        default:
            _OB_assert("tk_alias not supported for comparison");
        }

        return false; // The compiler needs this
    }

    private static boolean compareValuesAsInputStreams(Any any1, Any any2) {
        BufferReader buf1 = ((InputStream) any1.value_).getBuffer();
        BufferReader buf2 = ((InputStream) any2.value_).getBuffer();
        return buf1.dataEquals(buf2);
    }

    public synchronized org.omg.CORBA.TypeCode type() {
        return type_;
    }

    public synchronized void type(org.omg.CORBA.TypeCode tc) {
        setType(tc);
        value_ = null;
    }

    public synchronized void read_value(org.omg.CORBA.portable.InputStream in, org.omg.CORBA.TypeCode tc) throws MARSHAL {
        setType(tc);
        readValue(in);
    }

    public synchronized void write_value(org.omg.CORBA.portable.OutputStream out) {
        int kind = origType_.kind().value();

        if (value_ instanceof Streamable
                && kind != _tk_value
                && kind != _tk_value_box
                && kind != _tk_abstract_interface) {
            ((Streamable) value_)._write(out);
            return;
        }

        switch (kind) {
        case _tk_null:
        case _tk_void:
            break;

        case _tk_short:
            out.write_short(((Integer) value_).shortValue());
            break;

        case _tk_long:
            out.write_long((Integer) value_);
            break;

        case _tk_longlong:
            out.write_longlong((Long) value_);
            break;

        case _tk_ushort:
            out.write_ushort(((Integer) value_).shortValue());
            break;

        case _tk_ulong:

        case _tk_enum:
            out.write_ulong((Integer) value_);
            break;

        case _tk_ulonglong:
            out.write_ulonglong((Long) value_);
            break;

        case _tk_float:
            out.write_float((Float) value_);
            break;

        case _tk_double:
            out.write_double((Double) value_);
            break;

        case _tk_boolean:
            out.write_boolean((Boolean) value_);
            break;

        case _tk_char:
            out.write_char((Character) value_);
            break;

        case _tk_wchar:
            out.write_wchar((Character) value_);
            break;

        case _tk_octet:
            out.write_octet((Byte) value_);
            break;

        case _tk_any:
            out.write_any((org.omg.CORBA.Any) value_);
            break;

        case _tk_TypeCode:
            out.write_TypeCode((org.omg.CORBA.TypeCode) value_);
            break;

        case _tk_Principal:
            out.write_Principal((Principal) value_);
            break;

        case _tk_objref:
            out.write_Object((org.omg.CORBA.Object) value_);
            break;

        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array: {
            OutputStream o = (OutputStream) out;
            InputStream in = (InputStream) value_;
            in._OB_reset();
            o.write_InputStream(in, type_);
            break;
        }

        case _tk_value: {
            OutputStream o = (OutputStream) out;
            if (value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                o.write_InputStream(in, type_);
            } else
                o.write_value((Serializable) value_);
            break;
        }

        case _tk_value_box: {
            OutputStream o = (OutputStream) out;
            if (value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                o.write_InputStream(in, type_);
            } else {
                o.write_value((Serializable) value_, origType_, null);
            }
            break;
        }

        case _tk_string:
            out.write_string((String) value_);
            break;

        case _tk_wstring:
            out.write_wstring((String) value_);
            break;

        case _tk_fixed: {
            // TODO: check ranges here? compare scale against TypeCode?
            try {
                out.write_fixed(((BigDecimal) value_).movePointRight(origType_.fixed_scale()));
            } catch (BadKind ex) {
                _OB_assert(ex);
            }

            break;
        }

        case _tk_abstract_interface: {
            OutputStream o = (OutputStream) out;
            if (value_ != null && value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                _OB_assert(!in.read_boolean());
                o.write_abstract_interface(in.read_value());
            } else
                o.write_abstract_interface(value_);
            break;
        }

        case _tk_native:
            throw new MARSHAL(describeMarshal(MinorNativeNotSupported), MinorNativeNotSupported, COMPLETED_NO);

        case TCKind._tk_local_interface:
            throw new MARSHAL(describeMarshal(MinorLocalObject), MinorLocalObject, COMPLETED_NO);

        case _tk_alias:
        default:
            _OB_assert("unable to write tk_alias types");
        }
    }

    public synchronized org.omg.CORBA.portable.OutputStream create_output_stream() {
        // TODO:
        // Spec says that calling create_output_stream and
        // writing to the any will update the state of the
        // last streamable object, if present.
        OutputStream out = new OutputStream();
        out._OB_ORBInstance(orbInstance_);
        return out;
    }

    public synchronized org.omg.CORBA.portable.InputStream create_input_stream() {
        if (value_ instanceof InputStream) {
            return new InputStream(((InputStream) value_));
        } else {
            try (OutputStream out = new OutputStream()) {
                out._OB_ORBInstance(orbInstance_);
                write_value(out);
                return out.create_input_stream();
            }
        }
    }

    public synchronized short extract_short() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_short, false);
        return ((Integer) value_).shortValue();
    }

    public synchronized void insert_short(short val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_short));
        value_ = (int) val;
    }

    public synchronized int extract_long() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_long, false);
        return (Integer) value_;
    }

    public synchronized void insert_long(int val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_long));
        value_ = val;
    }

    public synchronized long extract_longlong() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_longlong, false);
        return (Long) value_;
    }

    public synchronized void insert_longlong(long val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_longlong));
        value_ = val;
    }

    public synchronized short extract_ushort() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ushort, false);
        return ((Integer) value_).shortValue();
    }

    public synchronized void insert_ushort(short val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_ushort));
        value_ = (int) val;
    }

    public synchronized int extract_ulong() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ulong, false);
        return (Integer) value_;
    }

    public synchronized void insert_ulong(int val) {
        type(TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_ulong));
        value_ = val;
    }

    public synchronized long extract_ulonglong() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ulonglong, false);
        return (Long) value_;
    }

    public synchronized void insert_ulonglong(long val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_ulonglong));
        value_ = val;
    }

    public synchronized boolean extract_boolean() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_boolean, false);
        return (Boolean) value_;
    }

    public synchronized void insert_boolean(boolean val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_boolean));
        value_ = val;
    }

    public synchronized char extract_char() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_char, false);
        return (Character) value_;
    }

    public synchronized void insert_char(char val) throws DATA_CONVERSION {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_char));
        value_ = val;
    }

    public synchronized char extract_wchar() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_wchar, false);
        return (Character) value_;
    }

    public synchronized void insert_wchar(char val) throws DATA_CONVERSION {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_wchar));
        value_ = val;
    }

    public synchronized byte extract_octet() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_octet, false);
        return (Byte) value_;
    }

    public synchronized void insert_octet(byte val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_octet));
        value_ = val;
    }

    public synchronized float extract_float() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_float, false);
        return (Float) value_;
    }

    public synchronized void insert_float(float val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_float));
        value_ = val;
    }

    public synchronized double extract_double() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_double, false);
        return (Double) value_;
    }

    public synchronized void insert_double(double val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_double));
        value_ = val;
    }

    public synchronized org.omg.CORBA.Any extract_any() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_any, false);
        return (org.omg.CORBA.Any) value_;
    }

    public synchronized void insert_any(org.omg.CORBA.Any val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_any));
        value_ = val;
    }

    public synchronized org.omg.CORBA.TypeCode extract_TypeCode() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_TypeCode, false);
        return (org.omg.CORBA.TypeCode) value_;
    }

    public synchronized void insert_TypeCode(org.omg.CORBA.TypeCode val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_TypeCode));
        value_ = val;
    }

    public synchronized Principal extract_Principal() throws BAD_OPERATION {
        // Deprecated by CORBA 2.2
        throw new NO_IMPLEMENT();
    }

    public synchronized void insert_Principal(Principal val) {
        // Deprecated by CORBA 2.2
        throw new NO_IMPLEMENT();
    }

    public synchronized org.omg.CORBA.Object extract_Object() throws BAD_OPERATION {
        org.omg.CORBA.TCKind kind = origType_.kind();
        if (kind != tk_objref && kind != tk_abstract_interface && kind != tk_local_interface) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (value_ != null && !(value_ instanceof org.omg.CORBA.Object)) {
            throw new BAD_OPERATION(
                describeBadOperation(MinorTypeMismatch),
                MinorTypeMismatch, COMPLETED_NO);
        }

        return (org.omg.CORBA.Object) value_;
    }

    public synchronized void insert_Object(org.omg.CORBA.Object val) {
        //
        // If we don't have an ORB instance, then try to get one from
        // the object reference
        //
        if (orbInstance_ == null && val != null) {
            try {
                Delegate d = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) val)._get_delegate();
                orbInstance_ = d._OB_ORBInstance();
            } catch (BAD_OPERATION ex) {
                // Object has no delegate - ignore
            }
        }

        org.omg.CORBA.TypeCode tc = TypeCodeFactory.createPrimitiveTC(tk_objref);
        insert_Object(val, tc);
    }

    public synchronized void insert_Object(org.omg.CORBA.Object val,
            org.omg.CORBA.TypeCode tc) {
        //
        // If we don't have an ORB instance, then try to get one from
        // the object reference
        //
        if (orbInstance_ == null && val != null) {
            try {
                Delegate d = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) val)
                        ._get_delegate();
                orbInstance_ = d._OB_ORBInstance();
            } catch (BAD_OPERATION ex) {
                // Object has no delegate - ignore
            }
        }

        type(tc);
        value_ = val;
    }

    public synchronized String extract_string() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_string, false);
        return (String) value_;
    }

    public synchronized void insert_string(String val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_string));
        value_ = val;
    }

    public synchronized String extract_wstring() throws BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_wstring, false);
        return (String) value_;
    }

    public synchronized void insert_wstring(String val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_wstring));
        value_ = val;
    }

    public Streamable extract_Streamable() throws BAD_INV_ORDER {
        if (!(value_ instanceof Streamable)) throw new BAD_INV_ORDER("Type mismatch");
        return (Streamable) value_;
    }

    public synchronized void insert_Streamable(Streamable val) {
        type(val._type());
        value_ = val;
    }

    public synchronized BigDecimal extract_fixed() {
        checkValue(org.omg.CORBA.TCKind.tk_fixed, false);
        return (BigDecimal) value_;
    }

    public synchronized void insert_fixed(BigDecimal val) {
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_fixed));
        value_ = val;
    }

    public synchronized void insert_fixed(BigDecimal val, org.omg.CORBA.TypeCode tc) throws BAD_INV_ORDER {
        type(tc);
        value_ = val;
    }

    public Serializable extract_Value() throws BAD_OPERATION {
        org.omg.CORBA.TCKind kind = origType_.kind();

        if (kind != tk_value && kind != tk_value_box && kind != tk_abstract_interface) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (kind == tk_abstract_interface && value_ instanceof org.omg.CORBA.Object) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (value_ instanceof InputStream) {
            InputStream in = (InputStream) value_;
            in._OB_reset();
            if (kind == tk_abstract_interface) _OB_assert(!in.read_boolean());
            return in.read_value();
        } else
            return (Serializable) value_;
    }

    public synchronized void insert_Value(Serializable val) {
        org.omg.CORBA.TypeCode tc = TypeCodeFactory.createPrimitiveTC(tk_value);
        insert_Value(val, tc);
    }

    public synchronized void insert_Value(Serializable val, org.omg.CORBA.TypeCode tc) throws MARSHAL {
        type(tc);

        value_ = val;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Any() {
        this((ORBInstance) null);
    }

    public Any(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
        type(TypeCodeFactory.createPrimitiveTC(org.omg.CORBA.TCKind.tk_null));
        value_ = null;
    }

    public Any(Any any) {
        copyFrom(any);
    }

    public Any(org.omg.CORBA.Any any) {
        //
        // This constructor creates a new Any using the standard interface
        //

        //
        // Optimization
        //
        if (any instanceof Any) {
            copyFrom((Any) any);
            return;
        }

        orbInstance_ = null;
        setType(any.type());

        switch (origType_.kind().value()) {
        case _tk_null:
        case _tk_void:
        case _tk_Principal:
            break;

        case _tk_short:
            value_ = (int) any.extract_short();
            break;

        case _tk_long:
            value_ = any.extract_long();
            break;

        case _tk_longlong:
            value_ = any.extract_longlong();
            break;

        case _tk_ushort:
            value_ = (int) any.extract_ushort();
            break;

        case _tk_ulong:
            value_ = any.extract_ulong();
            break;

        case _tk_ulonglong:
            value_ = any.extract_ulonglong();
            break;

        case _tk_float:
            value_ = any.extract_float();
            break;

        case _tk_double:
            value_ = any.extract_double();
            break;

        case _tk_boolean:
            value_ = any.extract_boolean();
            break;

        case _tk_char:
            value_ = any.extract_char();
            break;

        case _tk_wchar:
            value_ = any.extract_wchar();
            break;

        case _tk_octet:
            value_ = any.extract_octet();
            break;

        case _tk_string:
            value_ = any.extract_string();
            break;

        case _tk_wstring:
            value_ = any.extract_wstring();
            break;

        case _tk_fixed:
            value_ = any.extract_fixed();
            break;

        case _tk_TypeCode:
            value_ = any.extract_TypeCode();
            break;

        case _tk_objref:
        case _tk_abstract_interface:
        case TCKind._tk_local_interface:
            try {
                value_ = any.extract_Object();
                break;
            } catch (BAD_OPERATION ex) {
                //
                // Any must hold an abstract interface representing
                // a valuetype, so fall through to default case
                //
            }

        default:
            readValue(any.create_input_stream());
            break;
        }
    }

    public Any(ORBInstance orbInstance, org.omg.CORBA.TypeCode type, Object value) {
        orbInstance_ = orbInstance;
        setType(type);
        value_ = value;
    }

    public synchronized Object value() {
        return value_;
    }

    public synchronized void replace(org.omg.CORBA.TypeCode tc, Object value) {
        setType(tc);
        value_ = value;
    }

    public synchronized ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    public synchronized void _OB_ORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    public synchronized org.omg.CORBA.TypeCode _OB_type() {
        return obType_;
    }
}
