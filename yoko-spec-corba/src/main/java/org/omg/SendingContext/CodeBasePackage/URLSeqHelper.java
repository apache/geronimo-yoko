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

public class URLSeqHelper {
	private static org.omg.CORBA.TypeCode _type = org.omg.CORBA.ORB
			.init()
			.create_alias_tc(
					org.omg.SendingContext.CodeBasePackage.URLSeqHelper.id(),
					"URLSeq",
					org.omg.CORBA.ORB
							.init()
							.create_sequence_tc(
									0,
									org.omg.CORBA.ORB
											.init()
											.create_alias_tc(
													org.omg.SendingContext.CodeBasePackage.URLHelper
															.id(),
													"URL",
													org.omg.CORBA.ORB
															.init()
															.create_string_tc(0))));

	public URLSeqHelper() {
	}

	public static void insert(org.omg.CORBA.Any any, java.lang.String[] s) {
		any.type(type());
		write(any.create_output_stream(), s);
	}

	public static java.lang.String[] extract(org.omg.CORBA.Any any) {
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
		return "IDL:omg.org/SendingContext/CodeBase/URLSeq:1.0";
	}

	public static java.lang.String[] read(org.omg.CORBA.portable.InputStream _in) {
		java.lang.String[] _result;
		int _l_result = _in.read_long();
		_result = new java.lang.String[_l_result];
		for (int i = 0; i < _result.length; i++) {
			_result[i] = org.omg.SendingContext.CodeBasePackage.URLHelper
					.read(_in);
		}

		return _result;
	}

	public static void write(org.omg.CORBA.portable.OutputStream _out,
			java.lang.String[] _s) {

		_out.write_long(_s.length);
		for (int i = 0; i < _s.length; i++) {
			org.omg.SendingContext.CodeBasePackage.URLHelper.write(_out, _s[i]);
		}

	}
}
