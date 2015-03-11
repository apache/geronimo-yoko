package test.tnaming;

import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_ORB_INIT_PROP;
import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_REMOTE_ACCESS_ARG;
import static org.apache.yoko.orb.spi.naming.RemoteAccess.readWrite;

import java.util.Properties;

public class ServerWithReadWriteIntegralNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        System.out.println("1");
        Properties props = new Properties();
        System.out.println("2");
        props.put(NS_ORB_INIT_PROP, "");
        System.out.println("3");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        System.out.println("4");
        try (Server s = new Server(refFile, props, NS_REMOTE_ACCESS_ARG, readWrite.name())) {
            System.out.println("5");
            s.bindObjectFactories();
            s.run();
            
        }
    }
}
