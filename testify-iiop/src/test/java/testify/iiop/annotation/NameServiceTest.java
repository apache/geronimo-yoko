package testify.iiop.annotation;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextHelper;

import javax.rmi.PortableRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testify.iiop.annotation.ConfigureOrb.NameService.READ_WRITE;

@ConfigureServer(serverOrb = @ConfigureOrb(nameService = READ_WRITE))
public class NameServiceTest {
    public interface Echo extends Remote {
        String echo(String s) throws RemoteException;
    }

    public static class EchoImpl implements Echo {
        public String echo(String s) { return s; }
    }

    @ConfigureServer.Control
    public static ServerControl serverControl;

    @ConfigureServer.NameServiceUrl
    public static String nameServiceUrl;

    @ConfigureServer.CorbanameUrl(EchoImpl.class)
    public static String stubUrl;

    /** Test the framework is functioning correctly */
    @Test
    public void testNameServiceStarted(ORB clientOrb) throws Exception {
        assertNotNull(nameServiceUrl);
        NamingContextHelper.narrow(clientOrb.string_to_object(nameServiceUrl));
    }

    /** Test the framework is functioning correctly */
    @Test
    public void testCorbanameUrl(ORB clientOrb) throws Exception {
        assertNotNull(stubUrl);
        Echo echo = toStub(stubUrl, clientOrb, Echo.class);
        echo.echo("wibble");
        String oldStubUrl = stubUrl;
        serverControl.restart();
        assertEquals(oldStubUrl, stubUrl);
        echo = toStub(stubUrl, clientOrb, Echo.class);
        echo.echo("splong");
    }

    public static <T> T toStub(String stringifiedForm, ORB orb, Class<T> intf) {
        Object o = orb.string_to_object(stringifiedForm);
        return intf.cast(PortableRemoteObject.narrow(o, intf));
    }
}
