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

import javax.rmi.CORBA.Util;

import org.omg.SendingContext.CodeBase;

final public class InputStream extends org.omg.CORBA_2_3.portable.InputStream {
    static final Logger logger = Logger.getLogger(InputStream.class.getName());
    
    org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    public org.apache.yoko.orb.OCI.Buffer buf_;

    boolean swap_;

    private int GIOPVersion_ = org.apache.yoko.orb.OB.OB_Extras.DEFAULT_GIOP_VERSION;

    private int origPos_;

    private boolean origSwap_;

    //
    // Handles all OBV marshaling
    // 
    private org.apache.yoko.orb.OB.ValueReader valueReader_;

    private org.apache.yoko.orb.OB.TypeCodeCache cache_;

    //
    // Character conversion properties
    // 
    private org.apache.yoko.orb.OB.CodeConverters codeConverters_;

    private boolean charReaderRequired_;

    private boolean charConversionRequired_;

    private boolean wCharReaderRequired_;

    private boolean wCharConversionRequired_;

	private CodeBase sendingContextRuntime_;

	private String codebase_;

    // ------------------------------------------------------------------
    // Private and protected members
    // ------------------------------------------------------------------

    private TypeCode checkCache(String id, int startPos, int length) {
        TypeCode tc = null;

        if (id.length() > 0) {
            tc = cache_.get(id);
            if (tc != null) {
                _OB_skip(length + startPos - buf_.pos_);
            }
        }

        return tc;
    }

    private org.omg.CORBA.TypeCode readTypeCodeImpl(
            java.util.Hashtable history, boolean isTopLevel) {
        int kind = read_ulong();
        int oldPos = buf_.pos_ - 4;
        logger.finest("Reading a TypeCode of kind " + kind + " from position " + oldPos); 

        TypeCode tc = null;
        if (kind == -1) {
            int offs = read_long();
            int indirectionPos = buf_.pos_ - 4 + offs;
            indirectionPos += (indirectionPos & 0x3); // adjust for alignment
            TypeCode p = (TypeCode) history.get(new Integer(indirectionPos));
            if (p == null) {
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            history.put(new Integer(oldPos), p);
            tc = p;
        } else {
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
                tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                        .createPrimitiveTC(org.omg.CORBA_2_4.TCKind
                                .from_int(kind));
                history.put(new Integer(oldPos), tc);
                break;

            case org.omg.CORBA.TCKind._tk_fixed: {
                short digits = read_ushort();
                short scale = read_short();
                tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                        .createFixedTC(digits, scale);
                history.put(new Integer(oldPos), tc);
                break;
            }

            case org.omg.CORBA.TCKind._tk_objref: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createInterfaceTC(id, read_string());

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                history.put(new Integer(oldPos), tc);
                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_struct:
            case org.omg.CORBA.TCKind._tk_except: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();  

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    //
                    // For potentially recursive types, we must
                    // construct the TypeCode manually in order to
                    // add it to the history
                    //
                    TypeCode p = new TypeCode();
                    history.put(new Integer(oldPos), p);
                    p.kind_ = org.omg.CORBA_2_4.TCKind.from_int(kind);
                    p.id_ = id;
                    p.name_ = read_string();
                    int num = read_ulong();
                    p.memberNames_ = new String[num];
                    p.memberTypes_ = new TypeCode[num];
                    for (int i = 0; i < num; i++) {
                        p.memberNames_[i] = read_string();
                        p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(
                                history, false);
                    }

                    tc = p;

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_union: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    //
                    // For potentially recursive types, we must construct
                    // the TypeCode manually in order to add it to the
                    // history
                    //
                    TypeCode p = new TypeCode();
                    history.put(new Integer(oldPos), p);
                    p.kind_ = org.omg.CORBA.TCKind.tk_union;
                    p.id_ = id;
                    p.name_ = read_string();
                    p.discriminatorType_ = (TypeCode) readTypeCodeImpl(history,
                            false);
                    int defaultIndex = read_long();
                    int num = read_ulong();
                    p.labels_ = new Any[num];
                    p.memberNames_ = new String[num];
                    p.memberTypes_ = new TypeCode[num];

                    //
                    // Check the discriminator type
                    //
                    TypeCode origTC = p.discriminatorType_._OB_getOrigType();

                    switch (origTC.kind().value()) {
                    case org.omg.CORBA.TCKind._tk_short:
                    case org.omg.CORBA.TCKind._tk_ushort:
                    case org.omg.CORBA.TCKind._tk_long:
                    case org.omg.CORBA.TCKind._tk_ulong:
                    case org.omg.CORBA.TCKind._tk_longlong:
                    case org.omg.CORBA.TCKind._tk_ulonglong:
                    case org.omg.CORBA.TCKind._tk_boolean:
                    case org.omg.CORBA.TCKind._tk_char:
                    case org.omg.CORBA.TCKind._tk_enum:
                        break;
                    default:
                        //
                        // Invalid discriminator type
                        //
                        throw new org.omg.CORBA.BAD_TYPECODE(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUnionDiscriminator),
                                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUnionDiscriminator,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                    }

                    for (int i = 0; i < num; i++) {
                        p.labels_[i] = new Any();
                        if (i == defaultIndex) {
                            //
                            // Unmarshal a dummy value of the
                            // appropriate size for the
                            // discriminator type
                            //
                            Any dummy = new Any();
                            dummy.read_value(this, p.discriminatorType_);

                            //
                            // Default label value is the zero octet
                            //
                            p.labels_[i].insert_octet((byte) 0);
                        } else {
                            p.labels_[i].read_value(this, p.discriminatorType_);
                        }
                        p.memberNames_[i] = read_string();
                        p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(
                                history, false);
                    }

                    tc = p;

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_enum: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    String name = read_string();
                    int num = read_ulong();
                    String[] members = new String[num];
                    for (int i = 0; i < num; i++)
                        members[i] = read_string();
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createEnumTC(id, name, members);
                    history.put(new Integer(oldPos), tc);

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_string: {
                tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                        .createStringTC(read_ulong());
                history.put(new Integer(oldPos), tc);
                break;
            }

            case org.omg.CORBA.TCKind._tk_wstring: {
                tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                        .createWStringTC(read_ulong());
                history.put(new Integer(oldPos), tc);
                break;
            }

            case org.omg.CORBA.TCKind._tk_sequence:
            case org.omg.CORBA.TCKind._tk_array: {
                read_ulong(); // encapsulation length
                boolean swap = swap_;
                _OB_readEndian();

                //
                // For potentially recursive types, we must construct
                // the TypeCode manually in order to add it to the
                // history
                //
                TypeCode p = new TypeCode();
                history.put(new Integer(oldPos), p);
                p.kind_ = org.omg.CORBA_2_4.TCKind.from_int(kind);
                p.contentType_ = (TypeCode) readTypeCodeImpl(history, false);
                p.length_ = read_ulong();

                tc = p;

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_alias: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createAliasTC(id, read_string(), readTypeCodeImpl(
                                    history, false));

                    history.put(new Integer(oldPos), tc);

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_value: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    //
                    // For potentially recursive types, we must
                    // construct the TypeCode manually in order to
                    // add it to the history
                    //
                    TypeCode p = new TypeCode();
                    history.put(new Integer(oldPos), p);
                    p.kind_ = org.omg.CORBA_2_4.TCKind.from_int(kind);
                    p.id_ = id;
                    p.name_ = read_string();
                    p.typeModifier_ = read_short();
                    p.concreteBaseType_ = (TypeCode) readTypeCodeImpl(history,
                            false);
                    if (p.concreteBaseType_.kind().value() == org.omg.CORBA.TCKind._tk_null)
                        p.concreteBaseType_ = null;
                    int num = read_ulong();
                    p.memberNames_ = new String[num];
                    p.memberTypes_ = new TypeCode[num];
                    p.memberVisibility_ = new short[num];
                    for (int i = 0; i < num; i++) {
                        p.memberNames_[i] = read_string();
                        p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(
                                history, false);
                        p.memberVisibility_[i] = read_short();
                    }

                    tc = p;

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_value_box: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createValueBoxTC(id, read_string(),
                                    readTypeCodeImpl(history, false));
                    history.put(new Integer(oldPos), tc);

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_abstract_interface: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();
                
                logger.fine("Abstract interface typecode encapsulaton length=" + length + " id=" + id); 

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createAbstractInterfaceTC(id, read_string());
                    history.put(new Integer(oldPos), tc);

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            case org.omg.CORBA.TCKind._tk_native: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createNativeTC(id, read_string());

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                history.put(new Integer(oldPos), tc);
                swap_ = swap;
                break;
            }

            case org.omg.CORBA_2_4.TCKind._tk_local_interface: {
                int length = read_ulong(); // encapsulation length
                // save this position after the read, since we might be on a chunk boundary.
                // however, we do an explicit check for the chunk boundary before doing the 
                // read. 
                checkChunk(); 
                int typePos = buf_.pos_;
                boolean swap = swap_;
                _OB_readEndian();

                String id = read_string();

                if (isTopLevel && cache_ != null)
                    tc = checkCache(id, typePos, length); // may advance pos
                if (tc == null) {
                    tc = (TypeCode) org.apache.yoko.orb.OB.TypeCodeFactory
                            .createLocalInterfaceTC(id, read_string());
                    history.put(new Integer(oldPos), tc);

                    if (id.length() > 0 && cache_ != null)
                        cache_.put(id, tc);
                }

                swap_ = swap;
                break;
            }

            default:
                throw new org.omg.CORBA.BAD_TYPECODE("Unknown TypeCode kind: "
                        + kind);
            }
        }

        return tc;
    }

    private org.apache.yoko.orb.OB.ValueReader valueReader() {
        if (valueReader_ == null)
            valueReader_ = new org.apache.yoko.orb.OB.ValueReader(this);
        return valueReader_;
    }

    //
    // reads wide-characters using the old non-compliant method
    //
    private char _OB_read_wchar_old() {
        checkChunk(); 

        if (wCharConversionRequired_) {
            final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputWcharConverter;

            char value;

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (GIOPVersion_) {

            case 0x0101: {
                if (converter.getFrom().max_bytes <= 2)
                    value = (char) read_ushort();
                else
                    value = (char) read_ulong();

                break;
            }

            default: {
                final int wcharLen = buf_.data_[buf_.pos_++] & 0xff;
                if (buf_.pos_ + wcharLen > buf_.len_)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow),
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                value = converter.read_wchar(this, wcharLen);

                break;
            }
            }

            return converter.convert(value);
        }
        //
        // UTF-16
        //
        else {
            switch (GIOPVersion_) {

            //
            // Orbix2000/ORBacus/E compatible GIOP 1.0 marshaling
            //
            case 0x0100: {
                buf_.pos_ += (buf_.pos_ & 0x1);

                if (buf_.pos_ + 2 > buf_.len_)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                //
                // Assume big endian
                //
                return (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));
            }

            case 0x0101: {
                return (char) read_ushort();
            }

            default: {
                final int wcharLen = buf_.data_[buf_.pos_++] & 0xff;
                if (buf_.pos_ + wcharLen > buf_.len_)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow),
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                return (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));
            }
            }
        }
    }

    //
    // reads wide-characters using compliant method
    //
    private char _OB_read_wchar_new(boolean partOfString) {
        checkChunk(); 
        
        char value;
        final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputWcharConverter;

        if (wCharReaderRequired_) {
            if (partOfString == false)
                converter
                        .set_reader_flags(org.apache.yoko.orb.OB.CodeSetReader.FIRST_CHAR);

            int wcLen = 2;

            switch (GIOPVersion_) {
            case 0x0100:
                //
                // we should not require a reader for GIOP 1.0
                // wchars since this would mean we are using UTF-16.
                // This is not available in Orbix/E compatibility,
                // only UCS-2...
                //
                org.apache.yoko.orb.OB.Assert._OB_assert(false);
                break;

            case 0x0101:
                //
                // align on two-byte boundary
                // 
                buf_.pos_ += (buf_.pos_ & 1);

                break;

            default:
                //
                // get the octet indicating the wchar len
                //
                wcLen = buf_.data_[buf_.pos_++] & 0xff;

                break;
            }

            //
            // check for an overflow condition
            //
            if (buf_.pos_ + wcLen > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            //
            // read in the value with the reader
            //
            value = converter.read_wchar(this, wcLen);
        } else {
            //
            // no reader is required then
            //
            switch (GIOPVersion_) {
            case 0x0100:
                //
                // UCS-2 is the native wchar codeset for both Orbacus
                // and Orbix/E so conversion should not be necessary
                // 
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(!wCharConversionRequired_);

                //
                // align to 2-byte boundary
                //
                buf_.pos_ += (buf_.pos_ & 1);

                //
                // check for overflow on reader
                // 
                if (buf_.pos_ + 2 > buf_.len_)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                //
                // assume big-endian (both Orbacus and Orbix/E do here)
                // and read in the wchar
                //
                return (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));

            case 0x0101:
                //
                // read according to the endian of the message
                //
                if (converter.getFrom().max_bytes <= 2)
                    value = (char) read_ushort();
                else
                    value = (char) read_ulong();

                break;

            default: {
                //
                // read the length octet off the front
                // 
                final int wcLen = buf_.data_[buf_.pos_++] & 0xff;

                //
                // check for an overflow
                //
                if (buf_.pos_ + wcLen > buf_.len_)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                //
                // read the character off in proper endian format
                // 
                if (swap_) {
                    //
                    // the message was in little endian format
                    //
                    value = (char) ((buf_.data_[buf_.pos_++] & 0xff) | (buf_.data_[buf_.pos_++] << 8));
                } else {
                    //
                    // the message was in big endian format
                    //
                    value = (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));
                }

                break;
            }
            }
        }

        //
        // perform conversion is necessary
        //
        if (wCharConversionRequired_)
            value = converter.convert(value);

        return value;
    }

    //
    // reads wide strings using the old improper way
    //
    private String _OB_read_wstring_old() {
        String s = "";

        checkChunk(); 
        int len = read_ulong();

        //
        // 15.3.2.7: For GIOP version 1.1, a wide string is encoded as an
        // unsigned long indicating the length of the string in octets or
        // unsigned integers (determined by the transfer syntax for wchar)
        // followed by the individual wide characters. Both the string length
        // and contents include a terminating null. The terminating null
        // character for a wstring is also a wide character.
        //
        // For GIOP 1.1 the length must not be 0.
        //
        switch (GIOPVersion_) {

        case 0x0100:
        case 0x0101: {
            if (len == 0)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringZeroLength),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringZeroLength,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            char[] tmp = new char[len];
            read_wchar_array(tmp, 0, len);

            //
            // Check for terminating null wchar
            //
            if (tmp[len - 1] != 0)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNoTerminator),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNoTerminator,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            s = new String(tmp, 0, len - 1);

            break;
        }

        default: {
            StringBuffer stringBuffer = new StringBuffer(len);

            if (wCharConversionRequired_) {
                final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputWcharConverter;

                while (len > 0) {
                    final int wcharLen = converter
                            .read_count_wchar((char) buf_.data_[buf_.pos_]);
                    len -= wcharLen;
                    if (buf_.pos_ + wcharLen > buf_.len_)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    char c = converter.read_wchar(this, wcharLen);

                    c = converter.convert(c);

                    //
                    // String must not contain null characters
                    //
                    if (c == 0)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    stringBuffer.append(c);
                }
            }
            //
            // UTF-16
            //
            else {
                while (len > 0) {
                    final int wcharLen = 2;
                    len -= wcharLen;
                    if (buf_.pos_ + wcharLen > buf_.len_)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    char c = (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));

                    //
                    // String must not contain null characters
                    //
                    if (c == 0)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    stringBuffer.append(c);
                }
            }

            s = stringBuffer.toString();

            break;
        }
        }

        return s;
    }

    //
    // reads wide strings using the new compliant method
    //
    private String _OB_read_wstring_new() {
        String s = "";
        checkChunk(); 

        final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputWcharConverter;

        //
        // read the length of the string (specified in characters for
        // GIOP 1.0/1.1 and in octets for GIOP 1.2+)
        // 
        int len = read_ulong();
        logger.fine("Reading wstring of length " + len); 

        switch (GIOPVersion_) {

        case 0x0100:
        case 0x0101: {
            //
            // it is not legal in GIOP 1.0/1.1 for a string to be 0 in
            // length... it MUST have a null terminator
            // 
            if (len == 0) {
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringZeroLength),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringZeroLength,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            char[] tmp = new char[len];

            if (wCharReaderRequired_) {
                converter.set_reader_flags(org.apache.yoko.orb.OB.CodeSetReader.FIRST_CHAR);
            }

            for (int i = 0; i < len; i++) {
                tmp[i] = read_wchar(true);
            }

            //
            // Check for terminating null wchar
            //
            if (tmp[len - 1] != 0)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNoTerminator),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNoTerminator,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            //
            // create the final string
            // 
            s = new String(tmp, 0, len - 1);

            break;
        }

        default: {
            StringBuffer stringBuffer = new StringBuffer(len);

            if (wCharReaderRequired_) {
                converter
                        .set_reader_flags(org.apache.yoko.orb.OB.CodeSetReader.FIRST_CHAR);

                //
                // start adding the characters to the string buffer
                //
                while (len > 0) {
                    if (buf_.pos_ + 2 > buf_.len_)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    int checkChar = (buf_.data_[buf_.pos_] << 8)
                            | (buf_.data_[buf_.pos_ + 1] & 0xff);

                    int wcLen = converter.read_count_wchar((char) checkChar);

                    len -= wcLen;

                    // 
                    // check for an overflow in the read
                    //
                    if (buf_.pos_ + wcLen > buf_.len_)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    //
                    // read the character and convert if necessary
                    // 
                    char c = converter.read_wchar(this, wcLen);
                    if (wCharConversionRequired_)
                        c = converter.convert(c);

                    //
                    // check for invalid null character
                    //
                    if (c == 0)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    // 
                    // append to the string buffer
                    //
                    stringBuffer.append(c);
                }
            } else {
                final int wcLen = 2;

                while (len > 0) {
                    len -= wcLen;

                    //
                    // check for an overflow condition
                    // 
                    if (buf_.pos_ + wcLen > buf_.len_)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    //
                    // read in the char using the message endian
                    // format for GIOP 1.2/1.3
                    // REVISIT: GIOP 1.4 changes these rules
                    // 
                    char c;
                    if (swap_)
                        c = (char) ((buf_.data_[buf_.pos_++] & 0xff) | (buf_.data_[buf_.pos_++] << 8));
                    else
                        c = (char) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));

                    if (wCharConversionRequired_)
                        c = converter.convert(c);

                    //
                    // check for invalid null character
                    //
                    if (c == 0)
                        throw new org.omg.CORBA.MARSHAL(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar),
                                org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNullWChar,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                    //
                    // append to the string buffer
                    //
                    stringBuffer.append(c);
                }
            }

            s = stringBuffer.toString();

            break;
        }
        }

        return s;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------
    public int available() throws java.io.IOException {
        org.apache.yoko.orb.OB.Assert._OB_assert(buf_.len_ >= buf_.pos_);

        return buf_.len_ - buf_.pos_;
    }

    public int read() throws java.io.IOException {
        checkChunk(); 
        if (buf_.pos_ + 1 > buf_.len_)
            return -1;

        return (0xff & buf_.data_[buf_.pos_++]);
    }

    public org.omg.CORBA.ORB orb() {
        if (orbInstance_ != null)
            return orbInstance_.getORB();
        return null;
    }

    public boolean read_boolean() {
        checkChunk(); 

        if (buf_.pos_ + 1 > buf_.len_) {
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        logger.finest("Boolean value is " + buf_.data_[buf_.pos_] + " from position " + buf_.pos_); 
        return buf_.data_[buf_.pos_++] != (byte) 0;
    }

    public char read_char() {
        checkChunk(); 
        if (buf_.pos_ + 1 > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadCharOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadCharOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (charReaderRequired_ || charConversionRequired_) {
            final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputCharConverter;

            if (charReaderRequired_ && charConversionRequired_)
                return converter.convert(converter.read_char(this));
            else if (charReaderRequired_)
                return converter.read_char(this);
            else
                return converter
                        .convert((char) (buf_.data_[buf_.pos_++] & 0xff));
        } else {
            //
            // Note: byte must be masked with 0xff to correct negative values
            //
            return (char) (buf_.data_[buf_.pos_++] & 0xff);
        }
    }

    public char read_wchar() {
        return read_wchar(false);
    }

    public char read_wchar(boolean partOfString) {
        if (org.apache.yoko.orb.OB.OB_Extras.COMPAT_WIDE_MARSHAL == false)
            return _OB_read_wchar_new(partOfString);
        return _OB_read_wchar_old();
    }

    public byte read_octet() {
        checkChunk(); 
        if (buf_.pos_ + 1 > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        return buf_.data_[buf_.pos_++];
    }

    public short read_short() {
        checkChunk(); 
        buf_.pos_ += (buf_.pos_ & 0x1);

        if (buf_.pos_ + 2 > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadShortOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadShortOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            return (short) ((buf_.data_[buf_.pos_++] & 0xff) | (buf_.data_[buf_.pos_++] << 8));
        else
            return (short) ((buf_.data_[buf_.pos_++] << 8) | (buf_.data_[buf_.pos_++] & 0xff));
    }

    public short read_ushort() {
        return read_short();
    }

    public int read_long() {
        checkChunk(); 
        return _OB_readLongUnchecked();
    }

    public int read_ulong() {
        return read_long();
    }

    public long read_longlong() {
        checkChunk(); 
        final int pmod8 = buf_.pos_ & 0x7;
        if (pmod8 != 0)
            buf_.pos_ += 8 - pmod8;

        if (buf_.pos_ + 8 > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            return ((long) buf_.data_[buf_.pos_++] & 0xffL)
                    | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                    | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                    | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                    | ((long) buf_.data_[buf_.pos_++] << 56);
        else
            return ((long) buf_.data_[buf_.pos_++] << 56)
                    | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                    | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                    | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                    | ((long) buf_.data_[buf_.pos_++] & 0xffL);
    }

    public long read_ulonglong() {
        return read_longlong();
    }

    public float read_float() {
        return Float.intBitsToFloat(read_long());
    }

    public double read_double() {
        return Double.longBitsToDouble(read_longlong());
    }

    public String read_string() {
        checkChunk(); 

        //
        // Number of octets (i.e. bytes) in the string (including
        // the null terminator). This may not be the same as the
        // number of characters if encoding was done.
        //
        int length = read_ulong();

        if (length == 0) {
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadStringZeroLength),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadStringZeroLength,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        int newPos = buf_.pos_ + length;
        if (newPos < buf_.pos_ || newPos > buf_.len_) {
            logger.fine("String length=" + length + " newPos=" + newPos + " buf_.pos=" + buf_.pos_ + " buf_.len=" + buf_.len_); 
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadStringOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadStringOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        length--;

        char[] arr = new char[length];

        //
        // Character Count
        //
        int maxChars = length;
        int numChars = 0;

        if (!(charReaderRequired_ || charConversionRequired_)) {
            for (int i = 0; i < length; i++) {
                //
                // Note: byte must be masked with 0xff to correct negative
                // values
                //
                arr[i] = (char) (buf_.data_[buf_.pos_++] & 0xff);

                //
                // String must not contain null characters
                //
                if (arr[i] == 0)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNullChar),
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNullChar,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
        } else {
            final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputCharConverter;

            int bufPos0 = buf_.pos_;
            int i = 0;
            while (buf_.pos_ - bufPos0 < length) {
                char value;

                if (charReaderRequired_)
                    value = converter.read_char(this);
                else
                    //
                    // Note: byte must be masked with 0xff to correct negative
                    // values
                    //
                    value = (char) (buf_.data_[buf_.pos_++] & 0xff);

                //
                // String must not contain null characters
                //
                if (value == 0)
                    throw new org.omg.CORBA.MARSHAL(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNullChar),
                            org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNullChar,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                if (charConversionRequired_)
                    arr[i] = converter.convert(value);
                else
                    arr[i] = value;

                i++;
            }
            numChars = i;
        }

        buf_.pos_ = newPos;
        if (buf_.data_[buf_.pos_ - 1] != 0)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNoTerminator),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNoTerminator,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        int numExtraBytes = 0;
        if (numChars != 0 && numChars != maxChars) {
            //
            // Multiple bytes were required to represent at least one of
            // the characters present.
            //
            numExtraBytes = maxChars - numChars;
        }

        if (numExtraBytes > 0) {
            //
            // Need to ignore the last 'n' chars in 'arr', where
            // n = numExtraChars
            //
            String arrStr = new String(arr);
            return arrStr.substring(0, numChars);
        } else
            return new String(arr);
    }

    public String read_wstring() {
        if (org.apache.yoko.orb.OB.OB_Extras.COMPAT_WIDE_MARSHAL == false)
            return _OB_read_wstring_new();
        return _OB_read_wstring_old();
    }

    public void read_boolean_array(boolean[] value, int offset, int length) {
        if (length > 0) {
            checkChunk(); 

            if (buf_.pos_ + length < buf_.pos_
                    || buf_.pos_ + length > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanArrayOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanArrayOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            for (int i = offset; i < offset + length; i++)
                value[i] = buf_.data_[buf_.pos_++] != (byte) 0;
        }
    }

    public void read_char_array(char[] value, int offset, int length) {
        if (length > 0) {
            checkChunk(); 

            if (buf_.pos_ + length < buf_.pos_
                    || buf_.pos_ + length > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadCharArrayOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadCharArrayOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            if (!(charReaderRequired_ || charConversionRequired_)) {
                for (int i = offset; i < offset + length; i++) {
                    //
                    // Note: byte must be masked with 0xff to correct negative
                    // values
                    //
                    value[i] = (char) (buf_.data_[buf_.pos_++] & 0xff);
                }
            } else {
                final org.apache.yoko.orb.OB.CodeConverterBase converter = codeConverters_.inputCharConverter;

                //
                // Intermediate variable used for efficiency
                //
                boolean bothRequired = charReaderRequired_
                        && charConversionRequired_;

                for (int i = offset; i < offset + length; i++) {
                    if (bothRequired)
                        value[i] = converter.convert(converter.read_char(this));
                    else if (charReaderRequired_)
                        value[i] = converter.read_char(this);
                    else {
                        //
                        // Note: byte must be masked with 0xff
                        // to correct negative values
                        //
                        final char c = (char) (buf_.data_[buf_.pos_++] & 0xff);
                        value[i] = converter.convert(c);
                    }
                }
            }
        }
    }

    public void read_wchar_array(char[] value, int offset, int length) {
        if (length > 0) {
            if (buf_.pos_ + length < buf_.pos_
                    || buf_.pos_ + length > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadCharArrayOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadCharArrayOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            for (int i = offset; i < offset + length; i++)
                value[i] = read_wchar(false);
        }
    }

    public void read_octet_array(byte[] value, int offset, int length) {
        if (length > 0) {
            checkChunk(); 

            int newPos = buf_.pos_ + length;
            if (newPos < buf_.pos_ || newPos > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetArrayOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetArrayOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            System.arraycopy(buf_.data_, buf_.pos_, value, offset, length);

            buf_.pos_ = newPos;
        }
    }

    public void read_short_array(short[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk(); 
        buf_.pos_ += (buf_.pos_ & 0x1);

        int newPos = buf_.pos_ + length * 2;
        if (newPos < buf_.pos_ || newPos > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadShortArrayOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadShortArrayOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = (short) (((short) buf_.data_[buf_.pos_++] & 0xff) | ((short) buf_.data_[buf_.pos_++] << 8));
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = (short) (((short) buf_.data_[buf_.pos_++] << 8) | ((short) buf_.data_[buf_.pos_++] & 0xff));
    }

    public void read_ushort_array(short[] value, int offset, int length) {
        read_short_array(value, offset, length);
    }

    public void read_long_array(int[] value, int offset, int length) {
        if (length <= 0)
            return;
        checkChunk(); 
        final int pmod4 = buf_.pos_ & 0x3;
        if (pmod4 != 0)
            buf_.pos_ += 4 - pmod4;

        int newPos = buf_.pos_ + length * 4;
        if (newPos < buf_.pos_ || newPos > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadLongArrayOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadLongArrayOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = ((int) buf_.data_[buf_.pos_++] & 0xff)
                        | (((int) buf_.data_[buf_.pos_++] << 8) & 0xff00)
                        | (((int) buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                        | ((int) buf_.data_[buf_.pos_++] << 24);
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = ((int) buf_.data_[buf_.pos_++] << 24)
                        | (((int) buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                        | (((int) buf_.data_[buf_.pos_++] << 8) & 0xff00)
                        | ((int) buf_.data_[buf_.pos_++] & 0xff);
    }

    public void read_ulong_array(int[] value, int offset, int length) {
        read_long_array(value, offset, length);
    }

    public void read_longlong_array(long[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk(); 
        
        final int pmod8 = buf_.pos_ & 0x7;
        if (pmod8 != 0)
            buf_.pos_ += 8 - pmod8;

        int newPos = buf_.pos_ + length * 8;
        if (newPos < buf_.pos_ || newPos > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongArrayOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongArrayOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = ((long) buf_.data_[buf_.pos_++] & 0xffL)
                        | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                        | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                        | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                        | ((long) buf_.data_[buf_.pos_++] << 56);
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = ((long) buf_.data_[buf_.pos_++] << 56)
                        | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                        | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                        | ((long) buf_.data_[buf_.pos_++] & 0xffL);
    }

    public void read_ulonglong_array(long[] value, int offset, int length) {
        read_longlong_array(value, offset, length);
    }

    public void read_float_array(float[] value, int offset, int length) {
        if (length > 0) {
            checkChunk(); 

            final int pmod4 = buf_.pos_ & 0x3;
            if (pmod4 != 0)
                buf_.pos_ += 4 - pmod4;

            int newPos = buf_.pos_ + length * 4;
            if (newPos < buf_.pos_ || newPos > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadFloatArrayOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadFloatArrayOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            if (swap_)
                for (int i = offset; i < offset + length; i++) {
                    int v = (buf_.data_[buf_.pos_++] & 0xff)
                            | ((buf_.data_[buf_.pos_++] << 8) & 0xff00)
                            | ((buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                            | (buf_.data_[buf_.pos_++] << 24);

                    value[i] = Float.intBitsToFloat(v);
                }
            else
                for (int i = offset; i < offset + length; i++) {
                    int v = (buf_.data_[buf_.pos_++] << 24)
                            | ((buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                            | ((buf_.data_[buf_.pos_++] << 8) & 0xff00)
                            | (buf_.data_[buf_.pos_++] & 0xff);

                    value[i] = Float.intBitsToFloat(v);
                }
        }
    }

    public void read_double_array(double[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk(); 
        final int pmod8 = buf_.pos_ & 0x7;
        if (pmod8 != 0)
            buf_.pos_ += 8 - pmod8;

        int newPos = buf_.pos_ + length * 8;
        if (newPos < buf_.pos_ || newPos > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadDoubleArrayOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadDoubleArrayOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++) {
                long v = ((long) buf_.data_[buf_.pos_++] & 0xffL)
                        | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                        | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                        | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                        | ((long) buf_.data_[buf_.pos_++] << 56);

                value[i] = Double.longBitsToDouble(v);
            }
        else
            for (int i = offset; i < offset + length; i++) {
                long v = ((long) buf_.data_[buf_.pos_++] << 56)
                        | (((long) buf_.data_[buf_.pos_++] << 48) & 0xff000000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 40) & 0xff0000000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 32) & 0xff00000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 24) & 0xff000000L)
                        | (((long) buf_.data_[buf_.pos_++] << 16) & 0xff0000L)
                        | (((long) buf_.data_[buf_.pos_++] << 8) & 0xff00L)
                        | ((long) buf_.data_[buf_.pos_++] & 0xffL);

                value[i] = Double.longBitsToDouble(v);
            }
    }

    public org.omg.CORBA.Object read_Object() {
        checkChunk(); 

        org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(this);

        if ((ior.type_id.length() == 0) && (ior.profiles.length == 0))
            return null;

        if (orbInstance_ == null)
            throw new org.omg.CORBA.INITIALIZE("InputStream must be created "
                    + "by a full ORB");

        org.apache.yoko.orb.OB.ObjectFactory objectFactory = orbInstance_
                .getObjectFactory();
        return objectFactory.createObject(ior);
    }

    public org.omg.CORBA.Object read_Object(java.lang.Class clz) {
        //
        // clz represents the stub class of the expected static type
        //
        org.omg.CORBA.Object obj = read_Object();

        if (obj != null) {
            // OK, we have two possibilities here.  The usual possibility is we're asked to load 
            // an object using a specified Stub class.  We just create an instance of the stub class, 
            // attach the object as a delegate, and we're done. 
            // 
            // The second possibility is a request for an instance of an interface.  This will require
            // us to locate a stub class using the defined Stub search orders.  After that, the process 
            // is largely the same. 
            org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl) obj;
            
            if (org.omg.CORBA.portable.ObjectImpl.class.isAssignableFrom(clz)) {
                java.lang.Object stubObject;
                try {
                    stubObject = clz.newInstance();
                    org.omg.CORBA.portable.ObjectImpl stubImpl = (org.omg.CORBA.portable.ObjectImpl) stubObject;
                    stubImpl._set_delegate(impl._get_delegate());
                    return stubImpl;
                } catch (IllegalAccessException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                } catch (InstantiationException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                }
                throw new org.omg.CORBA.MARSHAL("Unable to create stub for class " + clz.getName(), 
                    org.apache.yoko.orb.OB.MinorCodes.MinorLoadStub, 
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
            else {
                try {
                    Class stubClass = getIDLStubClass(((org.omg.CORBA_2_3.portable.ObjectImpl)impl)._get_codebase(), clz); 
                    
                    java.lang.Object stubObject = stubClass.newInstance();
                    org.omg.CORBA.portable.ObjectImpl stubImpl = (org.omg.CORBA.portable.ObjectImpl)stubObject;
                    stubImpl._set_delegate(impl._get_delegate());
                    return stubImpl;
                } catch (IllegalAccessException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                } catch (InstantiationException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                } catch (ClassNotFoundException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                } catch (ClassCastException ex) {
                    logger.log(java.util.logging.Level.FINE, "Exception creating object stub", ex); 
                }
                throw new org.omg.CORBA.MARSHAL("Unable to create stub for class " + clz.getName(), 
                    org.apache.yoko.orb.OB.MinorCodes.MinorLoadStub, 
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
        }
        // null object, just return null. 
        return null;
    }


    /**
     * Convert a class type into a stub class name using the
     * ISL stub name rules.
     * 
     * @param c      The class we need to stub.
     * 
     * @return The target stub class name. 
     */
    private String getIDLStubClassName(Class c) {

        String cname = c.getName();

        //String pkgname = null;
        int idx = cname.lastIndexOf('.');

        if (idx == -1) {
            return "_" + cname + "Stub";
        } else {
            return cname.substring(0, idx + 1) + "_" + cname.substring(idx + 1) + "Stub";
        }
    }
    
    
    /**
     * Load a statically-created Stub class for a type, 
     * attempting both the old and new stub class rules.
     * 
     * @param codebase The search codebase to use.
     * @param type     The type we need a stub for.
     * 
     * @return A loaded stub class.
     */
    private Class getIDLStubClass(String codebase, Class type) throws ClassNotFoundException {
        String name = getIDLStubClassName(type); 
        return Util.loadClass(name, codebase, type.getClassLoader()); 
    }
    

    public org.omg.CORBA.TypeCode read_TypeCode() {
        //
        // NOTE:
        //
        // No data with natural alignment of greater than four octets
        // is needed for TypeCode. Therefore it is not necessary to do
        // encapsulation in a separate buffer.
        //

        checkChunk(); 
        java.util.Hashtable history = new java.util.Hashtable(11);
        return readTypeCodeImpl(history, true);
    }

    public org.omg.CORBA.Any read_any() {
        org.omg.CORBA.Any any = new Any(orbInstance_);
        any.read_value(this, read_TypeCode());
        return any;
    }

    public org.omg.CORBA.Context read_Context() {
        final int len = read_ulong();
        String[] values = new String[len];

        for (int i = 0; i < len; i++)
            values[i] = read_string();

        return new Context(orbInstance_.getORB(), "", values);
    }

    public org.omg.CORBA.Principal read_Principal() {
        // Deprecated by CORBA 2.2
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.math.BigDecimal read_fixed() {
        StringBuffer vBuffer = new StringBuffer("0");
        StringBuffer sBuffer = new StringBuffer();

        boolean first = true;
        while (true) {
            final byte b = read_octet();

            int hi = (b >>> 4) & 0x0f;
            if (hi > 9)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            //
            // 0 as high nibble is only valid if it's not the first nibble
            //
            if (!first || hi > 0)
                vBuffer.append((char) (hi + '0'));

            final int lo = b & 0x0f;
            if (lo < 10)
                vBuffer.append((char) (lo + '0'));
            else if (lo == 0x0c || lo == 0x0d) {
                if (lo == 0x0d)
                    sBuffer.append("-");
                break;
            } else
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            first = false;
        }

        sBuffer.append(vBuffer);

        try {
            return new java.math.BigDecimal(sBuffer.toString());
        } catch (NumberFormatException ex) {
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    public java.io.Serializable read_value() {
        return valueReader().readValue();
    }

    public java.io.Serializable read_value(String id) {
        return valueReader().readValue(id);
    }

    public java.io.Serializable read_value(Class clz) {
        return valueReader().readValue(clz);
    }

    public java.io.Serializable read_value(
            org.omg.CORBA.portable.BoxedValueHelper helper) {
        return valueReader().readValueBox(helper);
    }

    public java.io.Serializable read_value(java.io.Serializable value) {
        //
        // This version of read_value is intended for use by factories
        //

        valueReader().initializeValue(value);
        return value;
    }

    public java.lang.Object read_abstract_interface() {
        return valueReader().readAbstractInterface();
    }

    public java.lang.Object read_abstract_interface(java.lang.Class clz) {
        return valueReader().readAbstractInterface(clz);
    }

    // ------------------------------------------------------------------
    // ORBacus-specific methods
    // ------------------------------------------------------------------

    public void read_value(org.omg.CORBA.Any any, org.omg.CORBA.TypeCode tc) {
        valueReader().readValueAny(any, tc);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public InputStream(org.apache.yoko.orb.OCI.Buffer buf, int offs,
            boolean swap, org.apache.yoko.orb.OB.CodeConverters codeConverters,
            int GIOPVersion) {
        buf_ = buf;
        buf_.pos(offs);
        swap_ = swap;
        origPos_ = offs;
        origSwap_ = swap;

        _OB_codeConverters(codeConverters, GIOPVersion);
    }

    public InputStream(org.apache.yoko.orb.OCI.Buffer buf, int offs,
            boolean swap) {
        this(buf, offs, swap, null, 0);
    }

    public InputStream(org.apache.yoko.orb.OCI.Buffer buf) {
        this(buf, 0, false, null, 0);
    }

    public void _OB_codeConverters(
            org.apache.yoko.orb.OB.CodeConverters converters, int GIOPVersion) {
        if (GIOPVersion != 0)
            GIOPVersion_ = GIOPVersion;

        charReaderRequired_ = false;
        charConversionRequired_ = false;
        wCharReaderRequired_ = false;
        wCharConversionRequired_ = false;

        codeConverters_ = new org.apache.yoko.orb.OB.CodeConverters(converters);

        if (converters != null) {
            if (codeConverters_.inputCharConverter != null) {
                charReaderRequired_ = codeConverters_.inputCharConverter
                        .readerRequired();
                charConversionRequired_ = codeConverters_.inputCharConverter
                        .conversionRequired();
            }

            if (codeConverters_.inputWcharConverter != null) {
                wCharReaderRequired_ = codeConverters_.inputWcharConverter
                        .readerRequired();
                wCharConversionRequired_ = codeConverters_.inputWcharConverter
                        .conversionRequired();
            }
        }
    }

    public org.apache.yoko.orb.OB.CodeConverters _OB_codeConverters() {
        return codeConverters_;
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

    public boolean _OB_swap() {
        return swap_;
    }

    public void _OB_swap(boolean swap) {
        swap_ = swap;
    }

    public InputStream _OB_clone() {
        InputStream result;

        byte[] data = new byte[buf_.len_];
        System.arraycopy(buf_.data_, 0, data, 0, buf_.len_);
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                data, data.length);
        result = new InputStream(buf, origPos_, origSwap_, codeConverters_,
                GIOPVersion_);
        result.orbInstance_ = orbInstance_;

        return result;
    }

    public void _OB_reset() {
        swap_ = origSwap_;
        buf_.pos_ = origPos_;
    }

    public void _OB_skip(int n) {
        int newPos = buf_.pos_ + n;
        if (newPos < buf_.pos_ || newPos > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        buf_.pos_ = newPos;
    }

    public void _OB_skipAlign(int n) {
        if (buf_.pos_ % n != 0) {
            int newPos = buf_.pos_ + n - buf_.pos_ % n;

            if (newPos < buf_.pos_ || newPos > buf_.len_)
                throw new org.omg.CORBA.MARSHAL(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadOverflow),
                        org.apache.yoko.orb.OB.MinorCodes.MinorReadOverflow,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            buf_.pos_ = newPos;
        }
    }

    public void _OB_readEndian() {
        swap_ = read_boolean() != false; // false means big endian
    }

    public void _OB_ORBInstance(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;

        if (orbInstance_ != null && orbInstance_.useTypeCodeCache()) {
            //
            // Get the TypeCodeCache of this ORBInstance
            //
            cache_ = org.apache.yoko.orb.OB.TypeCodeCache.instance();
        }
    }

    public org.apache.yoko.orb.OB.ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    public int _OB_readLongUnchecked() {
        //
        // The chunking code needs to read a long value without entering
        // an infinite loop
        //

        final int pmod4 = buf_.pos_ & 0x3;
        if (pmod4 != 0)
            buf_.pos_ += 4 - pmod4;

        if (buf_.pos_ + 4 > buf_.len_)
            throw new org.omg.CORBA.MARSHAL(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadLongOverflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorReadLongOverflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (swap_)
            return (buf_.data_[buf_.pos_++] & 0xff)
                    | ((buf_.data_[buf_.pos_++] << 8) & 0xff00)
                    | ((buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                    | (buf_.data_[buf_.pos_++] << 24);
        else
            return (buf_.data_[buf_.pos_++] << 24)
                    | ((buf_.data_[buf_.pos_++] << 16) & 0xff0000)
                    | ((buf_.data_[buf_.pos_++] << 8) & 0xff00)
                    | (buf_.data_[buf_.pos_++] & 0xff);
    }

    public void _OB_beginValue() {
        valueReader().beginValue();
    }

    public void _OB_endValue() {
        valueReader().endValue();
    }

    public void _OB_remarshalValue(org.omg.CORBA.TypeCode tc, OutputStream out) {
        valueReader().remarshalValue(tc, out);
    }

    public void __setSendingContextRuntime(org.omg.SendingContext.CodeBase runtime) {
    		sendingContextRuntime_ = runtime;
    }
    
	public org.omg.SendingContext.CodeBase __getSendingContextRuntime() {
		return sendingContextRuntime_;
	}

	public void __setCodeBase(String codebase) {
		this.codebase_ = codebase;
	}
	
	public String __getCodeBase() {
		return codebase_;
	}
    
    public String dumpData() {
        return buf_.dumpData(); 
    }
    
    private void checkChunk() {
        if (valueReader_ != null) {
            valueReader_.checkChunk();
        }
    }
}

