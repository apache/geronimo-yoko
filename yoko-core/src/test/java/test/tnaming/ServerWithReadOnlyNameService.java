package test.tnaming;

import java.util.Properties;

import org.apache.yoko.orb.CosNaming.tnaming2.NameServiceReadOnlyInitializer;

public class ServerWithReadOnlyNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        Properties props = new Properties();
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + NameServiceReadOnlyInitializer.class.getName(), "");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        try (Server s = new Server(refFile, props)) {
            Util.createBindingsOverWhichToIterate(s.orb, s.rootNamingContext);
            s.run();
        }
    }

}
