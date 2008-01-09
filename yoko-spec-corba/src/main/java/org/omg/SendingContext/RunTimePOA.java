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

public abstract class RunTimePOA extends org.omg.PortableServer.Servant
		implements org.omg.CORBA.portable.InvokeHandler,
		org.omg.SendingContext.RunTimeOperations {
	private String[] ids = { "IDL:omg.org/SendingContext/RunTime:1.0",
			"IDL:omg.org/CORBA/Object:1.0" };

	public org.omg.SendingContext.RunTime _this() {
		return org.omg.SendingContext.RunTimeHelper.narrow(_this_object());
	}

	public org.omg.SendingContext.RunTime _this(org.omg.CORBA.ORB orb) {
		return org.omg.SendingContext.RunTimeHelper.narrow(_this_object(orb));
	}

	public org.omg.CORBA.portable.OutputStream _invoke(String method,
			org.omg.CORBA.portable.InputStream _input,
			org.omg.CORBA.portable.ResponseHandler handler)
			throws org.omg.CORBA.SystemException {
		org.omg.CORBA.portable.OutputStream _out = null;
		// do something
		throw new org.omg.CORBA.BAD_OPERATION(method + " not found");
	}

	public String[] _all_interfaces(org.omg.PortableServer.POA poa,
			byte[] obj_id) {
		return ids;
	}
}
