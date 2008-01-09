/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.omg.SendingContext.CodeBasePackage;

public class ValueDescSeqHelper {
	private static org.omg.CORBA.TypeCode _type = org.omg.CORBA.ORB
			.init()
			.create_alias_tc(
					org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper
							.id(),
					"ValueDescSeq",
					org.omg.CORBA.ORB
							.init()
							.create_sequence_tc(
									0,
									org.omg.CORBA.ORB
											.init()
											.create_struct_tc(
													org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper
															.id(),
													"FullValueDescription",
													new org.omg.CORBA.StructMember[] {
															new org.omg.CORBA.StructMember(
																	"name",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.IdentifierHelper
																							.id(),
																					"Identifier",
																					org.omg.CORBA.ORB
																							.init()
																							.create_string_tc(
																									0)),
																	null),
															new org.omg.CORBA.StructMember(
																	"id",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.RepositoryIdHelper
																							.id(),
																					"RepositoryId",
																					org.omg.CORBA.ORB
																							.init()
																							.create_string_tc(
																									0)),
																	null),
															new org.omg.CORBA.StructMember(
																	"is_abstract",
																	org.omg.CORBA.ORB
																			.init()
																			.get_primitive_tc(
																					org.omg.CORBA.TCKind
																							.from_int(8)),
																	null),
															new org.omg.CORBA.StructMember(
																	"is_custom",
																	org.omg.CORBA.ORB
																			.init()
																			.get_primitive_tc(
																					org.omg.CORBA.TCKind
																							.from_int(8)),
																	null),
															new org.omg.CORBA.StructMember(
																	"defined_in",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.RepositoryIdHelper
																							.id(),
																					"RepositoryId",
																					org.omg.CORBA.ORB
																							.init()
																							.create_string_tc(
																									0)),
																	null),
															new org.omg.CORBA.StructMember(
																	"version",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.VersionSpecHelper
																							.id(),
																					"VersionSpec",
																					org.omg.CORBA.ORB
																							.init()
																							.create_string_tc(
																									0)),
																	null),
															new org.omg.CORBA.StructMember(
																	"operations",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.OpDescriptionSeqHelper
																							.id(),
																					"OpDescriptionSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_struct_tc(
																													org.omg.CORBA.OperationDescriptionHelper
																															.id(),
																													"OperationDescription",
																													new org.omg.CORBA.StructMember[] {
																															new org.omg.CORBA.StructMember(
																																	"name",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.IdentifierHelper
																																							.id(),
																																					"Identifier",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"id",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"defined_in",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"version",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.VersionSpecHelper
																																							.id(),
																																					"VersionSpec",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"result",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.get_primitive_tc(
																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"mode",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_enum_tc(
																																					org.omg.CORBA.OperationModeHelper
																																							.id(),
																																					"OperationMode",
																																					new String[] {
																																							"OP_NORMAL",
																																							"OP_ONEWAY" }),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"contexts",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.ContextIdSeqHelper
																																							.id(),
																																					"ContextIdSeq",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_sequence_tc(
																																									0,
																																									org.omg.CORBA.ORB
																																											.init()
																																											.create_alias_tc(
																																													org.omg.CORBA.ContextIdentifierHelper
																																															.id(),
																																													"ContextIdentifier",
																																													org.omg.CORBA.ORB
																																															.init()
																																															.create_alias_tc(
																																																	org.omg.CORBA.IdentifierHelper
																																																			.id(),
																																																	"Identifier",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_string_tc(
																																																					0))))),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"parameters",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.ParDescriptionSeqHelper
																																							.id(),
																																					"ParDescriptionSeq",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_sequence_tc(
																																									0,
																																									org.omg.CORBA.ORB
																																											.init()
																																											.create_struct_tc(
																																													org.omg.CORBA.ParameterDescriptionHelper
																																															.id(),
																																													"ParameterDescription",
																																													new org.omg.CORBA.StructMember[] {
																																															new org.omg.CORBA.StructMember(
																																																	"name",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.IdentifierHelper
																																																							.id(),
																																																					"Identifier",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"type",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.get_primitive_tc(
																																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"type_def",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_interface_tc(
																																																					"IDL:omg.org/CORBA/IDLType:1.0",
																																																					"IDLType"),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"mode",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_enum_tc(
																																																					org.omg.CORBA.ParameterModeHelper
																																																							.id(),
																																																					"ParameterMode",
																																																					new String[] {
																																																							"PARAM_IN",
																																																							"PARAM_OUT",
																																																							"PARAM_INOUT" }),
																																																	null) }))),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"exceptions",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.ExcDescriptionSeqHelper
																																							.id(),
																																					"ExcDescriptionSeq",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_sequence_tc(
																																									0,
																																									org.omg.CORBA.ORB
																																											.init()
																																											.create_struct_tc(
																																													org.omg.CORBA.ExceptionDescriptionHelper
																																															.id(),
																																													"ExceptionDescription",
																																													new org.omg.CORBA.StructMember[] {
																																															new org.omg.CORBA.StructMember(
																																																	"name",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.IdentifierHelper
																																																							.id(),
																																																					"Identifier",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"id",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.RepositoryIdHelper
																																																							.id(),
																																																					"RepositoryId",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"defined_in",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.RepositoryIdHelper
																																																							.id(),
																																																					"RepositoryId",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"version",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.VersionSpecHelper
																																																							.id(),
																																																					"VersionSpec",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"type",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.get_primitive_tc(
																																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																																	null) }))),
																																	null) }))),
																	null),
															new org.omg.CORBA.StructMember(
																	"attributes",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.AttrDescriptionSeqHelper
																							.id(),
																					"AttrDescriptionSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_struct_tc(
																													org.omg.CORBA.AttributeDescriptionHelper
																															.id(),
																													"AttributeDescription",
																													new org.omg.CORBA.StructMember[] {
																															new org.omg.CORBA.StructMember(
																																	"name",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.IdentifierHelper
																																							.id(),
																																					"Identifier",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"id",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"defined_in",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"version",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.VersionSpecHelper
																																							.id(),
																																					"VersionSpec",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"type",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.get_primitive_tc(
																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"mode",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_enum_tc(
																																					org.omg.CORBA.AttributeModeHelper
																																							.id(),
																																					"AttributeMode",
																																					new String[] {
																																							"ATTR_NORMAL",
																																							"ATTR_READONLY" }),
																																	null) }))),
																	null),
															new org.omg.CORBA.StructMember(
																	"members",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.ValueMemberSeqHelper
																							.id(),
																					"ValueMemberSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_struct_tc(
																													org.omg.CORBA.ValueMemberHelper
																															.id(),
																													"ValueMember",
																													new org.omg.CORBA.StructMember[] {
																															new org.omg.CORBA.StructMember(
																																	"name",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.IdentifierHelper
																																							.id(),
																																					"Identifier",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"id",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"defined_in",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.RepositoryIdHelper
																																							.id(),
																																					"RepositoryId",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"version",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.VersionSpecHelper
																																							.id(),
																																					"VersionSpec",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"type",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.get_primitive_tc(
																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"type_def",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_interface_tc(
																																					"IDL:omg.org/CORBA/IDLType:1.0",
																																					"IDLType"),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"access",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.VisibilityHelper
																																							.id(),
																																					"Visibility",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.get_primitive_tc(
																																									org.omg.CORBA.TCKind
																																											.from_int(2))),
																																	null) }))),
																	null),
															new org.omg.CORBA.StructMember(
																	"initializers",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.InitializerSeqHelper
																							.id(),
																					"InitializerSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_struct_tc(
																													org.omg.CORBA.InitializerHelper
																															.id(),
																													"Initializer",
																													new org.omg.CORBA.StructMember[] {
																															new org.omg.CORBA.StructMember(
																																	"members",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.StructMemberSeqHelper
																																							.id(),
																																					"StructMemberSeq",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_sequence_tc(
																																									0,
																																									org.omg.CORBA.ORB
																																											.init()
																																											.create_struct_tc(
																																													org.omg.CORBA.StructMemberHelper
																																															.id(),
																																													"StructMember",
																																													new org.omg.CORBA.StructMember[] {
																																															new org.omg.CORBA.StructMember(
																																																	"name",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_alias_tc(
																																																					org.omg.CORBA.IdentifierHelper
																																																							.id(),
																																																					"Identifier",
																																																					org.omg.CORBA.ORB
																																																							.init()
																																																							.create_string_tc(
																																																									0)),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"type",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.get_primitive_tc(
																																																					org.omg.CORBA.TCKind.tk_TypeCode),
																																																	null),
																																															new org.omg.CORBA.StructMember(
																																																	"type_def",
																																																	org.omg.CORBA.ORB
																																																			.init()
																																																			.create_interface_tc(
																																																					"IDL:omg.org/CORBA/IDLType:1.0",
																																																					"IDLType"),
																																																	null) }))),
																																	null),
																															new org.omg.CORBA.StructMember(
																																	"name",
																																	org.omg.CORBA.ORB
																																			.init()
																																			.create_alias_tc(
																																					org.omg.CORBA.IdentifierHelper
																																							.id(),
																																					"Identifier",
																																					org.omg.CORBA.ORB
																																							.init()
																																							.create_string_tc(
																																									0)),
																																	null) }))),
																	null),
															new org.omg.CORBA.StructMember(
																	"supported_interfaces",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.RepositoryIdSeqHelper
																							.id(),
																					"RepositoryIdSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_alias_tc(
																													org.omg.CORBA.RepositoryIdHelper
																															.id(),
																													"RepositoryId",
																													org.omg.CORBA.ORB
																															.init()
																															.create_string_tc(
																																	0)))),
																	null),
															new org.omg.CORBA.StructMember(
																	"abstract_base_values",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.RepositoryIdSeqHelper
																							.id(),
																					"RepositoryIdSeq",
																					org.omg.CORBA.ORB
																							.init()
																							.create_sequence_tc(
																									0,
																									org.omg.CORBA.ORB
																											.init()
																											.create_alias_tc(
																													org.omg.CORBA.RepositoryIdHelper
																															.id(),
																													"RepositoryId",
																													org.omg.CORBA.ORB
																															.init()
																															.create_string_tc(
																																	0)))),
																	null),
															new org.omg.CORBA.StructMember(
																	"is_truncatable",
																	org.omg.CORBA.ORB
																			.init()
																			.get_primitive_tc(
																					org.omg.CORBA.TCKind
																							.from_int(8)),
																	null),
															new org.omg.CORBA.StructMember(
																	"base_value",
																	org.omg.CORBA.ORB
																			.init()
																			.create_alias_tc(
																					org.omg.CORBA.RepositoryIdHelper
																							.id(),
																					"RepositoryId",
																					org.omg.CORBA.ORB
																							.init()
																							.create_string_tc(
																									0)),
																	null),
															new org.omg.CORBA.StructMember(
																	"type",
																	org.omg.CORBA.ORB
																			.init()
																			.get_primitive_tc(
																					org.omg.CORBA.TCKind.tk_TypeCode),
																	null) })));

	public ValueDescSeqHelper() {
	}

	public static void insert(org.omg.CORBA.Any any,
			org.omg.CORBA.ValueDefPackage.FullValueDescription[] s) {
		any.type(type());
		write(any.create_output_stream(), s);
	}

	public static org.omg.CORBA.ValueDefPackage.FullValueDescription[] extract(
			org.omg.CORBA.Any any) {
		return read(any.create_input_stream());
	}

	public static org.omg.CORBA.TypeCode type() {
		return _type;
	}

	public String get_id() {
		return id();
	}

	public org.omg.CORBA.TypeCode get_type() {
		return type();
	}

	public void write_Object(org.omg.CORBA.portable.OutputStream out,
			java.lang.Object obj) {
		throw new RuntimeException(" not implemented");
	}

	public java.lang.Object read_Object(org.omg.CORBA.portable.InputStream in) {
		throw new RuntimeException(" not implemented");
	}

	public static String id() {
		return "IDL:omg.org/SendingContext/CodeBase/ValueDescSeq:1.0";
	}

	public static org.omg.CORBA.ValueDefPackage.FullValueDescription[] read(
			org.omg.CORBA.portable.InputStream _in) {
		org.omg.CORBA.ValueDefPackage.FullValueDescription[] _result;
		int _l_result = _in.read_long();
		_result = new org.omg.CORBA.ValueDefPackage.FullValueDescription[_l_result];
		for (int i = 0; i < _result.length; i++) {
			_result[i] = org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper
					.read(_in);
		}

		return _result;
	}

	public static void write(org.omg.CORBA.portable.OutputStream _out,
			org.omg.CORBA.ValueDefPackage.FullValueDescription[] _s) {

		_out.write_long(_s.length);
		for (int i = 0; i < _s.length; i++) {
			org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper.write(
					_out, _s[i]);
		}

	}
}
