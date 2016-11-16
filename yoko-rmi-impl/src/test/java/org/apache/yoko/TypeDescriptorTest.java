package org.apache.yoko;

import junit.framework.TestCase;
import org.apache.yoko.rmi.impl.TypeRepository;
import org.junit.Test;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.PortableInterceptor.UNKNOWN;

import javax.rmi.CORBA.ClassDesc;
import java.io.Externalizable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TypeDescriptorTest extends TestCase {
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
    private interface S3 extends Serializable {void x();}

    private static class SI implements S {}
    private static class RI implements R {}
    private static class SRI implements SR {}

    private static class T extends Throwable {}
    private static class E extends Exception {}
    private static class E2 extends Error {}

    private interface I extends IDLEntity {}
    private static class IHelper {}
    private static class II implements IDLEntity {}
    private static class IIHelper {}
    private static class II2 implements I {}
    private static class II2Helper {}

    private static class TestRecord {
        public final Class<?> type;
        public final String expected;

        private TestRecord(Class<?> type, String expected) {
            this.type = type;
            this.expected = expected;
        }

        public static Iterable<TestRecord> build(Object... args) {
            List<TestRecord> records = new ArrayList<>();
            Class<?> type = null;
            for (Object a: args) {
                if (null == type) {
                    type = (Class<?>)a;
                    continue;
                }
                records.add(new TestRecord(type, a.toString()));
                type = null;
            }
            return records;
        }
    }

    private enum DescTypes {
        BooleanDescriptor, ByteDescriptor, ShortDescriptor, CharDescriptor, IntegerDescriptor,
        LongDescriptor, FloatDescriptor, DoubleDescriptor, VoidDescriptor,
        StringDescriptor, ClassDescriptor, ClassDescDescriptor, DateValueDescriptor,
        AnyDescriptor, IDLEntityDescriptor, ExceptionDescriptor,
        EnumDescriptor, EnumSubclassDescriptor{},
        BooleanArrayDescriptor, ByteArrayDescriptor, CharArrayDescriptor, ShortArrayDescriptor,
        IntArrayDescriptor, LongArrayDescriptor, FloatArrayDescriptor, DoubleArrayDescriptor,
        ObjectArrayDescriptor, RemoteArrayDescriptor, ValueArrayDescriptor,
        ValueDescriptor, RemoteInterfaceDescriptor, RemoteClassDescriptor, AbstractObjectDescriptor
    }

    private final Iterable<TestRecord> tests = TestRecord.build(
            boolean.class, DescTypes.BooleanDescriptor,
            byte.class, DescTypes.ByteDescriptor,
            short.class, DescTypes.ShortDescriptor,
            char.class, DescTypes.CharDescriptor,
            int.class, DescTypes.IntegerDescriptor,
            long.class, DescTypes.LongDescriptor,
            float.class, DescTypes.FloatDescriptor,
            double.class, DescTypes.DoubleDescriptor,
            void.class, DescTypes.VoidDescriptor,
            String.class, DescTypes.StringDescriptor,
            Class.class, DescTypes.ClassDescriptor,
            ClassDesc.class, DescTypes.ClassDescDescriptor,
            Date.class, DescTypes.DateValueDescriptor,
            Object.class, DescTypes.AnyDescriptor,
            Serializable.class, DescTypes.AnyDescriptor,
            Externalizable.class, DescTypes.AnyDescriptor,
            Remote.class, DescTypes.AnyDescriptor, // TODO should really be an abstract object according to Java-to-IDL 1.4/4.3.4.1
            IDLEntity.class, DescTypes.AbstractObjectDescriptor, // TODO what should happen here? Maybe just throw an exception and don't deal with this case?
            I.class, DescTypes.IDLEntityDescriptor, // TODO what should happen here?
            II.class, DescTypes.IDLEntityDescriptor, // TODO what should happen here?
            II2.class, DescTypes.ValueDescriptor, // TODO what should happen here?
            Throwable.class, DescTypes.ExceptionDescriptor,
            Exception.class, DescTypes.ExceptionDescriptor,
            Error.class, DescTypes.ExceptionDescriptor,
            T.class, DescTypes.ExceptionDescriptor,
            E.class, DescTypes.ExceptionDescriptor,
            E2.class, DescTypes.ExceptionDescriptor,
            //UNKNOWN.class, DescTypes.ExceptionDescriptor,
            Enum.class, DescTypes.EnumDescriptor,
            DescTypes.class, DescTypes.EnumSubclassDescriptor,
            DescTypes.EnumSubclassDescriptor.getClass(), DescTypes.EnumSubclassDescriptor,
            boolean[].class, DescTypes.BooleanArrayDescriptor,
            byte[].class, DescTypes.ByteArrayDescriptor,
            short[].class, DescTypes.ShortArrayDescriptor,
            char[].class, DescTypes.CharArrayDescriptor,
            int[].class, DescTypes.IntArrayDescriptor,
            long[].class, DescTypes.LongArrayDescriptor,
            float[].class, DescTypes.FloatArrayDescriptor,
            double[].class, DescTypes.DoubleArrayDescriptor,
            Remote[].class, DescTypes.RemoteArrayDescriptor, // TODO should be an abstract object array, considering Java-to-IDL 1.4/4.3.4.1
            Serializable[].class, DescTypes.ObjectArrayDescriptor,
            S.class, DescTypes.AbstractObjectDescriptor,
            S[].class, DescTypes.ValueArrayDescriptor, // TODO should be an abstract object array?
            // TODO consider carefully whether the following tests (reflecting current behaviour) are correct wrt Java-to-IDL 1.4
            R.class, DescTypes.RemoteInterfaceDescriptor,
            R[].class, DescTypes.RemoteArrayDescriptor,
            SR.class, DescTypes.RemoteInterfaceDescriptor,
            SR[].class, DescTypes.ValueArrayDescriptor,
            S2.class, DescTypes.AbstractObjectDescriptor,
            R2.class, DescTypes.RemoteInterfaceDescriptor,
            S2R2.class, DescTypes.RemoteInterfaceDescriptor,
            S3.class, DescTypes.ValueDescriptor,
            S3[].class, DescTypes.ValueArrayDescriptor,
            SI.class, DescTypes.ValueDescriptor,
            SI[].class, DescTypes.ValueArrayDescriptor,
            RI.class, DescTypes.RemoteClassDescriptor,
            SRI.class, DescTypes.ValueDescriptor,
            SRI[].class, DescTypes.ValueArrayDescriptor,
            Object[].class, DescTypes.ObjectArrayDescriptor
    );

    @Test
    public void testDescriptorTypes() throws IOException {
        TypeRepository repo = TypeRepository.get();

        try (StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {

            boolean overallPassed = true;
            for (TestRecord test : tests) {
                boolean passed = false;
                try {
                    String name = ((Object) repo.getDescriptor(test.type)).getClass().getSimpleName();
                    passed = name.equals(test.expected);
                    pw.printf("%15s -> %5b  %s%n", test.type.getSimpleName(), passed, name);
                } catch (Throwable t) {
                    pw.printf("%15s EXCEPTION:%n", test.type.getSimpleName());
                    t.printStackTrace(pw);
                } finally {
                    overallPassed &= passed;
                }
            }
            pw.flush();
            String testOutput = sw.toString();
            System.out.print(testOutput);
            if (overallPassed) return;
            fail(String.format("Some tests FAILED:%n%s", sw.toString()));
        }
    }
}
