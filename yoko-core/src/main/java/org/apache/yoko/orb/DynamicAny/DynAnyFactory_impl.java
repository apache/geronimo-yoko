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

package org.apache.yoko.orb.DynamicAny;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.TypeCode;

final public class DynAnyFactory_impl extends org.omg.CORBA.LocalObject
        implements org.omg.DynamicAny.DynAnyFactory {
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    public DynAnyFactory_impl(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.omg.DynamicAny.DynAny create_dyn_any(org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode {
        DynValueReader dynValueReader = new DynValueReader(orbInstance_, this,
                true);

        try {
            org.omg.DynamicAny.DynAny p = prepare_dyn_any_from_type_code(value
                    .type(), dynValueReader);
            p.from_any(value);
            return p;
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
        }

        throw new org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode();
    }

    public org.omg.DynamicAny.DynAny create_dyn_any_without_truncation(
            org.omg.CORBA.Any value)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode,
            org.omg.DynamicAny.MustTruncate {
        DynValueReader dynValueReader = new DynValueReader(orbInstance_, this,
                false);

        try {
            org.omg.DynamicAny.DynAny p = prepare_dyn_any_from_type_code(value
                    .type(), dynValueReader);

            p.from_any(value);

            if (dynValueReader.mustTruncate)
                throw new org.omg.DynamicAny.MustTruncate();

            return p;
        } catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) {
        } catch (org.omg.DynamicAny.DynAnyPackage.InvalidValue ex) {
        }

        throw new org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode();
    }

    public org.omg.DynamicAny.DynAny prepare_dyn_any_from_type_code(
            org.omg.CORBA.TypeCode tc,
            org.apache.yoko.orb.DynamicAny.DynValueReader dvr)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode {
        org.omg.DynamicAny.DynAny result = null;

        TypeCode type = null;
        try {
            type = (TypeCode) tc;
        } catch (ClassCastException ex) {
            type = TypeCode._OB_convertForeignTypeCode(tc);
        }

        org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                ._OB_getOrigType(type);
        switch (origTC.kind().value()) {
        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
            result = new DynStruct_impl(this, orbInstance_, type, dvr);
            break;

        case org.omg.CORBA.TCKind._tk_union:
            result = new DynUnion_impl(this, orbInstance_, type, dvr);
            break;

        case org.omg.CORBA.TCKind._tk_sequence:
            result = new DynSequence_impl(this, orbInstance_, type, dvr);
            break;

        case org.omg.CORBA.TCKind._tk_array:
            result = new DynArray_impl(this, orbInstance_, type, dvr);
            break;

        case org.omg.CORBA.TCKind._tk_value:
            try {
                if (origTC.type_modifier() == org.omg.CORBA.VM_CUSTOM.value)
                    result = create_dyn_any_from_type_code(tc);
                else
                    result = new DynValue_impl(this, orbInstance_, type, dvr);
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            }
            break;

        default:
            result = create_dyn_any_from_type_code(tc);
        }

        return result;
    }

    public org.omg.DynamicAny.DynAny create_dyn_any_from_type_code(
            org.omg.CORBA.TypeCode tc)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode {
        org.omg.DynamicAny.DynAny result = null;

        TypeCode type = null;
        try {
            type = (TypeCode) tc;
        } catch (ClassCastException ex) {
            type = TypeCode._OB_convertForeignTypeCode(tc);
        }

        org.omg.CORBA.TypeCode origTC = org.apache.yoko.orb.CORBA.TypeCode
                ._OB_getOrigType(type);
        switch (origTC.kind().value()) {
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
        case org.omg.CORBA.TCKind._tk_objref:
        case org.omg.CORBA.TCKind._tk_string:
        case org.omg.CORBA.TCKind._tk_longlong:
        case org.omg.CORBA.TCKind._tk_ulonglong:
        case org.omg.CORBA.TCKind._tk_wchar:
        case org.omg.CORBA.TCKind._tk_wstring:
        case org.omg.CORBA.TCKind._tk_abstract_interface:
        case org.omg.CORBA_2_4.TCKind._tk_local_interface:
            result = new DynBasic_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_fixed:
            result = new DynFixed_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_enum:
            result = new DynEnum_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_struct:
        case org.omg.CORBA.TCKind._tk_except:
            result = new DynStruct_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_union:
            result = new DynUnion_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_sequence:
            result = new DynSequence_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_array:
            result = new DynArray_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_value:
            try {
                if (origTC.type_modifier() == org.omg.CORBA.VM_CUSTOM.value)
                    result = new DynBasic_impl(this, orbInstance_, type);
                else
                    result = new DynValue_impl(this, orbInstance_, type);
            } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            }
            break;

        case org.omg.CORBA.TCKind._tk_value_box:
            result = new DynValueBox_impl(this, orbInstance_, type);
            break;

        case org.omg.CORBA.TCKind._tk_Principal:
        case org.omg.CORBA.TCKind._tk_native:
        case org.omg.CORBA.TCKind._tk_longdouble:
            throw new org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode();

        case org.omg.CORBA.TCKind._tk_alias:
        default:
            org.apache.yoko.orb.OB.Assert._OB_assert("Unsupported type code");
        }

        return result;
    }

    public org.omg.DynamicAny.DynAny[] create_multiple_dyn_anys(
            org.omg.CORBA.Any[] values, boolean allow_truncate)
            throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode,
            org.omg.DynamicAny.MustTruncate {
        //
        // Put the sequence of Anys and marshal it. This ensures that
        // ValueType equality that "spans" dynAnys will be mapped by
        // indirections when marshalled.
        //
        org.omg.CORBA.Any aSeq = orbInstance_.getORB().create_any();
        org.omg.CORBA.AnySeqHelper.insert(aSeq, values);

        org.apache.yoko.orb.CORBA.Any valSeq;
        valSeq = (org.apache.yoko.orb.CORBA.Any) aSeq;

        InputStream in = (InputStream) valSeq.create_input_stream();

        // NOTE: the input stream I obtain does not contain
        // indirections that "span" the original members of the sequence.
        // (that is an issue with the implementation of Anys). Thus
        // ValueType that span the original Any instance are not
        // properly mapped.

        //
        // Create a sequence of Dynamic Anys
        //
        org.omg.DynamicAny.DynAny result[] = new org.omg.DynamicAny.DynAny[values.length];

        DynValueReader dynValueReader = new DynValueReader(orbInstance_, this,
                allow_truncate);

        for (int i = 0; i < values.length; i++) {
            org.omg.CORBA.TypeCode type = ((org.apache.yoko.orb.CORBA.Any) values[i])
                    ._OB_type();

            result[i] = prepare_dyn_any_from_type_code(type, dynValueReader);
        }

        //
        // Populate the DynAnys by unmarshalling the sequence of Anys.
        // Start by skipping the sequence lenght
        //
        in.read_ulong();

        for (int i = 0; i < values.length; i++) {
            in.read_TypeCode();
            DynAny_impl impl = (DynAny_impl) result[i];
            impl._OB_unmarshal(in);
        }

        return result;
    }

    public org.omg.CORBA.Any[] create_multiple_anys(
            org.omg.DynamicAny.DynAny[] values) {
        // TODO: DynValue equalities that "span" members of
        // the sequence of DynAnys are not maintained

        org.omg.CORBA.Any[] result = new org.omg.CORBA.Any[values.length];

        for (int i = 0; i < values.length; i++) {
            DynAny_impl impl = (DynAny_impl) values[i];
            result[i] = impl.to_any();
        }

        return result;
    }

}
