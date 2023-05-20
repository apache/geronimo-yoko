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
package org.apache.yoko.orb.CORBA;

import acme.RemoteFunction;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.PortableInterceptor.ORBInitInfo;
import test.iiopplugin.TestORBInitializer;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureOrb.NameService;
import testify.iiop.annotation.ConfigureOrb.UseWithOrb;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.NameServiceStub;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import javax.rmi.CORBA.Stub;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The codecs retrieved from an ORBInitInfo should be capable
 * of marshalling and demarshalling an object reference.
 */
@ConfigureServer(serverOrb = @ConfigureOrb(value = "server orb", nameService = NameService.READ_ONLY))
public class CodecObjectReferenceTest {
    @UseWithOrb("client orb")
    public static class ClientOrbInitializer extends TestORBInitializer {
        private static Codec codec;
        public void post_init(ORBInitInfo info) {
            try {
                codec = info.codec_factory().create_codec(CDR_1_2_ENCODING);
            } catch (UnknownEncoding e) {
                throw (INITIALIZE) new INITIALIZE("Could not create CDR 1.2 codec").initCause(e);
            }
        }
    }

    @UseWithOrb("server orb")
    public static class ServerOrbInitializer extends TestORBInitializer {
        private static Codec codec;
        public void post_init(ORBInitInfo info) {
            try {
                codec = info.codec_factory().create_codec(CDR_1_2_ENCODING);
            } catch (UnknownEncoding e) {
                throw (INITIALIZE) new INITIALIZE("Could not create CDR 1.2 codec").initCause(e);
            }
        }
    }

    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);

    @NameServiceStub
    public static NamingContext nameService;
interface Echo extends RemoteFunction<String, String> {}

    @RemoteImpl
    public static final Echo REMOTE = String::toString;

    @Test
    void testEncodeAndDecodeRmiObject(Echo echo) throws Exception {
        final byte[] bytes = encodeRefUsingClientCodec((Stub)echo);
        // Decode the RMI object using the server-side codec
        Any serverAny = ServerOrbInitializer.codec.decode(bytes);
        org.omg.CORBA.Object obj = serverAny.extract_Object();
        // try invoking something using the demarshalled object reference
        assertFalse(obj._is_a("RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8"));
    }

    protected static byte[] encodeRefUsingClientCodec(org.omg.CORBA.Object reference) throws InvalidTypeForEncoding {
        final Any clientAny = ORB.init().create_any();
        clientAny.insert_Object(reference);
        return ClientOrbInitializer.codec.encode(clientAny);
    }

    @Test
    void testEncodeAndDecodeIdlObject(ORB clientOrb) throws Exception {
        // Encode the root naming context as an IDL object using the server-side codec
        assertFalse(nameService._is_a("RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8"));
        final byte[] bytes = encodeRefUsingClientCodec(nameService);
        // Decode the IDL object using the server-side codec
        Any serverAny = ServerOrbInitializer.codec.decode(bytes);
        org.omg.CORBA.Object obj = NamingContextHelper.extract(serverAny);
        // try invoking the demarshalled object reference
        assertFalse(obj._is_a("RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8"));
    }
}
