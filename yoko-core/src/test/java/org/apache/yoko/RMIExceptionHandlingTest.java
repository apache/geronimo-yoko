package org.apache.yoko;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.rmi.PortableRemoteObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import test.rmi.exceptionhandling.MyAppException;
import test.rmi.exceptionhandling.MyClientRequestInterceptor;
import test.rmi.exceptionhandling.MyRuntimeException;
import test.rmi.exceptionhandling.MyServerRequestInterceptor;
import test.rmi.exceptionhandling.Thrower;
import test.rmi.exceptionhandling.ThrowerImpl;
import test.rmi.exceptionhandling._ThrowerImpl_Tie;

@SuppressWarnings("serial")
public class RMIExceptionHandlingTest {
    private static ORB serverOrb;
    private static ORB clientOrb;
    private static String ior;

    private static ORB initOrb(Properties props, String... args) {
        return ORB.init(args, props);
    }

    @BeforeClass
    public static void createServerORB() throws Exception {
        serverOrb = initOrb(new Properties() {{
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyClientRequestInterceptor.class.getName(),"");
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyServerRequestInterceptor.class.getName(),"");
        }});
        POA poa = POAHelper.narrow(serverOrb.resolve_initial_references("RootPOA"));
        poa.the_POAManager().activate();

        _ThrowerImpl_Tie tie = new _ThrowerImpl_Tie();
        tie.setTarget(new ThrowerImpl());

        poa.activate_object(tie);
        ior = serverOrb.object_to_string(tie.thisObject());
        System.out.println(ior);
    }

    @BeforeClass
    public static void createClientORB() {
        clientOrb = initOrb(new Properties() {{
            put("org.omg.PortableInterceptor.ORBInitializerClass." + MyClientRequestInterceptor.class.getName(),"");
        }});
    }

    @AfterClass
    public static void shutdownServerORB() {
        serverOrb.shutdown(true);
        serverOrb.destroy();
        ior = null;
    }

    @AfterClass
    public static void shutdownClientORB() {
        clientOrb.shutdown(true);
        clientOrb.destroy();
    }

    @Test(expected=MyRuntimeException.class)
    public void testRuntimeException() throws RemoteException {
        getThrower(clientOrb).throwRuntimeException();
    }

    @Test(expected=MyAppException.class)
    public void testAppException() throws RemoteException, MyAppException {
        getThrower(clientOrb).throwAppException();
    }

    private Thrower getThrower(ORB orb) {
        Object o = orb.string_to_object(ior);
        Thrower thrower = (Thrower) PortableRemoteObject.narrow(o, Thrower.class);
        return thrower;
    }
}
