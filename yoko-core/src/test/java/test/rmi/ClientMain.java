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
import java.io.FileReader;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.rmi.PortableRemoteObject;

import org.junit.Assert;
import org.omg.PortableServer.POA;

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

        // Make sure that a field definition for a value-type interface
        // gets marshaled correctly.  The SampleSerializable object defines a
        // List field into which we'll place a Vector object.  This should properly
        // be processed as a value type rather than an abstract interface.
        public void testVector() throws RemoteException {
            Vector v = new Vector(10);
            v.add("This is a test");
            SampleSerializable ser = new SampleSerializable();
            ser.setList(v);
            SampleSerializable ser2 = (SampleSerializable)sample.sendReceiveSerializable(ser);
            Vector v2 = (Vector)ser2.getList();
            assertEquals(10, v2.capacity());
            assertEquals(1, v2.size());
            assertEquals("This is a test", v2.elementAt(0));
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

        public void testCmsfv2Data() throws RemoteException {
            SampleCmsfv2ChildData d = new SampleCmsfv2ChildData();
            for (int i = 0; i < 10; i++) {
                System.out.println("Discarding " + d);
                d = new SampleCmsfv2ChildData();
            }

            sample.setSerializable(d);
            Serializable s = sample.getSerializable();
            assertNotSame(d, s);
            assertEquals(d, s);
        }

        public void testEnum() throws RemoteException {
            SampleEnum se = SampleEnum.SAMPLE2;
            sample.setSerializable(se);
            Serializable s = sample.getSerializable();
            assertSame(se, s);
        }

        public void testEnumArray() throws RemoteException {
            SampleEnum[] sa = { SampleEnum.SAMPLE3, SampleEnum.SAMPLE1, SampleEnum.SAMPLE3 };
            sample.setSerializable(sa);
            Object[] oa = (Object[])sample.getSerializable();
            assertTrue(Arrays.deepEquals(sa, oa));
        }

        public void testData() throws RemoteException {
            SampleData sd = new SampleData();
            sample.setSerializable(sd);
            Serializable s = sample.getSerializable();
            assertEquals(sd, s);
        }

        public void testTimeUnit() throws RemoteException {
            TimeUnit tu = TimeUnit.NANOSECONDS;
            sample.setSerializable(tu);
            Serializable s = sample.getSerializable();
            assertSame(tu, s);
        }

        public void testTimeUnitArray() throws RemoteException {
            TimeUnit[] tua = { TimeUnit.NANOSECONDS, TimeUnit.HOURS, TimeUnit.NANOSECONDS };
            sample.setSerializable(tua);
            Object[] oa = (Object[])sample.getSerializable();
            assertTrue(Arrays.deepEquals(tua, oa));
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

        public void testCorbaAttributeWithHelper(SampleCorba corbaRef) throws RemoteException {
            SampleSerializable ser = new SampleSerializable();
            ser.setCorbaObj(corbaRef);
            sample.setSerializable(ser);
            SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
            SampleCorba corbaRef2 = SampleCorbaHelper.narrow(ser2.getCorbaObj());
            corbaRef.i(42);
            assertEquals(42, corbaRef2.i());
            corbaRef.s("Don't panic!");
            assertEquals("Don't panic!", corbaRef2.s());
        }

        public void testCorbaAttributeWithPRO(SampleCorba corbaRef) throws RemoteException {
            SampleSerializable ser = new SampleSerializable();
            ser.setCorbaObj(corbaRef);
            sample.setSerializable(ser);
            SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
            SampleCorba corbaRef2 = (SampleCorba) PortableRemoteObject.narrow(ser2.getCorbaObj(), SampleCorba.class);
            corbaRef.i(42);
            assertEquals(42, corbaRef2.i());
            corbaRef.s("Don't panic!");
            assertEquals("Don't panic!", corbaRef2.s());
        }

        public void testComplexCorbaAttribute(SampleCorba corbaRef) throws RemoteException {
            SampleSerializable ser = new SampleSerializable();
            ser.setSampleCorba(corbaRef);
            sample.setSerializable(ser);
            SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
            SampleCorba corbaRef2 = ser2.getSampleCorba();
        }

        public void testHashMap() throws RemoteException {
            HashMap<Integer, Serializable> map = new HashMap<>();
            String str = new String("hello");
            map.put(0, str);
            map.put(1, str);
            Integer two = new Integer(2);
            map.put(3, two);
            map.put(4, two);
            sample.setSerializable(map);
            Map<?,?> map2 = (Map<?,?>) sample.getSerializable();
            assertEquals(map, map2);
            assertSame(map2.get(3), map2.get(4));
            assertSame(map2.get(0), map2.get(1));
        }

        public void testClass() throws RemoteException {
            final Class<?> type = Object.class;
            sample.setSerializable(type);
            Serializable s = sample.getSerializable();
            assertSame(s, type);
        }

        public void testClassArray() throws RemoteException {
            final Class<?>[] types = { Object.class, Map.class, String.class, Map.class };
            sample.setSerializable(types);
            Object[] oa = (Object[])sample.getSerializable();
            assertArrayEquals(types, oa);
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
        test.testVector();
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
        test.testCorbaAttributeWithHelper(SampleCorbaHelper.narrow(sampleCorbaRef));
        test.testCorbaAttributeWithPRO((SampleCorba) PortableRemoteObject.narrow(sampleCorbaRef, SampleCorba.class));
        test.testComplexCorbaAttribute(SampleCorbaHelper.narrow(sampleCorbaRef));
        test.testHashMap();
        test.testEnum();
        test.testEnumArray();
        test.testData();
        test.testTimeUnit();
        test.testTimeUnitArray();
        test.testCmsfv2Data();
        test.testClass();
        test.testClassArray();
        //myORB.destroy();
        System.out.println("Testing complete");
    }
}
