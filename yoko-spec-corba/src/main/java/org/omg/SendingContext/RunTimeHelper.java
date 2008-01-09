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

package org.omg.SendingContext;

public class RunTimeHelper {
	public RunTimeHelper() {
	}

	public static void insert(org.omg.CORBA.Any any,
			org.omg.SendingContext.RunTime s) {
		any.insert_Object(s);
	}

	public static org.omg.SendingContext.RunTime extract(org.omg.CORBA.Any any) {
		return narrow(any.extract_Object());
	}

	public static org.omg.CORBA.TypeCode type() {
		return org.omg.CORBA.ORB.init().create_interface_tc(
				"IDL:omg.org/SendingContext/RunTime:1.0", "RunTime");
	}

	public static String id() {
		return "IDL:omg.org/SendingContext/RunTime:1.0";
	}

	public static RunTime read(org.omg.CORBA.portable.InputStream in) {
		return narrow(in.read_Object());
	}

	public static void write(org.omg.CORBA.portable.OutputStream _out,
			org.omg.SendingContext.RunTime s) {
		_out.write_Object(s);
	}

	public static org.omg.SendingContext.RunTime narrow(org.omg.CORBA.Object obj) {
		if (obj == null)
			return null;
		try {
			return (org.omg.SendingContext.RunTime) obj;
		} catch (ClassCastException c) {
			if (obj._is_a("IDL:omg.org/SendingContext/RunTime:1.0")) {
				org.omg.SendingContext._RunTimeStub stub;
				stub = new org.omg.SendingContext._RunTimeStub();
				stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl) obj)
						._get_delegate());
				return stub;
			}
		}
		throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
	}

	public void write_Object(org.omg.CORBA.portable.OutputStream _out,
			java.lang.Object obj) {
		throw new RuntimeException(" not implemented");
	}

	public java.lang.Object read_Object(org.omg.CORBA.portable.InputStream in) {
		throw new RuntimeException(" not implemented");
	}

	public String get_id() {
		return id();
	}

	public org.omg.CORBA.TypeCode get_type() {
		return type();
	}
}
