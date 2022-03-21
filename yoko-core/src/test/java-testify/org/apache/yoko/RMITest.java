package org.apache.yoko;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import test.rmi.Sample;
import test.rmi.SampleCmsfv2ChildData;
import test.rmi.SampleCorba;
import test.rmi.SampleCorbaHelper;
import test.rmi.SampleCorba_impl;
import test.rmi.SampleData;
import test.rmi.SampleEnum;
import test.rmi.SampleImpl;
import test.rmi.SampleRemote;
import test.rmi.SampleRemoteImpl;
import test.rmi.SampleSerializable;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.RemoteImpl;

import javax.rmi.PortableRemoteObject;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ConfigureServer
public class RMITest {
    @RemoteImpl
    public static final Sample sampleImpl = new SampleImpl();
    @RemoteImpl
    public static final SampleRemote sampleRemoteImpl = new SampleRemoteImpl();

    // TODO: add framework support for corba objects (as opposed to rmi-iiop objects)
    private static SampleCorba sampleCorba;

    @BeforeAll
    public static void beforeAll(ORB orb, POA rootPoa) throws Exception {
        // Bind a sample CORBA object
        SampleCorba_impl impl = new SampleCorba_impl();
        byte [] id = rootPoa.activate_object(impl);
        org.omg.CORBA.Object o = rootPoa.create_reference_with_id(id, impl._all_interfaces(rootPoa, id)[0]);
        sampleCorba = SampleCorbaHelper.narrow(o);
    }

    @Test
    public void testVector(Sample sample) throws Exception {
        // Make sure that a field definition for a value-type interface
        // gets marshaled correctly.  The SampleSerializable object defines a
        // List field into which we'll place a Vector object.  This should properly
        // be processed as a value type rather than an abstract interface.
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

    @Test
    // Test invoking methods with primitive arguments
    public void testPrimitive(Sample sample) throws Exception {
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

    @Test
    // Test invoking methods with signature conflicts and arrays
    public void testArray(Sample sample) throws Exception {
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

    @Test
    // Invoke method with String argument
    public void testString(Sample sample) throws Exception {
        sample.setString("hello");
        assertEquals("hello", sample.getString());
    }
    @Test
    public void testIntArray(Sample sample) throws Exception {
        int[] intArray = new int[] {1, 2, 3};
        sample.setIntArray(intArray);
        int[] intArray2 = sample.getIntArray();
        for(int i = 0; i < intArray.length; i++) {
            assertEquals(intArray[i], intArray2[i]);
        }
    }

    @Test
    public void testBasicSerializable(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
    }

    @Test
    public void testCmsfv2Data(Sample sample) throws Exception {
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

    @Test
    public void testEnum(Sample sample) throws Exception {
        SampleEnum se = SampleEnum.SAMPLE2;
        sample.setSerializable(se);
        Serializable s = sample.getSerializable();
        assertSame(se, s);
    }

    @Test
    public void testEnumArray(Sample sample) throws Exception {
        SampleEnum[] sa = { SampleEnum.SAMPLE3, SampleEnum.SAMPLE1, SampleEnum.SAMPLE3 };
        sample.setSerializable(sa);
        Object[] oa = (Object[])sample.getSerializable();
        assertTrue(Arrays.deepEquals(sa, oa));
    }

    @Test
    public void testData(Sample sample) throws Exception {
        SampleData sd = new SampleData();
        sample.setSerializable(sd);
        Serializable s = sample.getSerializable();
        assertEquals(sd, s);
    }

    @Test
    public void testTimeUnit(Sample sample) throws Exception {
        TimeUnit tu = TimeUnit.NANOSECONDS;
        sample.setSerializable(tu);
        Serializable s = sample.getSerializable();
        assertSame(tu, s);
    }

    @Test
    public void testTimeUnitArray(Sample sample) throws Exception {
        TimeUnit[] tua = { TimeUnit.NANOSECONDS, TimeUnit.HOURS, TimeUnit.NANOSECONDS };
        sample.setSerializable(tua);
        Object[] oa = (Object[])sample.getSerializable();
        assertTrue(Arrays.deepEquals(tua, oa));
    }

    @Test
    public void testRemoteAttributeOnServer(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setRemote(sample);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        Sample sample2 = (Sample) PortableRemoteObject.narrow(ser2.getRemote(), Sample.class);
        assertEquals(sample, sample2);

    }

    @Test
    public void testRemoteAttributeOnClient(Sample sample, SampleRemote sampleRemote) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setRemote(sampleRemote);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleRemote sampleRemote2 =
                (SampleRemote) PortableRemoteObject.narrow(ser2.getRemote(), SampleRemote.class);
        sampleRemote.setInt(42);
        assertEquals(42, sampleRemote2.getInt());
    }

    @Test
    public void testComplexRemoteAttributeOnClient(Sample sample, SampleRemote sampleRemote) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setSampleRemote(sampleRemote);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleRemote sampleRemote2 = ser2.getSampleRemote();
        sampleRemote.setInt(42);
        assertEquals(42, sampleRemote2.getInt());
    }

    @Test
    public void testComplexRemoteArgument(Sample sample, SampleRemote sampleRemote) throws Exception {
        sample.setSampleRemote(sampleRemote);
        sample.getSampleRemote();
    }

    @Test
    public void testSerializableAttribute(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        SampleSerializable attr = new SampleSerializable();
        ser.setSerializable(attr);
        attr.setInt(42);
        sample.setSerializable(ser);
        SampleSerializable serCopy = (SampleSerializable) sample.getSerializable();
        SampleSerializable attrCopy = (SampleSerializable) serCopy.getSerializable();
        assertEquals(attr.getInt(), attrCopy.getInt());
    }

    @Test
    public void testSerializableSelfReference(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setSerializableObject(ser);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        assertTrue(ser2 == ser2.getSerializableObject());
    }

    @Test
    public void testRemoteObjectAttribute(Sample sample, SampleRemote sampleRemote) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setRemoteObject(sampleRemote);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleRemote sampleRemote2 =
                (SampleRemote) PortableRemoteObject.narrow(ser2.getRemoteObject(), SampleRemote.class);
        sampleRemote.setInt(42);
        assertEquals(42, sampleRemote2.getInt());
    }

    @Test
    public void testCorbaAttributeWithHelper(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setCorbaObj(sampleCorba);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleCorba corbaRef2 = SampleCorbaHelper.narrow(ser2.getCorbaObj());
        sampleCorba.i(42);
        assertEquals(42, corbaRef2.i());
        sampleCorba.s("Don't panic!");
        assertEquals("Don't panic!", corbaRef2.s());
    }

    @Test
    public void testCorbaAttributeWithPRO(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setCorbaObj(sampleCorba);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleCorba corbaRef2 = (SampleCorba) PortableRemoteObject.narrow(ser2.getCorbaObj(), SampleCorba.class);
        sampleCorba.i(42);
        assertEquals(42, corbaRef2.i());
        sampleCorba.s("Don't panic!");
        assertEquals("Don't panic!", corbaRef2.s());
    }

    @Test
    public void testComplexCorbaAttribute(Sample sample) throws Exception {
        SampleSerializable ser = new SampleSerializable();
        ser.setSampleCorba(sampleCorba);
        sample.setSerializable(ser);
        SampleSerializable ser2 = (SampleSerializable) sample.getSerializable();
        SampleCorba corbaRef2 = ser2.getSampleCorba();
    }

    @Test
    public void testHashMap(Sample sample) throws Exception {
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

    @Test
    public void testClass(Sample sample) throws Exception {
        final Class<?> type = Object.class;
        sample.setSerializable(type);
        Serializable s = sample.getSerializable();
        assertSame(s, type);
    }

    @Test
    public void testClassArray(Sample sample) throws Exception {
        final Class<?>[] types = { Object.class, Map.class, String.class, Map.class };
        sample.setSerializable(types);
        Object[] oa = (Object[])sample.getSerializable();
        assertArrayEquals(types, oa);
    }
}
