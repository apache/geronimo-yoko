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

public abstract class CodeBasePOA extends org.omg.PortableServer.Servant
		implements org.omg.CORBA.portable.InvokeHandler,
		org.omg.SendingContext.CodeBaseOperations {
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static {
		m_opsHash.put("get_ir", new java.lang.Integer(0));
		m_opsHash.put("bases", new java.lang.Integer(1));
		m_opsHash.put("meta", new java.lang.Integer(2));
		m_opsHash.put("implementations", new java.lang.Integer(3));
		m_opsHash.put("metas", new java.lang.Integer(4));
		m_opsHash.put("implementation", new java.lang.Integer(5));
	}

	private String[] ids = { "IDL:omg.org/SendingContext/CodeBase:1.0",
			"IDL:omg.org/SendingContext/RunTime:1.0",
			"IDL:omg.org/CORBA/Object:1.0" };

	public org.omg.SendingContext.CodeBase _this() {
		return org.omg.SendingContext.CodeBaseHelper.narrow(_this_object());
	}

	public org.omg.SendingContext.CodeBase _this(org.omg.CORBA.ORB orb) {
		return org.omg.SendingContext.CodeBaseHelper.narrow(_this_object(orb));
	}

	public org.omg.CORBA.portable.OutputStream _invoke(String method,
			org.omg.CORBA.portable.InputStream _input,
			org.omg.CORBA.portable.ResponseHandler handler)
			throws org.omg.CORBA.SystemException {
		org.omg.CORBA.portable.OutputStream _out = null;
		// do something
		// quick lookup of operation
		java.lang.Integer opsIndex = (java.lang.Integer) m_opsHash.get(method);
		if (null == opsIndex)
			throw new org.omg.CORBA.BAD_OPERATION(method + " not found");
		switch (opsIndex.intValue()) {
		case 0: // get_ir
		{
			_out = handler.createReply();
			org.omg.CORBA.RepositoryHelper.write(_out, get_ir());
			break;
		}
		case 1: // bases
		{
			java.lang.String _arg0 = _input.read_string();
			_out = handler.createReply();
			org.omg.CORBA.StringSeqHelper.write(_out, bases(_arg0));
			break;
		}
		case 2: // meta
		{
			java.lang.String _arg0 = _input.read_string();
			_out = handler.createReply();
			org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper.write(
					_out, meta(_arg0));
			break;
		}
		case 3: // implementations
		{
			java.lang.String[] _arg0 = org.omg.CORBA.StringSeqHelper
					.read(_input);
			_out = handler.createReply();
			org.omg.SendingContext.CodeBasePackage.URLSeqHelper.write(_out,
					implementations(_arg0));
			break;
		}
		case 4: // metas
		{
			java.lang.String _arg0 = _input.read_string();
			_out = handler.createReply();
			org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper.write(
					_out, metas(_arg0));
			break;
		}
		case 5: // implementation
		{
			java.lang.String _arg0 = _input.read_string();
			_out = handler.createReply();
			org.omg.SendingContext.CodeBasePackage.URLHelper.write(_out,
					implementation(_arg0));
			break;
		}
		}
		return _out;
	}

	public String[] _all_interfaces(org.omg.PortableServer.POA poa,
			byte[] obj_id) {
		return ids;
	}
}
