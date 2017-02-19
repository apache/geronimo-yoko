package test.fvd;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import test.common.TestBase;

public class MissingFieldsServer extends TestBase {
    static final String CLASS_NAME = new Object(){}.getClass().getEnclosingClass().getName();

    private static final ApeClassLoader apeLoader = new ApeClassLoader().doNotLoad();

    public static void main(String...args) {
        if (apeLoader.apeMain(args)) return;
        ////////////////////// CODE BELOW HERE EXECUTES IN APE LOADER ONLY //////////////////////
        final String refFile = args[0];
        Marshalling.valueOf(args[1]);          // client version
        Marshalling.valueOf(args[2]).select(); // server version

        ORB orb = ORB.init(args, null);
        System.out.println(CLASS_NAME + " opening file for writing: " + Paths.get(refFile).toAbsolutePath());
        try (PrintWriter out = new PrintWriter(new FileWriter(refFile))) {
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();
            ////// create a Bouncer object and write out the IOR //////
            BouncerImpl bouncer = new BouncerImpl(orb);
            _BouncerImpl_Tie tie = new _BouncerImpl_Tie();
            tie.setTarget(bouncer);
            rootPOA.activate_object(tie);
            writeRef(orb, out, tie.thisObject());
            out.flush();
        } catch (IOException | InvalidName | AdapterInactive | ServantAlreadyActive | WrongPolicy e) {
            e.printStackTrace();
        }
        System.out.println("Running the orb");
        orb.run();
        System.out.println("Destroying the orb");
        orb.destroy();
    }
}
