package testify.iiop.annotation;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.opentest4j.AssertionFailedError;
import testify.annotation.RetriedTest;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ConfigureServer
public class ServerControlTest {
    public interface Echo extends Remote {
        String echo(String msg) throws RemoteException;
    }

    @ConfigureServer.Control
    public static ServerControl serverControl;

    @RemoteImpl
    public static final Echo IMPL = String::toString;

    @Test
    public void testServerControlButDoNothing(ORB clientOrb) throws Exception {}

    @Test
    public void testServerControlLeaveServerStopped(ORB clientOrb) throws Exception {
        serverControl.stop();
    }

    @RetriedTest(maxRuns = 127) // Try this many times to flush out build environment problems
    public void testServerControl(ORB clientOrb, Echo stub) throws Exception {
        assertEquals("hello", stub.echo("hello"));
        assertThrows(AssertionFailedError.class, serverControl::start);
        serverControl.stop();
        assertThrows(RemoteException.class, () -> stub.echo(""));
        assertThrows(AssertionFailedError.class, serverControl::stop);
        assertThrows(AssertionFailedError.class, serverControl::restart);
        serverControl.start();
        assertEquals("hello again", stub.echo("hello again"));
        serverControl.restart();
        assertEquals("hello once more", stub.echo("hello once more"));
        serverControl.stop();
        assertThrows(Exception.class, () -> stub.echo("hello? anyone home?"));
        serverControl.start();
        assertEquals("again I say hello", stub.echo("again I say hello"));
    }
}
