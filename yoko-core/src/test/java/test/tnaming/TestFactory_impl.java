/*
 * Copyright 2015 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package test.tnaming;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.yoko.orb.spi.naming.Resolvable;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;

public class TestFactory_impl extends LocalObject implements Resolvable{

	POA _poa;
	ORB _orb;
	String _baseName;
	static final AtomicInteger _count = new AtomicInteger (0); 
	
	public TestFactory_impl (POA poa, ORB orb, String baseName) { 
		_poa = poa;
		_orb = orb;
	}
	
	@Override
	public Object resolve() {
		String thisOnesName = "_baseName" + _count.incrementAndGet();
		return new Test_impl(_poa, thisOnesName)._this_object(_orb);
	}
	
}
