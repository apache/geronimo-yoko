/*
 * Copyright 2023 IBM Corporation and others.
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

import org.junit.jupiter.api.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryHelper;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import testify.iiop.annotation.ConfigureOrb;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ConfigureOrb
public class AnyCodecTest {
    AnyCodecTest(ORB orb) throws Exception {
        final Encoding encoding = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);
        final CodecFactory codecFactory = CodecFactoryHelper.narrow(orb.resolve_initial_references("CodecFactory"));
        codec = codecFactory.create_codec(encoding);
    }

    private final Codec codec;

    // use string using a high code point (emoji)
    private static final String initialData = "wibble " + String.copyValueOf(Character.toChars(0x1f642));

    private <T> void testAnyCodecCreation(BiConsumer<Any, T> insert, Function<Any, T> extract, T initialData) throws Exception {
        final Any initialAny = ORB.init().create_any();
        insert.accept(initialAny, initialData);
        final byte[] encodedData = codec.encode(initialAny);
        System.out.println("@@@ data.length: " + encodedData.length);
        final Any finalAny = codec.decode(encodedData);
        System.out.println("@@@ client TypeCode: " + finalAny.type().kind().value());

        final T finalData = extract.apply(finalAny);
        assertEquals(initialData, finalData);
    }

    @Test
    public void testAnyStringCodecCreation() throws Exception {
        System.out.println("@@@ Testing string");
        testAnyCodecCreation(Any::insert_string, Any::extract_string, "wibble");
    }

    @Test
    public void testAnyWstringCodecCreation() throws Exception {
        System.out.println("@@@ Testing wstring");
        testAnyCodecCreation(Any::insert_wstring, Any::extract_wstring, initialData);
    }

    @Test
    public void testAnyValueCodecCreation() throws Exception {
        System.out.println("@@@ Testing value");
        testAnyCodecCreation(Any::insert_Value, Any::extract_Value, initialData);
    }
}
