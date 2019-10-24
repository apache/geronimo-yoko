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

import org.apache.yoko.orb.OB.TypeCodeFactory;
import org.omg.CORBA.AliasDefHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ArrayDefHelper;
import org.omg.CORBA.AttrDescriptionSeqHelper;
import org.omg.CORBA.AttributeDefHelper;
import org.omg.CORBA.AttributeDescriptionHelper;
import org.omg.CORBA.AttributeModeHelper;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.CompletionStatusHelper;
import org.omg.CORBA.ConstantDefHelper;
import org.omg.CORBA.ConstantDescriptionHelper;
import org.omg.CORBA.ContainedHelper;
import org.omg.CORBA.ContainedSeqHelper;
import org.omg.CORBA.ContainerHelper;
import org.omg.CORBA.ContainerPackage.DescriptionHelper;
import org.omg.CORBA.ContainerPackage.DescriptionSeqHelper;
import org.omg.CORBA.ContextIdSeqHelper;
import org.omg.CORBA.ContextIdentifierHelper;
import org.omg.CORBA.DefinitionKindHelper;
import org.omg.CORBA.EnumDefHelper;
import org.omg.CORBA.EnumMemberSeqHelper;
import org.omg.CORBA.ExcDescriptionSeqHelper;
import org.omg.CORBA.ExceptionDefHelper;
import org.omg.CORBA.ExceptionDefSeqHelper;
import org.omg.CORBA.ExceptionDescriptionHelper;
import org.omg.CORBA.IDLTypeHelper;
import org.omg.CORBA.IRObjectHelper;
import org.omg.CORBA.IdentifierHelper;
import org.omg.CORBA.InterfaceDefHelper;
import org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescriptionHelper;
import org.omg.CORBA.InterfaceDefSeqHelper;
import org.omg.CORBA.InterfaceDescriptionHelper;
import org.omg.CORBA.ModuleDefHelper;
import org.omg.CORBA.ModuleDescriptionHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OpDescriptionSeqHelper;
import org.omg.CORBA.OperationDefHelper;
import org.omg.CORBA.OperationDescriptionHelper;
import org.omg.CORBA.OperationModeHelper;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.omg.CORBA.ParDescriptionSeqHelper;
import org.omg.CORBA.ParameterDescriptionHelper;
import org.omg.CORBA.ParameterModeHelper;
import org.omg.CORBA.PrimitiveDefHelper;
import org.omg.CORBA.PrimitiveKindHelper;
import org.omg.CORBA.RepositoryHelper;
import org.omg.CORBA.RepositoryIdHelper;
import org.omg.CORBA.RepositoryIdSeqHelper;
import org.omg.CORBA.ScopedNameHelper;
import org.omg.CORBA.SequenceDefHelper;
import org.omg.CORBA.StringDefHelper;
import org.omg.CORBA.StructDefHelper;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.StructMemberHelper;
import org.omg.CORBA.StructMemberSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.BadKindHelper;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.TypeCodePackage.BoundsHelper;
import org.omg.CORBA.TypeDescriptionHelper;
import org.omg.CORBA.TypedefDefHelper;
import org.omg.CORBA.UnionDefHelper;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.UnionMemberHelper;
import org.omg.CORBA.UnionMemberSeqHelper;
import org.omg.CORBA.VM_NONE;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.VersionSpecHelper;
import test.common.TestBase;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestTypeCode extends TestBase {
    private static final boolean CHECK_IDL_NAMES = false;
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
            assertSame(kind, TCKind.tk_enum);

            String name = CompletionStatusHelper.type().name();
            assertTrue(name.equals("completion_status")
			|| name.equals("CompletionStatus"));

            String id = CompletionStatusHelper.type().id();
            assertTrue(id.equals("IDL:omg.org/CORBA/completion_status:1.0")
			|| id.equals("IDL:omg.org/CORBA/CompletionStatus:1.0"));

            int count = CompletionStatusHelper.type().member_count();
            assertEquals(3, count);

            String name0 = CompletionStatusHelper.type().member_name(0);
            assertEquals("COMPLETED_YES", name0);

            String name1 = CompletionStatusHelper.type().member_name(1);
            assertEquals("COMPLETED_NO", name1);

            String name2 = CompletionStatusHelper.type().member_name(2);
            assertEquals("COMPLETED_MAYBE", name2);
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TypeCode type = BoundsHelper.type();

            TCKind kind = type.kind();
            assertSame(kind, TCKind.tk_except);

            String name = type.name();
            assertEquals("Bounds", name);

            String id = type.id();
            assertEquals("IDL:omg.org/CORBA/TypeCode/Bounds:1.0", id);

            int count = type.member_count();
            assertEquals(0, count);
        } catch (BadKind ex) {
            fail();
        }

        try {
            TypeCode type = BadKindHelper.type();

            TCKind kind = type.kind();
            assertSame(kind, TCKind.tk_except);

            String name = type.name();
            assertEquals("BadKind", name);

            String id = type.id();
            assertEquals("IDL:omg.org/CORBA/TypeCode/BadKind:1.0", id);

            int count = type.member_count();
            assertEquals(0, count);
        } catch (BadKind ex) {
            fail();
        }

        try {
            TCKind kind = TestStruct1Helper.type().kind();
            assertSame(kind, TCKind.tk_struct);

            String name = TestStruct1Helper.type().name();
            assertEquals("TestStruct1", name);

            long count = TestStruct1Helper.type().member_count();
            assertEquals(7, count);

            String name0 = TestStruct1Helper.type().member_name(0);
            assertEquals("s", name0);

            String name1 = TestStruct1Helper.type().member_name(1);
            assertEquals("l", name1);

            String name2 = TestStruct1Helper.type().member_name(2);
            assertEquals("d", name2);

            String name3 = TestStruct1Helper.type().member_name(3);
            assertEquals("b", name3);

            String name4 = TestStruct1Helper.type().member_name(4);
            assertEquals("c", name4);

            String name5 = TestStruct1Helper.type().member_name(5);
            assertEquals("o", name5);

            String name6 = TestStruct1Helper.type().member_name(6);
            assertEquals("str", name6);

            TypeCode type0 = TestStruct1Helper.type().member_type(0);
            assertTrue(type0.equal(orb.get_primitive_tc(TCKind.tk_short)));

            TypeCode type1 = TestStruct1Helper.type().member_type(1);
            assertTrue(type1.equal(orb.get_primitive_tc(TCKind.tk_long)));

            TypeCode type2 = TestStruct1Helper.type().member_type(2);
            assertTrue(type2.equal(orb.get_primitive_tc(TCKind.tk_double)));

            TypeCode type3 = TestStruct1Helper.type().member_type(3);
            assertTrue(type3.equal(orb.get_primitive_tc(TCKind.tk_boolean)));

            TypeCode type4 = TestStruct1Helper.type().member_type(4);
            assertTrue(type4.equal(orb.get_primitive_tc(TCKind.tk_char)));

            TypeCode type5 = TestStruct1Helper.type().member_type(5);
            assertTrue(type5.equal(orb.get_primitive_tc(TCKind.tk_octet)));

            TypeCode type6 = TestStruct1Helper.type().member_type(6);
            assertTrue(type6.equal(orb.get_primitive_tc(TCKind.tk_string)));
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TCKind kind = TestStruct2Helper.type().kind();
            assertSame(kind, TCKind.tk_struct);

            String name = TestStruct2Helper.type().name();
            assertEquals("TestStruct2", name);

            long count = TestStruct2Helper.type().member_count();
            assertEquals(4, count);

            String name0 = TestStruct2Helper.type().member_name(0);
            assertEquals("s", name0);

            TypeCode type0 = TestStruct2Helper.type().member_type(0);
            assertTrue(type0.equal(TestStruct1Helper.type()));

            String name1 = TestStruct2Helper.type().member_name(1);
            assertEquals("a", name1);

            TypeCode type1 = TestStruct2Helper.type().member_type(1);
            assertTrue(type1.equal(orb.get_primitive_tc(TCKind.tk_any)));

            String name2 = TestStruct2Helper.type().member_name(2);
            assertEquals("da", name2);

            TypeCode type2 = TestStruct2Helper.type().member_type(2);
            assertTrue(type2.equal(DoubleArrayHelper.type()));

            String name3 = TestStruct2Helper.type().member_name(3);
            assertEquals("sa", name3);

            TypeCode type3 = TestStruct2Helper.type().member_type(3);
            assertTrue(type3.equal(orb.create_array_tc(100, orb
			.get_primitive_tc(TCKind.tk_string))));
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TCKind kind = TestStruct3Helper.type().kind();
            assertSame(kind, TCKind.tk_struct);

            String name = TestStruct3Helper.type().name();
            assertEquals("TestStruct3", name);

            int count = TestStruct3Helper.type().member_count();
            assertEquals(2, count);

            String name0 = TestStruct3Helper.type().member_name(0);
            assertEquals("l", name0);

            TypeCode type0 = TestStruct3Helper.type().member_type(0);
            assertTrue(type0.equal(orb.get_primitive_tc(TCKind.tk_long)));

            String name1 = TestStruct3Helper.type().member_name(1);
            assertEquals("seq", name1);

            TypeCode type1 = TestStruct3Helper.type().member_type(1);
            TypeCode contentType = type1.content_type();
            assertTrue(contentType.equal(TestStruct3Helper.type()));
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TCKind kind = TestStruct4Helper.type().kind();
            assertSame(kind, TCKind.tk_struct);

            String name = TestStruct4Helper.type().name();
            assertEquals("TestStruct4", name);

            int count = TestStruct4Helper.type().member_count();
            assertEquals(2, count);

            String name0 = TestStruct4Helper.type().member_name(0);
            assertEquals("a", name0);

            TypeCode type0 = TestStruct4Helper.type().member_type(0);
            assertTrue(type0.equal(TestStruct3Helper.type()));

            String name1 = TestStruct4Helper.type().member_name(1);
            assertEquals("b", name1);

            TypeCode type1 = TestStruct4Helper.type().member_type(1);
            TypeCode contentType = type1.content_type();
            assertTrue(contentType.equal(TestStruct3Helper.type()));
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TCKind kind = TestUnion4Helper.type().kind();
            assertSame(kind, TCKind.tk_union);

            String name = TestUnion4Helper.type().name();
            assertEquals("TestUnion4", name);

            int count = TestUnion4Helper.type().member_count();
            assertEquals(2, count);

            String name0 = TestUnion4Helper.type().member_name(0);
            assertEquals("seq", name0);

            TypeCode type0 = TestUnion4Helper.type().member_type(0);
            TypeCode contentType = type0.content_type();
            assertTrue(contentType.equal(TestUnion4Helper.type()));

            String name1 = TestUnion4Helper.type().member_name(1);
            assertEquals("c", name1);

            TypeCode type1 = TestUnion4Helper.type().member_type(1);
            assertTrue(type1.equal(orb.get_primitive_tc(TCKind.tk_char)));

            Any label = TestUnion4Helper.type().member_label(1);
            TypeCode labelType = label.type();
            assertTrue(labelType.equal(TypeCodeFactory
			.createPrimitiveTC(org.omg.CORBA_2_4.TCKind
			        .from_int(TCKind._tk_short))));
            short labelValue = label.extract_short();
            assertEquals(1, labelValue);
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            Any label = TestUnion1Helper.type().member_label(
                    TestUnion1Helper.type().default_index());
            byte defaultValue = label.extract_octet();
            assertEquals(0, defaultValue);
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            Any label = TestUnion2Helper.type().member_label(0);
            TestEnum enumValue = TestEnumHelper.extract(label);
            assertEquals(enumValue.value(), TestEnum._A);
        } catch (BadKind | Bounds ex) {
            fail();
        }

        try {
            TypeCode p;

            p = RepositoryIdHelper.type();
            assertEquals("RepositoryId", p.name());

            p = ScopedNameHelper.type();
            assertEquals("ScopedName", p.name());

            p = IdentifierHelper.type();
            assertEquals("Identifier", p.name());

            p = DefinitionKindHelper.type();
            assertEquals("DefinitionKind", p.name());

            p = IRObjectHelper.type();
            assertEquals("IRObject", p.name());

            p = VersionSpecHelper.type();
            assertEquals("VersionSpec", p.name());

            p = ContainedHelper.type();
            assertEquals("Contained", p.name());

            p = org.omg.CORBA.ContainedPackage.DescriptionHelper.type();
            assertEquals("Description", p.name());

            p = InterfaceDefSeqHelper.type();
            assertEquals("InterfaceDefSeq", p.name());

            p = ContainedSeqHelper.type();
            assertEquals("ContainedSeq", p.name());

            p = StructMemberHelper.type();
            assertEquals("StructMember", p.name());

            p = StructMemberSeqHelper.type();
            assertEquals("StructMemberSeq", p.name());

            p = UnionMemberHelper.type();
            assertEquals("UnionMember", p.name());

            p = UnionMemberSeqHelper.type();
            assertEquals("UnionMemberSeq", p.name());

            p = EnumMemberSeqHelper.type();
            assertEquals("EnumMemberSeq", p.name());

            p = ContainerHelper.type();
            assertEquals("Container", p.name());

            p = DescriptionHelper.type();
            assertEquals("Description", p.name());

            p = DescriptionSeqHelper.type();
            assertEquals("DescriptionSeq", p.name());

            p = IDLTypeHelper.type();
            assertEquals("IDLType", p.name());

            p = PrimitiveKindHelper.type();
            assertEquals("PrimitiveKind", p.name());

            p = RepositoryHelper.type();
            assertEquals("Repository", p.name());

            p = ModuleDefHelper.type();
            assertEquals("ModuleDef", p.name());

            p = ModuleDescriptionHelper.type();
            assertEquals("ModuleDescription", p.name());

            p = ConstantDefHelper.type();
            assertEquals("ConstantDef", p.name());

            p = ConstantDescriptionHelper.type();
            assertEquals("ConstantDescription", p.name());

            p = TypedefDefHelper.type();
            assertEquals("TypedefDef", p.name());

            p = TypeDescriptionHelper.type();
            assertEquals("TypeDescription", p.name());

            p = StructDefHelper.type();
            assertEquals("StructDef", p.name());

            p = UnionDefHelper.type();
            assertEquals("UnionDef", p.name());

            p = EnumDefHelper.type();
            assertEquals("EnumDef", p.name());

            p = AliasDefHelper.type();
            assertEquals("AliasDef", p.name());

            p = PrimitiveDefHelper.type();
            assertEquals("PrimitiveDef", p.name());

            p = StringDefHelper.type();
            assertEquals("StringDef", p.name());

            p = SequenceDefHelper.type();
            assertEquals("SequenceDef", p.name());

            p = ArrayDefHelper.type();
            assertEquals("ArrayDef", p.name());

            p = ExceptionDefHelper.type();
            assertEquals("ExceptionDef", p.name());

            p = ExceptionDescriptionHelper.type();
            assertEquals("ExceptionDescription", p.name());

            p = AttributeModeHelper.type();
            assertEquals("AttributeMode", p.name());

            p = AttributeDefHelper.type();
            assertEquals("AttributeDef", p.name());

            p = AttributeDescriptionHelper.type();
            assertEquals("AttributeDescription", p.name());

            p = OperationModeHelper.type();
            assertEquals("OperationMode", p.name());

            p = ParameterModeHelper.type();
            assertEquals("ParameterMode", p.name());

            p = ParameterDescriptionHelper.type();
            assertEquals("ParameterDescription", p.name());

            p = ParDescriptionSeqHelper.type();
            assertEquals("ParDescriptionSeq", p.name());

            p = ContextIdentifierHelper.type();
            assertEquals("ContextIdentifier", p.name());

            p = ContextIdSeqHelper.type();
            assertEquals("ContextIdSeq", p.name());

            p = ExceptionDefSeqHelper.type();
            assertEquals("ExceptionDefSeq", p.name());

            p = ExcDescriptionSeqHelper.type();
            assertEquals("ExcDescriptionSeq", p.name());

            p = OperationDefHelper.type();
            assertEquals("OperationDef", p.name());

            p = OperationDescriptionHelper.type();
            assertEquals("OperationDescription", p.name());

            p = RepositoryIdSeqHelper.type();
            assertEquals("RepositoryIdSeq", p.name());

            p = OpDescriptionSeqHelper.type();
            assertEquals("OpDescriptionSeq", p.name());

            p = AttrDescriptionSeqHelper.type();
            assertEquals("AttrDescriptionSeq", p.name());

            p = InterfaceDefHelper.type();
            assertEquals("InterfaceDef", p.name());

            p = FullInterfaceDescriptionHelper
                    .type();
            assertEquals("FullInterfaceDescription", p.name());

            p = InterfaceDescriptionHelper.type();
            assertEquals("InterfaceDescription", p.name());
        } catch (BadKind ex) {
            fail();
        }

        //
        // Check repository ID
        //
        {
            String[] bogusIds = { "foo", ":foo", "foo:" };
            for (String bogusId : bogusIds) {
                try {
                    StructMember[] members = new StructMember[1];
                    members[0] = new StructMember();
                    members[0].name = "a";
                    members[0].type = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_struct_tc(bogusId, "foo", members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    UnionMember[] members = new UnionMember[1];
                    members[0] = new UnionMember();
                    members[0].name = "a";
                    members[0].type = tcShort;
                    orb.get_primitive_tc(TCKind.tk_short);
                    members[0].label = orb.create_any();
                    members[0].label.insert_short((short) 1);
                    orb.create_union_tc(bogusId, "foo", tcShort, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_alias_tc(bogusId, "foo", tcShort);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    StructMember[] members = new StructMember[1];
                    members[0] = new StructMember();
                    members[0].name = "a";
                    members[0].type = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_exception_tc(bogusId, "foo", members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_interface_tc(bogusId, "foo");
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ValueMember[] members = new ValueMember[0];
                    orb.create_value_tc(bogusId, "foo", VM_NONE.value, null, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_value_box_tc(bogusId, "foo", tcShort);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_native_tc(bogusId, "foo");
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_abstract_interface_tc(bogusId, "foo");
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ((org.omg.CORBA_2_4.ORB) orb).create_local_interface_tc(bogusId, "foo");
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }
            }
        }

        //
        // Check IDL name
        //
        if (CHECK_IDL_NAMES) {
            String[] bogusNames = { "_foo", "1foo", "f.oo" };
            for (String bogusName : bogusNames) {
                try {
                    StructMember[] members = new StructMember[1];
                    members[0] = new StructMember();
                    members[0].name = "a";
                    members[0].type = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_struct_tc("IDL:foo:1.0", bogusName, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    UnionMember[] members = new UnionMember[1];
                    members[0] = new UnionMember();
                    members[0].name = "a";
                    members[0].type = tcShort;
                    orb.get_primitive_tc(TCKind.tk_short);
                    members[0].label = orb.create_any();
                    members[0].label.insert_short((short) 1);
                    orb.create_union_tc("IDL:foo:1.0", bogusName, tcShort, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_alias_tc("IDL:foo:1.0", bogusName, tcShort);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    StructMember[] members = new StructMember[1];
                    members[0] = new StructMember();
                    members[0].name = "a";
                    members[0].type = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_exception_tc("IDL:foo:1.0", bogusName, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_interface_tc("IDL:foo:1.0", bogusName);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ValueMember[] members = new ValueMember[0];
                    orb.create_value_tc("IDL:foo:1.0", bogusName, VM_NONE.value, null, members);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    TypeCode tcShort = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_value_box_tc("IDL:foo:1.0", bogusName, tcShort);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_native_tc("IDL:foo:1.0", bogusName);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    orb.create_abstract_interface_tc("IDL:foo:1.0", bogusName);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    ((org.omg.CORBA_2_4.ORB) orb).create_local_interface_tc("IDL:foo:1.0", bogusName);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }
            }
        }

        //
        // Check illegal TypeCodes
        //
        {
            StructMember[] members = new StructMember[0];
            TypeCode exTC = orb.create_exception_tc("IDL:ex:1.0",
                    "ex", members);

            TypeCode[] types = new TypeCode[3];
            types[0] = orb.get_primitive_tc(TCKind.tk_null);
            types[1] = orb.get_primitive_tc(TCKind.tk_void);
            types[2] = exTC;

            for (TypeCode type : types) {
                try {
                    StructMember[] seq = new StructMember[1];
                    seq[0] = new StructMember();
                    seq[0].name = "v";
                    seq[0].type = type;
                    orb.create_struct_tc("IDL:foo:1.0", "foo", seq);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    UnionMember[] seq = new UnionMember[1];
                    seq[0] = new UnionMember();
                    seq[0].name = "v";
                    seq[0].type = orb.get_primitive_tc(TCKind.tk_long);
                    seq[0].label = orb.create_any();
                    seq[0].label.insert_short((short) 1);
                    orb.create_union_tc("IDL:foo:1.0", "foo", type, seq);
                    fail();
                } catch (BAD_PARAM ex) {
                    // Expected
                }

                try {
                    UnionMember[] seq = new UnionMember[1];
                    seq[0] = new UnionMember();
                    seq[0].name = "v";
                    seq[0].type = type;
                    seq[0].label = orb.create_any();
                    seq[0].label.insert_short((short) 1);
                    TypeCode disc = orb.get_primitive_tc(TCKind.tk_short);
                    orb.create_union_tc("IDL:foo:1.0", "foo", disc, seq);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_alias_tc("IDL:foo:1.0", "foo", type);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    StructMember[] seq = new StructMember[1];
                    seq[0] = new StructMember();
                    seq[0].name = "v";
                    seq[0].type = type;
                    orb.create_exception_tc("IDL:foo:1.0", "foo", seq);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_sequence_tc(0, type);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_array_tc(1, type);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    ValueMember[] seq = new ValueMember[1];
                    seq[0] = new ValueMember();
                    seq[0].name = "v";
                    seq[0].type = type;
                    seq[0].access = PUBLIC_MEMBER.value;
                    orb.create_value_tc("IDL:foo:1.0", "foo", VM_NONE.value, null, seq);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }

                try {
                    orb.create_value_box_tc("IDL:foo:1.0", "foo", type);
                    fail();
                } catch (BAD_TYPECODE ex) {
                    // Expected
                }
            }
        }
    }

    public static void main(String[] args) {
        Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            //
            // Run tests
            //
            System.out.print("Testing TypeCode type... ");
            System.out.flush();
            new TestTypeCode(orb);
            System.out.println("Done!");
        } finally {
            if (orb != null) orb.destroy();
        }

    }
}
