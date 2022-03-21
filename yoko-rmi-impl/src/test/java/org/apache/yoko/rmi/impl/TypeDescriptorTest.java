package org.apache.yoko.rmi.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import testify.jupiter.annotation.ClassSource;

import javax.rmi.CORBA.ClassDesc;
import java.io.Externalizable;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;

public class TypeDescriptorTest {
    private interface S extends Serializable {}

    private interface R extends Remote {}

    private interface SR extends Serializable, Remote {}

    private interface S2 extends S {}

    private interface R2 extends R {}

    private interface S2R2 extends SR {}

    /**
     * This is an abstract value type because x does not throw RemoteException
     * (see Java-to-IDL 1.4 4.3.10 Mapping of non-conforming classes and interfaces)
     */
    private interface S3 extends Serializable {
        void x();
    }

    private static class SI implements S {}

    private static class RI implements R {}

    private static class SRI implements SR {}

    private static class T extends Throwable {}

    private static class E extends Exception {}

    private static class E2 extends Error {}

    private interface I extends IDLEntity {}

    @SuppressWarnings("unused")
    private static class IHelper {
        // static methods mandated by IDL2Java spec 1.5.2
        public static void insert(Any a, I value) { throw new UnsupportedOperationException(); }
        public static I extract(Any a) { throw new UnsupportedOperationException(); }
        public static TypeCode type() { return null; }
        public static String id() { throw new UnsupportedOperationException(); }
        public static I read(InputStream is) { throw new UnsupportedOperationException(); }
        public static void write(OutputStream os, I value) { throw new UnsupportedOperationException(); }
        public static I narrow(Object o) { throw new UnsupportedOperationException(); }
    }

    private static class II implements IDLEntity {}

    @SuppressWarnings("unused")
    private static class IIHelper {
        // static methods mandated by IDL2Java spec 1.5.2
        public static void insert(Any a, II value) { throw new UnsupportedOperationException(); }
        public static II extract(Any a) { throw new UnsupportedOperationException(); }
        public static TypeCode type() { return null; }
        public static String id() { throw new UnsupportedOperationException(); }
        public static II read(InputStream is) { throw new UnsupportedOperationException(); }
        public static void write(OutputStream os, II value) { throw new UnsupportedOperationException(); }
        public static II narrow(Object o) { throw new UnsupportedOperationException(); }
    }

    private static class II2 implements I {}

    @SuppressWarnings("unused")
    private static class II2Helper {
        // static methods mandated by IDL2Java spec 1.5.2
        public static void insert(Any a, II2 value) { throw new UnsupportedOperationException(); }
        public static II2 extract(Any a) { throw new UnsupportedOperationException(); }
        public static TypeCode type() { return null; }
        public static String id() { throw new UnsupportedOperationException(); }
        public static II2 read(InputStream is) { throw new UnsupportedOperationException(); }
        public static void write(OutputStream os, II2 value) { throw new UnsupportedOperationException(); }
        public static II2 narrow(Object o) { throw new UnsupportedOperationException(); }
    }

    private enum N {NC1, NC2 {}}

    @ParameterizedTest
    @ClassSource({
            boolean.class, BooleanDescriptor.class,
            byte.class, ByteDescriptor.class,
            short.class, ShortDescriptor.class,
            char.class, CharDescriptor.class,
            int.class, IntegerDescriptor.class,
            long.class, LongDescriptor.class,
            float.class, FloatDescriptor.class,
            double.class, DoubleDescriptor.class,
            void.class, VoidDescriptor.class,
            String.class, StringDescriptor.class,
            Class.class, ClassDescriptor.class,
            ClassDesc.class, ClassDescDescriptor.class,
            Date.class, DateValueDescriptor.class,
            Object.class, AnyDescriptor.class,
            Serializable.class, AnyDescriptor.class,
            Externalizable.class, AnyDescriptor.class,
            Remote.class, AnyDescriptor.class, // TODO should really be an abstract object according to Java-to-IDL 1.4/4.3.4.1
            IDLEntity.class, AbstractObjectDescriptor.class, // TODO what should happen here? Maybe just throw an exception and don't deal with this case?
            I.class, IDLEntityDescriptor.class, // TODO what should happen here?
            II.class, IDLEntityDescriptor.class, // TODO what should happen here?
            II2.class, ValueDescriptor.class, // TODO what should happen here?
            Throwable.class, ExceptionDescriptor.class,
            Exception.class, ExceptionDescriptor.class,
            Error.class, ExceptionDescriptor.class,
            T.class, ExceptionDescriptor.class,
            E.class, ExceptionDescriptor.class,
            E2.class, ExceptionDescriptor.class,
            //UNKNOWN.class, DescTypes.ExceptionDescriptor,
            Enum.class, EnumDescriptor.class,
            N.class, EnumSubclassDescriptor.class,
            boolean[].class, BooleanArrayDescriptor.class,
            byte[].class, ByteArrayDescriptor.class,
            short[].class, ShortArrayDescriptor.class,
            char[].class, CharArrayDescriptor.class,
            int[].class, IntArrayDescriptor.class,
            long[].class, LongArrayDescriptor.class,
            float[].class, FloatArrayDescriptor.class,
            double[].class, DoubleArrayDescriptor.class,
            Remote[].class, RemoteArrayDescriptor.class, // TODO should be an abstract object array, considering Java-to-IDL 1.4/4.3.4.1
            Serializable[].class, ObjectArrayDescriptor.class,
            S.class, AbstractObjectDescriptor.class,
            S[].class, ValueArrayDescriptor.class, // TODO should be an abstract object array?
            // TODO consider carefully whether the following tests (reflecting current behaviour) are correct wrt Java-to-IDL 1.4
            R.class, RemoteInterfaceDescriptor.class,
            R[].class, RemoteArrayDescriptor.class,
            SR.class, RemoteInterfaceDescriptor.class,
            SR[].class, ValueArrayDescriptor.class,
            S2.class, AbstractObjectDescriptor.class,
            R2.class, RemoteInterfaceDescriptor.class,
            S2R2.class, RemoteInterfaceDescriptor.class,
            S3.class, ValueDescriptor.class,
            S3[].class, ValueArrayDescriptor.class,
            SI.class, ValueDescriptor.class,
            SI[].class, ValueArrayDescriptor.class,
            RI.class, RemoteClassDescriptor.class,
            SRI.class, ValueDescriptor.class,
            SRI[].class, ValueArrayDescriptor.class,
            Object[].class, ObjectArrayDescriptor.class})
    public void testDescriptorType(Class<?> marshalledType, Class<?> descriptorType) {
        final TypeDescriptor descriptor = TypeRepository.get().getDescriptor(marshalledType);
        Assertions.assertEquals(descriptor.getClass(), descriptorType);
    }

    @ParameterizedTest
    @EnumSource(N.class)
    public void testEnumDescriptorType(Enum<?> e) {
        testDescriptorType(e.getClass(), EnumSubclassDescriptor.class);
    }
}
