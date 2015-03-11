package test.tnaming;

import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_ORB_INIT_PROP;
import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.NS_REMOTE_ACCESS_ARG;
import static org.apache.yoko.orb.spi.naming.RemoteAccess.readOnly;

import java.util.Properties;

public class ServerWithReadOnlyIntegralNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        Properties props = new Properties();
        props.put(NS_ORB_INIT_PROP, "");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        try (Server s = new Server(refFile, props, NS_REMOTE_ACCESS_ARG, readOnly.name())) {
            Util.createBindingsOverWhichToIterate(s.orb, s.rootNamingContext);
            s.bindObjectFactories();
            s.run();
            
        }
    }
}
