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

import org.omg.PortableServer.POA;

public class CodeBasePOATie extends CodeBasePOA {
	private CodeBaseOperations _delegate;

	private POA _poa;

	public CodeBasePOATie(CodeBaseOperations delegate) {
		_delegate = delegate;
	}

	public CodeBasePOATie(CodeBaseOperations delegate, POA poa) {
		_delegate = delegate;
		_poa = poa;
	}

	public org.omg.SendingContext.CodeBase _this() {
		return org.omg.SendingContext.CodeBaseHelper.narrow(_this_object());
	}

	public org.omg.SendingContext.CodeBase _this(org.omg.CORBA.ORB orb) {
		return org.omg.SendingContext.CodeBaseHelper.narrow(_this_object(orb));
	}

	public CodeBaseOperations _delegate() {
		return _delegate;
	}

	public void _delegate(CodeBaseOperations delegate) {
		_delegate = delegate;
	}

	public org.omg.CORBA.Repository get_ir() {
		return _delegate.get_ir();
	}

	public java.lang.String[] bases(java.lang.String id) {
		return _delegate.bases(id);
	}

	public org.omg.CORBA.ValueDefPackage.FullValueDescription meta(
			java.lang.String id) {
		return _delegate.meta(id);
	}

	public java.lang.String[] implementations(java.lang.String[] ids) {
		return _delegate.implementations(ids);
	}

	public org.omg.CORBA.ValueDefPackage.FullValueDescription[] metas(
			java.lang.String id) {
		return _delegate.metas(id);
	}

	public java.lang.String implementation(java.lang.String id) {
		return _delegate.implementation(id);
	}

}
