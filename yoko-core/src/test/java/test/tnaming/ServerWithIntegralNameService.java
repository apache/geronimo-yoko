package test.tnaming;

import java.util.Properties;

import org.apache.yoko.orb.CosNaming.tnaming2.NameServiceInitializer;

public class ServerWithIntegralNameService {
    public static void main(String args[]) throws Exception {
        final String refFile = args[0];
        System.out.println("1");
        Properties props = new Properties();
        System.out.println("2");
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + NameServiceInitializer.class.getName(), "");
        System.out.println("3");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        System.out.println("4");
        try (Server s = new Server(refFile, props)) {
            System.out.println("5");
            s.run();
        }
    }
}
