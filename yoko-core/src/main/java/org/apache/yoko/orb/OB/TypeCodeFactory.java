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

package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.Any;
import org.apache.yoko.orb.CORBA.TypeCode;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.VM_ABSTRACT;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA_2_4.TCKind;

public final class TypeCodeFactory {
    //
    // Cache the primitive TypeCodes
    //
    private static org.omg.CORBA.TypeCode[] primitives_;
    static {
        primitives_ = new org.omg.CORBA.TypeCode[34];
    }

    private TypeCodeFactory() {
    }

    // ----------------------------------------------------------------------
    // TypeCodeFactory private and protected member implementation
    // ----------------------------------------------------------------------

    private static boolean checkId(String id) {
        //
        // Check for a valid repository ID (<format>:<string>)
        //
        if (id.length() > 0) {
            int colon = id.indexOf(':');
            if (colon == -1 || colon == 0 || colon == id.length() - 1)
                return false;
        }

        return true;
    }

    private static final boolean CHECK_IDL_NAMES = false;

    private static boolean checkName(String name) {
        if (!CHECK_IDL_NAMES) return true;
        //
        // Check for a valid IDL name
        //
        if (name.length() > 0) {
            if (!Character.isLetter(name.charAt(0)))
                return false;
            for (int i = 1; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (!Character.isLetterOrDigit(ch) && ch != '_')
                    return false;
            }
        }

        return true;
    }

    private static boolean checkType(org.omg.CORBA.TypeCode type) {
        //
        // Check for an illegal content or member type
        //
        try {
            org.omg.CORBA.TypeCode origType = TypeCode._OB_getOrigType(type);
            org.omg.CORBA.TCKind kind = origType.kind();
            if (kind == org.omg.CORBA.TCKind.tk_null
                    || kind == org.omg.CORBA.TCKind.tk_void
                    || kind == org.omg.CORBA.TCKind.tk_except)
                return false;
        } catch (BAD_TYPECODE ex) {
            // TypeCode may be recursive
        }

        return true;
    }

    private static boolean compareLabels(org.omg.CORBA.TCKind kind,
            org.omg.CORBA.Any a1, org.omg.CORBA.Any a2) {
        switch (kind.value()) {
        case org.omg.CORBA.TCKind._tk_short:
            return a1.extract_short() == a2.extract_short();

        case org.omg.CORBA.TCKind._tk_ushort:
            return a1.extract_ushort() == a2.extract_ushort();

        case org.omg.CORBA.TCKind._tk_long:
            return a1.extract_long() == a2.extract_long();

        case org.omg.CORBA.TCKind._tk_ulong:
            return a1.extract_ulong() == a2.extract_ulong();

        case org.omg.CORBA.TCKind._tk_longlong:
            return a1.extract_longlong() == a2.extract_longlong();

        case org.omg.CORBA.TCKind._tk_ulonglong:
            return a1.extract_ulonglong() == a2.extract_ulonglong();

        case org.omg.CORBA.TCKind._tk_char:
            return a1.extract_char() == a2.extract_char();

        case org.omg.CORBA.TCKind._tk_boolean:
            return a1.extract_boolean() == a2.extract_boolean();

        case org.omg.CORBA.TCKind._tk_wchar:
            return a1.extract_wchar() == a2.extract_wchar();

        case org.omg.CORBA.TCKind._tk_enum:
            return a1.create_input_stream().read_ulong() == a2
                    .create_input_stream().read_ulong();

        default:
            throw Assert.fail("Unsupported typecode for compare");
        }
    }

    // ----------------------------------------------------------------------
    // TypeCodeFactory public member implementation
    // ----------------------------------------------------------------------

    public static org.omg.CORBA.TypeCode createPrimitiveTC(
            org.omg.CORBA.TCKind kind) {
        Assert.ensure(kind.value() < primitives_.length);

        if (primitives_[kind.value()] != null)
            return primitives_[kind.value()];

        org.omg.CORBA.TypeCode tc = null;

        switch (kind.value()) {
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
        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_longdouble:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_wstring:
        case org.omg.CORBA.TCKind._tk_fixed: {
            TypeCode p = new TypeCode();
            p.kind_ = kind;
            p.length_ = 0; // For strings
            tc = p;
            break;
        }

        case org.omg.CORBA.TCKind._tk_objref:
            tc = createInterfaceTC("IDL:omg.org/CORBA/Object:1.0", "Object");
            break;

        case org.omg.CORBA.TCKind._tk_value:
            tc = createValueTC("IDL:omg.org/CORBA/ValueBase:1.0", "ValueBase", VM_ABSTRACT.value, null, new ValueMember[0]);
            break;

        default:
            throw Assert.fail();
        }

        primitives_[kind.value()] = tc;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createStructTC(String id, String name,
            StructMember[] members) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        for (int i = 0; i < members.length; i++) {
            if (!checkName(members[i].name))
                throw new BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidMemberName)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberName,
                        CompletionStatus.COMPLETED_NO);
            if (!checkType(members[i].type))
                throw new BAD_TYPECODE(MinorCodes
                        .describeBadTypecode(MinorCodes.MinorInvalidMemberType)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberType,
                        CompletionStatus.COMPLETED_NO);
            for (int j = i + 1; j < members.length; j++)
                if (members[i].name.length() > 0
                        && members[i].name.equalsIgnoreCase(members[j].name)) {
                    throw new BAD_PARAM(
                            MinorCodes
                                    .describeBadParam(MinorCodes.MinorInvalidMemberName)
                                    + ": " + members[i].name,
                            MinorCodes.MinorInvalidMemberName,
                            CompletionStatus.COMPLETED_NO);
                }
        }

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_struct;
        tc.id_ = id;
        tc.name_ = name;

        tc.memberNames_ = new String[members.length];
        tc.memberTypes_ = new TypeCode[members.length];

        for (int i = 0; i < members.length; i++) {
            tc.memberNames_[i] = members[i].name;
            try {
                tc.memberTypes_[i] = (TypeCode) members[i].type;
            } catch (ClassCastException ex) {
                tc.memberTypes_[i] = TypeCode
                        ._OB_convertForeignTypeCode(members[i].type);
            }
        }

        TypeCode._OB_embedRecTC(tc);
        return tc;
    }

    public static org.omg.CORBA.TypeCode createUnionTC(String id, String name,
            org.omg.CORBA.TypeCode discriminator_type,
            UnionMember[] members) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        org.omg.CORBA.TypeCode origDisc = TypeCode
                ._OB_getOrigType(discriminator_type);
        switch (origDisc.kind().value()) {
        case org.omg.CORBA.TCKind._tk_short:
        case org.omg.CORBA.TCKind._tk_ushort:
        case org.omg.CORBA.TCKind._tk_long:
        case org.omg.CORBA.TCKind._tk_ulong:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_char:
        case org.omg.CORBA.TCKind._tk_boolean:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_enum:
            break;

        default:
            throw new BAD_PARAM(
                    MinorCodes
                            .describeBadParam(MinorCodes.MinorInvalidDiscriminatorType),
                    MinorCodes.MinorInvalidDiscriminatorType,
                    CompletionStatus.COMPLETED_NO);
        }

        for (int i = 0; i < members.length; i++) {
            if (!checkName(members[i].name))
                throw new BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidMemberName)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberName,
                        CompletionStatus.COMPLETED_NO);
            if (!checkType(members[i].type))
                throw new BAD_TYPECODE(MinorCodes
                        .describeBadTypecode(MinorCodes.MinorInvalidMemberType)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberType,
                        CompletionStatus.COMPLETED_NO);
            org.omg.CORBA.TypeCode labelType = members[i].label.type();
            org.omg.CORBA.TypeCode origLabelType = TypeCode
                    ._OB_getOrigType(labelType);
            org.omg.CORBA.TCKind kind = origLabelType.kind();
            if (kind != org.omg.CORBA.TCKind.tk_octet
                    && !origLabelType.equivalent(discriminator_type)) {
                throw new BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorIncompatibleLabelType)
                                + ": " + members[i].name,
                        MinorCodes.MinorIncompatibleLabelType,
                        CompletionStatus.COMPLETED_NO);
            }
            for (int j = i + 1; j < members.length; j++) {
                if (kind != org.omg.CORBA.TCKind.tk_octet) {
                    org.omg.CORBA.TypeCode otherLabelType = members[j].label
                            .type();
                    if (origLabelType.equivalent(otherLabelType)
                            && compareLabels(kind, members[i].label,
                                    members[j].label)) {
                        throw new BAD_PARAM(
                                MinorCodes
                                        .describeBadParam(MinorCodes.MinorDuplicateLabel)
                                        + ": " + members[i].name,
                                MinorCodes.MinorDuplicateLabel,
                                CompletionStatus.COMPLETED_NO);
                    }
                }
            }
        }

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_union;
        tc.id_ = id;
        tc.name_ = name;
        try {
            tc.discriminatorType_ = (TypeCode) discriminator_type;
        } catch (ClassCastException ex) {
            tc.discriminatorType_ = TypeCode
                    ._OB_convertForeignTypeCode(discriminator_type);
        }

        tc.labels_ = new Any[members.length];
        tc.memberNames_ = new String[members.length];
        tc.memberTypes_ = new TypeCode[members.length];

        for (int i = 0; i < members.length; i++) {
            try {
                tc.labels_[i] = (Any) members[i].label;
            } catch (ClassCastException ex) {
                tc.labels_[i] = new Any(
                        members[i].label);
            }

            tc.memberNames_[i] = members[i].name;

            try {
                tc.memberTypes_[i] = (TypeCode) members[i].type;
            } catch (ClassCastException ex) {
                tc.memberTypes_[i] = TypeCode
                        ._OB_convertForeignTypeCode(members[i].type);
            }
        }

        TypeCode._OB_embedRecTC(tc);
        return tc;
    }

    public static org.omg.CORBA.TypeCode createEnumTC(String id, String name,
            String[] members) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        for (int i = 0; i < members.length; i++) {
            if (!checkName(members[i]))
                throw new BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidMemberName)
                        + ": " + members[i], MinorCodes.MinorInvalidMemberName,
                        CompletionStatus.COMPLETED_NO);
            for (int j = i + 1; j < members.length; j++)
                if (members[i].length() > 0
                        && members[i].equalsIgnoreCase(members[j])) {
                    throw new BAD_PARAM(
                            MinorCodes
                                    .describeBadParam(MinorCodes.MinorInvalidMemberName)
                                    + ": " + members[i],
                            MinorCodes.MinorInvalidMemberName,
                            CompletionStatus.COMPLETED_NO);
                }
        }

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_enum;
        tc.id_ = id;
        tc.name_ = name;

        tc.memberNames_ = new String[members.length];

        System.arraycopy(members, 0, tc.memberNames_, 0, members.length);

        return tc;
    }

    public static org.omg.CORBA.TypeCode createAliasTC(String id, String name,
            org.omg.CORBA.TypeCode original_type) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);
        if (!checkType(original_type))
            throw new BAD_TYPECODE(MinorCodes
                    .describeBadTypecode(MinorCodes.MinorInvalidMemberType),
                    MinorCodes.MinorInvalidMemberType,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_alias;
        tc.id_ = id;
        tc.name_ = name;

        try {
            tc.contentType_ = (TypeCode) original_type;
        } catch (ClassCastException ex) {
            tc.contentType_ = TypeCode
                    ._OB_convertForeignTypeCode(original_type);
        }

        return tc;
    }

    public static org.omg.CORBA.TypeCode createExceptionTC(String id,
            String name, StructMember[] members) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        for (int i = 0; i < members.length; i++) {
            if (!checkName(members[i].name))
                throw new BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidMemberName)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberName,
                        CompletionStatus.COMPLETED_NO);
            if (!checkType(members[i].type))
                throw new BAD_TYPECODE(MinorCodes
                        .describeBadTypecode(MinorCodes.MinorInvalidMemberType)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberType,
                        CompletionStatus.COMPLETED_NO);
            for (int j = i + 1; j < members.length; j++)
                if (members[i].name.length() > 0
                        && members[i].name.equalsIgnoreCase(members[j].name)) {
                    throw new BAD_PARAM(
                            MinorCodes
                                    .describeBadParam(MinorCodes.MinorInvalidMemberName)
                                    + ": " + members[i].name,
                            MinorCodes.MinorInvalidMemberName,
                            CompletionStatus.COMPLETED_NO);
                }
        }

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_except;
        tc.id_ = id;
        tc.name_ = name;

        tc.memberNames_ = new String[members.length];
        tc.memberTypes_ = new TypeCode[members.length];

        for (int i = 0; i < members.length; i++) {
            tc.memberNames_[i] = members[i].name;
            try {
                tc.memberTypes_[i] = (TypeCode) members[i].type;
            } catch (ClassCastException ex) {
                tc.memberTypes_[i] = TypeCode
                        ._OB_convertForeignTypeCode(members[i].type);
            }
        }

        TypeCode._OB_embedRecTC(tc);
        return tc;
    }

    public static org.omg.CORBA.TypeCode createInterfaceTC(String id,
            String name) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_objref;
        tc.id_ = id;
        tc.name_ = name;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createStringTC(int bound) {
        Assert.ensure(bound >= 0);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_string;
        tc.length_ = bound;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createWStringTC(int bound) {
        Assert.ensure(bound >= 0);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_wstring;
        tc.length_ = bound;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createFixedTC(short digits, short scale) {
        Assert.ensure(digits >= 0);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_fixed;
        tc.fixedDigits_ = digits;
        tc.fixedScale_ = scale;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createSequenceTC(int bound,
            org.omg.CORBA.TypeCode element_type) {
        Assert.ensure(bound >= 0);

        if (!checkType(element_type))
            throw new BAD_TYPECODE(MinorCodes
                    .describeBadTypecode(MinorCodes.MinorInvalidMemberType),
                    MinorCodes.MinorInvalidMemberType,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_sequence;
        tc.length_ = bound;
        try {
            tc.contentType_ = (TypeCode) element_type;
        } catch (ClassCastException ex) {
            tc.contentType_ = TypeCode._OB_convertForeignTypeCode(element_type);
        }

        return tc;
    }

    public static org.omg.CORBA.TypeCode createRecursiveSequenceTC(int bound,
            int offset) {
        throw new NO_IMPLEMENT();
    }

    public static org.omg.CORBA.TypeCode createArrayTC(int length,
            org.omg.CORBA.TypeCode element_type) {
        Assert.ensure(length > 0);

        if (!checkType(element_type))
            throw new BAD_TYPECODE(MinorCodes
                    .describeBadTypecode(MinorCodes.MinorInvalidMemberType),
                    MinorCodes.MinorInvalidMemberType,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_array;
        tc.length_ = length;
        try {
            tc.contentType_ = (TypeCode) element_type;
        } catch (ClassCastException ex) {
            tc.contentType_ = TypeCode._OB_convertForeignTypeCode(element_type);
        }

        return tc;
    }

    public static org.omg.CORBA.TypeCode createValueTC(String id, String name,
            short type_modifier, org.omg.CORBA.TypeCode concrete_base,
            ValueMember[] members) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);
        if (concrete_base != null) {
            try {
                org.omg.CORBA.TypeCode origBaseType = TypeCode
                        ._OB_getOrigType(concrete_base);
                if (origBaseType.kind() != org.omg.CORBA.TCKind.tk_value)
                    throw new BAD_TYPECODE();
                // TODO: No standard minor code
            } catch (BAD_TYPECODE ex) {
                // TypeCode may be recursive
            }
        }

        for (int i = 0; i < members.length; i++) {
            if (!checkName(members[i].name))
                throw new BAD_PARAM(MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidMemberName)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberName,
                        CompletionStatus.COMPLETED_NO);
            if (!checkType(members[i].type))
                throw new BAD_TYPECODE(MinorCodes
                        .describeBadTypecode(MinorCodes.MinorInvalidMemberType)
                        + ": " + members[i].name,
                        MinorCodes.MinorInvalidMemberType,
                        CompletionStatus.COMPLETED_NO);
            if (!CHECK_IDL_NAMES) continue;
            for (int j = i + 1; j < members.length; j++)
                if (members[i].name.length() > 0
                        && members[i].name.equalsIgnoreCase(members[j].name)) {
                    throw new BAD_PARAM(
                            MinorCodes
                                    .describeBadParam(MinorCodes.MinorInvalidMemberName)
                                    + ": " + members[i].name,
                            MinorCodes.MinorInvalidMemberName,
                            CompletionStatus.COMPLETED_NO);
                }
        }

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_value;
        tc.id_ = id;
        tc.name_ = name;
        tc.typeModifier_ = type_modifier;
        try {
            tc.concreteBaseType_ = (TypeCode) concrete_base;
        } catch (ClassCastException ex) {
            tc.concreteBaseType_ = TypeCode
                    ._OB_convertForeignTypeCode(concrete_base);
        }

        tc.memberNames_ = new String[members.length];
        tc.memberTypes_ = new TypeCode[members.length];
        tc.memberVisibility_ = new short[members.length];

        for (int i = 0; i < members.length; i++) {
            tc.memberNames_[i] = members[i].name;

            try {
                tc.memberTypes_[i] = (TypeCode) members[i].type;
            } catch (ClassCastException ex) {
                tc.memberTypes_[i] = TypeCode
                        ._OB_convertForeignTypeCode(members[i].type);
            }

            tc.memberVisibility_[i] = members[i].access;
        }

        TypeCode._OB_embedRecTC(tc);
        return tc;
    }

    public static org.omg.CORBA.TypeCode createValueBoxTC(String id,
            String name, org.omg.CORBA.TypeCode boxed_type) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);
        if (!checkType(boxed_type))
            throw new BAD_TYPECODE(MinorCodes
                    .describeBadTypecode(MinorCodes.MinorInvalidMemberType),
                    MinorCodes.MinorInvalidMemberType,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_value_box;
        tc.id_ = id;
        tc.name_ = name;
        try {
            tc.contentType_ = (TypeCode) boxed_type;
        } catch (ClassCastException ex) {
            tc.contentType_ = TypeCode._OB_convertForeignTypeCode(boxed_type);
        }

        return tc;
    }

    public static org.omg.CORBA.TypeCode createNativeTC(String id, String name) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_native;
        tc.id_ = id;
        tc.name_ = name;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createRecursiveTC(String id) {
        Assert.ensure(id != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.recId_ = id;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createAbstractInterfaceTC(String id,
            String name) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = org.omg.CORBA.TCKind.tk_abstract_interface;
        tc.id_ = id;
        tc.name_ = name;

        return tc;
    }

    public static org.omg.CORBA.TypeCode createLocalInterfaceTC(String id,
            String name) {
        Assert.ensure(id != null && name != null);

        if (!checkId(id))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidId)
                    + ": " + id, MinorCodes.MinorInvalidId,
                    CompletionStatus.COMPLETED_NO);
        if (!checkName(name))
            throw new BAD_PARAM(MinorCodes
                    .describeBadParam(MinorCodes.MinorInvalidName)
                    + ": " + name, MinorCodes.MinorInvalidName,
                    CompletionStatus.COMPLETED_NO);

        TypeCode tc = new TypeCode();

        tc.kind_ = TCKind.tk_local_interface;
        tc.id_ = id;
        tc.name_ = name;

        return tc;
    }
}
