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
import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.apache.yoko.orb.spi.naming.NameServiceInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.BeforeServer;
import testify.jupiter.annotation.iiop.ConfigureServer.RemoteObject;
import testify.jupiter.annotation.iiop.ConfigureServer.UseWithServerOrb;

import javax.rmi.CORBA.Stub;
import javax.rmi.PortableRemoteObject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The codecs retrieved from an ORBInitInfo should be capable
 * of marshalling and demarshalling an object reference.
 */
@ConfigureServer(newProcess = false)
public class CodecObjectReferenceTest {
    @UseWithOrb
    public static class CreateClientSideCodec extends LocalObject implements ORBInitializer {
        public void pre_init(ORBInitInfo info) {}
        public void post_init(ORBInitInfo info) {
            try {
                clientCodec = info.codec_factory().create_codec(CDR_1_2_ENCODING);
            } catch (UnknownEncoding e) {
                throw (INITIALIZE) new INITIALIZE("Could not create CDR 1.2 codec").initCause(e);
            }
        }
    }

    @UseWithServerOrb
    public static class CreateServerSideCodec extends LocalObject implements ORBInitializer {
        public void pre_init(ORBInitInfo info) {}
        public void post_init(ORBInitInfo info) {
            try {
                serverCodec = info.codec_factory().create_codec(CDR_1_2_ENCODING);
            } catch (UnknownEncoding e) {
                throw (INITIALIZE) new INITIALIZE("Could not create CDR 1.2 codec").initCause(e);
            }
        }
    }

    @UseWithServerOrb
    public static class StartNameService extends NameServiceInitializer {} // ensure server ORB has a NameService

    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);
    private static Codec clientCodec;
    private static Codec serverCodec;
    private static ORB clientOrb;
    private static ORB serverOrb;
    private static String nameServiceUrl;

    @RemoteObject
    public static Echo echo;

    @BeforeAll
    public static void getClientOrb(ORB orb) { clientOrb = orb; }

    @BeforeServer
    public static void getServerOrb(ORB orb) throws Exception {
        serverOrb = orb;
        POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        POAManager_impl poaMgr = (POAManager_impl) rootPoa.the_POAManager();
        poaMgr.activate();
        AcceptorInfo info = (AcceptorInfo) poaMgr._OB_getAcceptors()[0].get_info();
        final int port = 0xFFFF & info.port(); // treat short as unsigned 16-bit integer
        nameServiceUrl = "corbaloc::localhost:" + port + "/NameService";
    }

    @Test
    public void testEncodeAndDecodeRmiObject() throws Exception {
        final byte[] bytes = encodeRefUsingClientCodec((Stub)echo);
        // Decode the RMI object using the server-side codec
        Any serverAny = serverCodec.decode(bytes);
        Object obj = serverAny.extract_Object();
        Echo localEcho = Echo.class.cast(PortableRemoteObject.narrow(obj, Echo.class));
        // try invoking something using the demarshalled object reference
        String actual = localEcho.echo("Hello");
        assertThat(actual, equalTo("Hello"));
    }

    protected static byte[] encodeRefUsingClientCodec(org.omg.CORBA.Object reference) throws InvalidTypeForEncoding {
        final Any clientAny = clientOrb.create_any();
        clientAny.insert_Object(reference);
        return clientCodec.encode(clientAny);
    }

    @Test
    public void testEncodeAndDecodeIdlObject(ORB clientOrb) throws Exception {
        // Encode the root naming context as an IDL object using the server-side codec
        NamingContext localCtx = NamingContextHelper.narrow(clientOrb.string_to_object(nameServiceUrl));
        final byte[] bytes = encodeRefUsingClientCodec(localCtx);
        // Decode the IDL object using the server-side codec
        Any serverAny = serverCodec.decode(bytes);
        NamingContext remoteCtx = NamingContextHelper.extract(serverAny);
        // try invoking the demarshalled object reference
        assertFalse(remoteCtx._is_a("RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8"));
    }
}
