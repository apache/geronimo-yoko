/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.yoko.orb.CORBA;

import acme.Echo;
import acme.EchoImpl;
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
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureOrb.NameService;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;
import testify.jupiter.annotation.iiop.ConfigureServer.NameServiceStub;

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

    @ClientStub(EchoImpl.class)
    public static Echo echo;

    @Test
    void testEncodeAndDecodeRmiObject() throws Exception {
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
