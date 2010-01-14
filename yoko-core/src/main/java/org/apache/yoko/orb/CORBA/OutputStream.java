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

import java.util.logging.Level;
import java.util.logging.Logger;

final public class OutputStream extends org.omg.CORBA_2_3.portable.OutputStream {
    static final Logger logger = Logger.getLogger(OutputStream.class.getName());
    
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_; // Java only

    public org.apache.yoko.orb.OCI.Buffer buf_;

    private int GIOPVersion_ = org.apache.yoko.orb.OB.OB_Extras.DEFAULT_GIOP_VERSION;

    private org.apache.yoko.orb.OB.CodeConverters codeConverters_;

    private boolean charWriterRequired_;

    private boolean charConversionRequired_;

    private boolean wCharWriterRequired_;

    private boolean wCharConversionRequired_;

    //
    // Handles all OBV marshalling
    //
    private org.apache.yoko.orb.OB.ValueWriter valueWriter_;

    //
    // If alignNext_ > 0, the next write should be aligned on this
    // boundary
    //
    private int alignNext_;

    private java.lang.Object invocationContext_; // Java only

    private java.lang.Object delegateContext_; // Java only

    // ------------------------------------------------------------------
    // Private and protected functions
    // ------------------------------------------------------------------

    // Write a gap of four bytes (ulong aligned), avoids byte shifts
    private int writeGap() {
        logger.finest("Writing a gap value"); 
        addCapacity(4, 4);
        int result = buf_.pos_;
        buf_.pos_ += 4;
        return result;
    }

    private void writeLength(int start) {
        int length = buf_.pos_ - (start + 4);
        logger.finest("Writing a length value of " + length + " at offset " + start); 

        buf_.data_[start++] = (byte) (length >>> 24);
        buf_.data_[start++] = (byte) (length >>> 16);
        buf_.data_[start++] = (byte) (length >>> 8);
        buf_.data_[start] = (byte) length;
    }

    public void writeTypeCodeImpl(org.omg.CORBA.TypeCode tc,
            java.util.Hashtable history) {
        //
        // Try casting the TypeCode to org.apache.yoko.orb.CORBA.TypeCode. This
        // could
        // fail if the TypeCode was created by a foreign singleton ORB.
        //
        TypeCode obTC = null;
        try {
            obTC = (TypeCode) tc;
        } catch (ClassCastException ex) {
        }

        if (obTC != null) {
            if (obTC.recId_ != null) {
                if (obTC.recType_ == null)
                    throw new org.omg.CORBA.BAD_TYPECODE(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                            org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                writeTypeCodeImpl(obTC.recType_, history);
                return;
            }
        }
        
        logger.finest("Writing a type code of type " + tc.kind().value()); 

        //
        // For performance reasons, handle the primitive TypeCodes first
        //
        switch (tc.kind().value()) {
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
        case org.omg.CORBA.TCKind._tk_longdouble:
        case org.omg.CORBA.TCKind._tk_boolean:
        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_octet:
        case org.omg.CORBA.TCKind._tk_any:
        case org.omg.CORBA.TCKind._tk_TypeCode:
        case org.omg.CORBA.TCKind._tk_Principal:
            write_ulong(tc.kind().value());
            return;

        default:
            break;
        }

        Integer indirectionPos = (Integer) history.get(tc);
        if (indirectionPos != null) {
            write_long(-1);
            int offs = indirectionPos.intValue() - buf_.pos_;
            logger.finest("Writing an indirect type code for offset " + offs); 
            write_long(offs);
        } else {
            write_ulong(tc.kind().value());
            Integer oldPos = new Integer(buf_.pos_ - 4);

            try {
                switch (tc.kind().value()) {
                case org.omg.CORBA.TCKind._tk_fixed: {
                    history.put(tc, oldPos);

                    write_ushort(tc.fixed_digits());
                    write_short(tc.fixed_scale());

                    break;
                }

                case org.omg.CORBA.TCKind._tk_objref: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA.TCKind._tk_struct:
                case org.omg.CORBA.TCKind._tk_except: {
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

                case org.omg.CORBA.TCKind._tk_union: {
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
                            org.omg.CORBA.TypeCode origDiscType = TypeCode
                                    ._OB_getOrigType(discType);
                            switch (origDiscType.kind().value()) {
                            case org.omg.CORBA.TCKind._tk_short:
                                write_short((short) 0);
                                break;
                            case org.omg.CORBA.TCKind._tk_ushort:
                                write_ushort((short) 0);
                                break;
                            case org.omg.CORBA.TCKind._tk_long:
                                write_long(0);
                                break;
                            case org.omg.CORBA.TCKind._tk_ulong:
                                write_ulong(0);
                                break;
                            case org.omg.CORBA.TCKind._tk_longlong:
                                write_longlong(0);
                                break;
                            case org.omg.CORBA.TCKind._tk_ulonglong:
                                write_ulonglong(0);
                                break;
                            case org.omg.CORBA.TCKind._tk_boolean:
                                write_boolean(false);
                                break;
                            case org.omg.CORBA.TCKind._tk_char:
                                write_char((char) 0);
                                break;
                            case org.omg.CORBA.TCKind._tk_enum:
                                write_ulong(0);
                                break;
                            default:
                                org.apache.yoko.orb.OB.Assert._OB_assert("Invalid sub-type in tk_union");
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

                case org.omg.CORBA.TCKind._tk_enum: {
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

                case org.omg.CORBA.TCKind._tk_string:
                case org.omg.CORBA.TCKind._tk_wstring:
                    write_ulong(tc.length());
                    break;

                case org.omg.CORBA.TCKind._tk_sequence:
                case org.omg.CORBA.TCKind._tk_array: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    writeTypeCodeImpl(tc.content_type(), history);
                    write_ulong(tc.length());
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA.TCKind._tk_alias: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeTypeCodeImpl(tc.content_type(), history);
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA.TCKind._tk_value: {
                    history.put(tc, oldPos);

                    org.omg.CORBA.TypeCode concreteBase = tc
                            .concrete_base_type();
                    if (concreteBase == null) {
                        concreteBase = org.apache.yoko.orb.OB.TypeCodeFactory
                                .createPrimitiveTC(org.omg.CORBA.TCKind.tk_null);
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

                case org.omg.CORBA.TCKind._tk_value_box: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeTypeCodeImpl(tc.content_type(), history);
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA.TCKind._tk_abstract_interface: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA.TCKind._tk_native: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                case org.omg.CORBA_2_4.TCKind._tk_local_interface: {
                    history.put(tc, oldPos);

                    int start = writeGap();
                    _OB_writeEndian();
                    write_string(tc.id());
                    write_string(tc.name());
                    writeLength(start);

                    break;
                }

                default:
                    org.apache.yoko.orb.OB.Assert._OB_assert("Invalid typecode");
                }
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        }
    }

    //
    // Must be called prior to any writes
    //
    private void checkBeginChunk() {
        org.apache.yoko.orb.OB.Assert._OB_assert(valueWriter_ != null);
        valueWriter_.checkBeginChunk();
    }

    private org.apache.yoko.orb.OB.ValueWriter valueWriter() {
        if (valueWriter_ == null)
            valueWriter_ = new org.apache.yoko.orb.OB.ValueWriter(this);
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
            if (buf_.pos_ == buf_.len_ && valueWriter_ != null) {
                checkBeginChunk();
            }

            //
            // If there isn't enough room, then reallocate the buffer
            //
            final int len = buf_.pos_ + size;
            if (len > buf_.len_) {
                buf_.realloc(len);
            }
        }
    }

    private void addCapacity(int size, int align) {
        // use addCapacity(int) if align == 0
        org.apache.yoko.orb.OB.Assert._OB_assert(align > 0);

        //
        // If we're at the end of the current buffer, then we are about
        // to write new data. We must first check if we need to start a
        // chunk, which may result in a recursive call to addCapacity().
        //
        if (buf_.pos_ == buf_.len_ && valueWriter_ != null) {
            checkBeginChunk();
        }

        //
        // If alignNext_ is set, then use the larger of alignNext_ and align
        //
        if (alignNext_ > 0) {
            align = (alignNext_ > align ? alignNext_ : align);
            alignNext_ = 0;
        }

        //
        // Align to the requested boundary
        //
        int newPos = buf_.pos_ + align - 1;
        newPos -= newPos % align;
        buf_.pos_ = newPos;

        //
        // If there isn't enough room, then reallocate the buffer
        //
        final int len = newPos + size;
        if (len > buf_.len_) {
            buf_.realloc(len);
        }
    }

    //
    // write wchar using old non-compliant method
    //
    private void _OB_write_wchar_old(char value) {
        if (wCharConversionRequired_) {
            final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputWcharConverter;

            value = converter.convert(value);

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (GIOPVersion_) {
            case 0x0101: {
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
            switch (GIOPVersion_) {
            case 0x0100:
            case 0x0101: {
                write_ushort((short) value);
            }
                break;

            default: {
                addCapacity(3);
                buf_.data_[buf_.pos_++] = 2;
                buf_.data_[buf_.pos_++] = (byte) (value >> 8);
                buf_.data_[buf_.pos_++] = (byte) value;
            }
                break;
            }
        }
    }

    //
    // write wchar using new compilant method
    //
    private void _OB_write_wchar_new(char value, boolean partOfString) {
        final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputWcharConverter;

        //
        // pre-convert the character if necessary
        // 
        if (wCharConversionRequired_)
            value = converter.convert(value);

        if (wCharWriterRequired_) {
            if (partOfString == false)
                converter
                        .set_writer_flags(org.apache.yoko.orb.OB.CodeSetWriter.FIRST_CHAR);

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (GIOPVersion_) {
            case 0x0100: {
                //
                // we don't support special writers for GIOP 1.0 if
                // conversion is required or if a writer is required
                //
                org.apache.yoko.orb.OB.Assert._OB_assert(false);
            }
                break;

            case 0x0101: {
                //
                // get the length of the character
                //
                int len = converter.write_count_wchar(value);

                //
                // For GIOP 1.1 we are limited to 2-byte wchars
                // so make sure to check for that
                //
                org.apache.yoko.orb.OB.Assert._OB_assert(len == 2);

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
            switch (GIOPVersion_) {
            case 0x0100: {
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
                buf_.data_[buf_.pos_++] = (byte) (value >>> 8);
                buf_.data_[buf_.pos_++] = (byte) (value & 0xff);
            }
                break;

            case 0x0101: {
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
                buf_.data_[buf_.pos_++] = 2;

                //
                // write the character in big endian format
                // 
                buf_.data_[buf_.pos_++] = (byte) (value >>> 8);
                buf_.data_[buf_.pos_++] = (byte) (value & 0xff);
            }
                break;
            }
        }
    }

    //
    // write wstring using old non-compliant method
    //
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
        switch (GIOPVersion_) {
        case 0x0100:
        case 0x0101: {
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
                final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputWcharConverter;

                for (int i = 0; i < len; i++) {
                    char v = converter.convert(arr[i]);

                    if (v == 0)
                        throw new org.omg.CORBA.DATA_CONVERSION(
                                "illegal wchar value for wstring: " + (int) v);

                    addCapacity(converter.write_count_wchar(v));
                    converter.write_wchar(this, v);
                }
            }
            //
            // UTF-16
            //
            else {
                addCapacity(2 * len);

                for (int i = 0; i < len; i++) {
                    char v = arr[i];

                    if (v == 0)
                        throw new org.omg.CORBA.DATA_CONVERSION(
                                "illegal wchar value for wstring: " + (int) v);

                    buf_.data_[buf_.pos_++] = (byte) (v >> 8);
                    buf_.data_[buf_.pos_++] = (byte) v;
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

    //
    // write wstring using new compliant method
    //
    private void _OB_write_wstring_new(String value) {
        final char[] arr = value.toCharArray();
        final int len = arr.length;

        logger.finest("Writing wstring value " + value); 
        //
        // get converter/writer instance
        //
        final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputWcharConverter;

        //
        // some writers (specially UTF-16) requires the possible BOM
        // only found at the beginning of a string... this will
        // indicate that we are at the start of the first character
        // of the string to the writer
        if (wCharWriterRequired_)
            converter
                    .set_writer_flags(org.apache.yoko.orb.OB.CodeSetWriter.FIRST_CHAR);

        //
        // for GIOP 1.0/1.1 we don't need to differentiate between
        // strings requiring a writer/converter (or not) since they can
        // be handled by the write_wchar() method
        //
        if (GIOPVersion_ == 0x0100 || GIOPVersion_ == 0x0101) {
            //
            // write the length of the string
            //
            write_ulong(len + 1);

            //
            // now write all the characters
            //
            for (int i = 0; i < len; i++)
                write_wchar(arr[i], true);

            // 
            // and the null terminator
            //
            write_wchar((char) 0, true);

            return;
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
            for (int i = 0; i < len; i++) {
                char v = arr[i];

                //
                // check if the character requires conversion
                //
                if (wCharConversionRequired_)
                    v = converter.convert(v);

                //
                // illegal for the string to contain nulls
                //
                if (v == 0)
                    throw new org.omg.CORBA.DATA_CONVERSION(
                            "illegal wchar value for wstring: " + (int) v);

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

            for (int i = 0; i < len; i++) {
                char v = arr[i];

                // 
                // check for conversion
                //
                if (wCharConversionRequired_)
                    v = converter.convert(v);

                //
                // write character in big endian format
                //
                buf_.data_[buf_.pos_++] = (byte) (v >>> 8);
                buf_.data_[buf_.pos_++] = (byte) (v & 0xff);
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

    public void write(int b) throws java.io.IOException {
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

    public org.omg.CORBA.portable.InputStream create_input_stream() {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                buf_.len_);
        if (buf_.len_ > 0)
            System.arraycopy(buf_.data_, 0, buf.data_, 0, buf_.len_);
        
// this is a useful tracepoint, but produces a lot of data, so turn on only 
// if really needed. 
//      if (logger.isLoggable(Level.FINEST)) {
//          logger.fine("new input stream created:\n" + buf.dumpData()); 
//      }

        InputStream in = new InputStream(buf, 0, false, codeConverters_,
                GIOPVersion_);
        in._OB_ORBInstance(orbInstance_);
        return in;
    }

    public void write_boolean(boolean value) {
        addCapacity(1);

        buf_.data_[buf_.pos_++] = value ? (byte) 1 : (byte) 0;
    }

    public void write_char(char value) {
        if (value > 255)
            throw new org.omg.CORBA.DATA_CONVERSION("char value exceeds 255: "
                    + (int) value);

        addCapacity(1);

        final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputCharConverter;

        if (charConversionRequired_)
            value = converter.convert(value);

        if (charWriterRequired_)
            converter.write_char(this, value);
        else
            buf_.data_[buf_.pos_++] = (byte) value;
    }

    public void write_wchar(char value) {
        write_wchar(value, false);
    }

    public void write_wchar(char value, boolean partOfString) {
        if (org.apache.yoko.orb.OB.OB_Extras.COMPAT_WIDE_MARSHAL == false)
            _OB_write_wchar_new(value, partOfString);
        else
            _OB_write_wchar_old(value);
    }

    public void write_octet(byte value) {
        addCapacity(1);
        buf_.data_[buf_.pos_++] = value;
    }

    public void write_short(short value) {
        addCapacity(2, 2);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 8);
        buf_.data_[buf_.pos_++] = (byte) value;
    }

    public void write_ushort(short value) {
        write_short(value);
    }

    public void write_long(int value) {
        addCapacity(4, 4);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 24);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 16);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 8);
        buf_.data_[buf_.pos_++] = (byte) value;
    }

    public void write_ulong(int value) {
        write_long(value);
    }

    public void write_longlong(long value) {
        addCapacity(8, 8);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 56);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 48);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 40);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 32);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 24);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 16);
        buf_.data_[buf_.pos_++] = (byte) (value >>> 8);
        buf_.data_[buf_.pos_++] = (byte) value;
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
        logger.finest("Writing string value " + value); 
        final char[] arr = value.toCharArray();
        int len = arr.length;
        int capacity = len + 1;

        if (!(charWriterRequired_ || charConversionRequired_)) {
            write_ulong(capacity);
            addCapacity(capacity);

            for (int i = 0; i < len; i++) {
                char c = arr[i];

                if (c == 0 || c > 255)
                    throw new org.omg.CORBA.DATA_CONVERSION(
                            "illegal char value for string: " + (int) c);

                buf_.data_[buf_.pos_++] = (byte) c;
            }
        } else {
            final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputCharConverter;

            //
            // Intermediate variable used for efficiency
            //
            boolean bothRequired = charWriterRequired_
                    && charConversionRequired_;

            //
            // Temporary OCI buffer required
            //
            org.apache.yoko.orb.OCI.Buffer buffer = new org.apache.yoko.orb.OCI.Buffer(
                    64);
            OutputStream tmpStream = new OutputStream(buffer);

            for (int i = 0; i < len; i++) {
                char c = arr[i];

                if (c == 0 || c > 255)
                    throw new org.omg.CORBA.DATA_CONVERSION(
                            "illegal char value for string: " + (int) c);

                //
                // Expand the temporary buffer, if necessary
                //
                if (buffer.length() - buffer.pos() <= 4)
                    buffer.realloc(buffer.length() * 2);

                if (bothRequired)
                    converter.write_char(tmpStream, converter.convert(c));
                else if (charWriterRequired_)
                    converter.write_char(tmpStream, c);
                else
                    buffer.data_[buffer.pos_++] = (byte) converter.convert(c);
            }

            //
            // Copy the contents from the temporary buffer
            //
            int bufSize = buffer.pos_;

            write_ulong(bufSize + 1);
            addCapacity(bufSize + 1);

            for (int i = 0; i < bufSize; i++) {
                buf_.data_[buf_.pos_++] = buffer.data_[i];
            }
        }

        buf_.data_[buf_.pos_++] = (byte) 0;
    }

    public void write_wstring(String value) {
        if (org.apache.yoko.orb.OB.OB_Extras.COMPAT_WIDE_MARSHAL == false)
            _OB_write_wstring_new(value);
        else
            _OB_write_wstring_old(value);
    }

    public void write_boolean_array(boolean[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length);

            for (int i = offset; i < offset + length; i++)
                buf_.data_[buf_.pos_++] = value[i] ? (byte) 1 : (byte) 0;
        }
    }

    public void write_char_array(char[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length);

            if (!(charWriterRequired_ || charConversionRequired_)) {
                for (int i = offset; i < offset + length; i++) {
                    if (value[i] > 255)
                        throw new org.omg.CORBA.DATA_CONVERSION(
                                "char value exceeds 255: " + (int) value[i]);

                    buf_.data_[buf_.pos_++] = (byte) value[i];
                }
            } else {
                final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.outputCharConverter;

                //
                // Intermediate variable used for efficiency
                //
                boolean bothRequired = charWriterRequired_
                        && charConversionRequired_;

                for (int i = offset; i < offset + length; i++) {
                    if (value[i] > 255)
                        throw new org.omg.CORBA.DATA_CONVERSION(
                                "char value exceeds 255: " + (int) value[i]);

                    if (bothRequired)
                        converter.write_char(this, converter.convert(value[i]));
                    else if (charWriterRequired_)
                        converter.write_char(this, value[i]);
                    else
                        buf_.data_[buf_.pos_++] = (byte) converter
                                .convert(value[i]);
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
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 8);
                buf_.data_[buf_.pos_++] = (byte) value[i];
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
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 24);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 16);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 8);
                buf_.data_[buf_.pos_++] = (byte) value[i];
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
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 56);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 48);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 40);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 32);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 24);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 16);
                buf_.data_[buf_.pos_++] = (byte) (value[i] >>> 8);
                buf_.data_[buf_.pos_++] = (byte) value[i];
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

                buf_.data_[buf_.pos_++] = (byte) (v >>> 24);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 16);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 8);
                buf_.data_[buf_.pos_++] = (byte) v;
            }
        }
    }

    public void write_double_array(double[] value, int offset, int length) {
        if (length > 0) {
            addCapacity(length * 8, 8);

            for (int i = offset; i < offset + length; i++) {
                long v = Double.doubleToLongBits(value[i]);

                buf_.data_[buf_.pos_++] = (byte) (v >>> 56);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 48);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 40);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 32);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 24);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 16);
                buf_.data_[buf_.pos_++] = (byte) (v >>> 8);
                buf_.data_[buf_.pos_++] = (byte) v;
            }
        }
    }

    public void write_Object(org.omg.CORBA.Object value) {
        if (value == null) {
            logger.finest("Writing a null CORBA object value"); 
            org.omg.IOP.IOR ior = new org.omg.IOP.IOR();
            ior.type_id = "";
            ior.profiles = new org.omg.IOP.TaggedProfile[0];
            org.omg.IOP.IORHelper.write(this, ior);
        } else {
            if (value instanceof org.omg.CORBA.LocalObject)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject),
                        org.apache.yoko.orb.OB.MinorCodes.MinorLocalObject,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            Delegate p = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) value)
                    ._get_delegate();

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
            throw new org.omg.CORBA.BAD_TYPECODE("TypeCode is nil");

        java.util.Hashtable history = new java.util.Hashtable(11);
        writeTypeCodeImpl(t, history);
    }

    public void write_any(org.omg.CORBA.Any value) {
        logger.finest("Writing an ANY value of type " + value.type().kind()); 
        write_TypeCode(value.type());
        value.write_value(this);
    }

    public void write_Context(org.omg.CORBA.Context ctx,
            org.omg.CORBA.ContextList contexts) {
        int count = contexts.count();
        java.util.Vector v = new java.util.Vector();
        org.apache.yoko.orb.CORBA.Context ctxImpl = (org.apache.yoko.orb.CORBA.Context) ctx;
        for (int i = 0; i < count; i++) {
            try {
                String pattern = contexts.item(i);
                ctxImpl._OB_getValues("", 0, pattern, v);
            } catch (org.omg.CORBA.Bounds ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            }
        }

        write_ulong(v.size());

        java.util.Enumeration e = v.elements();
        while (e.hasMoreElements())
            write_string((String) e.nextElement());
    }

    public void write_Principal(org.omg.CORBA.Principal value) {
        // Deprecated by CORBA 2.2
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void write_fixed(java.math.BigDecimal value) {
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

    public void write_value(java.io.Serializable value) {
        valueWriter().writeValue(value, null);
    }

    public void write_value(java.io.Serializable value, java.lang.String rep_id) {
        valueWriter().writeValue(value, rep_id);
    }

    public void write_value(java.io.Serializable value, Class clz) {
        valueWriter().writeValue(value, null);
    }

    public void write_value(java.io.Serializable value,
            org.omg.CORBA.portable.BoxedValueHelper helper) {
        valueWriter().writeValueBox(value, null, helper);
    }

    public void write_abstract_interface(java.lang.Object obj) {
        valueWriter().writeAbstractInterface(obj);
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public void write_value(java.io.Serializable value,
            org.omg.CORBA.TypeCode tc,
            org.omg.CORBA.portable.BoxedValueHelper helper) {
        valueWriter().writeValueBox(value, tc, helper);
    }

    public void write_InputStream(org.omg.CORBA.portable.InputStream in,
            org.omg.CORBA.TypeCode tc) {
        InputStream obin = null;
        try {
            obin = (InputStream) in;
        } catch (ClassCastException ex) {
            // InputStream may have been created by a different ORB
        }

        try {
            logger.fine("writing a value of type " + tc.kind().value()); 
            
            switch (tc.kind().value()) {
            case org.omg.CORBA.TCKind._tk_null:
            case org.omg.CORBA.TCKind._tk_void:
                break;

            case org.omg.CORBA.TCKind._tk_short:
            case org.omg.CORBA.TCKind._tk_ushort:
                write_short(in.read_short());
                break;

            case org.omg.CORBA.TCKind._tk_long:
            case org.omg.CORBA.TCKind._tk_ulong:
            case org.omg.CORBA.TCKind._tk_float:
            case org.omg.CORBA.TCKind._tk_enum:
                write_long(in.read_long());
                break;

            case org.omg.CORBA.TCKind._tk_double:
            case org.omg.CORBA.TCKind._tk_longlong:
            case org.omg.CORBA.TCKind._tk_ulonglong:
                write_longlong(in.read_longlong());
                break;

            case org.omg.CORBA.TCKind._tk_boolean:
            case org.omg.CORBA.TCKind._tk_octet:
                write_octet(in.read_octet());
                break;

            case org.omg.CORBA.TCKind._tk_char:
                write_char(in.read_char());
                break;

            case org.omg.CORBA.TCKind._tk_wchar:
                write_wchar(in.read_wchar());
                break;

            case org.omg.CORBA.TCKind._tk_fixed:
                write_fixed(in.read_fixed());
                break;

            case org.omg.CORBA.TCKind._tk_any: {
                // Don't do this: write_any(in.read_any())
                // This is faster:
                org.omg.CORBA.TypeCode p = in.read_TypeCode();
                write_TypeCode(p);
                write_InputStream(in, p);
                break;
            }

            case org.omg.CORBA.TCKind._tk_TypeCode: {
                // Don't do this: write_TypeCode(in.read_TypeCode())
                // This is faster:

                int kind = in.read_ulong();

                //
                // An indirection is not permitted at this level
                //
                if (kind == -1) {
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection),
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }

                write_ulong(kind);

                switch (kind) {
                case org.omg.CORBA.TCKind._tk_null:
                case org.omg.CORBA.TCKind._tk_void:
                case org.omg.CORBA.TCKind._tk_short:
                case org.omg.CORBA.TCKind._tk_long:
                case org.omg.CORBA.TCKind._tk_ushort:
                case org.omg.CORBA.TCKind._tk_ulong:
                case org.omg.CORBA.TCKind._tk_float:
                case org.omg.CORBA.TCKind._tk_double:
                case org.omg.CORBA.TCKind._tk_boolean:
                case org.omg.CORBA.TCKind._tk_char:
                case org.omg.CORBA.TCKind._tk_octet:
                case org.omg.CORBA.TCKind._tk_any:
                case org.omg.CORBA.TCKind._tk_TypeCode:
                case org.omg.CORBA.TCKind._tk_Principal:
                case org.omg.CORBA.TCKind._tk_longlong:
                case org.omg.CORBA.TCKind._tk_ulonglong:
                case org.omg.CORBA.TCKind._tk_longdouble:
                case org.omg.CORBA.TCKind._tk_wchar:
                    break;

                case org.omg.CORBA.TCKind._tk_fixed:
                    write_ushort(in.read_ushort());
                    write_short(in.read_short());
                    break;

                case org.omg.CORBA.TCKind._tk_objref:
                case org.omg.CORBA.TCKind._tk_struct:
                case org.omg.CORBA.TCKind._tk_union:
                case org.omg.CORBA.TCKind._tk_enum:
                case org.omg.CORBA.TCKind._tk_sequence:
                case org.omg.CORBA.TCKind._tk_array:
                case org.omg.CORBA.TCKind._tk_alias:
                case org.omg.CORBA.TCKind._tk_except:
                case org.omg.CORBA.TCKind._tk_value:
                case org.omg.CORBA.TCKind._tk_value_box:
                case org.omg.CORBA.TCKind._tk_abstract_interface:
                case org.omg.CORBA.TCKind._tk_native:
                case org.omg.CORBA_2_4.TCKind._tk_local_interface: {
                    final int len = in.read_ulong();
                    write_ulong(len);
                    addCapacity(len);
                    in.read_octet_array(buf_.data_, buf_.pos_, len);
                    buf_.pos_ += len;
                    break;
                }

                case org.omg.CORBA.TCKind._tk_string:
                case org.omg.CORBA.TCKind._tk_wstring: {
                    int bound = in.read_ulong();
                    write_ulong(bound);
                    break;
                }

                default:
                    throw new InternalError();
                }

                break;
            }

            case org.omg.CORBA.TCKind._tk_Principal:
                write_Principal(in.read_Principal());
                break;

            case org.omg.CORBA.TCKind._tk_objref: {
                // Don't do this: write_Object(in.read_Object())
                // This is faster:
                org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
                org.omg.IOP.IORHelper.write(this, ior);
                break;
            }

            case org.omg.CORBA.TCKind._tk_struct:
                for (int i = 0; i < tc.member_count(); i++)
                    write_InputStream(in, tc.member_type(i));
                break;

            case org.omg.CORBA.TCKind._tk_except:
                write_string(in.read_string());
                for (int i = 0; i < tc.member_count(); i++)
                    write_InputStream(in, tc.member_type(i));
                break;

            case org.omg.CORBA.TCKind._tk_union: {
                int defaultIndex = tc.default_index();
                int memberIndex = -1;

                org.omg.CORBA.TypeCode origDiscType = TypeCode
                        ._OB_getOrigType(tc.discriminator_type());

                switch (origDiscType.kind().value()) {
                case org.omg.CORBA.TCKind._tk_short: {
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

                case org.omg.CORBA.TCKind._tk_ushort: {
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

                case org.omg.CORBA.TCKind._tk_long: {
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

                case org.omg.CORBA.TCKind._tk_ulong: {
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

                case org.omg.CORBA.TCKind._tk_longlong: {
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

                case org.omg.CORBA.TCKind._tk_ulonglong: {
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

                case org.omg.CORBA.TCKind._tk_char: {
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

                case org.omg.CORBA.TCKind._tk_boolean: {
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

                case org.omg.CORBA.TCKind._tk_enum: {
                    int val = in.read_long();
                    write_long(val);

                    for (int i = 0; i < tc.member_count(); i++)
                        if (i != defaultIndex) {
                            if (val == tc.member_label(i).create_input_stream()
                                    .read_long()) {
                                memberIndex = i;
                                break;
                            }
                        }

                    break;
                }

                default:
                    org.apache.yoko.orb.OB.Assert._OB_assert("Invalid typecode in tk_union");
                }

                if (memberIndex >= 0)
                    write_InputStream(in, tc.member_type(memberIndex));
                else if (defaultIndex >= 0)
                    write_InputStream(in, tc.member_type(defaultIndex));

                break;
            }

            case org.omg.CORBA.TCKind._tk_string:
                write_string(in.read_string());
                break;

            case org.omg.CORBA.TCKind._tk_wstring:
                write_wstring(in.read_wstring());
                break;

            case org.omg.CORBA.TCKind._tk_sequence:
            case org.omg.CORBA.TCKind._tk_array: {
                int len;

                if (tc.kind().value() == org.omg.CORBA.TCKind._tk_sequence) {
                    len = in.read_ulong();
                    write_ulong(len);
                } else
                    len = tc.length();

                if (len > 0) {
                    org.omg.CORBA.TypeCode origContentType = TypeCode
                            ._OB_getOrigType(tc.content_type());

                    switch (origContentType.kind().value()) {
                    case org.omg.CORBA.TCKind._tk_null:
                    case org.omg.CORBA.TCKind._tk_void:
                        break;

                    case org.omg.CORBA.TCKind._tk_short:
                    case org.omg.CORBA.TCKind._tk_ushort: {
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
                                org.apache.yoko.orb.OCI.Buffer buf = obin
                                        ._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_,
                                        buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case org.omg.CORBA.TCKind._tk_long:
                    case org.omg.CORBA.TCKind._tk_ulong:
                    case org.omg.CORBA.TCKind._tk_float: {
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
                                org.apache.yoko.orb.OCI.Buffer buf = obin
                                        ._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_,
                                        buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case org.omg.CORBA.TCKind._tk_double:
                    case org.omg.CORBA.TCKind._tk_longlong:
                    case org.omg.CORBA.TCKind._tk_ulonglong: {
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
                                org.apache.yoko.orb.OCI.Buffer buf = obin
                                        ._OB_buffer();
                                System.arraycopy(buf.data_, buf.pos_,
                                        buf_.data_, buf_.pos_, n);
                                buf.pos_ += n;
                                buf_.pos_ += n;
                            }
                        }
                        break;
                    }

                    case org.omg.CORBA.TCKind._tk_boolean:
                    case org.omg.CORBA.TCKind._tk_octet:
                        if (obin == null) {
                            addCapacity(len);
                            in.read_octet_array(buf_.data_, buf_.pos_, len);
                            buf_.pos_ += len;
                        } else {
                            addCapacity(len);
                            org.apache.yoko.orb.OCI.Buffer buf = obin
                                    ._OB_buffer();
                            System.arraycopy(buf.data_, buf.pos_, buf_.data_,
                                    buf_.pos_, len);
                            buf.pos_ += len;
                            buf_.pos_ += len;
                        }
                        break;

                    case org.omg.CORBA.TCKind._tk_char:
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

                    case org.omg.CORBA.TCKind._tk_wchar: {
                        char[] wch = new char[len];
                        in.read_wchar_array(wch, 0, len);
                        write_wchar_array(wch, 0, len);
                        break;
                    }

                    case org.omg.CORBA.TCKind._tk_alias:
                        org.apache.yoko.orb.OB.Assert._OB_assert("tk_alias not supported in tk_array or tk_sequence");
                        break;

                    default:
                        for (int i = 0; i < len; i++)
                            write_InputStream(in, tc.content_type());
                        break;
                    }
                }

                break;
            }

            case org.omg.CORBA.TCKind._tk_alias:
                write_InputStream(in, tc.content_type());
                break;

            case org.omg.CORBA.TCKind._tk_value:
            case org.omg.CORBA.TCKind._tk_value_box:
                if (obin == null) {
                    org.omg.CORBA_2_3.portable.InputStream i = (org.omg.CORBA_2_3.portable.InputStream) in;
                    write_value(i.read_value());
                } else
                    obin._OB_remarshalValue(tc, this);
                break;

            case org.omg.CORBA.TCKind._tk_abstract_interface: {
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
                        obin._OB_remarshalValue(org.omg.CORBA.ValueBaseHelper
                                .type(), this);
                    }
                }
                break;
            }

            case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            case org.omg.CORBA.TCKind._tk_native:
            default:
                org.apache.yoko.orb.OB.Assert._OB_assert("unsupported types");
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);    
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);    
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public OutputStream(org.apache.yoko.orb.OCI.Buffer buf) {
        this(buf, null, 0);
    }

    public OutputStream(org.apache.yoko.orb.OCI.Buffer buf,
            org.apache.yoko.orb.OB.CodeConverters converters, int GIOPVersion) {
        buf_ = buf;

        if (GIOPVersion != 0)
            GIOPVersion_ = GIOPVersion;

        charWriterRequired_ = false;
        charConversionRequired_ = false;
        wCharWriterRequired_ = false;
        wCharConversionRequired_ = false;

        codeConverters_ = new org.apache.yoko.orb.OB.CodeConverters(converters);

        if (converters != null) {
            if (codeConverters_.outputCharConverter != null) {
                charWriterRequired_ = codeConverters_.outputCharConverter
                        .writerRequired();
                charConversionRequired_ = codeConverters_.outputCharConverter
                        .conversionRequired();
            }

            if (codeConverters_.outputWcharConverter != null) {
                wCharWriterRequired_ = codeConverters_.outputWcharConverter
                        .writerRequired();
                wCharConversionRequired_ = codeConverters_.outputWcharConverter
                        .conversionRequired();
            }
        }
    }

    public org.apache.yoko.orb.OCI.Buffer _OB_buffer() {
        return buf_;
    }

    public int _OB_pos() {
        return buf_.pos_;
    }

    public void _OB_pos(int pos) {
        buf_.pos_ = pos;
    }

    public void _OB_align(int n) {
        if (buf_.pos_ % n != 0)
            addCapacity(0, n);
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
    public void _OB_ORBInstance(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    // Java only
    public org.apache.yoko.orb.OB.ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    // Java only
    public void _OB_invocationContext(java.lang.Object invocationContext) {
        invocationContext_ = invocationContext;
    }

    // Java only
    public java.lang.Object _OB_invocationContext() {
        return invocationContext_;
    }

    // Java only
    public void _OB_delegateContext(java.lang.Object delegateContext) {
        delegateContext_ = delegateContext;
    }

    // Java only
    public java.lang.Object _OB_delegateContext() {
        return delegateContext_;
    }
}
