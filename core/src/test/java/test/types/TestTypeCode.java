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

package test.types;

import org.omg.CORBA.*;

public class TestTypeCode extends test.common.TestBase {
    public TestTypeCode(ORB orb) {
        //
        // Test CompletionStatus
        //
        // Note: In JDK 1.3, the repository ID of CompletionStatus
        // is IDL:omg.org/CORBA/CompletionStatus:1.0 (which is
        // incorrect)
        //
        try {
            TCKind kind = CompletionStatusHelper.type().kind();
            TEST(kind == TCKind.tk_enum);

            String name = CompletionStatusHelper.type().name();
            TEST(name.equals("completion_status")
                    || name.equals("CompletionStatus"));

            String id = CompletionStatusHelper.type().id();
            TEST(id.equals("IDL:omg.org/CORBA/completion_status:1.0")
                    || id.equals("IDL:omg.org/CORBA/CompletionStatus:1.0"));

            int count = CompletionStatusHelper.type().member_count();
            TEST(count == 3);

            String name0 = CompletionStatusHelper.type().member_name(0);
            TEST(name0.equals("COMPLETED_YES"));

            String name1 = CompletionStatusHelper.type().member_name(1);
            TEST(name1.equals("COMPLETED_NO"));

            String name2 = CompletionStatusHelper.type().member_name(2);
            TEST(name2.equals("COMPLETED_MAYBE"));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TypeCode type = org.omg.CORBA.TypeCodePackage.BoundsHelper.type();

            TCKind kind = type.kind();
            TEST(kind == TCKind.tk_except);

            String name = type.name();
            TEST(name.equals("Bounds"));

            String id = type.id();
            TEST(id.equals("IDL:omg.org/CORBA/TypeCode/Bounds:1.0"));

            int count = type.member_count();
            TEST(count == 0);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        }

        try {
            TypeCode type = org.omg.CORBA.TypeCodePackage.BadKindHelper.type();

            TCKind kind = type.kind();
            TEST(kind == TCKind.tk_except);

            String name = type.name();
            TEST(name.equals("BadKind"));

            String id = type.id();
            TEST(id.equals("IDL:omg.org/CORBA/TypeCode/BadKind:1.0"));

            int count = type.member_count();
            TEST(count == 0);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        }

        try {
            TCKind kind = TestStruct1Helper.type().kind();
            TEST(kind == TCKind.tk_struct);

            String name = TestStruct1Helper.type().name();
            TEST(name.equals("TestStruct1"));

            long count = TestStruct1Helper.type().member_count();
            TEST(count == 7);

            String name0 = TestStruct1Helper.type().member_name(0);
            TEST(name0.equals("s"));

            String name1 = TestStruct1Helper.type().member_name(1);
            TEST(name1.equals("l"));

            String name2 = TestStruct1Helper.type().member_name(2);
            TEST(name2.equals("d"));

            String name3 = TestStruct1Helper.type().member_name(3);
            TEST(name3.equals("b"));

            String name4 = TestStruct1Helper.type().member_name(4);
            TEST(name4.equals("c"));

            String name5 = TestStruct1Helper.type().member_name(5);
            TEST(name5.equals("o"));

            String name6 = TestStruct1Helper.type().member_name(6);
            TEST(name6.equals("str"));

            TypeCode type0 = TestStruct1Helper.type().member_type(0);
            TEST(type0.equal(orb.get_primitive_tc(TCKind.tk_short)));

            TypeCode type1 = TestStruct1Helper.type().member_type(1);
            TEST(type1.equal(orb.get_primitive_tc(TCKind.tk_long)));

            TypeCode type2 = TestStruct1Helper.type().member_type(2);
            TEST(type2.equal(orb.get_primitive_tc(TCKind.tk_double)));

            TypeCode type3 = TestStruct1Helper.type().member_type(3);
            TEST(type3.equal(orb.get_primitive_tc(TCKind.tk_boolean)));

            TypeCode type4 = TestStruct1Helper.type().member_type(4);
            TEST(type4.equal(orb.get_primitive_tc(TCKind.tk_char)));

            TypeCode type5 = TestStruct1Helper.type().member_type(5);
            TEST(type5.equal(orb.get_primitive_tc(TCKind.tk_octet)));

            TypeCode type6 = TestStruct1Helper.type().member_type(6);
            TEST(type6.equal(orb.get_primitive_tc(TCKind.tk_string)));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TCKind kind = TestStruct2Helper.type().kind();
            TEST(kind == TCKind.tk_struct);

            String name = TestStruct2Helper.type().name();
            TEST(name.equals("TestStruct2"));

            long count = TestStruct2Helper.type().member_count();
            TEST(count == 4);

            String name0 = TestStruct2Helper.type().member_name(0);
            TEST(name0.equals("s"));

            TypeCode type0 = TestStruct2Helper.type().member_type(0);
            TEST(type0.equal(TestStruct1Helper.type()));

            String name1 = TestStruct2Helper.type().member_name(1);
            TEST(name1.equals("a"));

            TypeCode type1 = TestStruct2Helper.type().member_type(1);
            TEST(type1.equal(orb.get_primitive_tc(TCKind.tk_any)));

            String name2 = TestStruct2Helper.type().member_name(2);
            TEST(name2.equals("da"));

            TypeCode type2 = TestStruct2Helper.type().member_type(2);
            TEST(type2.equal(DoubleArrayHelper.type()));

            String name3 = TestStruct2Helper.type().member_name(3);
            TEST(name3.equals("sa"));

            TypeCode type3 = TestStruct2Helper.type().member_type(3);
            TEST(type3.equal(orb.create_array_tc(100, orb
                    .get_primitive_tc(TCKind.tk_string))));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TCKind kind = TestStruct3Helper.type().kind();
            TEST(kind == TCKind.tk_struct);

            String name = TestStruct3Helper.type().name();
            TEST(name.equals("TestStruct3"));

            int count = TestStruct3Helper.type().member_count();
            TEST(count == 2);

            String name0 = TestStruct3Helper.type().member_name(0);
            TEST(name0.equals("l"));

            TypeCode type0 = TestStruct3Helper.type().member_type(0);
            TEST(type0.equal(orb.get_primitive_tc(TCKind.tk_long)));

            String name1 = TestStruct3Helper.type().member_name(1);
            TEST(name1.equals("seq"));

            TypeCode type1 = TestStruct3Helper.type().member_type(1);
            TypeCode contentType = type1.content_type();
            TEST(contentType.equal(TestStruct3Helper.type()));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TCKind kind = TestStruct4Helper.type().kind();
            TEST(kind == TCKind.tk_struct);

            String name = TestStruct4Helper.type().name();
            TEST(name.equals("TestStruct4"));

            int count = TestStruct4Helper.type().member_count();
            TEST(count == 2);

            String name0 = TestStruct4Helper.type().member_name(0);
            TEST(name0.equals("a"));

            TypeCode type0 = TestStruct4Helper.type().member_type(0);
            TEST(type0.equal(TestStruct3Helper.type()));

            String name1 = TestStruct4Helper.type().member_name(1);
            TEST(name1.equals("b"));

            TypeCode type1 = TestStruct4Helper.type().member_type(1);
            TypeCode contentType = type1.content_type();
            TEST(contentType.equal(TestStruct3Helper.type()));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TCKind kind = TestUnion4Helper.type().kind();
            TEST(kind == TCKind.tk_union);

            String name = TestUnion4Helper.type().name();
            TEST(name.equals("TestUnion4"));

            int count = TestUnion4Helper.type().member_count();
            TEST(count == 2);

            String name0 = TestUnion4Helper.type().member_name(0);
            TEST(name0.equals("seq"));

            TypeCode type0 = TestUnion4Helper.type().member_type(0);
            TypeCode contentType = type0.content_type();
            TEST(contentType.equal(TestUnion4Helper.type()));

            String name1 = TestUnion4Helper.type().member_name(1);
            TEST(name1.equals("c"));

            TypeCode type1 = TestUnion4Helper.type().member_type(1);
            TEST(type1.equal(orb.get_primitive_tc(TCKind.tk_char)));

            Any label = TestUnion4Helper.type().member_label(1);
            TypeCode labelType = label.type();
            TEST(labelType.equal(org.apache.yoko.orb.OB.TypeCodeFactory
                    .createPrimitiveTC(org.omg.CORBA_2_4.TCKind
                            .from_int(org.omg.CORBA.TCKind._tk_short))));
            short labelValue = label.extract_short();
            TEST(labelValue == 1);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            Any label = TestUnion1Helper.type().member_label(
                    TestUnion1Helper.type().default_index());
            byte defaultValue = label.extract_octet();
            TEST(defaultValue == 0);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            Any label = TestUnion2Helper.type().member_label(0);
            TestEnum enumValue = TestEnumHelper.extract(label);
            TEST(enumValue.value() == TestEnum._A);
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        } catch (org.omg.CORBA.TypeCodePackage.Bounds ex) {
            TEST(false);
        }

        try {
            TypeCode p;

            p = RepositoryIdHelper.type();
            TEST(p.name().equals("RepositoryId"));

            p = ScopedNameHelper.type();
            TEST(p.name().equals("ScopedName"));

            p = IdentifierHelper.type();
            TEST(p.name().equals("Identifier"));

            p = DefinitionKindHelper.type();
            TEST(p.name().equals("DefinitionKind"));

            p = IRObjectHelper.type();
            TEST(p.name().equals("IRObject"));

            p = VersionSpecHelper.type();
            TEST(p.name().equals("VersionSpec"));

            p = ContainedHelper.type();
            TEST(p.name().equals("Contained"));

            p = org.omg.CORBA.ContainedPackage.DescriptionHelper.type();
            TEST(p.name().equals("Description"));

            p = InterfaceDefSeqHelper.type();
            TEST(p.name().equals("InterfaceDefSeq"));

            p = ContainedSeqHelper.type();
            TEST(p.name().equals("ContainedSeq"));

            p = StructMemberHelper.type();
            TEST(p.name().equals("StructMember"));

            p = StructMemberSeqHelper.type();
            TEST(p.name().equals("StructMemberSeq"));

            p = UnionMemberHelper.type();
            TEST(p.name().equals("UnionMember"));

            p = UnionMemberSeqHelper.type();
            TEST(p.name().equals("UnionMemberSeq"));

            p = EnumMemberSeqHelper.type();
            TEST(p.name().equals("EnumMemberSeq"));

            p = ContainerHelper.type();
            TEST(p.name().equals("Container"));

            p = org.omg.CORBA.ContainerPackage.DescriptionHelper.type();
            TEST(p.name().equals("Description"));

            p = org.omg.CORBA.ContainerPackage.DescriptionSeqHelper.type();
            TEST(p.name().equals("DescriptionSeq"));

            p = IDLTypeHelper.type();
            TEST(p.name().equals("IDLType"));

            p = PrimitiveKindHelper.type();
            TEST(p.name().equals("PrimitiveKind"));

            p = RepositoryHelper.type();
            TEST(p.name().equals("Repository"));

            p = ModuleDefHelper.type();
            TEST(p.name().equals("ModuleDef"));

            p = ModuleDescriptionHelper.type();
            TEST(p.name().equals("ModuleDescription"));

            p = ConstantDefHelper.type();
            TEST(p.name().equals("ConstantDef"));

            p = ConstantDescriptionHelper.type();
            TEST(p.name().equals("ConstantDescription"));

            p = TypedefDefHelper.type();
            TEST(p.name().equals("TypedefDef"));

            p = TypeDescriptionHelper.type();
            TEST(p.name().equals("TypeDescription"));

            p = StructDefHelper.type();
            TEST(p.name().equals("StructDef"));

            p = UnionDefHelper.type();
            TEST(p.name().equals("UnionDef"));

            p = EnumDefHelper.type();
            TEST(p.name().equals("EnumDef"));

            p = AliasDefHelper.type();
            TEST(p.name().equals("AliasDef"));

            p = PrimitiveDefHelper.type();
            TEST(p.name().equals("PrimitiveDef"));

            p = StringDefHelper.type();
            TEST(p.name().equals("StringDef"));

            p = SequenceDefHelper.type();
            TEST(p.name().equals("SequenceDef"));

            p = ArrayDefHelper.type();
            TEST(p.name().equals("ArrayDef"));

            p = ExceptionDefHelper.type();
            TEST(p.name().equals("ExceptionDef"));

            p = ExceptionDescriptionHelper.type();
            TEST(p.name().equals("ExceptionDescription"));

            p = AttributeModeHelper.type();
            TEST(p.name().equals("AttributeMode"));

            p = AttributeDefHelper.type();
            TEST(p.name().equals("AttributeDef"));

            p = AttributeDescriptionHelper.type();
            TEST(p.name().equals("AttributeDescription"));

            p = OperationModeHelper.type();
            TEST(p.name().equals("OperationMode"));

            p = ParameterModeHelper.type();
            TEST(p.name().equals("ParameterMode"));

            p = ParameterDescriptionHelper.type();
            TEST(p.name().equals("ParameterDescription"));

            p = ParDescriptionSeqHelper.type();
            TEST(p.name().equals("ParDescriptionSeq"));

            p = ContextIdentifierHelper.type();
            TEST(p.name().equals("ContextIdentifier"));

            p = ContextIdSeqHelper.type();
            TEST(p.name().equals("ContextIdSeq"));

            p = ExceptionDefSeqHelper.type();
            TEST(p.name().equals("ExceptionDefSeq"));

            p = ExcDescriptionSeqHelper.type();
            TEST(p.name().equals("ExcDescriptionSeq"));

            p = OperationDefHelper.type();
            TEST(p.name().equals("OperationDef"));

            p = OperationDescriptionHelper.type();
            TEST(p.name().equals("OperationDescription"));

            p = RepositoryIdSeqHelper.type();
            TEST(p.name().equals("RepositoryIdSeq"));

            p = OpDescriptionSeqHelper.type();
            TEST(p.name().equals("OpDescriptionSeq"));

            p = AttrDescriptionSeqHelper.type();
            TEST(p.name().equals("AttrDescriptionSeq"));

            p = InterfaceDefHelper.type();
            TEST(p.name().equals("InterfaceDef"));

            p = org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescriptionHelper
                    .type();
            TEST(p.name().equals("FullInterfaceDescription"));

            p = InterfaceDescriptionHelper.type();
            TEST(p.name().equals("InterfaceDescription"));
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
            TEST(false);
        }

        //
        // Check repository ID
        //
        {
            String[] bogusIds = { "foo", ":foo", "foo:" };
            for (int i = 0; i < bogusIds.length; i++) {
                try {
                    org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
                    members[0] = new org.omg.CORBA.StructMember();
                    members[0].name = "a";
                    members[0].type = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_struct_tc(bogusIds[i], "foo", members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[1];
                    members[0] = new org.omg.CORBA.UnionMember();
                    members[0].name = "a";
                    members[0].type = tcShort;
                    orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    members[0].label = orb.create_any();
                    members[0].label.insert_short((short) 1);
                    orb.create_union_tc(bogusIds[i], "foo", tcShort, members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_alias_tc(bogusIds[i], "foo", tcShort);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
                    members[0] = new org.omg.CORBA.StructMember();
                    members[0].name = "a";
                    members[0].type = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_exception_tc(bogusIds[i], "foo", members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_interface_tc(bogusIds[i], "foo");
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[0];
                    orb.create_value_tc(bogusIds[i], "foo",
                            org.omg.CORBA.VM_NONE.value, null, members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_value_box_tc(bogusIds[i], "foo", tcShort);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_native_tc(bogusIds[i], "foo");
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_abstract_interface_tc(bogusIds[i], "foo");
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ((org.omg.CORBA_2_4.ORB) orb).create_local_interface_tc(
                            bogusIds[i], "foo");
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }
            }
        }

        //
        // Check IDL name
        //
        {
            String[] bogusNames = { "_foo", "1foo", "f.oo" };
            for (int i = 0; i < bogusNames.length; i++) {
                try {
                    org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
                    members[0] = new org.omg.CORBA.StructMember();
                    members[0].name = "a";
                    members[0].type = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_struct_tc("IDL:foo:1.0", bogusNames[i], members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[1];
                    members[0] = new org.omg.CORBA.UnionMember();
                    members[0].name = "a";
                    members[0].type = tcShort;
                    orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    members[0].label = orb.create_any();
                    members[0].label.insert_short((short) 1);
                    orb.create_union_tc("IDL:foo:1.0", bogusNames[i], tcShort,
                            members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_alias_tc("IDL:foo:1.0", bogusNames[i], tcShort);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
                    members[0] = new org.omg.CORBA.StructMember();
                    members[0].name = "a";
                    members[0].type = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_exception_tc("IDL:foo:1.0", bogusNames[i],
                            members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_interface_tc("IDL:foo:1.0", bogusNames[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[0];
                    orb.create_value_tc("IDL:foo:1.0", bogusNames[i],
                            org.omg.CORBA.VM_NONE.value, null, members);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.TypeCode tcShort = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_value_box_tc("IDL:foo:1.0", bogusNames[i],
                            tcShort);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_native_tc("IDL:foo:1.0", bogusNames[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_abstract_interface_tc("IDL:foo:1.0",
                            bogusNames[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ((org.omg.CORBA_2_4.ORB) orb).create_local_interface_tc(
                            "IDL:foo:1.0", bogusNames[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }
            }
        }

        //
        // Check illegal TypeCodes
        //
        {
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[0];
            org.omg.CORBA.TypeCode exTC = orb.create_exception_tc("IDL:ex:1.0",
                    "ex", members);

            org.omg.CORBA.TypeCode[] types = new org.omg.CORBA.TypeCode[3];
            types[0] = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_null);
            types[1] = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_void);
            types[2] = exTC;

            for (int i = 0; i < types.length; i++) {
                try {
                    org.omg.CORBA.StructMember[] seq = new org.omg.CORBA.StructMember[1];
                    seq[0] = new org.omg.CORBA.StructMember();
                    seq[0].name = "v";
                    seq[0].type = types[i];
                    orb.create_struct_tc("IDL:foo:1.0", "foo", seq);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.UnionMember[] seq = new org.omg.CORBA.UnionMember[1];
                    seq[0] = new org.omg.CORBA.UnionMember();
                    seq[0].name = "v";
                    seq[0].type = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
                    seq[0].label = orb.create_any();
                    seq[0].label.insert_short((short) 1);
                    orb.create_union_tc("IDL:foo:1.0", "foo", types[i], seq);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_PARAM ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.UnionMember[] seq = new org.omg.CORBA.UnionMember[1];
                    seq[0] = new org.omg.CORBA.UnionMember();
                    seq[0].name = "v";
                    seq[0].type = types[i];
                    seq[0].label = orb.create_any();
                    seq[0].label.insert_short((short) 1);
                    org.omg.CORBA.TypeCode disc = orb
                            .get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
                    orb.create_union_tc("IDL:foo:1.0", "foo", disc, seq);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_alias_tc("IDL:foo:1.0", "foo", types[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.StructMember[] seq = new org.omg.CORBA.StructMember[1];
                    seq[0] = new org.omg.CORBA.StructMember();
                    seq[0].name = "v";
                    seq[0].type = types[i];
                    orb.create_exception_tc("IDL:foo:1.0", "foo", seq);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_sequence_tc(0, types[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_array_tc(1, types[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    org.omg.CORBA.ValueMember[] seq = new org.omg.CORBA.ValueMember[1];
                    seq[0] = new org.omg.CORBA.ValueMember();
                    seq[0].name = "v";
                    seq[0].type = types[i];
                    seq[0].access = org.omg.CORBA.PUBLIC_MEMBER.value;
                    orb.create_value_tc("IDL:foo:1.0", "foo",
                            org.omg.CORBA.VM_NONE.value, null, seq);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_value_box_tc("IDL:foo:1.0", "foo", types[i]);
                    TEST(false);
                } catch (org.omg.CORBA.BAD_TYPECODE ex) {
                    // Expected
                }
            }
        }
    }

    public static void main(String args[]) {
        java.util.Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        org.omg.CORBA.ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = org.omg.CORBA.ORB.init(args, props);

            //
            // Run tests
            //
            System.out.print("Testing TypeCode type... ");
            System.out.flush();
            new TestTypeCode(orb);
            System.out.println("Done!");
        } catch (org.omg.CORBA.SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (org.omg.CORBA.SystemException ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}
