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
 
import java.util.logging.Logger;
import java.util.logging.Level;
 
import org.apache.yoko.orb.OB.MinorCodes;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;

final public class Any extends org.omg.CORBA.Any {
    static final Logger logger = Logger.getLogger(Any.class.getName());
    
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    private org.omg.CORBA.TypeCode type_;

    private org.omg.CORBA.TypeCode obType_;

    private org.omg.CORBA.TypeCode origType_;

    private java.lang.Object value_;

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void checkValue(org.omg.CORBA.TCKind kind, boolean allowNull)
            throws org.omg.CORBA.BAD_OPERATION {
        if (origType_.kind().value() != kind.value())
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, CompletionStatus.COMPLETED_NO);
        if (!allowNull && value_ == null)
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorNullValueNotAllowed), 
                org.apache.yoko.orb.OB.MinorCodes.MinorNullValueNotAllowed, CompletionStatus.COMPLETED_NO);
    }

    private boolean compare(org.apache.yoko.orb.OCI.Buffer buf1,
            org.apache.yoko.orb.OCI.Buffer buf2) {
        int len1 = buf1.length();
        int len2 = buf2.length();
        if (len1 != len2)
            return false;

        byte[] data1 = buf1.data();
        byte[] data2 = buf2.data();
        for (int i = 0; i < len1; i++)
            if (data1[i] != data2[i])
                return false;

        return true;
    }

    public void setType(org.omg.CORBA.TypeCode tc) {
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

    private void readValue(org.omg.CORBA.portable.InputStream in) {
        int kind = origType_.kind().value();

        logger.fine("Reading ANY value of kind " + kind); 
        //
        // Spec says that calling read_value when a Streamable has
        // previously been inserted will update the Streamable
        //
        if (value_ instanceof org.omg.CORBA.portable.Streamable
                && kind != org.omg.CORBA.TCKind._tk_value
                && kind != org.omg.CORBA.TCKind._tk_value_box
                && kind != org.omg.CORBA.TCKind._tk_abstract_interface) {
            ((org.omg.CORBA.portable.Streamable) value_)._read(in);
            return;
        }

        switch (kind) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
            value_ = null;
            break;

        case org.omg.CORBA.TCKind._tk_short:
            value_ = new Integer(in.read_short());
            break;

        case org.omg.CORBA.TCKind._tk_long:
            value_ = new Integer(in.read_long());
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
            value_ = new Long(in.read_longlong());
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            value_ = new Integer(in.read_ushort());
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            value_ = new Integer(in.read_ulong());
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            value_ = new Long(in.read_ulonglong());
            break;

        case org.omg.CORBA.TCKind._tk_float:
            value_ = new Float(in.read_float());
            break;

        case org.omg.CORBA.TCKind._tk_double:
            value_ = new Double(in.read_double());
            break;

        case org.omg.CORBA.TCKind._tk_boolean:
            value_ = Boolean.valueOf(in.read_boolean());
            break;

        case org.omg.CORBA.TCKind._tk_char:
            value_ = new Character(in.read_char());
            break;

        case org.omg.CORBA.TCKind._tk_wchar:
            value_ = new Character(in.read_wchar());
            break;

        case org.omg.CORBA.TCKind._tk_octet:
            value_ = new Byte(in.read_octet());
            break;

        case org.omg.CORBA.TCKind._tk_any:
            value_ = in.read_any();
            break;

        case org.omg.CORBA.TCKind._tk_TypeCode:
            value_ = in.read_TypeCode();
            break;

        case org.omg.CORBA.TCKind._tk_Principal:
            value_ = in.read_Principal();
            break;

        case org.omg.CORBA.TCKind._tk_objref:
            value_ = in.read_Object();
            break;

        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
        case org.omg.CORBA.TCKind._tk_union:
        case org.omg.CORBA.TCKind._tk_sequence:
        case org.omg.CORBA.TCKind._tk_array: {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            OutputStream out = new OutputStream(buf);
            out._OB_ORBInstance(orbInstance_);
            out.write_InputStream(in, origType_);
            InputStream is = (InputStream) out.create_input_stream();
            // This is not necessary
            // in._OB_ORBInstance(orbInstance_);
            value_ = is;
            break;
        }

        case org.omg.CORBA.TCKind._tk_value:
        case org.omg.CORBA.TCKind._tk_value_box:
        case org.omg.CORBA.TCKind._tk_abstract_interface: {
            try {
                InputStream is = (InputStream) in;
// this is a useful tracepoint, but produces a lot of data, so turn on only 
// if really needed. 
//              if (logger.isLoggable(Level.FINEST)) {
//                  logger.finest("Reading value from \n\n" + is.dumpData()); 
//              }
                is.read_value(this, type_);
            } catch (ClassCastException ex) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream is = (org.omg.CORBA_2_3.portable.InputStream) in;
                    value_ = is.read_value(type_.id());
                } catch (org.omg.CORBA.TypeCodePackage.BadKind e) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(e);
                }
            }
            break;
        }

        case org.omg.CORBA.TCKind._tk_enum:
            value_ = new Integer(in.read_ulong());
            break;

        case org.omg.CORBA.TCKind._tk_string: {
            try {
                String str = in.read_string();
                int len = origType_.length();
                if (len != 0 && str.length() > len)
                    throw new org.omg.CORBA.MARSHAL("string length ("
                            + str.length() + ") exceeds " + "bound (" + len
                            + ")",  
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadStringOverflow, 
                            CompletionStatus.COMPLETED_NO);
                value_ = str;
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
            break;
        }

        case org.omg.CORBA.TCKind._tk_wstring: {
            try {
                String str = in.read_wstring();
                int len = origType_.length();
                if (len != 0 && str.length() > len)
                    throw new org.omg.CORBA.MARSHAL("wstring length ("
                            + str.length() + ") exceeds " + "bound (" + len
                            + ")", 
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow, 
                            CompletionStatus.COMPLETED_NO);
                value_ = str;
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
            break;
        }

        case org.omg.CORBA.TCKind._tk_fixed: {
            try {
                value_ = in.read_fixed().movePointLeft(origType_.fixed_scale());
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }

            break;
        }

        case org.omg.CORBA.TCKind._tk_native:
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorNativeNotSupported),
                org.apache.yoko.orb.OB.MinorCodes.MinorNativeNotSupported,  
                CompletionStatus.COMPLETED_NO);

        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject),
                org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        case org.omg.CORBA.TCKind._tk_alias:
        default:
            throw new org.omg.CORBA.DATA_CONVERSION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeDataConversion(org.apache.yoko.orb.OB.MinorCodes.MinorNoAlias),
                org.apache.yoko.orb.OB.MinorCodes.MinorNoAlias,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    private void copyFrom(Any any) {
        orbInstance_ = any.orbInstance_;
        type_ = any.type_;
        obType_ = any.obType_;
        origType_ = any.origType_;

        if (any.value_ instanceof org.omg.CORBA.portable.Streamable) {
            readValue(any.create_input_stream());
            return;
        }

        if (any.value_ == null) {
            value_ = null;
            return;
        }

        int kind = origType_.kind().value();
        switch (kind) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
        case org.omg.CORBA.TCKind._tk_short:
        case org.omg.CORBA.TCKind._tk_long:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ushort:
        case org.omg.CORBA.TCKind._tk_ulong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_float:
        case org.omg.CORBA.TCKind._tk_double:
        case org.omg.CORBA.TCKind._tk_boolean:
        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_octet:
        case org.omg.CORBA.TCKind._tk_enum:
        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_wstring:
        case org.omg.CORBA.TCKind._tk_fixed:
        case org.omg.CORBA.TCKind._tk_TypeCode:
        case org.omg.CORBA.TCKind._tk_Principal:
        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            value_ = any.value_;
            break;

        case org.omg.CORBA.TCKind._tk_any:
        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
        case org.omg.CORBA.TCKind._tk_union:
        case org.omg.CORBA.TCKind._tk_sequence:
        case org.omg.CORBA.TCKind._tk_array:
            readValue(any.create_input_stream());
            break;

        case org.omg.CORBA.TCKind._tk_value:
        case org.omg.CORBA.TCKind._tk_value_box:
        case org.omg.CORBA.TCKind._tk_abstract_interface:
            if (any.value_ instanceof InputStream)
                readValue(any.create_input_stream());
            else
                value_ = any.value_;
            break;

        case org.omg.CORBA.TCKind._tk_native:
            value_ = any.value_;
            break;

        case org.omg.CORBA.TCKind._tk_alias:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("tk_alias not supported for copying");
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

        Any any = null;
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

        if (value_ instanceof org.omg.CORBA.portable.Streamable
                && any.value_ instanceof org.omg.CORBA.portable.Streamable) {
            OutputStream os1 = (OutputStream) create_output_stream();
            ((org.omg.CORBA.portable.Streamable) value_)._write(os1);
            OutputStream os2 = (OutputStream) create_output_stream();
            ((org.omg.CORBA.portable.Streamable) any.value_)._write(os2);
            return compare(os1._OB_buffer(), os2._OB_buffer());
        }

        int kind = origType_.kind().value();
        switch (kind) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
            return true;

        case org.omg.CORBA.TCKind._tk_short:
        case org.omg.CORBA.TCKind._tk_long:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ushort:
        case org.omg.CORBA.TCKind._tk_ulong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_float:
        case org.omg.CORBA.TCKind._tk_double:
        case org.omg.CORBA.TCKind._tk_boolean:
        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_octet:
        case org.omg.CORBA.TCKind._tk_enum:
        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_wstring:
        case org.omg.CORBA.TCKind._tk_fixed:
            return value_.equals(any.value_);

        case org.omg.CORBA.TCKind._tk_any:
            return extract_any().equal(any.extract_any());

        case org.omg.CORBA.TCKind._tk_TypeCode:
            return extract_TypeCode().equal(any.extract_TypeCode());

        case org.omg.CORBA.TCKind._tk_Principal:
            return extract_Principal().equals(any.extract_Principal());

        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            return extract_Object()._is_equivalent(any.extract_Object());

        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
        case org.omg.CORBA.TCKind._tk_union:
        case org.omg.CORBA.TCKind._tk_sequence:
        case org.omg.CORBA.TCKind._tk_array: {
            org.apache.yoko.orb.OCI.Buffer buf1 = ((InputStream) value_)
                    ._OB_buffer();
            org.apache.yoko.orb.OCI.Buffer buf2 = ((InputStream) any.value_)
                    ._OB_buffer();
            return compare(buf1, buf2);
        }

        case org.omg.CORBA.TCKind._tk_value:
        case org.omg.CORBA.TCKind._tk_value_box: {
            if (value_ instanceof InputStream
                    && any.value_ instanceof InputStream) {
                org.apache.yoko.orb.OCI.Buffer buf1 = ((InputStream) value_)
                        ._OB_buffer();
                org.apache.yoko.orb.OCI.Buffer buf2 = ((InputStream) any.value_)
                        ._OB_buffer();
                return compare(buf1, buf2);
            } else
                return false;
        }

        case org.omg.CORBA.TCKind._tk_abstract_interface: {
            if (value_ instanceof org.omg.CORBA.Object
                    && any.value_ instanceof org.omg.CORBA.Object) {
                return extract_Object()._is_equivalent(any.extract_Object());
            } else if (value_ instanceof InputStream
                    && any.value_ instanceof InputStream) {
                org.apache.yoko.orb.OCI.Buffer buf1 = ((InputStream) value_)
                        ._OB_buffer();
                org.apache.yoko.orb.OCI.Buffer buf2 = ((InputStream) any.value_)
                        ._OB_buffer();
                return compare(buf1, buf2);
            }
            return false;
        }

        case org.omg.CORBA.TCKind._tk_native:
            return (value_ == any.value_);

        case org.omg.CORBA.TCKind._tk_alias:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("tk_alias not supported for comparison");
        }

        return false; // The compiler needs this
    }

    public synchronized org.omg.CORBA.TypeCode type() {
        return type_;
    }

    public synchronized void type(org.omg.CORBA.TypeCode tc) {
        setType(tc);
        value_ = null;
    }

    public synchronized void read_value(org.omg.CORBA.portable.InputStream in,
            org.omg.CORBA.TypeCode tc) throws org.omg.CORBA.MARSHAL {
        setType(tc);
        readValue(in);
    }

    public synchronized void write_value(org.omg.CORBA.portable.OutputStream out) {
        int kind = origType_.kind().value();

        if (value_ instanceof org.omg.CORBA.portable.Streamable
                && kind != org.omg.CORBA.TCKind._tk_value
                && kind != org.omg.CORBA.TCKind._tk_value_box
                && kind != org.omg.CORBA.TCKind._tk_abstract_interface) {
            ((org.omg.CORBA.portable.Streamable) value_)._write(out);
            return;
        }

        switch (kind) {
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
            break;

        case org.omg.CORBA.TCKind._tk_short:
            out.write_short((short) ((Integer) value_).intValue());
            break;

        case org.omg.CORBA.TCKind._tk_long:
            out.write_long(((Integer) value_).intValue());
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
            out.write_longlong(((Long) value_).longValue());
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            out.write_ushort((short) ((Integer) value_).intValue());
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            out.write_ulong(((Integer) value_).intValue());
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            out.write_ulonglong(((Long) value_).longValue());
            break;

        case org.omg.CORBA.TCKind._tk_float:
            out.write_float(((Float) value_).floatValue());
            break;

        case org.omg.CORBA.TCKind._tk_double:
            out.write_double(((Double) value_).doubleValue());
            break;

        case org.omg.CORBA.TCKind._tk_boolean:
            out.write_boolean(((Boolean) value_).booleanValue());
            break;

        case org.omg.CORBA.TCKind._tk_char:
            out.write_char(((Character) value_).charValue());
            break;

        case org.omg.CORBA.TCKind._tk_wchar:
            out.write_wchar(((Character) value_).charValue());
            break;

        case org.omg.CORBA.TCKind._tk_octet:
            out.write_octet(((Byte) value_).byteValue());
            break;

        case org.omg.CORBA.TCKind._tk_any:
            out.write_any((org.omg.CORBA.Any) value_);
            break;

        case org.omg.CORBA.TCKind._tk_TypeCode:
            out.write_TypeCode((org.omg.CORBA.TypeCode) value_);
            break;

        case org.omg.CORBA.TCKind._tk_Principal:
            out.write_Principal((org.omg.CORBA.Principal) value_);
            break;

        case org.omg.CORBA.TCKind._tk_objref:
            out.write_Object((org.omg.CORBA.Object) value_);
            break;

        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
        case org.omg.CORBA.TCKind._tk_union:
        case org.omg.CORBA.TCKind._tk_sequence:
        case org.omg.CORBA.TCKind._tk_array: {
            OutputStream o = (OutputStream) out;
            InputStream in = (InputStream) value_;
            in._OB_reset();
            o.write_InputStream(in, type_);
            break;
        }

        case org.omg.CORBA.TCKind._tk_value: {
            OutputStream o = (OutputStream) out;
            if (value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                o.write_InputStream(in, type_);
            } else
                o.write_value((java.io.Serializable) value_);
            break;
        }

        case org.omg.CORBA.TCKind._tk_value_box: {
            OutputStream o = (OutputStream) out;
            if (value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                o.write_InputStream(in, type_);
            } else {
                o.write_value((java.io.Serializable) value_, origType_, null);
            }
            break;
        }

        case org.omg.CORBA.TCKind._tk_enum:
            out.write_ulong(((Integer) value_).intValue());
            break;

        case org.omg.CORBA.TCKind._tk_string:
            out.write_string((String) value_);
            break;

        case org.omg.CORBA.TCKind._tk_wstring:
            out.write_wstring((String) value_);
            break;

        case org.omg.CORBA.TCKind._tk_fixed: {
            // TODO: check ranges here? compare scale against TypeCode?
            try {
                out.write_fixed(((java.math.BigDecimal) value_)
                        .movePointRight(origType_.fixed_scale()));
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }

            break;
        }

        case org.omg.CORBA.TCKind._tk_abstract_interface: {
            OutputStream o = (OutputStream) out;
            if (value_ != null && value_ instanceof InputStream) {
                InputStream in = (InputStream) value_;
                in._OB_reset();
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(in.read_boolean() == false);
                o.write_abstract_interface(in.read_value());
            } else
                o.write_abstract_interface(value_);
            break;
        }

        case org.omg.CORBA.TCKind._tk_native:
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorNativeNotSupported),
                org.apache.yoko.orb.OB.MinorCodes.MinorNativeNotSupported,  
                CompletionStatus.COMPLETED_NO);

        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject),
                org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        case org.omg.CORBA.TCKind._tk_alias:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("unable to write tk_alias types");
        }
    }

    public synchronized org.omg.CORBA.portable.OutputStream create_output_stream() {
        // TODO:
        // Spec says that calling create_output_stream and
        // writing to the any will update the state of the
        // last streamable object, if present.
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        OutputStream out = new OutputStream(buf);
        out._OB_ORBInstance(orbInstance_);
        return out;
    }

    public synchronized org.omg.CORBA.portable.InputStream create_input_stream() {
        if (value_ instanceof InputStream) {
            return ((InputStream) value_)._OB_clone();
        } else {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            OutputStream out = new OutputStream(buf);
            out._OB_ORBInstance(orbInstance_);
            write_value(out);
            return out.create_input_stream();
        }
    }

    public synchronized short extract_short()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_short, false);
        return (short) ((Integer) value_).intValue();
    }

    public synchronized void insert_short(short val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_short));
        value_ = new Integer(val);
    }

    public synchronized int extract_long() throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_long, false);
        return ((Integer) value_).intValue();
    }

    public synchronized void insert_long(int val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_long));
        value_ = new Integer(val);
    }

    public synchronized long extract_longlong()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_longlong, false);
        return ((Long) value_).longValue();
    }

    public synchronized void insert_longlong(long val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_longlong));
        value_ = new Long(val);
    }

    public synchronized short extract_ushort()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ushort, false);
        return (short) ((Integer) value_).intValue();
    }

    public synchronized void insert_ushort(short val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_ushort));
        value_ = new Integer(val);
    }

    public synchronized int extract_ulong() throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ulong, false);
        return ((Integer) value_).intValue();
    }

    public synchronized void insert_ulong(int val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_ulong));
        value_ = new Integer(val);
    }

    public synchronized long extract_ulonglong()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_ulonglong, false);
        return ((Long) value_).longValue();
    }

    public synchronized void insert_ulonglong(long val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_ulonglong));
        value_ = new Long(val);
    }

    public synchronized boolean extract_boolean()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_boolean, false);
        return ((Boolean) value_).booleanValue();
    }

    public synchronized void insert_boolean(boolean val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_boolean));
        value_ = new Boolean(val);
    }

    public synchronized char extract_char() throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_char, false);
        return ((Character) value_).charValue();
    }

    public synchronized void insert_char(char val)
            throws org.omg.CORBA.DATA_CONVERSION {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_char));
        value_ = new Character(val);
    }

    public synchronized char extract_wchar() throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_wchar, false);
        return ((Character) value_).charValue();
    }

    public synchronized void insert_wchar(char val)
            throws org.omg.CORBA.DATA_CONVERSION {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_wchar));
        value_ = new Character(val);
    }

    public synchronized byte extract_octet() throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_octet, false);
        return ((Byte) value_).byteValue();
    }

    public synchronized void insert_octet(byte val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_octet));
        value_ = new Byte(val);
    }

    public synchronized float extract_float()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_float, false);
        return ((Float) value_).floatValue();
    }

    public synchronized void insert_float(float val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_float));
        value_ = new Float(val);
    }

    public synchronized double extract_double()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_double, false);
        return ((Double) value_).doubleValue();
    }

    public synchronized void insert_double(double val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_double));
        value_ = new Double(val);
    }

    public synchronized org.omg.CORBA.Any extract_any()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_any, false);
        return (org.omg.CORBA.Any) value_;
    }

    public synchronized void insert_any(org.omg.CORBA.Any val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_any));
        value_ = val;
    }

    public synchronized org.omg.CORBA.TypeCode extract_TypeCode()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_TypeCode, false);
        return (org.omg.CORBA.TypeCode) value_;
    }

    public synchronized void insert_TypeCode(org.omg.CORBA.TypeCode val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_TypeCode));
        value_ = val;
    }

    public synchronized org.omg.CORBA.Principal extract_Principal()
            throws org.omg.CORBA.BAD_OPERATION {
        // Deprecated by CORBA 2.2
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public synchronized void insert_Principal(org.omg.CORBA.Principal val) {
        // Deprecated by CORBA 2.2
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public synchronized org.omg.CORBA.Object extract_Object()
            throws org.omg.CORBA.BAD_OPERATION {
        org.omg.CORBA.TCKind kind = origType_.kind();
        if (kind != org.omg.CORBA.TCKind.tk_objref
                && kind != org.omg.CORBA.TCKind.tk_abstract_interface
                && kind != org.omg.CORBA_2_4.TCKind.tk_local_interface) {
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, CompletionStatus.COMPLETED_NO);
        }

        if (value_ != null && !(value_ instanceof org.omg.CORBA.Object)) {
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, CompletionStatus.COMPLETED_NO);
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
                Delegate d = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) val)
                        ._get_delegate();
                orbInstance_ = d._OB_ORBInstance();
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                // Object has no delegate - ignore
            }
        }

        org.omg.CORBA.TypeCode tc = org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_objref);
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
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                // Object has no delegate - ignore
            }
        }

        type(tc);
        value_ = val;
    }

    public synchronized String extract_string()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_string, false);
        return (String) value_;
    }

    public synchronized void insert_string(String val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_string));
        value_ = val;
    }

    public synchronized String extract_wstring()
            throws org.omg.CORBA.BAD_OPERATION {
        checkValue(org.omg.CORBA.TCKind.tk_wstring, false);
        return (String) value_;
    }

    public synchronized void insert_wstring(String val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_wstring));
        value_ = val;
    }

    public org.omg.CORBA.portable.Streamable extract_Streamable()
            throws org.omg.CORBA.BAD_INV_ORDER {
        if (!(value_ instanceof org.omg.CORBA.portable.Streamable))
            throw new org.omg.CORBA.BAD_INV_ORDER("Type mismatch");
        return (org.omg.CORBA.portable.Streamable) value_;
    }

    public synchronized void insert_Streamable(
            org.omg.CORBA.portable.Streamable val) {
        type(val._type());
        value_ = val;
    }

    public synchronized java.math.BigDecimal extract_fixed() {
        checkValue(org.omg.CORBA.TCKind.tk_fixed, false);
        return (java.math.BigDecimal) value_;
    }

    public synchronized void insert_fixed(java.math.BigDecimal val) {
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_fixed));
        value_ = val;
    }

    public synchronized void insert_fixed(java.math.BigDecimal val,
            org.omg.CORBA.TypeCode tc) throws org.omg.CORBA.BAD_INV_ORDER {
        type(tc);
        value_ = val;
    }

    public java.io.Serializable extract_Value()
            throws org.omg.CORBA.BAD_OPERATION {
        org.omg.CORBA.TCKind kind = origType_.kind();

        if (kind != org.omg.CORBA.TCKind.tk_value
                && kind != org.omg.CORBA.TCKind.tk_value_box
                && kind != org.omg.CORBA.TCKind.tk_abstract_interface) {
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, CompletionStatus.COMPLETED_NO);
        }

        if (kind == org.omg.CORBA.TCKind.tk_abstract_interface
                && value_ instanceof org.omg.CORBA.Object) {
            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, CompletionStatus.COMPLETED_NO);
        }

        if (value_ instanceof InputStream) {
            InputStream in = (InputStream) value_;
            in._OB_reset();
            if (kind == org.omg.CORBA.TCKind.tk_abstract_interface)
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(in.read_boolean() == false);
            return in.read_value();
        } else
            return (java.io.Serializable) value_;
    }

    public synchronized void insert_Value(java.io.Serializable val) {
        org.omg.CORBA.TypeCode tc = org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_value);
        insert_Value(val, tc);
    }

    public synchronized void insert_Value(java.io.Serializable val,
            org.omg.CORBA.TypeCode tc) throws org.omg.CORBA.MARSHAL {
        type(tc);

        value_ = val;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Any() {
        this((org.apache.yoko.orb.OB.ORBInstance) null);
    }

    public Any(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
        type(org.apache.yoko.orb.OB.TypeCodeFactory
                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_null));
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
        case org.omg.CORBA.TCKind._tk_null:
        case org.omg.CORBA.TCKind._tk_void:
        case org.omg.CORBA.TCKind._tk_Principal:
            break;

        case org.omg.CORBA.TCKind._tk_short:
            value_ = new Integer(any.extract_short());
            break;

        case org.omg.CORBA.TCKind._tk_long:
            value_ = new Integer(any.extract_long());
            break;

        case org.omg.CORBA.TCKind._tk_longlong:
            value_ = new Long(any.extract_longlong());
            break;

        case org.omg.CORBA.TCKind._tk_ushort:
            value_ = new Integer(any.extract_ushort());
            break;

        case org.omg.CORBA.TCKind._tk_ulong:
            value_ = new Integer(any.extract_ulong());
            break;

        case org.omg.CORBA.TCKind._tk_ulonglong:
            value_ = new Long(any.extract_ulonglong());
            break;

        case org.omg.CORBA.TCKind._tk_float:
            value_ = new Float(any.extract_float());
            break;

        case org.omg.CORBA.TCKind._tk_double:
            value_ = new Double(any.extract_double());
            break;

        case org.omg.CORBA.TCKind._tk_boolean:
            value_ = new Boolean(any.extract_boolean());
            break;

        case org.omg.CORBA.TCKind._tk_char:
            value_ = new Character(any.extract_char());
            break;

        case org.omg.CORBA.TCKind._tk_wchar:
            value_ = new Character(any.extract_wchar());
            break;

        case org.omg.CORBA.TCKind._tk_octet:
            value_ = new Byte(any.extract_octet());
            break;

        case org.omg.CORBA.TCKind._tk_string:
            value_ = any.extract_string();
            break;

        case org.omg.CORBA.TCKind._tk_wstring:
            value_ = any.extract_wstring();
            break;

        case org.omg.CORBA.TCKind._tk_fixed:
            value_ = any.extract_fixed();
            break;

        case org.omg.CORBA.TCKind._tk_TypeCode:
            value_ = any.extract_TypeCode();
            break;

        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA.TCKind._tk_abstract_interface:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            try {
                value_ = any.extract_Object();
                break;
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
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

    public Any(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.TypeCode type, java.lang.Object value) {
        orbInstance_ = orbInstance;
        setType(type);
        value_ = value;
    }

    public synchronized java.lang.Object value() {
        return value_;
    }

    public synchronized void replace(org.omg.CORBA.TypeCode tc,
            java.lang.Object value) {
        setType(tc);
        value_ = value;
    }

    public synchronized org.apache.yoko.orb.OB.ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    public synchronized void _OB_ORBInstance(
            org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    public synchronized org.omg.CORBA.TypeCode _OB_type() {
        return obType_;
    }
}
