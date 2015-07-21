package test.fvd;

import static test.fvd.Marshalling.VERSION1;
import static test.fvd.Marshalling.VERSION2;

import java.io.File;

import org.apache.yoko.AbstractOrbTestBase;

public class MissingFields {
    public static void main(String[] args) throws Exception {
        final String fileName = "bouncer.ref";
        final String[] serverArgs = { fileName, VERSION1.name(), VERSION2.name() };
        final String[] clientArgs = serverArgs;
        new Thread() {
            @Override
            public void run() {
                MissingFieldsServer.main(serverArgs);
            }
        }.start();
        AbstractOrbTestBase.waitFor(new File(fileName), 20000);
        Thread.sleep(2000);
        MissingFieldsClient.main(clientArgs);
    }
}
