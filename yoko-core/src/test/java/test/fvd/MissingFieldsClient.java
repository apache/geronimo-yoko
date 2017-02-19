package test.fvd;

import java.io.BufferedReader;
import java.io.IOException;

import org.omg.CORBA.ORB;

import test.common.TestBase;

public class MissingFieldsClient extends TestBase {

    private static final ApeClassLoader apeLoader = new ApeClassLoader().doNotLoad();

    public static void main(String...args) throws Exception {
        if (apeLoader.apeMain(args))
            return;
        ////////////////////// CODE BELOW HERE EXECUTES IN APE LOADER ONLY //////////////////////
        final String refFile = args[0];
        Marshalling.valueOf(args[1]).select(); // client version
        Marshalling.valueOf(args[2]);          // server version

        ORB orb = ORB.init(args, null);

        try (BufferedReader file = openFileReader(refFile)) {
            Bouncer bouncer = readRmiStub(orb, file, Bouncer.class);
            try {
                Bounceable bounceable = new BounceableImpl().validateAndReplace();
                Bounceable returned = (Bounceable) bouncer.bounceObject(bounceable);
                returned.validateAndReplace();
            } finally {
                bouncer.shutdown();
            }
        } finally {
            orb.shutdown(true);
            orb.destroy();
        }
    }
}
