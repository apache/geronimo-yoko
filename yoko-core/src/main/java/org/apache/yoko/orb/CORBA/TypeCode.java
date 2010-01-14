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

// Note: TypeCodes are immutable, so I don't need thread
// synchronization

final public class TypeCode extends org.omg.CORBA.TypeCode {
    //
    // TypeCode kind
    //
    public org.omg.CORBA.TCKind kind_;

    //
    // tk_objref, tk_struct, tk_union, tk_enum, tk_alias, tk_value,
    // tk_value_box, tk_native, tk_abstract_interface, tk_except,
    // tk_local_interface
    //
    public String id_;

    public String name_;

    //
    // tk_struct, tk_union, tk_enum, tk_value, tk_except
    //
    public String[] memberNames_;

    //
    // tk_struct, tk_union, tk_value, tk_except
    //
    public TypeCode[] memberTypes_;

    //
    // tk_union
    //
    public Any[] labels_;

    //
    // tk_union
    //
    public TypeCode discriminatorType_;

    //
    // tk_string, tk_wstring, tk_sequence, tk_array
    //
    public int length_;

    //
    // tk_sequence, tk_array, tk_value_box, tk_alias
    //
    public TypeCode contentType_;

    //
    // tk_fixed
    //
    public short fixedDigits_;

    public short fixedScale_;

    //
    // tk_value
    //
    public short[] memberVisibility_;

    public short typeModifier_;

    public TypeCode concreteBaseType_;

    //
    // If recId_ is set, this is a placeholder recursive TypeCode that
    // was generated with create_recursive_tc(). If the placeholder
    // recursive TypeCode is already embedded, recType_ points to the
    // recursive TypeCode this placeholder delegates to.
    //
    public String recId_;

    TypeCode recType_;

    // ------------------------------------------------------------------
    // Private member implementations
    // ------------------------------------------------------------------

    private boolean equivalentRecHelper(org.omg.CORBA.TypeCode t,
            java.util.Vector history, java.util.Vector otherHistory) {
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
            java.util.Vector history, java.util.Vector otherHistory) {
        TypeCode tc = null;
        try {
            tc = (TypeCode) t;
        } catch (ClassCastException ex) {
            tc = _OB_convertForeignTypeCode(t);
        }

        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.equivalentRecHelper(t, history, otherHistory);
        }

        if (tc.recId_ != null) {
            if (tc.recType_ == null)
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCodeParameter),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCodeParameter,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return equivalentRecHelper(tc.recType_, history, otherHistory);
        }

        TypeCode tc1 = _OB_getOrigType();
        TypeCode tc2 = tc._OB_getOrigType();

        if (tc1.kind_ != tc2.kind_)
            return false;

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_objref
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_struct
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_union
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_enum
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_alias
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_value
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_value_box
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_native
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_abstract_interface
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_except
                || tc1.kind_ == org.omg.CORBA_2_4.TCKind.tk_local_interface) {
            if (!tc1.id_.equals("") && !tc2.id_.equals("")) {
                if (tc1.id_.equals(tc2.id_))
                    return true;
                else
                    return false;
            }
        }

        // names_ and memberNames_ must be ignored

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_struct
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_union
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_value
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_except) {
            if (tc1.memberTypes_.length != tc2.memberTypes_.length)
                return false;

            for (int i = 0; i < tc1.memberTypes_.length; i++) {
                if (!(tc1.memberTypes_[i].equivalentRecHelper(
                        tc2.memberTypes_[i], history, otherHistory)))
                    return false;
            }
        }

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_union) {
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

                java.lang.Object v1 = tc1.labels_[i].value();
                java.lang.Object v2 = tc2.labels_[i].value();

                ltc1 = _OB_getOrigType(ltc1);
                switch (ltc1.kind().value()) {
                case org.omg.CORBA.TCKind._tk_short:
                case org.omg.CORBA.TCKind._tk_ushort:
                case org.omg.CORBA.TCKind._tk_long:
                case org.omg.CORBA.TCKind._tk_ulong:
                case org.omg.CORBA.TCKind._tk_enum:
                case org.omg.CORBA.TCKind._tk_longlong:
                case org.omg.CORBA.TCKind._tk_ulonglong:
                case org.omg.CORBA.TCKind._tk_char:
                case org.omg.CORBA.TCKind._tk_boolean:
                    if (!v1.equals(v2))
                        return false;
                    break;

                case org.omg.CORBA.TCKind._tk_octet:
                    break;

                default:
                    org.apache.yoko.orb.OB.Assert._OB_assert("unsupported type in tk_union");
                    break;
                }
            }

            //
            // Don't use equivalentRecHelper here
            //
            if (!tc1.discriminatorType_.equivalent(tc2.discriminatorType_))
                return false;
        }

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_string
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_wstring
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_sequence
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_array) {
            if (tc1.length_ != tc2.length_)
                return false;
        }

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_sequence
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_array
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_value_box
                || tc1.kind_ == org.omg.CORBA.TCKind.tk_alias) {
            if (!(tc1.contentType_.equivalentRecHelper(tc2.contentType_,
                    history, otherHistory)))
                return false;
        }

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_fixed) {
            if (tc1.fixedDigits_ != tc2.fixedDigits_
                    || tc1.fixedScale_ != tc2.fixedScale_)
                return false;
        }

        if (tc1.kind_ == org.omg.CORBA.TCKind.tk_value) {
            if (tc1.memberVisibility_.length != tc2.memberVisibility_.length)
                return false;

            for (int i = 0; i < tc1.memberVisibility_.length; i++)
                if (tc1.memberVisibility_[i] != tc2.memberVisibility_[i])
                    return false;

            if (tc1.typeModifier_ != tc2.typeModifier_)
                return false;

            if (tc1.concreteBaseType_ != null || tc2.concreteBaseType_ != null) {
                if (!(tc1.concreteBaseType_ != null && tc2.concreteBaseType_ != null))
                    return false;

                //
                // Don't use equivalentRecHelper here
                //
                if (!(tc1.concreteBaseType_.equivalent(tc2.concreteBaseType_)))
                    return false;
            }
        }

        return true;
    }

    private TypeCode getCompactTypeCodeRec(java.util.Vector history,
            java.util.Vector compacted) {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.getCompactTypeCodeRec(history, compacted);
        }

        //
        // Avoid infinite loops
        //
        for (int i = 0; i < history.size(); i++)
            if (this == history.elementAt(i))
                return (TypeCode) compacted.elementAt(i);

        history.addElement(this);

        //
        // Create the new compacted type code (needed for recursive type
        // codes).
        //
        TypeCode result = new TypeCode();
        compacted.addElement(result);

        String[] names = null;
        if (memberNames_ != null) {
            names = new String[memberNames_.length];
            for (int i = 0; i < memberNames_.length; i++)
                names[i] = "";
        }

        TypeCode[] types = null;
        if (memberTypes_ != null) {
            types = new TypeCode[memberTypes_.length];
            for (int i = 0; i < memberTypes_.length; i++)
                types[i] = memberTypes_[i].getCompactTypeCodeRec(history,
                        compacted);
        }

        //
        // Compact content type
        //
        TypeCode content = null;
        if (contentType_ != null)
            content = contentType_.getCompactTypeCodeRec(history, compacted);

        //
        // Compact discriminator type
        //
        TypeCode discriminator = null;
        if (discriminatorType_ != null)
            discriminator = discriminatorType_.getCompactTypeCodeRec(history,
                    compacted);

        //
        // Compact concrete base type
        //
        TypeCode concrete = null;
        if (concreteBaseType_ != null)
            concrete = concreteBaseType_.getCompactTypeCodeRec(history,
                    compacted);

        switch (kind_.value()) {
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
            result.kind_ = kind_;
            break;

        case org.omg.CORBA.TCKind._tk_fixed:
            result.kind_ = kind_;
            result.fixedDigits_ = fixedDigits_;
            result.fixedScale_ = fixedScale_;
            break;

        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA.TCKind._tk_abstract_interface:
        case org.omg.CORBA.TCKind._tk_native:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            break;

        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            result.memberTypes_ = types;
            break;

        case org.omg.CORBA.TCKind._tk_union:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            result.memberTypes_ = types;
            result.labels_ = labels_;
            result.discriminatorType_ = discriminator;
            break;

        case org.omg.CORBA.TCKind._tk_enum:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.memberNames_ = names;
            break;

        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_wstring:
            result.kind_ = kind_;
            result.length_ = length_;
            break;

        case org.omg.CORBA.TCKind._tk_sequence:
        case org.omg.CORBA.TCKind._tk_array:
            result.kind_ = kind_;
            result.length_ = length_;
            result.contentType_ = content;
            break;

        case org.omg.CORBA.TCKind._tk_alias:
        case org.omg.CORBA.TCKind._tk_value_box:
            result.kind_ = kind_;
            result.id_ = id_;
            result.name_ = "";
            result.contentType_ = content;
            break;

        case org.omg.CORBA.TCKind._tk_value:
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
            org.apache.yoko.orb.OB.Assert._OB_assert("unsupported typecode");
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
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.equal(t);
        }

        TypeCode tc = null;
        try {
            tc = (TypeCode) t;
        } catch (ClassCastException ex) {
            tc = _OB_convertForeignTypeCode(t);
        }

        if (tc.recId_ != null) {
            if (tc.recType_ == null)
                throw new org.omg.CORBA.BAD_PARAM(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCodeParameter),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCodeParameter,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return equal(tc.recType_);
        }

        if (kind_ != tc.kind_)
            return false;

        if (kind_ == org.omg.CORBA.TCKind.tk_objref
                || kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_alias
                || kind_ == org.omg.CORBA.TCKind.tk_value
                || kind_ == org.omg.CORBA.TCKind.tk_value_box
                || kind_ == org.omg.CORBA.TCKind.tk_native
                || kind_ == org.omg.CORBA.TCKind.tk_abstract_interface
                || kind_ == org.omg.CORBA.TCKind.tk_except
                || kind_ == org.omg.CORBA_2_4.TCKind.tk_local_interface) {
            if (!id_.equals("") || !tc.id_.equals("")) {
                if (id_.equals(tc.id_))
                    return true;
                else
                    return false;
            }

            if (!name_.equals("") || !tc.name_.equals("")) {
                if (!name_.equals(tc.name_))
                    return false;
            }
        }

        if (kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_value
                || kind_ == org.omg.CORBA.TCKind.tk_except) {
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

        if (kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_value
                || kind_ == org.omg.CORBA.TCKind.tk_except) {
            if (memberTypes_.length != tc.memberTypes_.length)
                return false;

            for (int i = 0; i < memberTypes_.length; i++)
                if (!memberTypes_[i].equal(tc.memberTypes_[i]))
                    return false;
        }

        if (kind_ == org.omg.CORBA.TCKind.tk_union) {
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

        if (kind_ == org.omg.CORBA.TCKind.tk_string
                || kind_ == org.omg.CORBA.TCKind.tk_wstring
                || kind_ == org.omg.CORBA.TCKind.tk_sequence
                || kind_ == org.omg.CORBA.TCKind.tk_array) {
            if (length_ != tc.length_)
                return false;
        }

        if (kind_ == org.omg.CORBA.TCKind.tk_sequence
                || kind_ == org.omg.CORBA.TCKind.tk_array
                || kind_ == org.omg.CORBA.TCKind.tk_value_box
                || kind_ == org.omg.CORBA.TCKind.tk_alias) {
            if (!contentType_.equal(tc.contentType_))
                return false;
        }

        if (kind_ == org.omg.CORBA.TCKind.tk_fixed) {
            if (fixedDigits_ != tc.fixedDigits_
                    || fixedScale_ != tc.fixedScale_)
                return false;
        }

        if (kind_ == org.omg.CORBA.TCKind.tk_value) {
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

                if (!(concreteBaseType_.equal(tc.concreteBaseType_)))
                    return false;
            }
        }

        return true;
    }

    public boolean equivalent(org.omg.CORBA.TypeCode t) {
        java.util.Vector history = new java.util.Vector();
        java.util.Vector otherHistory = new java.util.Vector();

        boolean result = equivalentRecHelper(t, history, otherHistory);

        org.apache.yoko.orb.OB.Assert._OB_assert(history.size() == 0);
        org.apache.yoko.orb.OB.Assert._OB_assert(otherHistory.size() == 0);

        return result;
    }

    public org.omg.CORBA.TypeCode get_compact_typecode() {
        java.util.Vector history = new java.util.Vector();
        java.util.Vector compacted = new java.util.Vector();

        return getCompactTypeCodeRec(history, compacted);
    }

    public org.omg.CORBA.TCKind kind() {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.kind();
        }

        return kind_;
    }

    public String id() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.id();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_objref
                || kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_alias
                || kind_ == org.omg.CORBA.TCKind.tk_value
                || kind_ == org.omg.CORBA.TCKind.tk_value_box
                || kind_ == org.omg.CORBA.TCKind.tk_native
                || kind_ == org.omg.CORBA.TCKind.tk_abstract_interface
                || kind_ == org.omg.CORBA.TCKind.tk_except || kind_ == org.omg.CORBA_2_4.TCKind.tk_local_interface))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return id_;
    }

    public String name() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.name();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_objref
                || kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_alias
                || kind_ == org.omg.CORBA.TCKind.tk_value
                || kind_ == org.omg.CORBA.TCKind.tk_value_box
                || kind_ == org.omg.CORBA.TCKind.tk_native
                || kind_ == org.omg.CORBA.TCKind.tk_abstract_interface
                || kind_ == org.omg.CORBA.TCKind.tk_except || kind_ == org.omg.CORBA_2_4.TCKind.tk_local_interface))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return name_;
    }

    public int member_count() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.member_count();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_value || kind_ == org.omg.CORBA.TCKind.tk_except))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return memberNames_.length;
    }

    public String member_name(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.member_name(index);
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_enum
                || kind_ == org.omg.CORBA.TCKind.tk_value || kind_ == org.omg.CORBA.TCKind.tk_except))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        try {
            return memberNames_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.TypeCodePackage.Bounds();
        }
    }

    public org.omg.CORBA.TypeCode member_type(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.member_type(index);
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_struct
                || kind_ == org.omg.CORBA.TCKind.tk_union
                || kind_ == org.omg.CORBA.TCKind.tk_value || kind_ == org.omg.CORBA.TCKind.tk_except))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        try {
            return memberTypes_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.TypeCodePackage.Bounds();
        }
    }

    public org.omg.CORBA.Any member_label(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.member_label(index);
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_union))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        try {
            return labels_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.TypeCodePackage.Bounds();
        }
    }

    public org.omg.CORBA.TypeCode discriminator_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.discriminator_type();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_union))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return discriminatorType_;
    }

    public int default_index() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.default_index();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_union))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        for (int i = 0; i < labels_.length; i++) {
            org.omg.CORBA.TypeCode tc = labels_[i].type();
            if (tc.kind() == org.omg.CORBA.TCKind.tk_octet)
                return i;
        }

        return -1;
    }

    public int length() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.length();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_string
                || kind_ == org.omg.CORBA.TCKind.tk_wstring
                || kind_ == org.omg.CORBA.TCKind.tk_sequence || kind_ == org.omg.CORBA.TCKind.tk_array))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return length_;
    }

    public org.omg.CORBA.TypeCode content_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.content_type();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_sequence
                || kind_ == org.omg.CORBA.TCKind.tk_array
                || kind_ == org.omg.CORBA.TCKind.tk_value_box || kind_ == org.omg.CORBA.TCKind.tk_alias))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return contentType_;
    }

    public short fixed_digits() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.fixed_digits();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_fixed))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return fixedDigits_;
    }

    public short fixed_scale() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.fixed_scale();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_fixed))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return fixedScale_;
    }

    public short member_visibility(int index)
            throws org.omg.CORBA.TypeCodePackage.BadKind,
            org.omg.CORBA.TypeCodePackage.Bounds {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.member_visibility(index);
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_value))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        try {
            return memberVisibility_[index];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.TypeCodePackage.Bounds();
        }
    }

    public short type_modifier() throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.type_modifier();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_value))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

        return typeModifier_;
    }

    public org.omg.CORBA.TypeCode concrete_base_type()
            throws org.omg.CORBA.TypeCodePackage.BadKind {
        if (recId_ != null) {
            if (recType_ == null)
                throw new org.omg.CORBA.BAD_TYPECODE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeBadTypecode(org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode),
                        org.apache.yoko.orb.OB.MinorCodes.MinorIncompleteTypeCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return recType_.concrete_base_type();
        }

        if (!(kind_ == org.omg.CORBA.TCKind.tk_value))
            throw new org.omg.CORBA.TypeCodePackage.BadKind();

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
        return (TypeCode) _OB_getOrigType(this);
    }

    static public org.omg.CORBA.TypeCode _OB_getOrigType(
            org.omg.CORBA.TypeCode tc) {
        try {
            while (tc.kind() == org.omg.CORBA.TCKind.tk_alias)
                tc = tc.content_type();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return tc;
    }

    public boolean _OB_isSystemException() {
        if (kind_ != org.omg.CORBA.TCKind.tk_except)
            return false;

        return org.apache.yoko.orb.OB.Util.isSystemException(id_);
    }

    static private TypeCode _OB_convertForeignTypeCodeHelper(
            org.omg.CORBA.TypeCode tc, java.util.Hashtable history,
            java.util.Vector recHistory) {
        if (tc instanceof TypeCode)
            return (TypeCode) tc;

        TypeCode result = null;

        try {
            org.omg.CORBA.TCKind kind = tc.kind();
            int kindValue = kind.value();

            //
            // Check for recursion
            //
            if (kindValue == org.omg.CORBA.TCKind._tk_struct
                    || kindValue == org.omg.CORBA.TCKind._tk_except
                    || kindValue == org.omg.CORBA.TCKind._tk_union
                    || kindValue == org.omg.CORBA.TCKind._tk_value) {
                for (int i = 0; i < recHistory.size(); i++)
                    if (tc == recHistory.elementAt(i)) {
                        result = new TypeCode();
                        result.recId_ = tc.id();
                        result.recType_ = (TypeCode) history.get(tc);
                        org.apache.yoko.orb.OB.Assert
                                ._OB_assert(result.recType_ != null);
                        return result;
                    }
            }

            //
            // Avoid creating the TypeCode again
            //
            result = (TypeCode) history.get(tc);
            if (result != null)
                return result;

            result = new TypeCode();
            history.put(tc, result);

            switch (kindValue) {
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
                result.kind_ = kind;
                break;

            case org.omg.CORBA.TCKind._tk_fixed:
                result.kind_ = kind;
                result.fixedDigits_ = tc.fixed_digits();
                result.fixedScale_ = tc.fixed_scale();
                break;

            case org.omg.CORBA.TCKind._tk_objref:
            case org.omg.CORBA.TCKind._tk_abstract_interface:
            case org.omg.CORBA.TCKind._tk_native:
            case org.omg.CORBA_2_4.TCKind._tk_local_interface:
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                break;

            case org.omg.CORBA.TCKind._tk_struct:
            case org.omg.CORBA.TCKind._tk_except: {
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

            case org.omg.CORBA.TCKind._tk_union: {
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

            case org.omg.CORBA.TCKind._tk_enum: {
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                int count = tc.member_count();
                result.memberNames_ = new String[count];
                for (int i = 0; i < count; i++)
                    result.memberNames_[i] = tc.member_name(i);
                break;
            }

            case org.omg.CORBA.TCKind._tk_string:
            case org.omg.CORBA.TCKind._tk_wstring:
                result.kind_ = kind;
                result.length_ = tc.length();
                break;

            case org.omg.CORBA.TCKind._tk_sequence:
            case org.omg.CORBA.TCKind._tk_array:
                result.kind_ = kind;
                result.length_ = tc.length();
                result.contentType_ = _OB_convertForeignTypeCodeHelper(tc
                        .content_type(), history, recHistory);
                break;

            case org.omg.CORBA.TCKind._tk_alias:
            case org.omg.CORBA.TCKind._tk_value_box:
                result.kind_ = kind;
                result.id_ = tc.id();
                result.name_ = tc.name();
                result.contentType_ = _OB_convertForeignTypeCodeHelper(tc
                        .content_type(), history, recHistory);
                break;

            case org.omg.CORBA.TCKind._tk_value:
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
                org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported typecode");
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }

        return result;
    }

    static public TypeCode _OB_convertForeignTypeCode(org.omg.CORBA.TypeCode tc) {
        org.apache.yoko.orb.OB.Assert._OB_assert(!(tc instanceof TypeCode));

        java.util.Hashtable history = new java.util.Hashtable(7);
        java.util.Vector recHistory = new java.util.Vector();

        return _OB_convertForeignTypeCodeHelper(tc, history, recHistory);
    }

    // ----------------------------------------------------------------------
    // Embed recursive placeholder TypeCodes
    // ----------------------------------------------------------------------

    static public void _OB_embedRecTC(TypeCode outer) {
        //
        // Recursive placeholder TypeCodes are illegal as "outer" argument
        //
        org.apache.yoko.orb.OB.Assert._OB_assert(outer.recId_ == null);

        //
        // Check for illegal recursion
        //
        if (!(outer.kind_ == org.omg.CORBA.TCKind.tk_struct
                || outer.kind_ == org.omg.CORBA.TCKind.tk_except
                || outer.kind_ == org.omg.CORBA.TCKind.tk_union || outer.kind_ == org.omg.CORBA.TCKind.tk_value)) {
            throw new org.omg.CORBA.BAD_TYPECODE("Illegal recursion");
        }

        _OB_embedRecTC(outer, outer);
    }

    static public void _OB_embedRecTC(TypeCode outer, TypeCode inner) {
        //
        // Embed recursive placeholder TypeCodes
        //
        if (inner.recId_ != null) {
            if (inner.recId_.equals(outer.id_)) {
                if (inner.recType_ != null) {
                    org.apache.yoko.orb.OB.Assert
                            ._OB_assert(inner.recType_ != outer);
                } else {
                    //
                    // Embed the recursive placeholder TypeCode
                    //
                    inner.recType_ = outer;
                }
            }
        } else {
            //
            // Embed content type
            //
            if (inner.kind_ == org.omg.CORBA.TCKind.tk_sequence
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_value_box
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_array
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_alias) {
                org.apache.yoko.orb.OB.Assert
                        ._OB_assert(inner.contentType_ != outer);
                _OB_embedRecTC(outer, inner.contentType_);
            }

            //
            // Embed member types
            //
            if (inner.kind_ == org.omg.CORBA.TCKind.tk_struct
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_union
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_value
                    || inner.kind_ == org.omg.CORBA.TCKind.tk_except) {
                for (int i = 0; i < inner.memberTypes_.length; i++) {
                    org.apache.yoko.orb.OB.Assert
                            ._OB_assert(inner.memberTypes_[i] != outer);
                    _OB_embedRecTC(outer, inner.memberTypes_[i]);
                }
            }

            //
            // Embed base valuetype
            //
            if (inner.kind_ == org.omg.CORBA.TCKind.tk_value
                    && inner.concreteBaseType_ != null)
                _OB_embedRecTC(outer, inner.concreteBaseType_);
        }
    }
}
