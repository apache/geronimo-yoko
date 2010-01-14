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
package test.rmi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Tie;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Initialize ORB
		ORB orb = ORB.init(new String[0], null);
		System.out.println("ORB: " + orb.getClass().getName());
		
		POA rootPoa = (POA) orb.resolve_initial_references("RootPOA");
		
		// Create a POA
		Policy[] tpolicy = new Policy[3];
		tpolicy[0] = rootPoa.create_lifespan_policy(
				LifespanPolicyValue.TRANSIENT );
		tpolicy[1] = rootPoa.create_request_processing_policy(
				RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY );
		tpolicy[2] = rootPoa.create_servant_retention_policy(
				ServantRetentionPolicyValue.RETAIN);
		POA poa = rootPoa.create_POA("SamplePOA", null, tpolicy);

		poa.the_POAManager().activate();
		
		// Create a SampleImpl and bind it to the POA
		Sample sample = new SampleImpl();
		
		Tie tie = javax.rmi.CORBA.Util.getTie(sample);
		
		byte[] id = poa.activate_object((Servant) tie);
		org.omg.CORBA.Object obj = poa.create_reference_with_id(id, ((Servant)tie)._all_interfaces(poa, id)[0]);
		
		// Write a IOR to a file so the client can obtain a reference to the Sample
		File sampleRef = new File("Sample.ref");
		PrintWriter writer = new PrintWriter(new FileOutputStream(sampleRef));
		writer.write(orb.object_to_string(obj));
		writer.close();
		orb.run();
	}

}
