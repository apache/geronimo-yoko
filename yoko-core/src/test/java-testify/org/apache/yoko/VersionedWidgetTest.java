/*
 * Copyright 2021 IBM Corporation and others.
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
    @ParameterizedTest(name = "Encode and decode a {0}")
    @ValueSource(strings = {"versioned.SmallWidget", "versioned.BigWidget"})
    public void testEncodeAndDecode(String widgetClassName) throws Exception {
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
                    ObjectOutputStream oos = new ObjectOutputStream(baos)
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

    /** Test widgets can be marshalled and unmarshalled */
    public static class WidgetMarshallingTest extends VersionedWidgetTest<InputStream> {
        @Override
        InputStream encode(Widget widget) {
            WriteBuffer b = Buffer.createWriteBuffer();
            OutputStream out = new OutputStream(b);
            out.write_value(widget);
            return out.create_input_stream();
        }

        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) throws Exception {
            // Marshalling across versions will need a runtime codebase
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            Class<? extends Widget> knownType = context.loadClass(widgetClassName);
            return (Widget) in.read_value(knownType);
        }
    }

    /** Test widgets can be demarshalled using a provider to resolve classes */
    public static class WidgetProviderLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            // Marshalling across versions will need a runtime codebase
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            ProviderRegistryImpl reg = new ProviderRegistryImpl();
            reg.registerPackages(context.newInstance("versioned.VersionedPackageProvider"));
            reg.start();
            try {
                return (Widget) in.read_value();
            } finally {
                reg.stop();
            }
        }
    }

    /** Test widgets can be demarshalled using the stack loader to resolve classes */
    public static class WidgetStackLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) {
            // Marshalling across versions will need a runtime codebase
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            // To unmarshal from the correct class loading context,
            // delegate the read_value() to a WidgetReader from the context loader
            Function<InputStream, Widget> widgetReader = context.newInstance("versioned.WidgetReader");
            return widgetReader.apply(in);
        }
    }


    /** Test ProviderLoader classes on the stack are ignored when selecting a stack loader */
    public static class WidgetDeepStackLoaderTest extends WidgetMarshallingTest {
        @Override
        Widget decode(InputStream in, String widgetClassName, Loader context) throws Exception {
            // Marshalling across versions will need a runtime codebase
            in.__setSendingContextRuntime(SendingContextRuntimes.LOCAL_CODE_BASE);
            // Register the V0 package provider with the provider registry so it can be ignored in the call stack.
            ProviderRegistryImpl reg = new ProviderRegistryImpl();
            reg.registerPackages(Loader.V0.newInstance("versioned.VersionedPackageProvider"));
            reg.start();
            // To insert an extra layer into the call stack, use the WidgetReader from the WRONG loader, V0.
            // NOTE: if we do not load something via the registry, it will never know about the class loader
            Class<? extends Function<InputStream, Widget>> widgetReaderClass = reg.locate("versioned.WidgetReader");
            Function<InputStream, Widget> widgetReader = widgetReaderClass.getConstructor().newInstance();
            // Then invoke the WidgetReader from another object loaded by the RIGHT loader.
            widgetReader = chain(widgetReader, context);
            try {
                return widgetReader.apply(in);
            } finally {
                reg.stop();
            }
        }

        private static <T, R> Function<T,R> chain(Function<T,R> fun, Loader context) {
            Function<Function<T,R>, Function<T,R>> chainer = context.newInstance("versioned.FunctionChainer");
            return chainer.apply(fun);
        }
    }
}
