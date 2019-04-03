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
import org.apache.yoko.orb.OB.CodeSetReader;
import org.apache.yoko.orb.OB.OB_Extras;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ObjectFactory;
import org.apache.yoko.orb.OB.TypeCodeCache;
import org.apache.yoko.orb.OB.ValueReader;
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.rmi.impl.InputStreamWithOffsets;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA_2_4.TCKind;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.SendingContext.CodeBase;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidUnionDiscriminator;
import static org.apache.yoko.orb.OB.MinorCodes.MinorLoadStub;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadBooleanOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadCharArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadCharOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadDoubleArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadFixedInvalid;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadFloatArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadInvTypeCodeIndirection;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadLongArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadLongLongOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadLongOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadOctetOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadShortArrayOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadShortOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNoTerminator;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadStringNullChar;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadStringOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadStringZeroLength;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadWCharOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringNoTerminator;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringOverflow;
import static org.apache.yoko.orb.OB.MinorCodes.MinorReadWStringZeroLength;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadTypecode;
import static org.apache.yoko.orb.OB.MinorCodes.describeMarshal;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createAbstractInterfaceTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createAliasTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createEnumTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createFixedTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createInterfaceTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createLocalInterfaceTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createNativeTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createPrimitiveTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createStringTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createValueBoxTC;
import static org.apache.yoko.orb.OB.TypeCodeFactory.createWStringTC;
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
import static org.omg.CORBA.TCKind.tk_union;

final public class InputStream extends InputStreamWithOffsets {
    private static final Logger logger = Logger.getLogger(InputStream.class.getName());

    private ORBInstance orbInstance_;

    public final Buffer buf_;

    boolean swap_;

    private GiopVersion giopVersion_ = OB_Extras.DEFAULT_GIOP_VERSION;

    private final int origPos_;

    private final boolean origSwap_;

    //
    // Handles all OBV marshaling
    // 
    private ValueReader valueReader_;

    private TypeCodeCache cache_;

    //
    // Character conversion properties
    // 
    private CodeConverters codeConverters_;

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

    private org.omg.CORBA.TypeCode readTypeCodeImpl(Hashtable history, boolean isTopLevel) {
        int kind = read_ulong();
        int oldPos = buf_.pos_ - 4;
        if (logger.isLoggable(Level.FINEST))
            logger.finest(String.format("Reading a TypeCode of kind %d from position 0x%x", kind, oldPos));

        TypeCode tc = null;
        if (kind == -1) {
            int offs = read_long();
            int indirectionPos = buf_.pos_ - 4 + offs;
            indirectionPos += (indirectionPos & 0x3); // adjust for alignment
            TypeCode p = (TypeCode) history.get(indirectionPos);
            if (p == null) {
                throw new MARSHAL(describeMarshal(MinorReadInvTypeCodeIndirection), MinorReadInvTypeCodeIndirection, COMPLETED_NO);
            }
            history.put(oldPos, p);
            tc = p;
        } else {
            switch (kind) {
                case _tk_null :
                case _tk_void :
                case _tk_short :
                case _tk_long :
                case _tk_ushort :
                case _tk_ulong :
                case _tk_float :
                case _tk_double :
                case _tk_boolean :
                case _tk_char :
                case _tk_octet :
                case _tk_any :
                case _tk_TypeCode :
                case _tk_Principal :
                case _tk_longlong :
                case _tk_ulonglong :
                case _tk_longdouble :
                case _tk_wchar :
                    tc = (TypeCode) createPrimitiveTC(TCKind.from_int(kind));
                    history.put(oldPos, tc);
                    break;

                case _tk_fixed : {
                    short digits = read_ushort();
                    short scale = read_short();
                    tc = (TypeCode) createFixedTC(digits, scale);
                    history.put(oldPos, tc);
                    break;
                }

                case _tk_objref : {
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
                        tc = (TypeCode) createInterfaceTC(id, read_string());

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    history.put(oldPos, tc);
                    swap_ = swap;
                    break;
                }

                case _tk_struct :
                case _tk_except : {
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
                        history.put(oldPos, p);
                        p.kind_ = TCKind.from_int(kind);
                        p.id_ = id;
                        p.name_ = read_string();
                        int num = read_ulong();
                        p.memberNames_ = new String[num];
                        p.memberTypes_ = new TypeCode[num];
                        for (int i = 0; i < num; i++) {
                            p.memberNames_[i] = read_string();
                            p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(history, false);
                        }

                        tc = p;

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_union : {
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
                        history.put(oldPos, p);
                        p.kind_ = tk_union;
                        p.id_ = id;
                        p.name_ = read_string();
                        p.discriminatorType_ = (TypeCode) readTypeCodeImpl(history, false);
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
                            case _tk_short :
                            case _tk_ushort :
                            case _tk_long :
                            case _tk_ulong :
                            case _tk_longlong :
                            case _tk_ulonglong :
                            case _tk_boolean :
                            case _tk_char :
                            case _tk_enum :
                                break;
                            default :
                                //
                                // Invalid discriminator type
                                //
                                throw new BAD_TYPECODE(describeBadTypecode(MinorInvalidUnionDiscriminator), MinorInvalidUnionDiscriminator, COMPLETED_NO);
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
                            p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(history, false);
                        }

                        tc = p;

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_enum : {
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
                        tc = (TypeCode) createEnumTC(id, name, members);
                        history.put(oldPos, tc);

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_string : {
                    tc = (TypeCode) createStringTC(read_ulong());
                    history.put(oldPos, tc);
                    break;
                }

                case _tk_wstring : {
                    tc = (TypeCode) createWStringTC(read_ulong());
                    history.put(oldPos, tc);
                    break;
                }

                case _tk_sequence :
                case _tk_array : {
                    read_ulong(); // encapsulation length
                    boolean swap = swap_;
                    _OB_readEndian();

                    //
                    // For potentially recursive types, we must construct
                    // the TypeCode manually in order to add it to the
                    // history
                    //
                    TypeCode p = new TypeCode();
                    history.put(oldPos, p);
                    p.kind_ = TCKind.from_int(kind);
                    p.contentType_ = (TypeCode) readTypeCodeImpl(history, false);
                    p.length_ = read_ulong();

                    tc = p;

                    swap_ = swap;
                    break;
                }

                case _tk_alias : {
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
                        tc = (TypeCode) createAliasTC(id, read_string(), readTypeCodeImpl(history, false));

                        history.put(oldPos, tc);

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_value : {
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
                        history.put(oldPos, p);
                        p.kind_ = TCKind.from_int(kind);
                        p.id_ = id;
                        p.name_ = read_string();
                        p.typeModifier_ = read_short();
                        p.concreteBaseType_ = (TypeCode) readTypeCodeImpl(history, false);
                        if (p.concreteBaseType_.kind().value() == _tk_null)
                            p.concreteBaseType_ = null;
                        int num = read_ulong();
                        p.memberNames_ = new String[num];
                        p.memberTypes_ = new TypeCode[num];
                        p.memberVisibility_ = new short[num];
                        for (int i = 0; i < num; i++) {
                            p.memberNames_[i] = read_string();
                            p.memberTypes_[i] = (TypeCode) readTypeCodeImpl(history, false);
                            p.memberVisibility_[i] = read_short();
                        }

                        tc = p;

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_value_box : {
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
                        tc = (TypeCode) createValueBoxTC(id, read_string(), readTypeCodeImpl(history, false));
                        history.put(oldPos, tc);

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_abstract_interface : {
                    int length = read_ulong(); // encapsulation length
                    // save this position after the read, since we might be on a chunk boundary.
                    // however, we do an explicit check for the chunk boundary before doing the 
                    // read. 
                    checkChunk();
                    int typePos = buf_.pos_;
                    boolean swap = swap_;
                    _OB_readEndian();

                    String id = read_string();

                    if (logger.isLoggable(Level.FINE))
                        logger.fine(String.format("Abstract interface typecode encapsulaton length=0x%x id=%s", length, id));

                    if (isTopLevel && cache_ != null)
                        tc = checkCache(id, typePos, length); // may advance pos
                    if (tc == null) {
                        tc = (TypeCode) createAbstractInterfaceTC(id, read_string());
                        history.put(oldPos, tc);

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                case _tk_native : {
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
                        tc = (TypeCode) createNativeTC(id, read_string());

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    history.put(oldPos, tc);
                    swap_ = swap;
                    break;
                }

                case TCKind._tk_local_interface : {
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
                        tc = (TypeCode) createLocalInterfaceTC(id, read_string());
                        history.put(oldPos, tc);

                        if (id.length() > 0 && cache_ != null)
                            cache_.put(id, tc);
                    }

                    swap_ = swap;
                    break;
                }

                default :
                    throw new BAD_TYPECODE("Unknown TypeCode kind: " + kind);
            }
        }

        return tc;
    }

    private ValueReader valueReader() {
        if (valueReader_ == null)
            valueReader_ = new ValueReader(this);
        return valueReader_;
    }

    //
    // reads wide-characters using the old non-compliant method
    //
    private char _OB_read_wchar_old() {
        checkChunk();

        if (wCharConversionRequired_) {
            final CodeConverterBase converter = codeConverters_.inputWcharConverter;

            char value;

            //
            // For GIOP 1.1 non byte-oriented wide characters are written
            // as ushort or ulong, depending on their maximum length
            // listed in the code set registry.
            //
            switch (giopVersion_) {

                case GIOP1_1: {
                    if (converter.getFrom().max_bytes <= 2)
                        value = (char) read_ushort();
                    else
                        value = (char) read_ulong();

                    break;
                }

                default : {
                    final int wcharLen = buf_.readByte() & 0xff;
                    if (buf_.available() < wcharLen) throw new MARSHAL(describeMarshal(MinorReadWCharOverflow), MinorReadWCharOverflow, COMPLETED_NO);

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
            switch (giopVersion_) {

            //
            // Orbix2000/ORBacus/E compatible GIOP 1.0 marshaling
            //
                case GIOP1_0: {
                    buf_.align2();

                    if (buf_.available() < 2) throw new MARSHAL(MinorReadWCharOverflow, COMPLETED_NO);

                    //
                    // Assume big endian
                    //
                    return (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));
                }

                case GIOP1_1: {
                    return (char) read_ushort();
                }

                default : {
                    final int wcharLen = buf_.readByte() & 0xff;
                    if (buf_.available() < wcharLen) throw new MARSHAL(describeMarshal(MinorReadWCharOverflow), MinorReadWCharOverflow, COMPLETED_NO);

                    return (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));
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
        final CodeConverterBase converter = codeConverters_.inputWcharConverter;

        if (wCharReaderRequired_) {
            if (!partOfString)
                converter.set_reader_flags(CodeSetReader.FIRST_CHAR);

            int wcLen = 2;

            switch (giopVersion_) {
                case GIOP1_0:
                    //
                    // we should not require a reader for GIOP 1.0
                    // wchars since this would mean we are using UTF-16.
                    // This is not available in Orbix/E compatibility,
                    // only UCS-2...
                    //
                    _OB_assert(false);
                    break;

                case GIOP1_1:
                    //
                    // align on two-byte boundary
                    // 
                    buf_.align2();

                    break;

                default :
                    //
                    // get the octet indicating the wchar len
                    //
                    wcLen = buf_.readByte() & 0xff;

                    break;
            }

            //
            // check for an overflow condition
            //
            if (buf_.available() < wcLen) throw new MARSHAL(describeMarshal(MinorReadWCharOverflow), MinorReadWCharOverflow, COMPLETED_NO);

            //
            // read in the value with the reader
            //
            value = converter.read_wchar(this, wcLen);
        } else {
            //
            // no reader is required then
            //
            switch (giopVersion_) {
                case GIOP1_0:
                    //
                    // UCS-2 is the native wchar codeset for both Orbacus
                    // and Orbix/E so conversion should not be necessary
                    // 
                    _OB_assert(!wCharConversionRequired_);

                    //
                    // align to 2-byte boundary
                    //
                    buf_.align2();

                    //
                    // check for overflow on reader
                    // 
                    if (buf_.available() < 2) throw new MARSHAL(MinorReadWCharOverflow, COMPLETED_NO);

                    //
                    // assume big-endian (both Orbacus and Orbix/E do here)
                    // and read in the wchar
                    //
                    return (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));

                case GIOP1_1:
                    //
                    // read according to the endian of the message
                    //
                    if (converter.getFrom().max_bytes <= 2)
                        value = (char) read_ushort();
                    else
                        value = (char) read_ulong();

                    break;

                default : {
                    //
                    // read the length octet off the front
                    // 
                    final int wcLen = buf_.readByte() & 0xff;

                    //
                    // check for an overflow
                    //
                    if (buf_.available() < wcLen) throw new MARSHAL(MinorReadWCharOverflow, COMPLETED_NO);

                    //
                    // read the character off in proper endian format
                    // 
                    if (swap_) {
                        //
                        // the message was in little endian format
                        //
                        value = (char) ((buf_.readByte() & 0xff) | (buf_.readByte() << 8));
                    } else {
                        //
                        // the message was in big endian format
                        //
                        value = (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));
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
        final String s;

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
        switch (giopVersion_) {

            case GIOP1_0:
            case GIOP1_1: {
                if (len == 0)
                    throw new MARSHAL(describeMarshal(MinorReadWStringZeroLength), MinorReadWStringZeroLength, COMPLETED_NO);

                char[] tmp = new char[len];
                read_wchar_array(tmp, 0, len);

                //
                // Check for terminating null wchar
                //
                if (tmp[len - 1] != 0)
                    throw new MARSHAL(describeMarshal(MinorReadWStringNoTerminator), MinorReadWStringNoTerminator, COMPLETED_NO);

                s = new String(tmp, 0, len - 1);

                break;
            }

            default : {
                StringBuilder stringBuffer = new StringBuilder(len);

                if (wCharConversionRequired_) {
                    final CodeConverterBase converter = codeConverters_.inputWcharConverter;

                    while (len > 0) {
                        final int wcharLen = converter.read_count_wchar((char) buf_.data_[buf_.pos_]);
                        len -= wcharLen;
                        if (buf_.available() < wcharLen)
                            throw new MARSHAL(describeMarshal(MinorReadWStringOverflow), MinorReadWStringOverflow, COMPLETED_NO);

                        char c = converter.read_wchar(this, wcharLen);

                        c = converter.convert(c);

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
                        if (buf_.available() < wcharLen)
                            throw new MARSHAL(describeMarshal(MinorReadWStringOverflow), MinorReadWStringOverflow, COMPLETED_NO);

                        char c = (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));

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
        final String s;
        checkChunk();

        final CodeConverterBase converter = codeConverters_.inputWcharConverter;

        //
        // read the length of the string (specified in characters for
        // GIOP 1.0/1.1 and in octets for GIOP 1.2+)
        // 
        int len = read_ulong();
        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("Reading wstring of length 0x%x", len));

        switch (giopVersion_) {

            case GIOP1_0:
            case GIOP1_1: {
                //
                // it is not legal in GIOP 1.0/1.1 for a string to be 0 in
                // length... it MUST have a null terminator
                // 
                if (len == 0) {
                    throw new MARSHAL(describeMarshal(MinorReadWStringZeroLength), MinorReadWStringZeroLength, COMPLETED_NO);
                }

                char[] tmp = new char[len];

                if (wCharReaderRequired_) {
                    converter.set_reader_flags(CodeSetReader.FIRST_CHAR);
                }

                for (int i = 0; i < len; i++) {
                    tmp[i] = read_wchar(true);
                }

                //
                // Check for terminating null wchar
                //
                if (tmp[len - 1] != 0)
                    throw new MARSHAL(describeMarshal(MinorReadWStringNoTerminator), MinorReadWStringNoTerminator, COMPLETED_NO);

                //
                // create the final string
                // 
                s = new String(tmp, 0, len - 1);

                break;
            }

            default : {
                StringBuilder stringBuffer = new StringBuilder(len);

                if (wCharReaderRequired_) {
                    converter.set_reader_flags(CodeSetReader.FIRST_CHAR);

                    //
                    // start adding the characters to the string buffer
                    //
                    while (len > 0) {
                        if (buf_.available() < 2)
                            throw new MARSHAL(describeMarshal(MinorReadWStringOverflow), MinorReadWStringOverflow, COMPLETED_NO);

                        int checkChar = (buf_.data_[buf_.pos_] << 8) | (buf_.data_[buf_.pos_ + 1] & 0xff);

                        int wcLen = converter.read_count_wchar((char) checkChar);

                        len -= wcLen;

                        // 
                        // check for an overflow in the read
                        //
                        if (buf_.available() < wcLen) throw new MARSHAL(describeMarshal(MinorReadWStringOverflow), MinorReadWStringOverflow, COMPLETED_NO);

                        //
                        // read the character and convert if necessary
                        // 
                        char c = converter.read_wchar(this, wcLen);
                        if (wCharConversionRequired_)
                            c = converter.convert(c);

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
                        if (buf_.available() < wcLen)
                            throw new MARSHAL(describeMarshal(MinorReadWStringOverflow), MinorReadWStringOverflow, COMPLETED_NO);

                        //
                        // read in the char using the message endian
                        // format for GIOP 1.2/1.3
                        // REVISIT: GIOP 1.4 changes these rules
                        // 
                        char c;
                        if (swap_)
                            c = (char) ((buf_.readByte() & 0xff) | (buf_.readByte() << 8));
                        else
                            c = (char) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));

                        if (wCharConversionRequired_)
                            c = converter.convert(c);

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
    public int available() throws IOException {
        _OB_assert(buf_.length() >= buf_.pos_);

        return buf_.length() - buf_.pos_;
    }

    public int read() throws IOException {
        checkChunk();
        if (buf_.available() < 1) return -1;

        return (0xff & buf_.readByte());
    }

    public org.omg.CORBA.ORB orb() {
        if (orbInstance_ != null)
            return orbInstance_.getORB();
        return null;
    }

    public boolean read_boolean() {
        checkChunk();

        if (buf_.available() < 1) throw new MARSHAL(describeMarshal(MinorReadBooleanOverflow), MinorReadBooleanOverflow, COMPLETED_NO);

        if (logger.isLoggable(Level.FINEST))
            logger.finest(String.format("Boolean value is %b from position 0x%x", buf_.data_[buf_.pos_], buf_.pos_));
        return buf_.readByte() != (byte) 0;
    }

    public char read_char() {
        checkChunk();
        if (buf_.available() < 1) throw new MARSHAL(describeMarshal(MinorReadCharOverflow), MinorReadCharOverflow, COMPLETED_NO);

        if (charReaderRequired_ || charConversionRequired_) {
            final CodeConverterBase converter = codeConverters_.inputCharConverter;

            if (charReaderRequired_ && charConversionRequired_)
                return converter.convert(converter.read_char(this));
            else if (charReaderRequired_)
                return converter.read_char(this);
            else
                return converter.convert((char) (buf_.readByte() & 0xff));
        } else {
            //
            // Note: byte must be masked with 0xff to correct negative values
            //
            return (char) (buf_.readByte() & 0xff);
        }
    }

    public char read_wchar() {
        return read_wchar(false);
    }

    private char read_wchar(boolean partOfString) {
        if (!OB_Extras.COMPAT_WIDE_MARSHAL)
            return _OB_read_wchar_new(partOfString);
        return _OB_read_wchar_old();
    }

    public byte read_octet() {
        checkChunk();
        if (buf_.available() < 1) throw new MARSHAL(describeMarshal(MinorReadOctetOverflow), MinorReadOctetOverflow, COMPLETED_NO);

        return buf_.readByte();
    }

    public short read_short() {
        checkChunk();
        buf_.align2();

        if (buf_.available() < 2) throw new MARSHAL(describeMarshal(MinorReadShortOverflow), MinorReadShortOverflow, COMPLETED_NO);
        if (swap_)
            return (short) ((buf_.readByte() & 0xff) | (buf_.readByte() << 8));
        else
            return (short) ((buf_.readByte() << 8) | (buf_.readByte() & 0xff));
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
        buf_.align8();

        if (buf_.available() < 8) throw new MARSHAL(describeMarshal(MinorReadLongLongOverflow), MinorReadLongLongOverflow, COMPLETED_NO);

        if (swap_)
            return ((long) buf_.readByte() & 0xffL)
                    | (((long) buf_.readByte() << 8) & 0xff00L)
                    | (((long) buf_.readByte() << 16) & 0xff0000L)
                    | (((long) buf_.readByte() << 24) & 0xff000000L)
                    | (((long) buf_.readByte() << 32) & 0xff00000000L)
                    | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                    | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                    | ((long) buf_.readByte() << 56);
        else
            return ((long) buf_.readByte() << 56)
                    | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                    | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                    | (((long) buf_.readByte() << 32) & 0xff00000000L)
                    | (((long) buf_.readByte() << 24) & 0xff000000L)
                    | (((long) buf_.readByte() << 16) & 0xff0000L)
                    | (((long) buf_.readByte() << 8) & 0xff00L)
                    | ((long) buf_.readByte() & 0xffL);
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
            throw new MARSHAL(describeMarshal(MinorReadStringZeroLength), MinorReadStringZeroLength, COMPLETED_NO);
        }

        int newPos = buf_.pos_ + length;
        if (newPos < buf_.pos_ || newPos > buf_.length()) {
            if (logger.isLoggable(Level.FINE)) logger.fine(String.format("String length=0x%x newPos=0x%x buf_.pos=0x%x buf_.len=0x%x", length, newPos, buf_.pos_, buf_.length()));
            throw new MARSHAL(describeMarshal(MinorReadStringOverflow), MinorReadStringOverflow, COMPLETED_NO);
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
                arr[i] = (char) (buf_.readByte() & 0xff);

                //
                // String must not contain null characters
                //
                if (arr[i] == 0)
                    throw new MARSHAL(describeMarshal(MinorReadStringNullChar), MinorReadStringNullChar, COMPLETED_NO);
            }
        } else {
            final CodeConverterBase converter = codeConverters_.inputCharConverter;

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
                    value = (char) (buf_.readByte() & 0xff);

                //
                // String must not contain null characters
                //
                if (value == 0)
                    throw new MARSHAL(describeMarshal(MinorReadStringNullChar), MinorReadStringNullChar, COMPLETED_NO);

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
            throw new MARSHAL(describeMarshal(MinorReadStringNoTerminator), MinorReadStringNoTerminator, COMPLETED_NO);

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
        if (!OB_Extras.COMPAT_WIDE_MARSHAL)
            return _OB_read_wstring_new();
        return _OB_read_wstring_old();
    }

    public void read_boolean_array(boolean[] value, int offset, int length) {
        if (length > 0) {
            checkChunk();

            if (buf_.pos_ + length < buf_.pos_ || buf_.pos_ + length > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadBooleanArrayOverflow), MinorReadBooleanArrayOverflow, COMPLETED_NO);

            for (int i = offset; i < offset + length; i++)
                value[i] = buf_.readByte() != (byte) 0;
        }
    }

    public void read_char_array(char[] value, int offset, int length) {
        if (length > 0) {
            checkChunk();

            if (buf_.pos_ + length < buf_.pos_ || buf_.pos_ + length > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadCharArrayOverflow), MinorReadCharArrayOverflow, COMPLETED_NO);

            if (!(charReaderRequired_ || charConversionRequired_)) {
                for (int i = offset; i < offset + length; i++) {
                    //
                    // Note: byte must be masked with 0xff to correct negative
                    // values
                    //
                    value[i] = (char) (buf_.readByte() & 0xff);
                }
            } else {
                final CodeConverterBase converter = codeConverters_.inputCharConverter;

                //
                // Intermediate variable used for efficiency
                //
                boolean bothRequired = charReaderRequired_ && charConversionRequired_;

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
                        final char c = (char) (buf_.readByte() & 0xff);
                        value[i] = converter.convert(c);
                    }
                }
            }
        }
    }

    public void read_wchar_array(char[] value, int offset, int length) {
        if (length > 0) {
            if (buf_.pos_ + length < buf_.pos_ || buf_.pos_ + length > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadCharArrayOverflow), MinorReadCharArrayOverflow, COMPLETED_NO);

            for (int i = offset; i < offset + length; i++)
                value[i] = read_wchar(false);
        }
    }

    public void read_octet_array(byte[] value, int offset, int length) {
        if (length > 0) {
            checkChunk();

            int newPos = buf_.pos_ + length;
            if (newPos < buf_.pos_ || newPos > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadOctetArrayOverflow), MinorReadOctetArrayOverflow, COMPLETED_NO);

            System.arraycopy(buf_.data_, buf_.pos_, value, offset, length);

            buf_.pos_ = newPos;
        }
    }

    public void read_short_array(short[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk();
        buf_.align2();

        int newPos = buf_.pos_ + length * 2;
        if (newPos < buf_.pos_ || newPos > buf_.length())
            throw new MARSHAL(describeMarshal(MinorReadShortArrayOverflow), MinorReadShortArrayOverflow, COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = (short) (((short) buf_.readByte() & 0xff) | ((short) buf_.readByte() << 8));
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = (short) (((short) buf_.readByte() << 8) | ((short) buf_.readByte() & 0xff));
    }

    public void read_ushort_array(short[] value, int offset, int length) {
        read_short_array(value, offset, length);
    }

    public void read_long_array(int[] value, int offset, int length) {
        if (length <= 0)
            return;
        checkChunk();
        buf_.align4();

        int newPos = buf_.pos_ + length * 4;
        if (newPos < buf_.pos_ || newPos > buf_.length())
            throw new MARSHAL(describeMarshal(MinorReadLongArrayOverflow), MinorReadLongArrayOverflow, COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = ((int) buf_.readByte() & 0xff)
                        | (((int) buf_.readByte() << 8) & 0xff00)
                        | (((int) buf_.readByte() << 16) & 0xff0000)
                        | ((int) buf_.readByte() << 24);
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = ((int) buf_.readByte() << 24)
                        | (((int) buf_.readByte() << 16) & 0xff0000)
                        | (((int) buf_.readByte() << 8) & 0xff00)
                        | ((int) buf_.readByte() & 0xff);
    }

    public void read_ulong_array(int[] value, int offset, int length) {
        read_long_array(value, offset, length);
    }

    public void read_longlong_array(long[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk();

        buf_.align8();

        int newPos = buf_.pos_ + length * 8;
        if (newPos < buf_.pos_ || newPos > buf_.length())
            throw new MARSHAL(describeMarshal(MinorReadLongLongArrayOverflow), MinorReadLongLongArrayOverflow, COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++)
                value[i] = ((long) buf_.readByte() & 0xffL)
                        | (((long) buf_.readByte() << 8) & 0xff00L)
                        | (((long) buf_.readByte() << 16) & 0xff0000L)
                        | (((long) buf_.readByte() << 24) & 0xff000000L)
                        | (((long) buf_.readByte() << 32) & 0xff00000000L)
                        | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                        | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                        | ((long) buf_.readByte() << 56);
        else
            for (int i = offset; i < offset + length; i++)
                value[i] = ((long) buf_.readByte() << 56)
                        | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                        | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                        | (((long) buf_.readByte() << 32) & 0xff00000000L)
                        | (((long) buf_.readByte() << 24) & 0xff000000L)
                        | (((long) buf_.readByte() << 16) & 0xff0000L)
                        | (((long) buf_.readByte() << 8) & 0xff00L)
                        | ((long) buf_.readByte() & 0xffL);
    }

    public void read_ulonglong_array(long[] value, int offset, int length) {
        read_longlong_array(value, offset, length);
    }

    public void read_float_array(float[] value, int offset, int length) {
        if (length > 0) {
            checkChunk();

            buf_.align4();

            int newPos = buf_.pos_ + length * 4;
            if (newPos < buf_.pos_ || newPos > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadFloatArrayOverflow), MinorReadFloatArrayOverflow, COMPLETED_NO);

            if (swap_)
                for (int i = offset; i < offset + length; i++) {
                    int v = (buf_.readByte() & 0xff)
                            | ((buf_.readByte() << 8) & 0xff00)
                            | ((buf_.readByte() << 16) & 0xff0000)
                            | (buf_.readByte() << 24);

                    value[i] = Float.intBitsToFloat(v);
                }
            else
                for (int i = offset; i < offset + length; i++) {
                    int v = (buf_.readByte() << 24)
                            | ((buf_.readByte() << 16) & 0xff0000)
                            | ((buf_.readByte() << 8) & 0xff00)
                            | (buf_.readByte() & 0xff);

                    value[i] = Float.intBitsToFloat(v);
                }
        }
    }

    public void read_double_array(double[] value, int offset, int length) {
        if (length <= 0)
            return;

        checkChunk();
        buf_.align8();

        int newPos = buf_.pos_ + length * 8;
        if (newPos < buf_.pos_ || newPos > buf_.length())
            throw new MARSHAL(describeMarshal(MinorReadDoubleArrayOverflow), MinorReadDoubleArrayOverflow, COMPLETED_NO);

        if (swap_)
            for (int i = offset; i < offset + length; i++) {
                long v = ((long) buf_.readByte() & 0xffL)
                        | (((long) buf_.readByte() << 8) & 0xff00L)
                        | (((long) buf_.readByte() << 16) & 0xff0000L)
                        | (((long) buf_.readByte() << 24) & 0xff000000L)
                        | (((long) buf_.readByte() << 32) & 0xff00000000L)
                        | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                        | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                        | ((long) buf_.readByte() << 56);

                value[i] = Double.longBitsToDouble(v);
            }
        else
            for (int i = offset; i < offset + length; i++) {
                long v = ((long) buf_.readByte() << 56)
                        | (((long) buf_.readByte() << 48) & 0xff000000000000L)
                        | (((long) buf_.readByte() << 40) & 0xff0000000000L)
                        | (((long) buf_.readByte() << 32) & 0xff00000000L)
                        | (((long) buf_.readByte() << 24) & 0xff000000L)
                        | (((long) buf_.readByte() << 16) & 0xff0000L)
                        | (((long) buf_.readByte() << 8) & 0xff00L)
                        | ((long) buf_.readByte() & 0xffL);

                value[i] = Double.longBitsToDouble(v);
            }
    }

    public org.omg.CORBA.Object read_Object() {
        checkChunk();

        IOR ior = IORHelper.read(this);

        if ((ior.type_id.length() == 0) && (ior.profiles.length == 0))
            return null;

        if (orbInstance_ == null)
            throw new INITIALIZE("InputStream must be created " + "by a full ORB");

        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
        return objectFactory.createObject(ior);
    }

    public org.omg.CORBA.Object read_Object(Class expectedType) {
        org.omg.CORBA.Object obj = read_Object();

        if (obj == null) return null;

        // OK, we have two possibilities here.  The usual possibility is we're asked to load 
        // an object using a specified Stub class.  We just create an instance of the stub class, 
        // attach the object as a delegate, and we're done. 
        // 
        // The second possibility is a request for an instance of an interface.  This will require
        // us to locate a stub class using the defined Stub search orders.  After that, the process 
        // is largely the same. 
        org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl) obj;

        if (org.omg.CORBA.portable.ObjectImpl.class.isAssignableFrom(expectedType)) {
            return createStub(expectedType, impl._get_delegate());
        }

        final String codebase = ((org.omg.CORBA_2_3.portable.ObjectImpl) impl)._get_codebase();

        try {
            if (IDLEntity.class.isAssignableFrom(expectedType)) {
                final Class<?> helperClass = Util.loadClass(expectedType.getName() + "Helper", codebase, expectedType.getClassLoader());
                final Method helperNarrow = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
                    @Override
                    public Method run() throws NoSuchMethodException {
                        return helperClass.getMethod("narrow", org.omg.CORBA.Object.class);
                    }
                });
                return (org.omg.CORBA.Object) helperNarrow.invoke(null, impl);
            }

            return createStub(getRMIStubClass(codebase, expectedType), impl._get_delegate());
        } catch (IllegalAccessException | ClassNotFoundException | ClassCastException | PrivilegedActionException | InvocationTargetException ex) {
            logger.log(Level.FINE, "Exception creating object stub", ex);
            MARSHAL m = new MARSHAL("Unable to create stub for class " + expectedType.getName(), MinorLoadStub, COMPLETED_NO);
            m.initCause(ex);
            throw m;
        }
    }

    private org.omg.CORBA.Object createStub(Class<?> stubClass, org.omg.CORBA.portable.Delegate delegate) {
        _OB_assert(ObjectImpl.class.isAssignableFrom(stubClass), "stub class " + stubClass.getName() + " must extend ObjectImpl");
        @SuppressWarnings("unchecked")
        Class<? extends ObjectImpl> clz = (Class<? extends ObjectImpl>) stubClass;
        try {
            org.omg.CORBA.portable.ObjectImpl stub = clz.newInstance();
            stub._set_delegate(delegate);
            return stub;
        } catch (IllegalAccessException | InstantiationException ex) {
            logger.log(Level.FINE, "Exception creating object stub", ex);
            MARSHAL m = new MARSHAL("Unable to create stub for class " + clz.getName(), MinorLoadStub, COMPLETED_NO);
            m.initCause(ex);
            throw m;
        }
    }

    /**
     * Convert a class type into a stub class name using the RMI stub name rules.
     * @param c The class we need to stub.
     * @return The target stub class name.
     */
    private String getRMIStubClassName(Class<?> c) {
        final String cname = c.getName();
        int idx = cname.lastIndexOf('.');
        return cname.substring(0, idx + 1) + "_" + cname.substring(idx + 1) + "_Stub";
    }

    /**
     * Load a statically-created Stub class for a type, attempting both the old
     * and new stub class rules.
     * @param codebase The search codebase to use.
     * @param type The type we need a stub for.
     * @return A loaded stub class.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends org.omg.CORBA.portable.ObjectImpl> getRMIStubClass(String codebase, Class<?> type) throws ClassNotFoundException {
        String name = getRMIStubClassName(type);
        ClassLoader cl = type.getClassLoader();
        try {
            return Util.loadClass(name, codebase, cl);
        } catch (ClassNotFoundException e1) {
            try {
                return Util.loadClass("org.omg.stub." + name, codebase, cl);
            } catch (ClassNotFoundException e2) {
                e2.addSuppressed(e1);
                throw e2;
            }
        }
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
        Hashtable history = new Hashtable(11);
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

    @SuppressWarnings("deprecation")
    public Principal read_Principal() {
        // Deprecated by CORBA 2.2
        throw new NO_IMPLEMENT();
    }

    public BigDecimal read_fixed() {
        StringBuilder vBuffer = new StringBuilder("0");
        StringBuilder sBuffer = new StringBuilder();

        boolean first = true;
        while (true) {
            final byte b = read_octet();

            int hi = (b >>> 4) & 0x0f;
            if (hi > 9)
                throw new MARSHAL(describeMarshal(MinorReadFixedInvalid), MinorReadFixedInvalid, COMPLETED_NO);

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
                throw new MARSHAL(describeMarshal(MinorReadFixedInvalid), MinorReadFixedInvalid, COMPLETED_NO);

            first = false;
        }

        sBuffer.append(vBuffer);

        try {
            return new BigDecimal(sBuffer.toString());
        } catch (NumberFormatException ex) {
            throw new MARSHAL(describeMarshal(MinorReadFixedInvalid), MinorReadFixedInvalid, COMPLETED_NO);
        }
    }

    public Serializable read_value() {
        return valueReader().readValue();
    }

    public Serializable read_value(String id) {
        return valueReader().readValue(id);
    }

    public Serializable read_value(Class clz) {
        return valueReader().readValue(clz);
    }

    public Serializable read_value(BoxedValueHelper helper) {
        return valueReader().readValueBox(helper);
    }

    public Serializable read_value(Serializable value) {
        //
        // This version of read_value is intended for use by factories
        //

        valueReader().initializeValue(value);
        return value;
    }

    public Object read_abstract_interface() {
        return valueReader().readAbstractInterface();
    }

    public Object read_abstract_interface(Class clz) {
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

    private InputStream(Buffer buf, int offs, boolean swap, CodeConverters codeConverters, GiopVersion giopVersion) {
        buf_ = buf;
        buf_.pos(offs);
        swap_ = swap;
        origPos_ = offs;
        origSwap_ = swap;

        _OB_codeConverters(codeConverters, giopVersion);
    }

    public InputStream(Buffer buf, boolean swap, CodeConverters codeConverters, GiopVersion giopVersion) {
        buf_ = buf;
        buf_.rewindToStart();
        swap_ = swap;
        origPos_ = 0;
        origSwap_ = swap;

        _OB_codeConverters(codeConverters, giopVersion);
    }

    public InputStream(Buffer buf, int offs, boolean swap) {
        this(buf, offs, swap, null, null);
    }

    public InputStream(Buffer buf, boolean swap) {
        this(buf, swap, null, null);
    }

    public InputStream(Buffer buf) {
        this(buf, false, null, null);
    }

    public void _OB_codeConverters(CodeConverters converters, GiopVersion giopVersion) {
        if (giopVersion != null)
            giopVersion_ = giopVersion;

        charReaderRequired_ = false;
        charConversionRequired_ = false;
        wCharReaderRequired_ = false;
        wCharConversionRequired_ = false;

        codeConverters_ = CodeConverters.createCopy(converters);

        if (converters != null) {
            if (codeConverters_.inputCharConverter != null) {
                charReaderRequired_ = codeConverters_.inputCharConverter.readerRequired();
                charConversionRequired_ = codeConverters_.inputCharConverter.conversionRequired();
            }

            if (codeConverters_.inputWcharConverter != null) {
                wCharReaderRequired_ = codeConverters_.inputWcharConverter.readerRequired();
                wCharConversionRequired_ = codeConverters_.inputWcharConverter.conversionRequired();
            }
        }
    }

    public CodeConverters _OB_codeConverters() {
        return codeConverters_;
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

    public void _OB_swap(boolean swap) {
        swap_ = swap;
    }

    public InputStream _OB_clone() {
        InputStream result;

        byte[] data = new byte[buf_.length()];
        System.arraycopy(buf_.data_, 0, data, 0, buf_.length());
        Buffer buf = new Buffer(data);
        result = new InputStream(buf, origPos_, origSwap_, codeConverters_, giopVersion_);
        result.orbInstance_ = orbInstance_;

        return result;
    }

    public void _OB_reset() {
        swap_ = origSwap_;
        buf_.pos_ = origPos_;
    }

    public void _OB_skip(int n) {
        if (n < 0 || buf_.available() < n)
            throw new MARSHAL(describeMarshal(MinorReadOverflow), MinorReadOverflow, COMPLETED_NO);

        buf_.skip(n);
    }

    public void _OB_skipAlign(int n) {
        if (buf_.pos_ % n != 0) {
            int newPos = buf_.pos_ + n - buf_.pos_ % n;

            if (newPos < buf_.pos_ || newPos > buf_.length())
                throw new MARSHAL(describeMarshal(MinorReadOverflow), MinorReadOverflow, COMPLETED_NO);

            buf_.pos_ = newPos;
        }
    }

    public void _OB_readEndian() {
        swap_ = read_boolean(); // false means big endian
    }

    public void _OB_ORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;

        if (orbInstance_ != null && orbInstance_.useTypeCodeCache()) {
            //
            // Get the TypeCodeCache of this ORBInstance
            //
            cache_ = TypeCodeCache.instance();
        }
    }

    public ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    public int _OB_readLongUnchecked() {
        //
        // The chunking code needs to read a long value without entering
        // an infinite loop
        //

        buf_.align4();

        if (buf_.available() < 4) throw new MARSHAL(describeMarshal(MinorReadLongOverflow), MinorReadLongOverflow, COMPLETED_NO);

        if (swap_)
            return (buf_.readByte() & 0xff)
                    | ((buf_.readByte() << 8) & 0xff00)
                    | ((buf_.readByte() << 16) & 0xff0000)
                    | (buf_.readByte() << 24);
        else
            return (buf_.readByte() << 24)
                    | ((buf_.readByte() << 16) & 0xff0000)
                    | ((buf_.readByte() << 8) & 0xff00)
                    | (buf_.readByte() & 0xff);
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

    public void __setSendingContextRuntime(CodeBase runtime) {
        sendingContextRuntime_ = runtime;
    }

    public CodeBase __getSendingContextRuntime() {
        return sendingContextRuntime_;
    }

    public void __setCodeBase(String codebase) {
        this.codebase_ = codebase;
    }

    public String __getCodeBase() {
        return codebase_;
    }

    public String dumpRemainingData() {
        return buf_.dumpRemainingData();
    }

    public String dumpAllData() {
        return buf_.dumpAllData();
    }

    private void checkChunk() {
        if (valueReader_ != null) {
            valueReader_.checkChunk();
        }
    }

    @Override
    public void end_value() {
        valueReader().endValue();
    }

    @Override
    public void start_value() {
        valueReader().beginValue();
    }
}
