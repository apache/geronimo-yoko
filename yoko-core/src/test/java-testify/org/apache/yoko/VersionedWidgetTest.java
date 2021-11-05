package org.apache.yoko;

import acme.Loader;
import acme.Widget;
import org.apache.yoko.io.Buffer;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OB.SendingContextRuntimes;
import org.apache.yoko.osgi.locator.ProviderRegistryImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Function;

public abstract class VersionedWidgetTest<T> {
    @ParameterizedTest(name = "Serialize and deserialize a {0}")
    @ValueSource(strings = {"versioned.SmallWidget", "versioned.BigWidget"})
    public void testSerializingAValue(String widgetClassName) throws Exception {
        // test that the validateAndReplace() methods work correctly when using normal serialization
        Widget w1 = Loader.V1.newInstance(widgetClassName);
        T intermediateForm = encode(w1);
        Widget w2 = decode(intermediateForm, widgetClassName, Loader.V2);
        w2 = w2.validateAndReplace();
        intermediateForm = encode(w2);
        w1 = decode(intermediateForm, widgetClassName, Loader.V1);
        w1.validateAndReplace();
    }

    abstract T encode(Widget widget) throws Exception;

    abstract Widget decode(T t, String widgetClassName, Loader context) throws Exception;

    /** Test widgets behave correctly under serialization */
    public static class WidgetSerializationTest extends VersionedWidgetTest<byte[]> {
        @Override
        byte[] encode(Widget widget) throws IOException {
            try (
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
            ) {
                oos.writeObject(widget);
                oos.flush();
                return baos.toByteArray();
            }
        }

        @Override
        Widget decode(byte[] bytes, String widgetClassName, Loader context) throws IOException, ClassNotFoundException {
            return context.deserializeFromBytes(bytes);
        }
    }

    /** Test widgets can be marshalled and demarshalled */
    public static class WidgetMarshallingTest extends VersionedWidgetTest<InputStream> {
        @Override
        InputStream encode(Widget widget) {
            WriteBuffer b = Buffer.createWriteBuffer();
            OutputStream out = new OutputStream(b);
            out.write_value(widget);
            return out.create_input_stream();
        }

        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            return (Widget)in.read_value(context.loadClass(widgetClassName));
        }
    }

    /** Test widgets can be demarshalled using a provider to resolve classes */
    public static class WidgetProviderLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            ProviderRegistryImpl rgy = new ProviderRegistryImpl();
            rgy.registerPackages(context.newInstance("versioned.VersionedPackageProvider"));
            rgy.start();
            try {
                return (Widget) in.read_value();
            } finally {
                rgy.stop();
            }
        }
    }

    /** Test widgets can be demarshalled using the stack loader to resolve classes */
    public static class WidgetStackLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            Function<InputStream, Widget> widgetReader = context.newInstance("versioned.WidgetReader");
            return widgetReader.apply(in);
        }
    }


    /** Test ProviderLoader classes on the stack are ignored when selecting a stack loader */
    public static class WidgetDeepStackLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            Function<InputStream, Widget> widgetReader = Loader.V0.newInstance("versioned.WidgetReader");
            widgetReader = chain(widgetReader, context);
            ProviderRegistryImpl rgy = new ProviderRegistryImpl();
            rgy.registerPackages(Loader.V0.newInstance("versioned.VersionedPackageProvider"));
            rgy.start();
            try {
                return widgetReader.apply(in);
            } finally {
                rgy.stop();
            }
        }

        <T, R> Function<T,R> chain(Function<T,R> fun, Loader context) {
            Function<Function<T,R>, Function<T,R>> chainer = context.newInstance("versioned.FunctionChainer");
            return chainer.apply(fun);
        }
    }
}
