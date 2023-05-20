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

import org.apache.yoko.orb.OB.Util;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import static java.lang.String.format;
import static org.apache.yoko.util.Assert.ensure;
import static org.apache.yoko.util.MinorCodes.MinorIncompleteTypeCode;
import static org.apache.yoko.util.MinorCodes.describeBadTypecode;
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
import static org.omg.CORBA.TCKind.tk_abstract_interface;
import static org.omg.CORBA.TCKind.tk_alias;
import static org.omg.CORBA.TCKind.tk_array;
import static org.omg.CORBA.TCKind.tk_enum;
import static org.omg.CORBA.TCKind.tk_except;
import static org.omg.CORBA.TCKind.tk_fixed;
import static org.omg.CORBA.TCKind.tk_native;
import static org.omg.CORBA.TCKind.tk_objref;
import static org.omg.CORBA.TCKind.tk_sequence;
import static org.omg.CORBA.TCKind.tk_string;
import static org.omg.CORBA.TCKind.tk_struct;
import static org.omg.CORBA.TCKind.tk_union;
import static org.omg.CORBA.TCKind.tk_value;
import static org.omg.CORBA.TCKind.tk_value_box;
import static org.omg.CORBA.TCKind.tk_wstring;
import static org.omg.CORBA_2_4.TCKind._tk_local_interface;
import static org.omg.CORBA_2_4.TCKind.tk_local_interface;

// Note: TypeCodes are (supposed to be) immutable, so I don't need thread synchronization
final public class TypeCode extends org.omg.CORBA.TypeCode {
    public TCKind kind_;

    // tk_objref, tk_struct, tk_union, tk_enum, tk_alias, tk_value, tk_value_box, tk_native, tk_abstract_interface, tk_except, tk_local_interface
    public String id_;
    public String name_;
    // tk_struct, tk_union, tk_enum, tk_value, tk_except
    public String[] memberNames_;
    // tk_struct, tk_union, tk_value, tk_except
    public TypeCode[] memberTypes_;
    // tk_union
    public Any[] labels_;
    // tk_union
    public TypeCode discriminatorType_;

    // tk_string, tk_wstring, tk_sequence, tk_array
    public int length_;

    // tk_sequence, tk_array, tk_value_box, tk_alias
    public TypeCode contentType_;

    // tk_fixed
    public short fixedDigits_;
    public short fixedScale_;

    // tk_value
    public short[] memberVisibility_;
    public short typeModifier_;

    public TypeCode concreteBaseType_;

    // If recId_ is set, this is a placeholder recursive TypeCode that
    // was generated with create_recursive_tc(). If the placeholder
    // recursive TypeCode is already embedded, recType_ points to the
    // recursive TypeCode this placeholder delegates to.
    public String recId_;

    TypeCode recType_;

    @Override
    public String toString() {
        return describe(new StringBuilder(), "", new HashSet<>()).toString();
    }

    private static final String[] tcKindDesc = {
            "tk_null", "tk_void", "tk_short", "tk_long", "tk_ushort", "tk_ulong", "tk_float", "tk_double",
            "tk_boolean", "tk_char", "tk_octet", "tk_any", "tk_TypeCode", "tk_Principal", "tk_objref",
            "tk_struct", "tk_union", "tk_enum", "tk_string", "tk_sequence", "tk_array", "tk_alias",
            "tk_except", "tk_longlong", "tk_ulonglong", "tk_longdouble", "tk_wchar", "tk_wstring",
            "tk_fixed", "tk_value", "tk_value_box", "tk_native", "tk_abstract_interface", "tk_local_interface"
    };

    private static final String NL = System.lineSeparator();

    private StringBuilder describe(StringBuilder sb, String prefix, Set<String> describedIds) {
        final String indent = prefix + "\t";
        sb.append("TypeCode {").append(NL);
        if (null != kind_) sb.append(indent).append("kind: ").append(tcKindDesc[kind_.value()]).append(NL);
        if (null != id_) sb.append(indent).append("id: ").append(id_).append(NL);
        if (null != name_) sb.append(indent).append("name: ").append(name_).append(NL);
        if (null != recId_) sb.append(indent).append("recursive id: ").append(recId_).append(NL);
        if (0 != typeModifier_) sb.append(indent).append("type modifier: ").append(typeModifier_).append(NL);
        if (0 != length_) sb.append(indent).append("length: ").append(length_).append(NL);
        if (0 != fixedDigits_) sb.append(indent).append("fixed digits: ").append(fixedDigits_).append(NL);
        if (0 != fixedScale_) sb.append(indent).append("fixed scale: ").append(fixedScale_).append(NL);
        describe2(sb, indent, describedIds);
        return sb.append(prefix).append("}");
    }

    private void describe2(StringBuilder sb, String indent, Set<String> describedIds) {
        if (null != id_) {
            if (describedIds.contains(id_)) {
                sb.append(indent).append("[already described]").append(NL);
                return;
            }
            describedIds.add(id_);
        }
        if (null != memberNames_) {
            int visCount = null == memberVisibility_ ? 0 : memberVisibility_.length;
            if (null == memberTypes_) {
                sb.append(indent).append("members: ").append(Arrays.toString(memberNames_)).append(NL);
            } else for (int i = 0; i < memberNames_.length; i++) {
                TypeCode tc = i < memberTypes_.length ? memberTypes_[i] : null;
                String prefix = format("%s%s: ", memberNames_[i],
                        (i < visCount) ? ((PRIVATE_MEMBER.value == memberVisibility_[i]) ? "[private]" : "[public]"): "");
                appendTC(sb, prefix, tc, indent, describedIds).append(NL);
            }
        }
//        if (labels_ != null) sb.append(indent).append("labels: ").append(Arrays.toString(labels_)).append(NL);
        if (null != recType_) appendTC(sb, "recursive typecode: ", recType_, indent, describedIds).append(NL);
        if (null != discriminatorType_) appendTC(sb, "discriminator type: ", discriminatorType_, indent, describedIds).append(NL);
        if (null != contentType_) appendTC(sb, "content type: ", contentType_, indent, describedIds).append(NL);
        if (null != concreteBaseType_) appendTC(sb, "concrete base type: ", concreteBaseType_, indent, describedIds).append(NL);
    }

    private static StringBuilder appendTC(StringBuilder sb, String prefix, TypeCode tc, String indent, Set<String> describedIds) {
        sb.append(indent).append(prefix);
        if (tc == null) sb.append("typecode was null");
        else tc.describe(sb, indent, describedIds);
        return sb;
    }

    private boolean equivalentRecHelper(org.omg.CORBA.TypeCode t,
                                        Vector<org.omg.CORBA.TypeCode> history, Vector<org.omg.CORBA.TypeCode> otherHistory) {
        if (t == null)
            return false;

        if (t == this)
            return true;

        //
        // Avoid infinite loops
        //
        boolean foundLoop = false, foundOtherLoop = false;
        for (int i = 0; i < history.size() && !foundLoop; i++)
            if (this == history.elementAt(i))
                foundLoop = true;
        for (int i = 0; i < otherHistory.size() && !foundOtherLoop; i++)
            if (t == otherHistory.elementAt(i))
                foundOtherLoop = true;
        if (foundLoop && foundOtherLoop)
            return true;

        history.addElement(this);
        otherHistory.addElement(t);

        boolean result = equivalentRec(t, history, otherHistory);

        history.setSize(history.size() - 1);
        otherHistory.setSize(otherHistory.size() - 1);

        return result;
    }

    private boolean equivalentRec(org.omg.CORBA.TypeCode t,
            Vector<org.omg.CORBA.TypeCode> history, Vector<org.omg.CORBA.TypeCode> otherHistory) {
        TypeCode tc;
        try {
            tc = (TypeCode) t;
        } catch (ClassCastException ex) {
            tc = _OB_convertForeignTypeCode(t);
        }

        if (null != recId_) {
            if (null == recType_)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.equivalentRecHelper(t, history, otherHistory);
        }

        if (null != tc.recId_) {
            if (null == tc.recType_)
                throw new BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorIncompleteTypeCodeParameter),
                        MinorCodes.MinorIncompleteTypeCodeParameter,
                        COMPLETED_NO);
            return equivalentRecHelper(tc.recType_, history, otherHistory);
        }

        TypeCode tc1 = _OB_getOrigType();
        TypeCode tc2 = tc._OB_getOrigType();

        if (tc1.kind_ != tc2.kind_)
            return false;

        if (tk_objref == tc1.kind_
                || tk_struct == tc1.kind_
                || tk_union == tc1.kind_
                || tk_enum == tc1.kind_
                || tk_alias == tc1.kind_
                || tk_value == tc1.kind_
                || tk_value_box == tc1.kind_
                || tk_native == tc1.kind_
                || tk_abstract_interface == tc1.kind_
                || tk_except == tc1.kind_
                || tk_local_interface == tc1.kind_) {
            if (!tc1.id_.equals("") && !tc2.id_.equals("")) {
                return tc1.id_.equals(tc2.id_);
            }
        }

        // names_ and memberNames_ must be ignored

        if (tk_struct == tc1.kind_
                || tk_union == tc1.kind_
                || tk_value == tc1.kind_
                || tk_except == tc1.kind_) {
            if (tc1.memberTypes_.length != tc2.memberTypes_.length)
                return false;

            for (int i = 0; i < tc1.memberTypes_.length; i++) {
                if (!(tc1.memberTypes_[i].equivalentRecHelper(
                        tc2.memberTypes_[i], history, otherHistory)))
                    return false;
            }
        }

        if (tk_union == tc1.kind_) {
            if (tc1.labels_.length != tc2.labels_.length)
                return false;

            for (int i = 0; i < tc1.labels_.length; i++) {
                org.omg.CORBA.TypeCode ltc1 = tc1.labels_[i]._OB_type();
                org.omg.CORBA.TypeCode ltc2 = tc2.labels_[i]._OB_type();

                //
                // Don't use equivalentRecHelper here
                //
                if (!ltc1.equivalent(ltc2))
                    return false;

                Object v1 = tc1.labels_[i].value();
                Object v2 = tc2.labels_[i].value();

                ltc1 = _OB_getOrigType(ltc1);
                switch (ltc1.kind().value()) {
                case _tk_short:
                case _tk_ushort:
                case _tk_long:
                case _tk_ulong:
                case _tk_enum:
                case _tk_longlong:
                case _tk_ulonglong:
                case _tk_char:
                case _tk_boolean:
                    if (!v1.equals(v2)) return false;
                    break;

                case _tk_octet:
                    break;

                default:
                    throw Assert.fail("unsupported type in tk_union");
                }
            }

            //
            // Don't use equivalentRecHelper here
            //
            if (!tc1.discriminatorType_.equivalent(tc2.discriminatorType_)) return false;
        }

        if (tc1.kind_ == tk_string
                || tk_wstring == tc1.kind_
                || tk_sequence == tc1.kind_
                || tk_array == tc1.kind_) {
            if (tc1.length_ != tc2.length_) return false;
        }

        if (tk_sequence == tc1.kind_
                || tk_array == tc1.kind_
                || tk_value_box == tc1.kind_
                || tk_alias == tc1.kind_) {
            if (!(tc1.contentType_.equivalentRecHelper(tc2.contentType_, history, otherHistory))) return false;
        }

        if (tk_fixed == tc1.kind_) {
            if (tc1.fixedDigits_ != tc2.fixedDigits_ || tc1.fixedScale_ != tc2.fixedScale_) return false;
        }

        if (tk_value == tc1.kind_) {
            if (tc1.memberVisibility_.length != tc2.memberVisibility_.length) return false;

            for (int i = 0; i < tc1.memberVisibility_.length; i++)
                if (tc1.memberVisibility_[i] != tc2.memberVisibility_[i]) return false;

            if (tc1.typeModifier_ != tc2.typeModifier_) return false;

            if (null != tc1.concreteBaseType_ || null != tc2.concreteBaseType_) {
                if (!(null != tc1.concreteBaseType_ && null != tc2.concreteBaseType_)) return false;

                //
                // Don't use equivalentRecHelper here
                //
                return tc1.concreteBaseType_.equivalent(tc2.concreteBaseType_);
            }
        }

        return true;
    }

    private TypeCode getCompactTypeCodeRec(Vector<org.omg.CORBA.TypeCode> history, Vector<org.omg.CORBA.TypeCode> compacted) {
        if (null != recId_) {
            if (null == recType_) throw new BAD_TYPECODE(describeBadTypecode(MinorIncompleteTypeCode), MinorIncompleteTypeCode, COMPLETED_NO);
            return recType_.getCompactTypeCodeRec(history, compacted);
        }

        //
        // Avoid infinite loops
        //
        for (int i = 0; i < history.size(); i++)
            if (this == history.elementAt(i))
                return (TypeCode) compacted.elementAt(i);

        history.addElement(this);

        // Create the new compacted type code (needed for recursive type codes).
        TypeCode result = new TypeCode();
        compacted.addElement(result);

        String[] names = (null == memberNames_) ?
                null : Arrays.stream(memberNames_).map(n -> "").toArray(String[]::new);

        TypeCode[] types = (null == memberTypes_) ?
                null : Arrays.stream(memberTypes_).map(t -> t.getCompactTypeCodeRec(history, compacted)).toArray(TypeCode[]::new);

        TypeCode content = (null == contentType_) ? null : contentType_.getCompactTypeCodeRec(history, compacted);

        TypeCode discriminator = (null == discriminatorType_) ? null : discriminatorType_.getCompactTypeCodeRec(history, compacted);

        //
        // Compact concrete base type
        //
        TypeCode concrete = (null == concreteBaseType_) ? null : concreteBaseType_.getCompactTypeCodeRec(history, compacted);

        switch (kind_.value()) {
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
            result.kind_ = kind_;
            break;

        case _tk_fixed:
            result.kind_ = kind_;
            result.fixedDigits_ = fixedDigits_;
            result.fixedScale_ = fixedScale_;
            break;

        case _tk_objref:
        case _tk_abstract_interface:
        case _tk_native:
        case _tk_local_interface:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            break;

        case _tk_struct:
        case _tk_except:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            result.memberTypes_ = types;
            break;

        case _tk_union:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            result.memberTypes_ = types;
            result.labels_ = labels_;
            result.discriminatorType_ = discriminator;
            break;

        case _tk_enum:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            break;

        case _tk_string:
        case _tk_wstring:
            result.kind_ = kind_;
            result.length_ = length_;
            break;

        case _tk_sequence:
        case _tk_array:
            result.kind_ = kind_;
            result.length_ = length_;
            result.contentType_ = content;
            break;

        case _tk_alias:
        case _tk_value_box:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.contentType_ = content;
            break;

        case _tk_value:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            result.memberTypes_ = types;
            result.memberVisibility_ = memberVisibility_;
            result.typeModifier_ = typeModifier_;
            result.concreteBaseType_ = concrete;
            break;

        default:
            throw Assert.fail("unsupported typecode");
        }

        return result;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public boolean equal(org.omg.CORBA.TypeCode t) {
        if (t == null)
            return false;

        if (t == this)
            return true;

        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.equal(t);
        }

        TypeCode tc;
        try {
            tc = (TypeCode) t;
        } catch (ClassCastException ex) {
            tc = _OB_convertForeignTypeCode(t);
        }

        if (tc.recId_ != null) {
            if (tc.recType_ == null)
                throw new BAD_PARAM(
                        MinorCodes
                                .describeBadParam(MinorCodes.MinorIncompleteTypeCodeParameter),
                        MinorCodes.MinorIncompleteTypeCodeParameter,
                        COMPLETED_NO);
            return equal(tc.recType_);
        }

        if (kind_ != tc.kind_)
            return false;

        if (kind_ == tk_objref
                || kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_alias
                || kind_ == tk_value
                || kind_ == tk_value_box
                || kind_ == tk_native
                || kind_ == tk_abstract_interface
                || kind_ == tk_except
                || kind_ == tk_local_interface) {
            if (!id_.equals("") || !tc.id_.equals("")) {
                return id_.equals(tc.id_);
            }

            if (!name_.equals("") || !tc.name_.equals("")) {
                if (!name_.equals(tc.name_))
                    return false;
            }
        }

        if (kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_value
                || kind_ == tk_except) {
            if (memberNames_.length != tc.memberNames_.length)
                return false;

            for (int i = 0; i < memberNames_.length; i++) {
                if (!memberNames_[i].equals("")
                        || !tc.memberNames_[i].equals("")) {
                    if (!memberNames_[i].equals(tc.memberNames_[i]))
                        return false;
                }
            }
        }

        if (kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_value
                || kind_ == tk_except) {
            if (memberTypes_.length != tc.memberTypes_.length)
                return false;

            for (int i = 0; i < memberTypes_.length; i++)
                if (!memberTypes_[i].equal(tc.memberTypes_[i]))
                    return false;
        }

        if (kind_ == tk_union) {
            if (labels_.length != tc.labels_.length)
                return false;

            for (int i = 0; i < labels_.length; i++) {
                if (!labels_[i].type().equal(tc.labels_[i].type()))
                    return false;

                if (!labels_[i].equal(tc.labels_[i]))
                    return false;
            }

            if (!discriminatorType_.equal(tc.discriminatorType_))
                return false;
        }

        if (kind_ == tk_string
                || kind_ == tk_wstring
                || kind_ == tk_sequence
                || kind_ == tk_array) {
            if (length_ != tc.length_)
                return false;
        }

        if (kind_ == tk_sequence
                || kind_ == tk_array
                || kind_ == tk_value_box
                || kind_ == tk_alias) {
            if (!contentType_.equal(tc.contentType_))
                return false;
        }

        if (kind_ == tk_fixed) {
            if (fixedDigits_ != tc.fixedDigits_
                    || fixedScale_ != tc.fixedScale_)
                return false;
        }

        if (kind_ == tk_value) {
            if (memberVisibility_.length != tc.memberVisibility_.length)
                return false;

            for (int i = 0; i < memberVisibility_.length; i++)
                if (memberVisibility_[i] != tc.memberVisibility_[i])
                    return false;

            if (typeModifier_ != tc.typeModifier_)
                return false;

            if (concreteBaseType_ != null || tc.concreteBaseType_ != null) {
                if (!(concreteBaseType_ != null && tc.concreteBaseType_ != null))
                    return false;

                return concreteBaseType_.equal(tc.concreteBaseType_);
            }
        }

        return true;
    }

    public boolean equivalent(org.omg.CORBA.TypeCode t) {
        Vector<org.omg.CORBA.TypeCode> history = new Vector<>();
        Vector<org.omg.CORBA.TypeCode> otherHistory = new Vector<>();

        boolean result = equivalentRecHelper(t, history, otherHistory);

        ensure(history.size() == 0);
        ensure(otherHistory.size() == 0);

        return result;
    }

    public org.omg.CORBA.TypeCode get_compact_typecode() {
        Vector<org.omg.CORBA.TypeCode> history = new Vector<>();
        Vector<org.omg.CORBA.TypeCode> compacted = new Vector<>();

        return getCompactTypeCodeRec(history, compacted);
    }

    public TCKind kind() {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.kind();
        }

        return kind_;
    }

    public String id() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.id();
        }

        if (!(kind_ == tk_objref
                || kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_alias
                || kind_ == tk_value
                || kind_ == tk_value_box
                || kind_ == tk_native
                || kind_ == tk_abstract_interface
                || kind_ == tk_except || kind_ == tk_local_interface))
            throw new BadKind();

        return id_;
    }

    public String name() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.name();
        }

        if (!(kind_ == tk_objref
                || kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_alias
                || kind_ == tk_value
                || kind_ == tk_value_box
                || kind_ == tk_native
                || kind_ == tk_abstract_interface
                || kind_ == tk_except || kind_ == tk_local_interface))
            throw new BadKind();

        return name_;
    }

    public int member_count() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.member_count();
        }

        if (!(kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_value || kind_ == tk_except))
            throw new BadKind();

        return memberNames_.length;
    }

    public String member_name(int index)
            throws BadKind,
            Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.member_name(index);
        }

        if (!(kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_enum
                || kind_ == tk_value || kind_ == tk_except))
            throw new BadKind();

        try {
            return memberNames_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }

    public org.omg.CORBA.TypeCode member_type(int index)
            throws BadKind,
            Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.member_type(index);
        }

        if (!(kind_ == tk_struct
                || kind_ == tk_union
                || kind_ == tk_value || kind_ == tk_except))
            throw new BadKind();

        try {
            return memberTypes_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }

    public org.omg.CORBA.Any member_label(int index)
            throws BadKind,
            Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.member_label(index);
        }

        if (!(kind_ == tk_union))
            throw new BadKind();

        try {
            return labels_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }

    public org.omg.CORBA.TypeCode discriminator_type()
            throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.discriminator_type();
        }

        if (!(kind_ == tk_union))
            throw new BadKind();

        return discriminatorType_;
    }

    public int default_index() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.default_index();
        }

        if (!(kind_ == tk_union))
            throw new BadKind();

        for (int i = 0; i < labels_.length; i++) {
            org.omg.CORBA.TypeCode tc = labels_[i].type();
            if (tc.kind() == TCKind.tk_octet)
                return i;
        }

        return -1;
    }

    public int length() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.length();
        }

        if (!(kind_ == tk_string
                || kind_ == tk_wstring
                || kind_ == tk_sequence || kind_ == tk_array))
            throw new BadKind();

        return length_;
    }

    public TypeCode content_type() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.content_type();
        }

        if (!(kind_ == tk_sequence
                || kind_ == tk_array
                || kind_ == tk_value_box || kind_ == tk_alias))
            throw new BadKind();

        return contentType_;
    }

    public short fixed_digits() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.fixed_digits();
        }

        if (!(kind_ == tk_fixed))
            throw new BadKind();

        return fixedDigits_;
    }

    public short fixed_scale() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.fixed_scale();
        }

        if (!(kind_ == tk_fixed))
            throw new BadKind();

        return fixedScale_;
    }

    public short member_visibility(int index)
            throws BadKind,
            Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.member_visibility(index);
        }

        if (!(kind_ == tk_value))
            throw new BadKind();

        try {
            return memberVisibility_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }

    public short type_modifier() throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.type_modifier();
        }

        if (!(kind_ == tk_value))
            throw new BadKind();

        return typeModifier_;
    }

    public org.omg.CORBA.TypeCode concrete_base_type()
            throws BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new BAD_TYPECODE(
                        describeBadTypecode(MinorIncompleteTypeCode),
                        MinorIncompleteTypeCode,
                        COMPLETED_NO);
            return recType_.concrete_base_type();
        }

        if (!(kind_ == tk_value))
            throw new BadKind();

        return concreteBaseType_;
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public TypeCode() {
    }

    public TypeCode _OB_getOrigType() {
        return _OB_getOrigType(this);
    }

    static public org.omg.CORBA.TypeCode _OB_getOrigType(org.omg.CORBA.TypeCode tc) {
        try {
            while (tc.kind() == tk_alias) tc = tc.content_type();
        } catch (BadKind ex) {
            throw Assert.fail(ex);
        }

        return tc;
    }

    static public TypeCode _OB_getOrigType(TypeCode tc) {
        try {
            while (tc.kind() == tk_alias) tc = tc.content_type();
        } catch (BadKind ex) {
            throw Assert.fail(ex);
        }

        return tc;
    }

    @SuppressWarnings("unused")
    public boolean _OB_isSystemException() {
        if (kind_ != tk_except) return false;
        return Util.isSystemException(id_);
    }

    static private TypeCode _OB_convertForeignTypeCodeHelper(
            org.omg.CORBA.TypeCode tc, Hashtable<org.omg.CORBA.TypeCode, TypeCode> history,
            Vector<org.omg.CORBA.TypeCode> recHistory) {
        if (tc instanceof TypeCode) return (TypeCode) tc;

        TypeCode result;

        try {
            TCKind kind = tc.kind();
            int kindValue = kind.value();

            //
            // Check for recursion
            //
            if (kindValue == _tk_struct
                    || kindValue == _tk_except
                    || kindValue == _tk_union
                    || kindValue == _tk_value) {
                for (int i = 0; i < recHistory.size(); i++)
                    if (tc == recHistory.elementAt(i)) {
                        result = new TypeCode();
                        result.recId_ = tc.id();
                        result.recType_ = history.get(tc);
                        ensure(result.recType_ != null);
                        return result;
                    }
            }

            //
            // Avoid creating the TypeCode again
            //
            result = history.get(tc);
            if (result != null)
                return result;

            result = new TypeCode();
            history.put(tc, result);

            switch (kindValue) {
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
                result.kind_ = kind;
                break;

            case _tk_fixed:
                result.kind_ = kind;
                result.fixedDigits_ = tc.fixed_digits();
                result.fixedScale_ = tc.fixed_scale();
                break;

            case _tk_objref:
            case _tk_abstract_interface:
            case _tk_native:
            case _tk_local_interface:
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                break;

            case _tk_struct:
            case _tk_except: {
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                int count = tc.member_count();
                result.memberNames_ = new String[count];
                for (int i = 0; i < count; i++)
                    result.memberNames_[i] = tc.member_name(i);
                recHistory.addElement(tc);
                result.memberTypes_ = new TypeCode[count];
                for (int i = 0; i < count; i++)
                    result.memberTypes_[i] = _OB_convertForeignTypeCodeHelper(
                            tc.member_type(i), history, recHistory);
                recHistory.setSize(recHistory.size() - 1);
                break;
            }

            case _tk_union: {
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                int count = tc.member_count();
                result.memberNames_ = new String[count];
                for (int i = 0; i < count; i++)
                    result.memberNames_[i] = tc.member_name(i);
                recHistory.addElement(tc);
                result.memberTypes_ = new TypeCode[count];
                for (int i = 0; i < count; i++)
                    result.memberTypes_[i] = _OB_convertForeignTypeCodeHelper(
                            tc.member_type(i), history, recHistory);
                recHistory.setSize(recHistory.size() - 1);
                result.labels_ = new Any[count];
                for (int i = 0; i < count; i++)
                    result.labels_[i] = new Any(tc.member_label(i));
                //
                // Discriminator can't be recursive, so no history needed
                //
                result.discriminatorType_ = _OB_convertForeignTypeCodeHelper(tc
                        .discriminator_type(), history, null);
                break;
            }

            case _tk_enum: {
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                int count = tc.member_count();
                result.memberNames_ = new String[count];
                for (int i = 0; i < count; i++)
                    result.memberNames_[i] = tc.member_name(i);
                break;
            }

            case _tk_string:
            case _tk_wstring:
                result.kind_ = kind;
                result.length_ = tc.length();
                break;

            case _tk_sequence:
            case _tk_array:
                result.kind_ = kind;
                result.length_ = tc.length();
                result.contentType_ = _OB_convertForeignTypeCodeHelper(tc
                        .content_type(), history, recHistory);
                break;

            case _tk_alias:
            case _tk_value_box:
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                result.contentType_ = _OB_convertForeignTypeCodeHelper(tc
                        .content_type(), history, recHistory);
                break;

            case _tk_value:
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                int count = tc.member_count();
                result.memberNames_ = new String[count];
                for (int i = 0; i < count; i++)
                    result.memberNames_[i] = tc.member_name(i);
                recHistory.addElement(tc);
                result.memberTypes_ = new TypeCode[count];
                for (int i = 0; i < count; i++)
                    result.memberTypes_[i] = _OB_convertForeignTypeCodeHelper(
                            tc.member_type(i), history, recHistory);
                recHistory.setSize(recHistory.size() - 1);
                result.memberVisibility_ = new short[count];
                for (int i = 0; i < count; i++)
                    result.memberVisibility_[i] = tc.member_visibility(i);
                result.typeModifier_ = tc.type_modifier();
                result.concreteBaseType_ = _OB_convertForeignTypeCodeHelper(tc
                        .concrete_base_type(), history, recHistory);
                break;

            default:
                throw Assert.fail("Unsupported typecode");
            }
        } catch (BadKind | Bounds ex) {
            throw Assert.fail(ex);
        }

        return result;
    }

    static public TypeCode _OB_convertForeignTypeCode(org.omg.CORBA.TypeCode tc) {
        ensure(!(tc instanceof TypeCode));

        Hashtable<org.omg.CORBA.TypeCode, TypeCode> history = new Hashtable<>(7);
        Vector<org.omg.CORBA.TypeCode> recHistory = new Vector<>();

        return _OB_convertForeignTypeCodeHelper(tc, history, recHistory);
    }

    // ----------------------------------------------------------------------
    // Embed recursive placeholder TypeCodes
    // ----------------------------------------------------------------------

    static public void _OB_embedRecTC(TypeCode outer) {
        //
        // Recursive placeholder TypeCodes are illegal as "outer" argument
        //
        ensure(outer.recId_ == null);

        //
        // Check for illegal recursion
        //
        if (!(tk_struct == outer.kind_ || tk_except == outer.kind_ || tk_union == outer.kind_ || tk_value == outer.kind_)) {
            throw new BAD_TYPECODE("Illegal recursion");
        }

        _OB_embedRecTC(outer, outer);
    }

    static public void _OB_embedRecTC(final TypeCode outer, final TypeCode inner) {
        //
        // Embed recursive placeholder TypeCodes
        //
        if (null != inner.recId_) {
            if (inner.recId_.equals(outer.id_)) {
                if (null == inner.recType_) {
                    //
                    // Embed the recursive placeholder TypeCode
                    //
                    inner.recType_ = outer;
                } else {
                    // Recursive TC already embedded - ensure it's the right one
                    ensure(inner.recType_ == outer);
                }
            }
        } else {
            //
            // Embed content type
            //
            switch (inner.kind().value()) {
            case _tk_sequence:
            case _tk_value_box:
            case _tk_array:
            case _tk_alias:
                ensure(outer != inner.contentType_);
                _OB_embedRecTC(outer, inner.contentType_);
                break;
            case _tk_struct:
            case _tk_union:
            case _tk_value:
            case _tk_except:
                for (TypeCode tc: inner.memberTypes_) {
                    ensure(outer != tc);
                    _OB_embedRecTC(outer, tc);
                }
                if (inner.kind() == tk_value && null != inner.concreteBaseType_) _OB_embedRecTC(outer, inner.concreteBaseType_);
                break;
            }
        }
    }
}
