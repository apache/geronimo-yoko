package org.apache.yoko;

import org.apache.yoko.orb.OBPortableServer.POA;
import org.apache.yoko.orb.OBPortableServer.POAHelper;
import org.apache.yoko.orb.OBPortableServer.POAManager;
import org.apache.yoko.orb.OBPortableServer.POAManagerHelper;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfoHelper;
import org.apache.yoko.orb.spi.naming.NameServiceInitializer;
import org.apache.yoko.orb.spi.naming.RemoteAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CosNaming.*;
import org.omg.PortableInterceptor.*;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import testify.iiop.Skellington;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

import static javax.rmi.PortableRemoteObject.narrow;

public class ConnectionCachingTest {
    private static final NameComponent[] OBJECT_NAME = { new NameComponent("object", "")};
    ORB serverORB;
    ORB clientORB;

    @Before
    public void setup() throws Exception {
        serverORB = Util.createServerOrb();
        clientORB = Util.createClientORB(serverORB);
        // make a GIOP 1.0 call first
        NamingContext ctx = NamingContextHelper.narrow(clientORB.string_to_object(Util.getNameServerUrl(serverORB)));
        ctx.new_context();
    }

    @Test
    public void testSingleNull() throws Exception {
        Assert.assertEquals(null, newRemoteImpl(clientORB).bounce(null));
    }

    @Test
    public void testSingleNullSameOrb() throws Exception {
        Assert.assertEquals(null, newRemoteImpl(serverORB).bounce(null));
    }

    @Test
    public void testSingleEmptyString() throws Exception {
        Assert.assertEquals("", newRemoteImpl(clientORB).bounce(""));
    }

    @Test
    public void testSingleEmptyStringSameOrb() throws Exception {
        Assert.assertEquals("", newRemoteImpl(serverORB).bounce(""));
    }

    @Test
    public void testSingleNonEmptyString() throws Exception {
        Assert.assertEquals("hello", newRemoteImpl(clientORB).bounce("hello"));
    }

    @Test
    public void testSingleNonEmptyStringSameOrb() throws Exception {
        Assert.assertEquals("hello", newRemoteImpl(serverORB).bounce("hello"));
    }

    @Test
    public void testLotsOfInvocations() throws Exception {
        Assert.assertEquals(null, newRemoteImpl(clientORB).bounce(null));
        Assert.assertEquals("", newRemoteImpl(clientORB).bounce(""));
        Assert.assertEquals("a", newRemoteImpl(clientORB).bounce("a"));
        Assert.assertEquals("ab", newRemoteImpl(clientORB).bounce("ab"));
        Assert.assertEquals("abc", newRemoteImpl(clientORB).bounce("abc"));
        Assert.assertEquals("abcd", newRemoteImpl(clientORB).bounce("abcd"));
        Assert.assertEquals("abcde", newRemoteImpl(clientORB).bounce("abcde"));
    }

    @Test
    public void testLotsOfInvocationsSameOrb() throws Exception {
        Assert.assertEquals(null, newRemoteImpl(serverORB).bounce(null));
        Assert.assertEquals("", newRemoteImpl(serverORB).bounce(""));
        Assert.assertEquals("a", newRemoteImpl(serverORB).bounce("a"));
        Assert.assertEquals("ab", newRemoteImpl(serverORB).bounce("ab"));
        Assert.assertEquals("abc", newRemoteImpl(serverORB).bounce("abc"));
        Assert.assertEquals("abcd", newRemoteImpl(serverORB).bounce("abcd"));
        Assert.assertEquals("abcde", newRemoteImpl(serverORB).bounce("abcde"));
    }

    private TheInterface newRemoteImpl(ORB callerOrb) throws Exception {
        TheImpl theImpl = new TheImpl();
        theImpl.publish(serverORB);
        // bind it into the naming context
        Util.getNameService(serverORB).rebind(OBJECT_NAME, theImpl.thisObject());
        // look it up from the caller orb
        Object stub = Util.getNameService(callerOrb).resolve(OBJECT_NAME);
        return (TheInterface)narrow(stub, TheInterface.class);
    }

    public interface TheInterface extends Remote {
        String bounce(String text) throws RemoteException;
    }

    private static class TheImpl extends Skellington implements TheInterface {
        @Override
        protected OutputStream dispatch(String method, InputStream in, ResponseHandler reply) throws RemoteException {
            switch (method) {
                case "bounce":
                    String result = bounce((String) in.read_value(String.class));
                    OutputStream out = reply.createReply();
                    ((org.omg.CORBA_2_3.portable.OutputStream) out).write_value(result, String.class);
                    return out;
                default:
                    throw new BAD_OPERATION();
            }
        }

        @Override
        public String bounce(String s) {return s;}
    }

    public static class DummyInterceptor extends LocalObject implements ORBInitializer, ServerRequestInterceptor {

        @Override
        public String name() {
            return "DummyInterceptor";
        }

        @Override
        public void destroy() {}

        @Override
        public void pre_init(ORBInitInfo info) {}

        @Override
        public void post_init(ORBInitInfo info) {
            try {
                info.add_server_request_interceptor(this);
            } catch (DuplicateName duplicateName) {
                throw new Error(duplicateName);
            }
        }

        @Override
        public void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest {}

        @Override
        public void receive_request(ServerRequestInfo ri) throws ForwardRequest {}

        @Override
        public void send_reply(ServerRequestInfo ri) {}

        @Override
        public void send_exception(ServerRequestInfo ri) throws ForwardRequest {}

        @Override
        public void send_other(ServerRequestInfo ri) throws ForwardRequest {}
    }


    private static class Util {

        private static int getPort(ORB orb) throws Exception {
            POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            POAManager poaMgr = POAManagerHelper.narrow(rootPoa.the_POAManager());
            for (Acceptor acceptor : poaMgr.get_acceptors()) {
                AcceptorInfo info = AcceptorInfoHelper.narrow(acceptor.get_info());
                if (info != null) return (char) info.port();
            }
            throw new Error("No IIOP Acceptor found");
        }

        private static String getNameServerUrl(ORB orb) throws Exception {
            return "corbaname::localhost:" + getPort(orb);
        }

        private static ORB createServerOrb() throws Exception {
            Properties serverProps = new Properties();
            serverProps.put(NameServiceInitializer.NS_ORB_INIT_PROP, "");
            serverProps.put(NameServiceInitializer.NS_REMOTE_ACCESS_ARG, RemoteAccess.readWrite.toString());
            serverProps.put(ORBInitializer.class.getName() + "Class." + DummyInterceptor.class.getName(), "");
            ORB orb =  ORB.init((String[])null, serverProps);
            POAHelper.narrow(orb.resolve_initial_references("RootPOA")).the_POAManager().activate();
            return orb;
        }

        private static ORB createClientORB(ORB targetORB) throws Exception {
            return ORB.init(new String[]{"-ORBInitRef", "NameService=" + getNameServerUrl(targetORB)}, null);
        }

        private static NamingContextExt getNameService(ORB orb) throws Exception {
            return NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
        }
    }
}
