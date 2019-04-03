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

import org.apache.yoko.orb.OB.CodeConverterBase;
import org.apache.yoko.orb.OB.CodeConverters;
import org.apache.yoko.orb.OB.CodeSetWriter;
import org.apache.yoko.orb.OB.MinorCodes;
import org.apache.yoko.orb.OB.OB_Extras;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.TypeCodeFactory;
import org.apache.yoko.orb.OB.ValueWriter;
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.util.Timeout;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TIMEOUT;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.ValueBaseHelper;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_4.TCKind;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.IOP.TaggedProfile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode;
import static org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadTypecode;
import static org.apache.yoko.orb.OB.MinorCodes.describeMarshal;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.CORBA.CompletionStatus.COMPLETED_YES;
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
import static org.omg.CORBA.TCKind._tk_longdouble;
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
import static org.omg.CORBA.TCKind.tk_null;

public final class OutputStream extends org.omg.CORBA_2_3.portable.OutputStream implements ValueOutputStream {
    private static final Logger LOGGER = Logger.getLogger(OutputStream.class.getName());

    private ORBInstance orbInstance_; // Java only

    public final Buffer buf_;

    private GiopVersion giopVersion_ = OB_Extras.DEFAULT_GIOP_VERSION;

    private final CodeConverters codeConverters_;

    private boolean charWriterRequired_;

    private boolean charConversionRequired_;

    private boolean wCharWriterRequired_;

    private boolean wCharConversionRequired_;

    //
    // Handles all OBV marshalling
    //
    private ValueWriter valueWriter_;

    //
    // If alignNext_ > 0, the next write should be aligned on this
    // boundary
    //
    private int alignNext_;

    private Object invocationContext_;

    private Object delegateContext_;
    private Timeout timeout = Timeout.NEVER;

    // ------------------------------------------------------------------
    // Private and protected functions
    // ------------------------------------------------------------------

    // Write a gap of four bytes (ulong aligned), avoids byte shifts
    private int writeGap() {
        LOGGER.finest("Writing a gap value");
        addCapacity(4, 4);
        int result = buf_.pos_;
        buf_.pos_ += 4;
        return result;
    }

    private void writeLength(int start) {
        int length = buf_.pos_ - (start + 4);
        LOGGER.finest("Writing a length value of " + length + " at offset " + start);

        buf_.data_[start++] = (byte) (length >>> 24);
        buf_.data_[start++] = (byte) (length >>> 16);
        buf_.data_[start++] = (byte) (length >>> 8);
        buf_.data_[start] = (byte) length;
    }

    private void writeTypeCodeImpl(org.omg.CORBA.TypeCode tc, Hashtable history) {
        //
        // Try casting the TypeCode to org.apache.yoko.orb.CORBA.TypeCode. This
        // could
        // fail if the TypeCode was created by a foreign singleton ORB.
        //
        TypeCode obTC = null;
        try {
            obTC = (TypeCode) tc;
        } catch (ClassCastException ignored) {
        }

        if (obTC != null) {
            if (obTC.recId_ != null) {
                if (obTC.recType_ == null)
                    throw new BAD_TYPECODE(describeBadTypecode(MinorIncompleteTypeCode), MinorIncompleteTypeCode, COMPLETED_NO);
                writeTypeCodeImpl(obTC.recType_, history);
                return;
            }
        }

        LOGGER.finest("Writing a type code of type " + tc.kind().value());

        //
        // For performance reasons, handle the primitive TypeCodes first
        //
        switch (tc.kind().value()) {
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
        case _tk_longdouble:
        case _tk_boolean:
        case _tk_char:
        case _tk_wchar:
        case _tk_octet:
        case _tk_any:
        case _tk_TypeCode:
        case _tk_Principal:
            write_ulong(tc.kind().value());
            return;

        default:
            break;
        }

        Integer indirectionPos = (Integer) history.get(tc);
        if (indirectionPos != null) {
            write_long(-1);
            int offs = indirectionPos - buf_.pos_;
            LOGGER.finest("Writing an indirect type code for offset " + offs);
            write_long(offs);
        } else {
            write_ulong(tc.kind().value());
            Integer oldPos = buf_.pos_ - 4;

            try {
                switch (tc.kind().value()) {
                case _tk_fixed: {
                    history.put(tc, oldPos);

                    write_ushort(tc.fixed_digits());
                    write_short(tc.fixed_scale());

                    break;
                }

                case _tk_objref: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case _tk_struct:
                case _tk_except: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    write_ulong(tc.member_count());
                    for (int i = 0; i < tc.member_count(); i++) {
                        write_string(tc.member_name(i));
                        writeTypeCodeImpl(tc.member_type(i), history);
                    }
                    writeLength(start);

                    break;
                }

                case _tk_union: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    org.omg.CORBA.TypeCode discType = tc.discriminator_type();
                    writeTypeCodeImpl(discType, history);
                    int defaultIndex = tc.default_index();
                    write_long(defaultIndex);
                    write_ulong(tc.member_count());
                    for (int i = 0; i < tc.member_count(); i++) {
                        //
                        // Check for default label value
                        //
                        if (i == defaultIndex) {
                            //
                            // Marshal a dummy value of the appropriate size
                            // for the discriminator type
                            //
                            org.omg.CORBA.TypeCode origDiscType = TypeCode._OB_getOrigType(discType);
                            switch (origDiscType.kind().value()) {
                            case _tk_short:
                                write_short((short) 0);
                                break;
                            case _tk_ushort:
                                write_ushort((short) 0);
                                break;
                            case _tk_long:
                                write_long(0);
                                break;
                            case _tk_ulong:
                                write_ulong(0);
                                break;
                            case _tk_longlong:
                                write_longlong(0);
                                break;
                            case _tk_ulonglong:
                                write_ulonglong(0);
                                break;
                            case _tk_boolean:
                                write_boolean(false);
                                break;
                            case _tk_char:
                                write_char((char) 0);
                                break;
                            case _tk_enum:
                                write_ulong(0);
                                break;
                            default:
                                _OB_assert("Invalid sub-type in tk_union");
                            }
                        } else {
                            tc.member_label(i).write_value(this);
                        }

                        write_string(tc.member_name(i));
                        writeTypeCodeImpl(tc.member_type(i), history);
                    }
                    writeLength(start);

                    break;
                }

                case _tk_enum: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    write_ulong(tc.member_count());
                    for (int i = 0; i < tc.member_count(); i++)
                        write_string(tc.member_name(i));
                    writeLength(start);

                    break;
                }

                case _tk_string:
                case _tk_wstring:
                    write_ulong(tc.length());
                    break;

                case _tk_sequence:
                case _tk_array: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    writeTypeCodeImpl(tc.content_type(), history);
                    write_ulong(tc.length());
                    writeLength(start);

                    break;
                }

                case _tk_alias: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeTypeCodeImpl(tc.content_type(), history);
                    writeLength(start);

                    break;
                }

                case _tk_value: {
                    history.put(tc, oldPos);

                    org.omg.CORBA.TypeCode concreteBase = tc.concrete_base_type();
                    if (concreteBase == null) {
                        concreteBase = TypeCodeFactory.createPrimitiveTC(tk_null);
                    }

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    write_short(tc.type_modifier());
                    writeTypeCodeImpl(concreteBase, history);
                    write_ulong(tc.member_count());
                    for (int i = 0; i < tc.member_count(); i++) {
                        write_string(tc.member_name(i));
                        writeTypeCodeImpl(tc.member_type(i), history);
                        write_short(tc.member_visibility(i));
                    }
                    writeLength(start);

                    break;
                }

                case _tk_value_box: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeTypeCodeImpl(tc.content_type(), history);
                    writeLength(start);

                    break;
                }

                case _tk_abstract_interface: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case _tk_native: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case TCKind._tk_local_interface: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                default:
                    _OB_assert("Invalid typecode");
                }
            } catch (BadKind | Bounds ex) {
                _OB_assert(ex);
            }
        }
    }

    //
    // Must be called prior to any writes
    //
    private void checkBeginChunk() {
        _OB_assert(valueWriter_ != null);
        valueWriter_.checkBeginChunk();
    }

    private ValueWriter valueWriter() {
        if (valueWriter_ == null)
            valueWriter_ = new ValueWriter(this);
        return valueWriter_;
    }

    private void addCapacity(int size) {
        //
        // Expand buffer to hold requested size
        //
        // Note: OutputStreams are not always written to in a linear
        // fashion, i.e., sometimes the position is reset to
        // an earlier point and data is patched in. Therefore,
        // do NOT do this:
        //
        // buf_.realloc(buf_.len_ + size);
        //
        //
        if (alignNext_ > 0) {
            int align = alignNext_;
            alignNext_ = 0;
            addCapacity(size, align);
        } else {
            //
            // If we're at the end of the current buffer, then we are about
            // to write new data. We must first check if we need to start a
            // chunk, which may result in a recursive call to addCapacity().
            //
            if (buf_.pos_ == buf_.length() && valueWriter_ != null) {
                checkBeginChunk();
            }

            //
            // If there isn't enough room, then reallocate the buffer
            //
            final int len = buf_.pos_ + size;
            if (len > buf_.length()) {
                buf_.realloc(len);
            }
        }
    }

    private void checkTimeout() {
        if (timeout.isExpired()) {
            // we only ever want to throw the exception once
            timeout = Timeout.NEVER;
            throw new TIMEOUT("Reply timed out on server", MinorCodes.MinorOther, COMPLETED_YES);
        }
    }

    private int roundUp(final int i, final int align) {
        switch (align) {
            case 0x00: return i;
            case 0x01: return i;
            case 0x02: return ((i + 0b0001) & ~(0b0001));
            case 0x04: return ((i + 0b0011) & ~(0b0011));
            case 0x08: return ((i + 0b0111) & ~(0b0111));
            case 0x10: return ((i + 0b1111) & ~(0b1111));
            default:
                if (LOGGER.isLoggable(Level.WARNING))
                    LOGGER.warning(String.format("Aligning on a strange number 0x%x", align));
                final int j = (i + align - 1);
                return (j - (j % align));
        }
    }

    private static final byte PAD_BYTE = (byte)0xbd;

    private void addCapacity(int size, int align) {
        // use addCapacity(int) if align == 0
        _OB_assert(align > 0);

        //
        // If we're at the end of the current buffer, then we are about
        // to write new data. We must first check if we need to start a
        // chunk, which may result in a recursive call to addCapacity().
        //
        if (buf_.pos_ == buf_.length() && valueWriter_ != null) {
            checkBeginChunk();
        }

        //
        // If alignNext_ is set, then use the larger of alignNext_ and align
        //
        if (alignNext_ > 0) {
            align = (alignNext_ > align ? alignNext_ : align);
            alignNext_ = 0;
        }

        final int newPos = roundUp(buf_.pos_, align);

        //
        // If there isn't enough room, then reallocate the buffer
        //
        final int len = newPos + size;
        if (len > buf_.length()) {
            buf_.realloc(len);
            checkTimeout();
        }

        //
        // Pad to the requested boundary
        //
        for (; buf_.pos_ < newPos; buf_.pos_++) {
            buf_.data_[buf_.pos_] = PAD_BYTE;
        }
    }

    /** write wchar using old non-compliant method */
    private void _OB_write_wchar_old(char value) {
        if (wCharConversionRequired_) {
            final CodeConverterBase converter = codeConverters_.outputWcharConverter;

            value = converter.convert(value);

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (giopVersion_) {
            case GIOP1_1: {
                if (converter.getTo().max_bytes <= 2)
                    write_ushort((short) value);
                else
                    write_ulong((int) value);
            }
                break;

            default: {
                final int length = converter.write_count_wchar(value);
                write_octet((byte) length);
                addCapacity(length);
                converter.write_wchar(this, value);
            }
                break;
            }
        }
        //
        // UTF-16
        //
        else {
            switch (giopVersion_) {
            case GIOP1_0:
            case GIOP1_1:
                write_ushort((short) value);
                break;

            default:
                addCapacity(3);
                buf_.writeByte(2);
                buf_.writeChar(value);
                break;
            }
        }
    }

    /** write wchar using new compliant method */
    private void _OB_write_wchar_new(char value, boolean partOfString) {
        final CodeConverterBase converter = codeConverters_.outputWcharConverter;

        //
        // pre-convert the character if necessary
        //
        if (wCharConversionRequired_)
            value = converter.convert(value);

        if (wCharWriterRequired_) {
            if (!partOfString)
                converter.set_writer_flags(CodeSetWriter.FIRST_CHAR);

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (giopVersion_) {
            case GIOP1_0: {
                //
                // we don't support special writers for GIOP 1.0 if
                // conversion is required or if a writer is required
                //
                _OB_assert(false);
            }
                break;

            case GIOP1_1: {
                //
                // get the length of the character
                //
                int len = converter.write_count_wchar(value);

                //
                // For GIOP 1.1 we are limited to 2-byte wchars
                // so make sure to check for that
                //
                _OB_assert(len == 2);

                //
                // allocate aligned space
                //
                addCapacity(2, 2);

                //
                // write using the writer
                //
                converter.write_wchar(this, value);
            }
                break;

            default: {
                //
                // get the length of the character
                //
                int len = converter.write_count_wchar(value);

                //
                // write the octet length at the beginning
                //
                write_octet((byte) len);

                //
                // add unaligned capacity
                //
                addCapacity(len);

                //
                // write the actual character
                //
                converter.write_wchar(this, value);
            }
                break;
            }
        } else {
            switch (giopVersion_) {
            case GIOP1_0: {
                //
                // Orbix2000/Orbacus/E compatible 1.0 marshal
                //

                //
                // add aligned capacity
                //
                addCapacity(2, 2);

                //
                // write 2-byte character in big endian
                //
                buf_.writeChar(value);
            }
                break;

            case GIOP1_1: {
                write_ushort((short) value);
            }
                break;

            default: {
                //
                // add unaligned space for character
                //
                addCapacity(3);

                //
                // write the octet length at the start
                //
                buf_.writeByte(2);

                //
                // write the character in big endian format
                //
                buf_.writeChar(value);
            }
                break;
            }
        }
    }

    /** write wstring using old non-compliant method */
    private void _OB_write_wstring_old(String value) {
        final char[] arr = value.toCharArray();
        final int len = arr.length;

        //
        // 15.3.2.7: For GIOP version 1.1, a wide string is encoded as an
        // unsigned long indicating the length of the string in octets or
        // unsigned integers (determined by the transfer syntax for wchar)
        // followed by the individual wide characters. Both the string length
        // and contents include a terminating null. The terminating null
        // character for a wstring is also a wide character.
        //
        switch (giopVersion_) {
        case GIOP1_0:
        case GIOP1_1: {
            write_ulong(len + 1);
            write_wchar_array(arr, 0, len);
            write_wchar((char) 0);
        }
            break;

        default: {
            //
            // For octet count
            //
            int start = writeGap();

            if (wCharConversionRequired_) {
                final CodeConverterBase converter = codeConverters_.outputWcharConverter;

                for (char anArr : arr) {
                    char v = converter.convert(anArr);

                    addCapacity(converter.write_count_wchar(v));
                    converter.write_wchar(this, v);
                }
            }
            //
            // UTF-16
            //
            else {
                addCapacity(2 * len);

                for (char v : arr) {
                    buf_.writeChar(v);
                }
            }

            //
            // Write octet count
            //
            writeLength(start);
        }
            break;
        }
    }

    /** write wstring using new compliant method */
    private void _OB_write_wstring_new(String value) {
        final char[] arr = value.toCharArray();
        final int len = arr.length;

        LOGGER.finest("Writing wstring value " + value);
        //
        // get converter/writer instance
        //
        final CodeConverterBase converter = codeConverters_.outputWcharConverter;

        //
        // some writers (specially UTF-16) requires the possible BOM
        // only found at the beginning of a string... this will
        // indicate that we are at the start of the first character
        // of the string to the writer
        if (wCharWriterRequired_)
            converter.set_writer_flags(CodeSetWriter.FIRST_CHAR);

        //
        // for GIOP 1.0/1.1 we don't need to differentiate between
        // strings requiring a writer/converter (or not) since they can
        // be handled by the write_wchar() method
        //
        switch (giopVersion_) {
            case GIOP1_0:
            case GIOP1_1:
                //
                // write the length of the string
                //
                write_ulong(len + 1);

                //
                // now write all the characters
                //
                for (char anArr : arr) write_wchar(anArr, true);

                //
                // and the null terminator
                //
                write_wchar((char) 0, true);
                return;
            default:
        }

        //
        // save the starting position and write the gap to place the
        // length of the string later
        //
        int start = writeGap();

        //
        // we've handled GIOP 1.0/1.1 above so this must be GIOP 1.2+
        //
        if (wCharWriterRequired_) {
            for (char anArr : arr) {
                char v = anArr;

                //
                // check if the character requires conversion
                //
                if (wCharConversionRequired_)
                    v = converter.convert(v);

                //
                // add capacity for the character
                //
                addCapacity(converter.write_count_wchar(v));

                //
                // write the character
                //
                converter.write_wchar(this, v);
            }
        } else {
            //
            // since we don't require a special writer, each character
            // MUST be 2-bytes in size
            //
            addCapacity(len << 1);

            for (char anArr : arr) {
                char v = anArr;

                //
                // check for conversion
                //
                if (wCharConversionRequired_)
                    v = converter.convert(v);

                //
                // write character in big endian format
                //
                buf_.writeChar(v);
            }
        }

        //
        // write the octet length
        //
        writeLength(start);
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public void write(int b) {
        //
        // this matches the behaviour of this function in the Java ORB
        // and not what is outlined in the java.io.OutputStream
        //
        write_long(b);
    }

    public org.omg.CORBA.ORB orb() {
        if (orbInstance_ != null)
            return orbInstance_.getORB();
        return null;
    }

    @Override
    public InputStream create_input_stream() {
        Buffer buf = new Buffer(buf_.length());
        if (buf_.length() > 0) System.arraycopy(buf_.data_, 0, buf.data_, 0, buf_.length());

// this is a useful tracepoint, but produces a lot of data, so turn on only
// if really needed.
//      if (logger.isLoggable(Level.FINEST)) {
//          logger.fine("new input stream created:\n" + buf.dumpRemainingData());
//      }

        InputStream in = new InputStream(buf, false, codeConverters_, giopVersion_);
        in._OB_ORBInstance(orbInstance_);
        return in;
    }

    public void write_boolean(boolean value) {
        addCapacity(1);

        buf_.writeByte(value ? (byte) 1 : (byte) 0);
    }

    public void write_char(char value) {
        if (value > 255)
            throw new DATA_CONVERSION("char value exceeds 255: " + (int) value);

        addCapacity(1);

        final CodeConverterBase converter = codeConverters_.outputCharConverter;

        if (charConversionRequired_)
            value = converter.convert(value);

        if (charWriterRequired_)
            converter.write_char(this, value);
        else
            buf_.writeByte(value);
    }

    public void write_wchar(char value) {
        write_wchar(value, false);
    }

    private void write_wchar(char value, boolean partOfString) {
        if (!OB_Extras.COMPAT_WIDE_MARSHAL)
            _OB_write_wchar_new(value, partOfString);
        else
            _OB_write_wchar_old(value);
    }

    public void write_octet(byte value) {
        addCapacity(1);
        buf_.writeByte(value);
    }

    public void write_short(short value) {
        addCapacity(2, 2);
        buf_.writeShort(value);
    }

    public void write_ushort(short value) {
        write_short(value);
    }

    public void write_long(int value) {
        addCapacity(4, 4);
        buf_.writeInt(value);
    }

    public void write_ulong(int value) {
        write_long(value);
    }

    public void write_longlong(long value) {
        addCapacity(8, 8);
        buf_.writeLong(value);
    }

    public void write_ulonglong(long value) {
        write_longlong(value);
    }

    public void write_float(float value) {
        write_long(Float.floatToIntBits(value));
    }

    public void write_double(double value) {
        write_longlong(Double.doubleToLongBits(value));
    }

    public void write_string(String value) {
        LOGGER.finest("Writing string value " + value);
        final char[] arr = value.toCharArray();
        int len = arr.length;
        int capacity = len + 1;

        if (!(charWriterRequired_ || charConversionRequired_)) {
            write_ulong(capacity);
            addCapacity(capacity);

            for (char c : arr) {
                if (c > 255)
                    throw new DATA_CONVERSION("illegal char value for string: " + (int) c);

                buf_.writeByte(c);
            }
        } else {
            final CodeConverterBase converter = codeConverters_.outputCharConverter;

            //
            // Intermediate variable used for efficiency
            //
            boolean bothRequired = charWriterRequired_ && charConversionRequired_;

            //
            // Temporary OCI buffer required
            //
            Buffer buffer = new Buffer(64);
            OutputStream tmpStream = new OutputStream(buffer);

            for (char c : arr) {
                if (c > 255)
                    throw new DATA_CONVERSION("illegal char value for string: " + (int) c);

                // Ensure we have 4 bytes - long enough for the widest UTF8 char and for a surrogate pair in UTF16
                if (buffer.available() < 4)
                    buffer.realloc(buffer.length() + 4);

                if (bothRequired)
                    converter.write_char(tmpStream, converter.convert(c));
                else if (charWriterRequired_)
                    converter.write_char(tmpStream, c);
                else
                    buffer.writeByte(converter.convert(c));
            }

            //
            // Copy the contents from the temporary buffer
            //
            int bufSize = buffer.pos_;

            write_ulong(bufSize + 1);
            addCapacity(bufSize + 1);

            for (int i = 0; i < bufSize; i++) {
                buf_.writeByte(buffer.data_[i]);
            }
        }
        // write null terminator
        buf_.writeByte(0);
    }

    public void write_wstring(String value) {
        if (!OB_Extras.COMPAT_WIDE_MARSHAL)
            _OB_write_wstring_new(value);
        else
            _OB_write_wstring_old(value);
    }

    public void write_boolean_array(boolean[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length);

            for (int i = offset; i < offset + length; i++)
                buf_.writeByte(value[i] ? (byte) 1 : (byte) 0);
        }
    }

    public void write_char_array(char[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length);

            if (!(charWriterRequired_ || charConversionRequired_)) {
                for (int i = offset; i < offset + length; i++) {
                    if (value[i] > 255)
                        throw new DATA_CONVERSION("char value exceeds 255: " + (int) value[i]);

                    buf_.writeByte(value[i]);
                }
            } else {
                final CodeConverterBase converter = codeConverters_.outputCharConverter;

                //
                // Intermediate variable used for efficiency
                //
                boolean bothRequired = charWriterRequired_
                        && charConversionRequired_;

                for (int i = offset; i < offset + length; i++) {
                    if (value[i] > 255)
                        throw new DATA_CONVERSION("char value exceeds 255: " + (int) value[i]);

                    if (bothRequired)
                        converter.write_char(this, converter.convert(value[i]));
                    else if (charWriterRequired_)
                        converter.write_char(this, value[i]);
                    else
                        buf_.writeByte(converter.convert(value[i]));
                }
            }
        }
    }

    public void write_wchar_array(char[] value, int offset, int length) {
        for (int i = offset; i < offset + length; i++)
            write_wchar(value[i], false);
    }

    public void write_octet_array(byte[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length);

            System.arraycopy(value, offset, buf_.data_, buf_.pos_, length);

            buf_.pos_ += length;
        }
    }

    public void write_short_array(short[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 2, 2);

            for (int i = offset; i < offset + length; i++) {
                buf_.writeShort(value[i]);
            }
        }
    }

    public void write_ushort_array(short[] value, int offset, int length) {
        write_short_array(value, offset, length);
    }

    public void write_long_array(int[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 4, 4);

            for (int i = offset; i < offset + length; i++) {
                buf_.writeInt(value[i]);
            }
        }
    }

    public void write_ulong_array(int[] value, int offset, int length) {
        write_long_array(value, offset, length);
    }

    public void write_longlong_array(long[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 8, 8);

            for (int i = offset; i < offset + length; i++) {
                buf_.writeLong(value[i]);
            }
        }
    }

    public void write_ulonglong_array(long[] value, int offset, int length) {
        write_longlong_array(value, offset, length);
    }

    public void write_float_array(float[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 4, 4);

            for (int i = offset; i < offset + length; i++) {
                int v = Float.floatToIntBits(value[i]);

                buf_.writeInt(v);
            }
        }
    }

    public void write_double_array(double[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 8, 8);

            for (int i = offset; i < offset + length; i++) {
                long v = Double.doubleToLongBits(value[i]);

                buf_.writeLong(v);
            }
        }
    }

    public void write_Object(org.omg.CORBA.Object value) {
        if (value == null) {
            LOGGER.finest("Writing a null CORBA object value");
            IOR ior = new IOR();
            ior.type_id = "";
            ior.profiles = new TaggedProfile[0];
            IORHelper.write(this, ior);
        } else {
            if (value instanceof LocalObject)
                throw new MARSHAL(describeMarshal(MinorLocalObject), MinorLocalObject, COMPLETED_NO);

            Delegate p = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) value)._get_delegate();

            p._OB_marshalOrigIOR(this);
        }
    }

    public void write_TypeCode(org.omg.CORBA.TypeCode t) {
        //
        // NOTE:
        //
        // No data with natural alignment of greater than four octets
        // is needed for TypeCode. Therefore it is not necessary to do
        // encapsulation in a separate buffer.
        //

        if (t == null)
            throw new BAD_TYPECODE("TypeCode is nil");

        Hashtable history = new Hashtable(11);
        writeTypeCodeImpl(t, history);
    }

    public void write_any(org.omg.CORBA.Any value) {
        LOGGER.finest("Writing an ANY value of type " + value.type().kind());
        write_TypeCode(value.type());
        value.write_value(this);
    }

    public void write_Context(org.omg.CORBA.Context ctx, org.omg.CORBA.ContextList contexts) {
        int count = contexts.count();
        Vector v = new Vector();
        Context ctxImpl = (Context) ctx;
        for (int i = 0; i < count; i++) {
            try {
                String pattern = contexts.item(i);
                ctxImpl._OB_getValues("", 0, pattern, v);
            } catch (org.omg.CORBA.Bounds ex) {
                _OB_assert(ex);
            }
        }

        write_ulong(v.size());

        Enumeration e = v.elements();
        while (e.hasMoreElements())
            write_string((String) e.nextElement());
    }

    @SuppressWarnings("deprecation")
    public void write_Principal(Principal value) {
        // Deprecated by CORBA 2.2
        throw new NO_IMPLEMENT();
    }

    public void write_fixed(BigDecimal value) {
        String v = value.abs().toString();

        //
        // Append coded sign to value string
        //
        if (value.signum() == -1)
            v += (char) ('0' + 0x0d);
        else
            v += (char) ('0' + 0x0c);

        String s = "";
        if ((v.length() & 1) != 0)
            s = "0";

        s += v;
        final int len = s.length();

        for (int i = 0; i < len - 1; i += 2) {
            char c1 = s.charAt(i);
            char c2 = s.charAt(i + 1);
            write_octet((byte) ((c1 - '0') << 4 | (c2 - '0')));
        }
    }

    public void write_value(Serializable value) {
        checkTimeout();
        valueWriter().writeValue(value, null);
        checkTimeout();
    }

    public void write_value(Serializable value, String rep_id) {
        checkTimeout();
        valueWriter().writeValue(value, rep_id);
        checkTimeout();
    }

    public void write_value(Serializable value, Class clz) {
        checkTimeout();
        valueWriter().writeValue(value, null);
        checkTimeout();

    }

    public void write_value(Serializable value, BoxedValueHelper helper) {
        checkTimeout();
        valueWriter().writeValueBox(value, null, helper);
        checkTimeout();
    }

    public void write_abstract_interface(Object obj) {
        checkTimeout();
        valueWriter().writeAbstractInterface(obj);
        checkTimeout();
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public void write_value(Serializable value, org.omg.CORBA.TypeCode tc, BoxedValueHelper helper) {
        checkTimeout();
        valueWriter().writeValueBox(value, tc, helper);
        checkTimeout();
    }

    public void write_InputStream(final org.omg.CORBA.portable.InputStream in, org.omg.CORBA.TypeCode tc) {
        final InputStream obin = in instanceof InputStream ? (InputStream) in : null;

        try {
            LOGGER.fine("writing a value of type " + tc.kind().value());

            switch (tc.kind().value()) {
            case _tk_null:
            case _tk_void:
                break;

            case _tk_short:
            case _tk_ushort:
                write_short(in.read_short());
                break;

            case _tk_long:
            case _tk_ulong:
            case _tk_float:
            case _tk_enum:
                write_long(in.read_long());
                break;

            case _tk_double:
            case _tk_longlong:
            case _tk_ulonglong:
                write_longlong(in.read_longlong());
                break;

            case _tk_boolean:
            case _tk_octet:
                write_octet(in.read_octet());
                break;

            case _tk_char:
                write_char(in.read_char());
                break;

            case _tk_wchar:
                write_wchar(in.read_wchar());
                break;

            case _tk_fixed:
                write_fixed(in.read_fixed());
                break;

            case _tk_any: {
                // Don't do this: write_any(in.read_any())
                // This is faster:
                org.omg.CORBA.TypeCode p = in.read_TypeCode();
                write_TypeCode(p);
                write_InputStream(in, p);
                break;
            }

            case _tk_TypeCode: {
                // Don't do this: write_TypeCode(in.read_TypeCode())
                // This is faster:

                int kind = in.read_ulong();

                //
                // An indirection is not permitted at this level
                //
                if (kind == -1) {
                    throw new MARSHAL(
                            describeMarshal(MinorReadInvTypeCodeIndirection),
                            MinorReadInvTypeCodeIndirection,
                            COMPLETED_NO);
                }

                write_ulong(kind);

                switch (kind) {
                case _tk_null:
                case _tk_void:
                case _tk_short:
                case _tk_long:
                case _tk_ushort:
                case _tk_ulong:
                case _tk_float:
                case _tk_double:
                case _tk_boolean:
                case _tk_char:
                case _tk_octet:
                case _tk_any:
                case _tk_TypeCode:
                case _tk_Principal:
                case _tk_longlong:
                case _tk_ulonglong:
                case _tk_longdouble:
                case _tk_wchar:
                    break;

                case _tk_fixed:
                    write_ushort(in.read_ushort());
                    write_short(in.read_short());
                    break;

                case _tk_objref:
                case _tk_struct:
                case _tk_union:
                case _tk_enum:
                case _tk_sequence:
                case _tk_array:
                case _tk_alias:
                case _tk_except:
                case _tk_value:
                case _tk_value_box:
                case _tk_abstract_interface:
                case _tk_native:
                case TCKind._tk_local_interface: {
                    final int len = in.read_ulong();
                    write_ulong(len);
                    addCapacity(len);
                    in.read_octet_array(buf_.data_, buf_.pos_, len);
                    buf_.pos_ += len;
                    break;
                }

                case _tk_string:
                case _tk_wstring: {
                    int bound = in.read_ulong();
                    write_ulong(bound);
                    break;
                }

                default:
                    throw new InternalError();
                }

                break;
            }

            case _tk_Principal:
                write_Principal(in.read_Principal());
                break;

            case _tk_objref: {
                // Don't do this: write_Object(in.read_Object())
                // This is faster:
                IOR ior = IORHelper.read(in);
                IORHelper.write(this, ior);
                break;
            }

            case _tk_struct:
                for (int i = 0; i < tc.member_count(); i++)
                    write_InputStream(in, tc.member_type(i));
                break;

            case _tk_except:
                write_string(in.read_string());
                for (int i = 0; i < tc.member_count(); i++)
                    write_InputStream(in, tc.member_type(i));
                break;

            case _tk_union: {
                int defaultIndex = tc.default_index();
                int memberIndex = -1;

                org.omg.CORBA.TypeCode origDiscType = TypeCode._OB_getOrigType(tc.discriminator_type());

                switch (origDiscType.kind().value()) {
                case _tk_short: {
                    short val = in.read_short();
                    write_short(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_short()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_ushort: {
                    short val = in.read_ushort();
                    write_ushort(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_ushort()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_long: {
                    int val = in.read_long();
                    write_long(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_long()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_ulong: {
                    int val = in.read_ulong();
                    write_ulong(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_ulong()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_longlong: {
                    long val = in.read_longlong();
                    write_longlong(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_longlong()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_ulonglong: {
                    long val = in.read_ulonglong();
                    write_ulonglong(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_ulonglong()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_char: {
                    char val = in.read_char();
                    write_char(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_char()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_boolean: {
                    boolean val = in.read_boolean();
                    write_boolean(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).extract_boolean()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                case _tk_enum: {
                    int val = in.read_long();
                    write_long(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).create_input_stream().read_long()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                default:
                    _OB_assert("Invalid typecode in tk_union");
                }

                if (memberIndex >= 0)
                    write_InputStream(in, tc.member_type(memberIndex));
                else if (defaultIndex >= 0)
                    write_InputStream(in, tc.member_type(defaultIndex));

                break;
            }

            case _tk_string:
                write_string(in.read_string());
                break;

            case _tk_wstring:
                write_wstring(in.read_wstring());
                break;

            case _tk_sequence:
            case _tk_array: {
                int len;

                if (tc.kind().value() == _tk_sequence) {
                    len = in.read_ulong();
                    write_ulong(len);
                } else
                    len = tc.length();

                if (len > 0) {
                    org.omg.CORBA.TypeCode origContentType = TypeCode._OB_getOrigType(tc.content_type());

                    switch (origContentType.kind().value()) {
                    case _tk_null:
                    case _tk_void:
                        break;

                    case _tk_short:
                    case _tk_ushort: {
                        if (obin == null || obin.swap_) {
                            short[] s = new short[len];
                            in.read_short_array(s, 0, len);
                            write_short_array(s, 0, len);
                        } else {
                            // Read one value for the alignment
                            write_short(obin.read_short());
                            final int n = 2 * (len - 1);

                            if (n > 0) {
                                // Copy the rest
                                addCapacity(n);
                                Buffer buf = obin._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_, buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case _tk_long:
                    case _tk_ulong:
                    case _tk_float: {
                        if (obin == null || obin.swap_) {
                            int[] i = new int[len];
                            in.read_long_array(i, 0, len);
                            write_long_array(i, 0, len);
                        } else {
                            // Read one value for the alignment
                            write_long(obin.read_long());
                            final int n = 4 * (len - 1);

                            if (n > 0) {
                                // Copy the rest
                                addCapacity(n);
                                Buffer buf = obin._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_, buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case _tk_double:
                    case _tk_longlong:
                    case _tk_ulonglong: {
                        if (obin == null || obin.swap_) {
                            long[] l = new long[len];
                            in.read_longlong_array(l, 0, len);
                            write_longlong_array(l, 0, len);
                        } else {
                            // Read one value for the alignment
                            write_longlong(obin.read_longlong());
                            final int n = 8 * (len - 1);
                            if (n > 0) {
                                // Copy the rest
                                addCapacity(n);
                                Buffer buf = obin._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_, buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case _tk_boolean:
                    case _tk_octet:
                        if (obin == null) {
                            addCapacity(len);
                            in.read_octet_array(buf_.data_, buf_.pos_, len);
                            buf_.pos_ += len;
                        } else {
                            addCapacity(len);
                            Buffer buf = obin._OB_buffer();
                            System.arraycopy(buf.data_, buf.pos_, buf_.data_, buf_.pos_, len);
                            buf.pos_ += len;
                            buf_.pos_ += len;
                        }
                        break;

                    case _tk_char:
                        if (charWriterRequired_ || charConversionRequired_) {
                            char[] ch = new char[len];
                            in.read_char_array(ch, 0, len);
                            write_char_array(ch, 0, len);
                        } else {
                            addCapacity(len);
                            in.read_octet_array(buf_.data_, buf_.pos_, len);
                            buf_.pos_ += len;
                        }
                        break;

                    case _tk_wchar: {
                        char[] wch = new char[len];
                        in.read_wchar_array(wch, 0, len);
                        write_wchar_array(wch, 0, len);
                        break;
                    }

                    case _tk_alias:
                        _OB_assert("tk_alias not supported in tk_array or tk_sequence");
                        break;

                    default:
                        for (int i = 0; i < len; i++)
                            write_InputStream(in, tc.content_type());
                        break;
                    }
                }

                break;
            }

            case _tk_alias:
                write_InputStream(in, tc.content_type());
                break;

            case _tk_value:
            case _tk_value_box:
                if (obin == null) {
                    org.omg.CORBA_2_3.portable.InputStream i = (org.omg.CORBA_2_3.portable.InputStream) in;
                    write_value(i.read_value());
                } else
                    obin._OB_remarshalValue(tc, this);
                break;

            case _tk_abstract_interface: {
                boolean b = in.read_boolean();
                write_boolean(b);
                if (b) {
                    write_Object(in.read_Object());
                } else {
                    if (obin == null) {
                        org.omg.CORBA_2_3.portable.InputStream i = (org.omg.CORBA_2_3.portable.InputStream) in;
                        write_value(i.read_value());
                    } else {
                        //
                        // We have no TypeCode information about the
                        // valuetype, so we must use _tc_ValueBase and
                        // rely on the type information sent on the wire
                        //
                        obin._OB_remarshalValue(ValueBaseHelper.type(), this);
                    }
                }
                break;
            }

            case TCKind._tk_local_interface:
            case _tk_native:
            default:
                _OB_assert("unsupported types");
            }
        } catch (BadKind | Bounds ex) {
            _OB_assert(ex);
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public OutputStream(Buffer buf) {
        this(buf, null, null);
    }

    public OutputStream(Buffer buf, CodeConverters converters, GiopVersion giopVersion) {
        buf_ = buf;

        if (giopVersion != null)
            giopVersion_ = giopVersion;

        charWriterRequired_ = false;
        charConversionRequired_ = false;
        wCharWriterRequired_ = false;
        wCharConversionRequired_ = false;

        codeConverters_ = CodeConverters.createCopy(converters);

        if (converters != null) {
            if (codeConverters_.outputCharConverter != null) {
                charWriterRequired_ = codeConverters_.outputCharConverter.writerRequired();
                charConversionRequired_ = codeConverters_.outputCharConverter.conversionRequired();
            }

            if (codeConverters_.outputWcharConverter != null) {
                wCharWriterRequired_ = codeConverters_.outputWcharConverter.writerRequired();
                wCharConversionRequired_ = codeConverters_.outputWcharConverter.conversionRequired();
            }
        }
    }

    public Buffer _OB_buffer() {
        return buf_;
    }

    public int _OB_pos() {
        return buf_.pos_;
    }

    public void _OB_pos(int pos) {
        buf_.pos_ = pos;
    }

    public void _OB_alignNext(int n) {
        alignNext_ = n;
    }

    public void _OB_writeEndian() {
        write_boolean(false); // false means big endian
    }

    public void _OB_beginValue(int tag, String[] ids, boolean chunked) {
        valueWriter().beginValue(tag, ids, null, chunked);
    }

    public void _OB_endValue() {
        valueWriter().endValue();
    }

    // Java only
    public void _OB_ORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    // Java only
    public void _OB_invocationContext(Object invocationContext) {
        invocationContext_ = invocationContext;
    }

    // Java only
    public Object _OB_invocationContext() {
        return invocationContext_;
    }

    // Java only
    void _OB_delegateContext(Object delegateContext) {
        delegateContext_ = delegateContext;
    }

    // Java only
    Object _OB_delegateContext() {
        return delegateContext_;
    }

    @Override
    public void end_value() {
        _OB_endValue();
    }

    @Override
    public void start_value(String rep_id) {
        final int tag = 0x7fffff02;
        final String[] ids = { rep_id };
        _OB_beginValue(tag, ids, true);
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }
}
