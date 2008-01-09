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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import junit.framework.Assert;

public class ClientMain extends Assert {

	public static class Test extends Assert {
		private Sample sample;
		public Test(Sample sample) {
			this.sample = sample;
		}
		
		// Test invoking methods with primitive arguments
		public void testPrimitive() throws RemoteException {
			sample.setBoolean(true);
			assertTrue(sample.getBoolean());
			sample.setByte((byte)64);
			assertEquals((byte)64, sample.getByte());
			sample.setShort((short)128);
			assertEquals((short)128, sample.getShort());
			sample.setInt(256);
			assertEquals(256, sample.getInt());
			sample.setLong(512);
			assertEquals(512, sample.getLong());	
			sample.setChar('a');
			assertEquals('a', sample.getChar());

		}

		// Test invoking methods with signature conflicts and arrays
		public void testArray() throws RemoteException {
                    assertTrue(10 == sample.sendReceiveInt(10));
                    int[] intA = new int[] {10, 20};

                    intA = sample.sendReceiveInt(intA); 
                    assertEquals(2, intA.length); 
                    assertTrue(20 == intA[0]); 
                    assertTrue(10 == intA[1]); 

                    assertTrue(10 == sample.sendReceiveShort((short)10));
                    short[] shortA = new short[] {10, 20};

                    shortA = sample.sendReceiveShort(shortA); 
                    assertEquals(2, shortA.length); 
                    assertTrue(20 == shortA[0]); 
                    assertTrue(10 == shortA[1]); 

                    assertTrue(10 == sample.sendReceiveChar((char)10));
                    char[] charA = new char[] {10, 20};

                    charA = sample.sendReceiveChar(charA); 
                    assertEquals(2, charA.length); 
                    assertTrue(20 == charA[0]); 
                    assertTrue(10 == charA[1]); 

                    assertTrue(10 == sample.sendReceiveByte((byte)10));
                    byte[] byteA = new byte[] {10, 20};

                    byteA = sample.sendReceiveByte(byteA); 
                    assertEquals(2, byteA.length); 
                    assertTrue(20 == byteA[0]); 
                    assertTrue(10 == byteA[1]); 

                    assertTrue(10L == sample.sendReceiveLong(10L));
                    long[] longA = new long[] {10L, 20L};

                    longA = sample.sendReceiveLong(longA); 
                    assertEquals(2, longA.length); 
                    assertTrue(20L == longA[0]); 
                    assertTrue(10L == longA[1]); 

                    assertTrue(10. == sample.sendReceiveFloat((float)10.));
                    float[] floatA = new float[] {(float)10., (float)20.};

                    floatA = sample.sendReceiveFloat(floatA); 
                    assertEquals(2, floatA.length); 
                    assertTrue(20. == floatA[0]); 
                    assertTrue(10. == floatA[1]); 

                    assertTrue(10. == sample.sendReceiveDouble(10.));
                    double[] doubleA = new double[] {10., 20.};

                    doubleA = sample.sendReceiveDouble(doubleA); 
                    assertEquals(2, doubleA.length); 
                    assertTrue(20. == doubleA[0]); 
                    assertTrue(10. == doubleA[1]); 

                    assertTrue(false == sample.sendReceiveBoolean(false));
                    boolean[] booleanA = new boolean[] {true, false};

                    booleanA = sample.sendReceiveBoolean(booleanA); 
                    assertEquals(2, booleanA.length); 
                    assertTrue(false == booleanA[0]); 
                    assertTrue(true == booleanA[1]); 

                    assertTrue("a".equals(sample.sendReceiveString("a")));
                    String[] StringA = new String[] {"a", "b"};

                    StringA = sample.sendReceiveString(StringA); 
                    assertEquals(2, StringA.length); 
                    assertTrue("b".equals(StringA[0])); 
                    assertTrue("a".equals(StringA[1])); 

                    SampleSerializable ser = new SampleSerializable();
                    ser.setInt(10); 
                    SampleSerializable ser2 = (SampleSerializable)sample.sendReceiveSerializable(ser); 
                    assertEquals(10, ser2.getInt()); 

                    Serializable[] sA = new Serializable[] { ser };
                    sA = sample.sendReceiveSerializable(sA); 

                    ser2 = (SampleSerializable)sA[0]; 
                    assertEquals(10, ser2.getInt()); 

                    Remote r = sample.sendReceiveRemote(sample); 
                    Sample sample2 = (Sample) PortableRemoteObject.narrow(r, Sample.class);
                    assertEquals(sample, sample2);

                    Remote[] rA = new Remote[] { sample }; 
                    rA = sample.sendReceiveRemote(rA); 
                    sample2 = (Sample) PortableRemoteObject.narrow(rA[0], Sample.class);
                    assertEquals(sample, sample2);
                }
		
		// Invoke method with String argument
		public void testString() throws RemoteException {
			sample.setString("hello");
			assertEquals("hello", sample.getString());
		}
		
		public void testIntArray() throws RemoteException {
			int[] intArray = new int[] {1, 2, 3};
			sample.setIntArray(intArray);
			int[] intArray2 = sample.getIntArray();
			for(int i = 0; i < intArray.length; i++) {
				assertEquals(intArray[i], intArray2[i]);
			}			
		}
		
		public void testBasicSerializable() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
		}
		
		public void testRemoteAttributeOnServer() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			ser.setRemote(sample);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			Sample sample2 = (Sample) PortableRemoteObject.narrow(ser2.getRemote(), Sample.class);
			assertEquals(sample, sample2);
			
		}
		
		public void testRemoteAttributeOnClient() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			SampleRemote sampleRemote = new SampleRemoteImpl();
			ser.setRemote(sampleRemote);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			SampleRemote sampleRemote2 = 
				(SampleRemote) PortableRemoteObject.narrow(ser2.getRemote(), SampleRemote.class);
			sampleRemote.setInt(42);
			assertEquals(42, sampleRemote2.getInt());
		}
		
		public void testComplexRemoteAttributeOnClient() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			SampleRemoteImpl sampleRemote = new SampleRemoteImpl();
			ser.setSampleRemote(sampleRemote);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			SampleRemote sampleRemote2 = ser2.getSampleRemote();
			sampleRemote.setInt(42);
			assertEquals(42, sampleRemote2.getInt());
		}
		
		public void testComplexRemoteArgument() throws RemoteException {
			SampleRemoteImpl sampleRemote = new SampleRemoteImpl();
			sample.setSampleRemote(sampleRemote);
			sample.getSampleRemote();
		}
		
		public void testSerializableAttribute() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			SampleSerializable attr = new SampleSerializable();
			ser.setSerializable(attr);
			attr.setInt(42);
			sample.setSerializable(ser);
			SampleSerializable serCopy = (SampleSerializable) sample.getSerializable();
			SampleSerializable attrCopy = (SampleSerializable) serCopy.getSerializable();
			assertEquals(attr.getInt(), attrCopy.getInt());
		}
		
		public void testSerializableSelfReference() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			ser.setSerializableObject(ser);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			assertTrue(ser2 == ser2.getSerializableObject());
		}
		
		public void testRemoteObjectAttribute() throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			SampleRemoteImpl sampleRemote = new SampleRemoteImpl();
			ser.setRemoteObject(sampleRemote);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			SampleRemote sampleRemote2 = 
				(SampleRemote) PortableRemoteObject.narrow(ser2.getRemoteObject(), SampleRemote.class);
			sampleRemote.setInt(42);
			assertEquals(42, sampleRemote2.getInt());
		}
		
		public void testCorbaAttribute(SampleCorba corbaRef) throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			ser.setCorbaObj(corbaRef);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			SampleCorba corbaRef2 = SampleCorbaHelper.narrow(ser2.getCorbaObj());
			corbaRef.i(42);
			assertEquals(42, corbaRef2.i());
		}
		
		public void testComplexCorbaAttribute(SampleCorba corbaRef) throws RemoteException {
			SampleSerializable ser = new SampleSerializable();
			ser.setSampleCorba(corbaRef);
			sample.setSerializable(ser);
			SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
			SampleCorba corbaRef2 = ser2.getSampleCorba();
		}
		
		public void testHashMap() throws RemoteException {
			HashMap map = new HashMap();
			String str = "hello";
			map.put(new Integer(0), str);
			map.put(new Integer(1), str);
			Integer i = new Integer(2);
			map.put(new Integer(3), i);
			map.put(new Integer(4), i);
			sample.setSerializable(map);
			HashMap map2 = (HashMap) sample.getSerializable();
			assertEquals(map, map2);			
			assertTrue(map2.get(new Integer(3)) == map2.get(new Integer(4)));
			assertTrue(map2.get(new Integer(0)) == map2.get(new Integer(1)));
		}
	}
	
	public static void main(String[] args) throws Exception {
		// Initialize ORB
		final org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], null);
		POA rootPoa = (POA) orb.resolve_initial_references("RootPOA");
		rootPoa.the_POAManager().activate();
		System.out.println("ORB: " + orb.getClass().getName());
		
		// Bind a sample CORBA object
		SampleCorba_impl sampleCorba = new SampleCorba_impl();
		byte [] id = rootPoa.activate_object(sampleCorba);
		org.omg.CORBA.Object sampleCorbaRef = rootPoa.create_reference_with_id(id, sampleCorba._all_interfaces(rootPoa, id)[0]);

		// Get IOR to Sample on server
		BufferedReader reader = new BufferedReader(new FileReader("Sample.ref"));
		String ref = reader.readLine();
		org.omg.CORBA.Object sampleRef = orb.string_to_object(ref);
		Sample sample = (Sample) PortableRemoteObject.narrow(sampleRef, Sample.class);

		// Run RMI tests
		Test test = new Test(sample);
		test.testPrimitive();
		test.testArray();
		test.testString();
		test.testIntArray();
		test.testBasicSerializable();
		test.testRemoteObjectAttribute();
		test.testRemoteAttributeOnServer();
		test.testRemoteAttributeOnClient();
		test.testComplexRemoteAttributeOnClient();
		test.testComplexRemoteArgument();
		test.testSerializableAttribute();
		test.testSerializableSelfReference();
		test.testCorbaAttribute(SampleCorbaHelper.narrow(sampleCorbaRef));
		test.testComplexCorbaAttribute(SampleCorbaHelper.narrow(sampleCorbaRef));
		test.testHashMap();
		//myORB.destroy();
	}
}
