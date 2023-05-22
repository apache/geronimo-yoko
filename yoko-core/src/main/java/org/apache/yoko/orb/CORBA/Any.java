/*
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.CORBA;

import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.Streamable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;
import static org.apache.yoko.orb.CORBA.TypeCode._OB_convertForeignTypeCode;
import static org.apache.yoko.orb.CORBA.TypeCode._OB_getOrigType;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createPrimitiveTC;
import static org.apache.yoko.util.Assert.ensure;
import static org.apache.yoko.util.Assert.fail;
import static org.apache.yoko.util.MinorCodes.MinorLocalObject;
import static org.apache.yoko.util.MinorCodes.MinorNativeNotSupported;
import static org.apache.yoko.util.MinorCodes.MinorNoAlias;
import static org.apache.yoko.util.MinorCodes.MinorNullValueNotAllowed;
import static org.apache.yoko.util.MinorCodes.MinorReadStringOverflow;
import static org.apache.yoko.util.MinorCodes.MinorReadWStringOverflow;
import static org.apache.yoko.util.MinorCodes.MinorTypeMismatch;
import static org.apache.yoko.util.MinorCodes.describeBadOperation;
import static org.apache.yoko.util.MinorCodes.describeDataConversion;
import static org.apache.yoko.util.MinorCodes.describeMarshal;
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
import static org.omg.CORBA.TCKind.tk_TypeCode;
import static org.omg.CORBA.TCKind.tk_abstract_interface;
import static org.omg.CORBA.TCKind.tk_any;
import static org.omg.CORBA.TCKind.tk_boolean;
import static org.omg.CORBA.TCKind.tk_char;
import static org.omg.CORBA.TCKind.tk_double;
import static org.omg.CORBA.TCKind.tk_fixed;
import static org.omg.CORBA.TCKind.tk_float;
import static org.omg.CORBA.TCKind.tk_long;
import static org.omg.CORBA.TCKind.tk_longlong;
import static org.omg.CORBA.TCKind.tk_null;
import static org.omg.CORBA.TCKind.tk_objref;
import static org.omg.CORBA.TCKind.tk_octet;
import static org.omg.CORBA.TCKind.tk_short;
import static org.omg.CORBA.TCKind.tk_string;
import static org.omg.CORBA.TCKind.tk_ulong;
import static org.omg.CORBA.TCKind.tk_ulonglong;
import static org.omg.CORBA.TCKind.tk_ushort;
import static org.omg.CORBA.TCKind.tk_value;
import static org.omg.CORBA.TCKind.tk_value_box;
import static org.omg.CORBA.TCKind.tk_wchar;
import static org.omg.CORBA.TCKind.tk_wstring;
import static org.omg.CORBA_2_4.TCKind._tk_local_interface;
import static org.omg.CORBA_2_4.TCKind.tk_local_interface;

final public class Any extends org.omg.CORBA.Any {
    private static final Logger logger = getLogger(Any.class.getName());
    
    private ORBInstance orbInstance;
    private org.omg.CORBA.TypeCode typeCode;
    private TypeCode yokoTypeCode;
    private TypeCode origTypeCode;
    private Object value;

    @Override
    public String toString() {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            pw.println("Any {");
            pw.printf("\t%s = %s%n", "tc", typeCode);
            pw.printf("\t%s = %s%n", "ytc", equalTypeCodes(yokoTypeCode, typeCode) ? "tc" : yokoTypeCode);
            pw.printf("\t%s = %s%n", "otc", equalTypeCodes(origTypeCode, typeCode) ? "tc" : equalTypeCodes(origTypeCode, yokoTypeCode) ? "ytc" : origTypeCode);
            pw.printf("\t%s = %s%n", "value", value);
            pw.println("}");
            return sw.toString();
        } catch(IOException ignored) { return "Any { ???? }"; }
    }

    private boolean equalTypeCodes(org.omg.CORBA.TypeCode tc1, org.omg.CORBA.TypeCode tc2) {
        try {
            return (null != tc1) && tc1.equal(tc2);
        } catch (Throwable t) { return false; }
    }

    private void checkValue(TCKind kind) throws BAD_OPERATION {
        if (!requireNonNull(kind).equals(origTypeCode.kind()))
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        if (null == value)
            throw new BAD_OPERATION(describeBadOperation(MinorNullValueNotAllowed), MinorNullValueNotAllowed, COMPLETED_NO);
    }

    private void setType(org.omg.CORBA.TypeCode tc) {
        // Save the original TypeCode. Note that this TypeCode
        // could have been created by another ORB. Also note that
        // DII-based portable stubs (such as those built into the
        // JDK in 1.2/1.3) use the equals() method to compare
        // TypeCodes, so we *must* preserve the original.
        typeCode = tc;

        // Get an equivalent Yoko TypeCode
        yokoTypeCode = tc instanceof TypeCode ? (TypeCode) tc : _OB_convertForeignTypeCode(tc);

        //
        // Cache the unaliased TypeCode
        //
        origTypeCode = _OB_getOrigType(yokoTypeCode);
    }

    private void readValue(org.omg.CORBA.portable.InputStream in) throws MARSHAL {
        final int kind = origTypeCode.kind().value();

        logger.fine("Reading ANY value of kind " + kind); 
        //
        // Spec says that calling read_value when a Streamable has
        // previously been inserted will update the Streamable
        //
        if (value instanceof Streamable
                && kind != _tk_value
                && kind != _tk_value_box
                && kind != _tk_abstract_interface) {
            ((Streamable) value)._read(in);
            return;
        }

        switch (kind) {
        case _tk_null:
        case _tk_void:
            value = null;
            break;

        case _tk_short:
            value = (int) in.read_short();
            break;

        case _tk_long:
            value = in.read_long();
            break;

        case _tk_longlong:
            value = in.read_longlong();
            break;

        case _tk_ushort:
            value = (int) in.read_ushort();
            break;

        case _tk_ulong:

        case _tk_enum:
            value = in.read_ulong();
            break;

        case _tk_ulonglong:
            value = in.read_ulonglong();
            break;

        case _tk_float:
            value = in.read_float();
            break;

        case _tk_double:
            value = in.read_double();
            break;

        case _tk_boolean:
            value = in.read_boolean();
            break;

        case _tk_char:
            value = in.read_char();
            break;

        case _tk_wchar:
            value = in.read_wchar();
            break;

        case _tk_octet:
            value = in.read_octet();
            break;

        case _tk_any:
            value = in.read_any();
            break;

        case _tk_TypeCode:
            value = in.read_TypeCode();
            break;

        case _tk_Principal:
            value = in.read_Principal();
            break;

        case _tk_objref:
            value = in.read_Object();
            break;

        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array: {
            try (OutputStream out = new OutputStream()) {
                out._OB_ORBInstance(orbInstance);
                out.write_InputStream(in, origTypeCode);
                value = out.create_input_stream();
            }
            break;
        }

        case _tk_value:
        case _tk_value_box:
        case _tk_abstract_interface: {
            try {
                InputStream is = (InputStream) in;
                is.read_value(this, typeCode);
            } catch (ClassCastException ex) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream is = (org.omg.CORBA_2_3.portable.InputStream) in;
                    value = is.read_value(typeCode.id());
                } catch (BadKind e) {
                    throw fail(e);
                }
            }
            break;
        }

        case _tk_string: {
            try {
                String str = in.read_string();
                int len = origTypeCode.length();
                if (len != 0 && str.length() > len)
                    throw new MARSHAL(format("string length (%d) exceeds bound (%d)", str.length(), len), MinorReadStringOverflow, COMPLETED_NO);
                value = str;
            } catch (BadKind ex) {
                throw fail(ex);
            }
            break;
        }

        case _tk_wstring: {
            try {
                String str = in.read_wstring();
                int len = origTypeCode.length();
                if (len != 0 && str.length() > len)
                    throw new MARSHAL(format("wstring length (%d) exceeds bound (%d)", str.length(), len), MinorReadWStringOverflow, COMPLETED_NO);
                value = str;
            } catch (BadKind ex) {
                throw fail(ex);
            }
            break;
        }

        case _tk_fixed: {
            try {
                value = in.read_fixed().movePointLeft(origTypeCode.fixed_scale());
            } catch (BadKind ex) {
                throw fail(ex);
            }

            break;
        }

        case _tk_native:
            throw new MARSHAL(
                describeMarshal(MinorNativeNotSupported), MinorNativeNotSupported, COMPLETED_NO);

        case _tk_local_interface:
            throw new MARSHAL(describeMarshal(MinorLocalObject), MinorLocalObject, COMPLETED_NO);

        case _tk_alias:
        default:
            throw new DATA_CONVERSION(describeDataConversion(MinorNoAlias), MinorNoAlias, COMPLETED_NO);
        }
    }

    private void copyFrom(Any any) {
        orbInstance = any.orbInstance;
        typeCode = any.typeCode;
        yokoTypeCode = any.yokoTypeCode;
        origTypeCode = any.origTypeCode;

        if (any.value instanceof Streamable) {
            readValue(any.create_input_stream());
            return;
        }

        if (any.value == null) {
            value = null;
            return;
        }

        int kind = origTypeCode.kind().value();
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
        case _tk_local_interface:

        case _tk_native:
            value = any.value;
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
            if (any.value instanceof InputStream)
                readValue(any.create_input_stream());
            else
                value = any.value;
            break;

        case _tk_alias:
        default:
            throw fail("tk_alias not supported for copying");
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

        if (!typeCode.equal(a.type()))
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

        if (value == any.value)
            return true;

        if (value == null || any.value == null)
            return false;

        if (value instanceof Streamable && any.value instanceof Streamable) {
            OutputStream os1 = (OutputStream) create_output_stream();
            ((Streamable) value)._write(os1);
            OutputStream os2 = (OutputStream) create_output_stream();
            ((Streamable) any.value)._write(os2);
            return os1.writtenBytesEqual(os2);
        }

        int kind = origTypeCode.kind().value();
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
            return value.equals(any.value);

        case _tk_any:
            return extract_any().equal(any.extract_any());

        case _tk_TypeCode:
            return extract_TypeCode().equal(any.extract_TypeCode());

        case _tk_Principal:
            return extract_Principal().equals(any.extract_Principal());

        case _tk_objref:
        case _tk_local_interface:
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
            if (value instanceof InputStream && any.value instanceof InputStream) {
                return compareValuesAsInputStreams(this, any);
            } else
                return false;
        }

        case _tk_abstract_interface: {
            if (value instanceof org.omg.CORBA.Object && any.value instanceof org.omg.CORBA.Object) {
                return extract_Object()._is_equivalent(any.extract_Object());
            } else if (value instanceof InputStream && any.value instanceof InputStream) {
                return compareValuesAsInputStreams(this, any);
            }
            return false;
        }

        case _tk_native:
            return (value == any.value);

        case _tk_alias:
        default:
            throw fail("tk_alias not supported for comparison");
        }
    }

    private static boolean compareValuesAsInputStreams(Any any1, Any any2) {
        ReadBuffer buf1 = ((InputStream) any1.value).getBuffer();
        ReadBuffer buf2 = ((InputStream) any2.value).getBuffer();
        return buf1.dataEquals(buf2);
    }

    public synchronized org.omg.CORBA.TypeCode type() {
        return typeCode;
    }

    public synchronized void type(org.omg.CORBA.TypeCode tc) {
        setType(tc);
        value = null;
    }

    public synchronized void read_value(org.omg.CORBA.portable.InputStream in, org.omg.CORBA.TypeCode tc) throws MARSHAL {
        setType(tc);
        readValue(in);
    }

    public synchronized void write_value(org.omg.CORBA.portable.OutputStream out) {
        int kind = origTypeCode.kind().value();

        if (value instanceof Streamable
                && kind != _tk_value
                && kind != _tk_value_box
                && kind != _tk_abstract_interface) {
            ((Streamable) value)._write(out);
            return;
        }

        switch (kind) {
        case _tk_null:
        case _tk_void:
            break;

        case _tk_short:
            out.write_short(((Integer) value).shortValue());
            break;

        case _tk_long:
            out.write_long((Integer) value);
            break;

        case _tk_longlong:
            out.write_longlong((Long) value);
            break;

        case _tk_ushort:
            out.write_ushort(((Integer) value).shortValue());
            break;

        case _tk_ulong:

        case _tk_enum:
            out.write_ulong((Integer) value);
            break;

        case _tk_ulonglong:
            out.write_ulonglong((Long) value);
            break;

        case _tk_float:
            out.write_float((Float) value);
            break;

        case _tk_double:
            out.write_double((Double) value);
            break;

        case _tk_boolean:
            out.write_boolean((Boolean) value);
            break;

        case _tk_char:
            out.write_char((Character) value);
            break;

        case _tk_wchar:
            out.write_wchar((Character) value);
            break;

        case _tk_octet:
            out.write_octet((Byte) value);
            break;

        case _tk_any:
            out.write_any((org.omg.CORBA.Any) value);
            break;

        case _tk_TypeCode:
            out.write_TypeCode((org.omg.CORBA.TypeCode) value);
            break;

        case _tk_Principal:
            out.write_Principal((Principal) value);
            break;

        case _tk_objref:
            out.write_Object((org.omg.CORBA.Object) value);
            break;

        case _tk_struct:
        case _tk_except:
        case _tk_union:
        case _tk_sequence:
        case _tk_array: {
            OutputStream o = (OutputStream) out;
            InputStream in = (InputStream) value;
            in._OB_reset();
            o.write_InputStream(in, typeCode);
            break;
        }

        case _tk_value: {
            OutputStream o = (OutputStream) out;
            if (value instanceof InputStream) {
                InputStream in = (InputStream) value;
                in._OB_reset();
                o.write_InputStream(in, typeCode);
            } else
                o.write_value((Serializable) value);
            break;
        }

        case _tk_value_box: {
            OutputStream o = (OutputStream) out;
            if (value instanceof InputStream) {
                InputStream in = (InputStream) value;
                in._OB_reset();
                o.write_InputStream(in, typeCode);
            } else {
                o.write_value((Serializable) value, origTypeCode, null);
            }
            break;
        }

        case _tk_string:
            out.write_string((String) value);
            break;

        case _tk_wstring:
            out.write_wstring((String) value);
            break;

        case _tk_fixed: {
            // TODO: check ranges here? compare scale against TypeCode?
            try {
                out.write_fixed(((BigDecimal) value).movePointRight(origTypeCode.fixed_scale()));
            } catch (BadKind ex) {
                throw fail(ex);
            }

            break;
        }

        case _tk_abstract_interface: {
            OutputStream o = (OutputStream) out;
            if (value != null && value instanceof InputStream) {
                InputStream in = (InputStream) value;
                in._OB_reset();
                ensure(!in.read_boolean());
                o.write_abstract_interface(in.read_value());
            } else
                o.write_abstract_interface(value);
            break;
        }

        case _tk_native:
            throw new MARSHAL(describeMarshal(MinorNativeNotSupported), MinorNativeNotSupported, COMPLETED_NO);

        case _tk_local_interface:
            throw new MARSHAL(describeMarshal(MinorLocalObject), MinorLocalObject, COMPLETED_NO);

        case _tk_alias:
        default:
            throw fail("unable to write tk_alias types");
        }
    }

    public synchronized org.omg.CORBA.portable.OutputStream create_output_stream() {
        // TODO:
        // Spec says that calling create_output_stream and
        // writing to the any will update the state of the
        // last streamable object, if present.
        OutputStream out = new OutputStream();
        out._OB_ORBInstance(orbInstance);
        return out;
    }

    public synchronized org.omg.CORBA.portable.InputStream create_input_stream() {
        if (value instanceof InputStream) {
            return new InputStream(((InputStream) value));
        } else {
            try (OutputStream out = new OutputStream()) {
                out._OB_ORBInstance(orbInstance);
                write_value(out);
                return out.create_input_stream();
            }
        }
    }

    public synchronized short extract_short() throws BAD_OPERATION {
        checkValue(tk_short);
        return ((Integer) value).shortValue();
    }

    public synchronized void insert_short(short val) {
        type(createPrimitiveTC(tk_short));
        value = (int) val;
    }

    public synchronized int extract_long() throws BAD_OPERATION {
        checkValue(tk_long);
        return (Integer) value;
    }

    public synchronized void insert_long(int val) {
        type(createPrimitiveTC(tk_long));
        value = val;
    }

    public synchronized long extract_longlong() throws BAD_OPERATION {
        checkValue(tk_longlong);
        return (Long) value;
    }

    public synchronized void insert_longlong(long val) {
        type(createPrimitiveTC(tk_longlong));
        value = val;
    }

    public synchronized short extract_ushort() throws BAD_OPERATION {
        checkValue(tk_ushort);
        return ((Integer) value).shortValue();
    }

    public synchronized void insert_ushort(short val) {
        type(createPrimitiveTC(tk_ushort));
        value = (int) val;
    }

    public synchronized int extract_ulong() throws BAD_OPERATION {
        checkValue(tk_ulong);
        return (Integer) value;
    }

    public synchronized void insert_ulong(int val) {
        type(createPrimitiveTC(tk_ulong));
        value = val;
    }

    public synchronized long extract_ulonglong() throws BAD_OPERATION {
        checkValue(tk_ulonglong);
        return (Long) value;
    }

    public synchronized void insert_ulonglong(long val) {
        type(createPrimitiveTC(tk_ulonglong));
        value = val;
    }

    public synchronized boolean extract_boolean() throws BAD_OPERATION {
        checkValue(tk_boolean);
        return (Boolean) value;
    }

    public synchronized void insert_boolean(boolean val) {
        type(createPrimitiveTC(tk_boolean));
        value = val;
    }

    public synchronized char extract_char() throws BAD_OPERATION {
        checkValue(tk_char);
        return (Character) value;
    }

    public synchronized void insert_char(char val) throws DATA_CONVERSION {
        type(createPrimitiveTC(tk_char));
        value = val;
    }

    public synchronized char extract_wchar() throws BAD_OPERATION {
        checkValue(tk_wchar);
        return (Character) value;
    }

    public synchronized void insert_wchar(char val) throws DATA_CONVERSION {
        type(createPrimitiveTC(tk_wchar));
        value = val;
    }

    public synchronized byte extract_octet() throws BAD_OPERATION {
        checkValue(tk_octet);
        return (Byte) value;
    }

    public synchronized void insert_octet(byte val) {
        type(createPrimitiveTC(tk_octet));
        value = val;
    }

    public synchronized float extract_float() throws BAD_OPERATION {
        checkValue(tk_float);
        return (Float) value;
    }

    public synchronized void insert_float(float val) {
        type(createPrimitiveTC(tk_float));
        value = val;
    }

    public synchronized double extract_double() throws BAD_OPERATION {
        checkValue(tk_double);
        return (Double) value;
    }

    public synchronized void insert_double(double val) {
        type(createPrimitiveTC(tk_double));
        value = val;
    }

    public synchronized org.omg.CORBA.Any extract_any() throws BAD_OPERATION {
        checkValue(tk_any);
        return (org.omg.CORBA.Any) value;
    }

    public synchronized void insert_any(org.omg.CORBA.Any val) {
        type(createPrimitiveTC(tk_any));
        value = val;
    }

    public synchronized org.omg.CORBA.TypeCode extract_TypeCode() throws BAD_OPERATION {
        checkValue(tk_TypeCode);
        return (org.omg.CORBA.TypeCode) value;
    }

    public synchronized void insert_TypeCode(org.omg.CORBA.TypeCode val) {
        type(createPrimitiveTC(tk_TypeCode));
        value = val;
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
        TCKind kind = origTypeCode.kind();
        if (kind != tk_objref && kind != tk_abstract_interface && kind != tk_local_interface) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (value != null && !(value instanceof org.omg.CORBA.Object)) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        return (org.omg.CORBA.Object) value;
    }

    public synchronized void insert_Object(org.omg.CORBA.Object val) {
        //
        // If we don't have an ORB instance, then try to get one from
        // the object reference
        //
        if (null == orbInstance && null != val) {
            try {
                Delegate d = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) val)._get_delegate();
                orbInstance = d._OB_ORBInstance();
            } catch (BAD_OPERATION ignored) {
                // Object has no delegate - ignore
            }
        }

        org.omg.CORBA.TypeCode tc = createPrimitiveTC(tk_objref);
        insert_Object(val, tc);
    }

    public synchronized void insert_Object(org.omg.CORBA.Object val,
            org.omg.CORBA.TypeCode tc) {
        //
        // If we don't have an ORB instance, then try to get one from
        // the object reference
        //
        if (null == orbInstance && null != val) {
            try {
                Delegate d = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) val)._get_delegate();
                orbInstance = d._OB_ORBInstance();
            } catch (BAD_OPERATION ignored) {
                // Object has no delegate - ignore
            }
        }

        type(tc);
        value = val;
    }

    public synchronized String extract_string() throws BAD_OPERATION {
        checkValue(tk_string);
        return (String) value;
    }

    public synchronized void insert_string(String val) {
        type(createPrimitiveTC(tk_string));
        value = val;
    }

    public synchronized String extract_wstring() throws BAD_OPERATION {
        checkValue(tk_wstring);
        return (String) value;
    }

    public synchronized void insert_wstring(String val) {
        type(createPrimitiveTC(tk_wstring));
        value = val;
    }

    public Streamable extract_Streamable() throws BAD_INV_ORDER {
        if (!(value instanceof Streamable)) throw new BAD_INV_ORDER("Type mismatch");
        return (Streamable) value;
    }

    public synchronized void insert_Streamable(Streamable val) {
        type(val._type());
        value = val;
    }

    public synchronized BigDecimal extract_fixed() {
        checkValue(tk_fixed);
        return (BigDecimal) value;
    }

    public synchronized void insert_fixed(BigDecimal val) {
        type(createPrimitiveTC(tk_fixed));
        value = val;
    }

    public synchronized void insert_fixed(BigDecimal val, org.omg.CORBA.TypeCode tc) throws BAD_INV_ORDER {
        type(tc);
        value = val;
    }

    public Serializable extract_Value() throws BAD_OPERATION {
        TCKind kind = origTypeCode.kind();

        if (tk_value != kind && tk_value_box != kind && tk_abstract_interface != kind) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (tk_abstract_interface == kind && value instanceof org.omg.CORBA.Object) {
            throw new BAD_OPERATION(describeBadOperation(MinorTypeMismatch), MinorTypeMismatch, COMPLETED_NO);
        }

        if (value instanceof InputStream) {
            InputStream in = (InputStream) value;
            in._OB_reset();
            if (kind == tk_abstract_interface) ensure(!in.read_boolean());
            return in.read_value();
        } else
            return (Serializable) value;
    }

    public synchronized void insert_Value(Serializable val) {
        org.omg.CORBA.TypeCode tc = createPrimitiveTC(tk_value);
        insert_Value(val, tc);
    }

    public synchronized void insert_Value(Serializable val, org.omg.CORBA.TypeCode tc) throws MARSHAL {
        type(tc);

        value = val;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Any() {
        this((ORBInstance) null);
    }

    public Any(ORBInstance orbInstance) {
        this.orbInstance = orbInstance;
        type(createPrimitiveTC(tk_null));
        value = null;
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

        orbInstance = null;
        setType(any.type());

        switch (origTypeCode.kind().value()) {
        case _tk_null:
        case _tk_void:
        case _tk_Principal:
            break;

        case _tk_short:
            value = (int) any.extract_short();
            break;

        case _tk_long:
            value = any.extract_long();
            break;

        case _tk_longlong:
            value = any.extract_longlong();
            break;

        case _tk_ushort:
            value = (int) any.extract_ushort();
            break;

        case _tk_ulong:
            value = any.extract_ulong();
            break;

        case _tk_ulonglong:
            value = any.extract_ulonglong();
            break;

        case _tk_float:
            value = any.extract_float();
            break;

        case _tk_double:
            value = any.extract_double();
            break;

        case _tk_boolean:
            value = any.extract_boolean();
            break;

        case _tk_char:
            value = any.extract_char();
            break;

        case _tk_wchar:
            value = any.extract_wchar();
            break;

        case _tk_octet:
            value = any.extract_octet();
            break;

        case _tk_string:
            value = any.extract_string();
            break;

        case _tk_wstring:
            value = any.extract_wstring();
            break;

        case _tk_fixed:
            value = any.extract_fixed();
            break;

        case _tk_TypeCode:
            value = any.extract_TypeCode();
            break;

        case _tk_objref:
        case _tk_abstract_interface:
        case _tk_local_interface:
            try {
                value = any.extract_Object();
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
        this.orbInstance = orbInstance;
        setType(type);
        this.value = value;
    }

    public synchronized Object value() {
        return value;
    }

    public synchronized void replace(org.omg.CORBA.TypeCode tc, Object value) {
        setType(tc);
        this.value = value;
    }

    public synchronized void _OB_ORBInstance(ORBInstance orbInstance) {
        this.orbInstance = orbInstance;
    }

    public synchronized org.omg.CORBA.TypeCode _OB_type() {
        return yokoTypeCode;
    }
}
