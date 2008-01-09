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

public class RunTimePOATie extends RunTimePOA {
	private RunTimeOperations _delegate;

	private POA _poa;

	public RunTimePOATie(RunTimeOperations delegate) {
		_delegate = delegate;
	}

	public RunTimePOATie(RunTimeOperations delegate, POA poa) {
		_delegate = delegate;
		_poa = poa;
	}

	public org.omg.SendingContext.RunTime _this() {
		return org.omg.SendingContext.RunTimeHelper.narrow(_this_object());
	}

	public org.omg.SendingContext.RunTime _this(org.omg.CORBA.ORB orb) {
		return org.omg.SendingContext.RunTimeHelper.narrow(_this_object(orb));
	}

	public RunTimeOperations _delegate() {
		return _delegate;
	}

	public void _delegate(RunTimeOperations delegate) {
		_delegate = delegate;
	}
}
