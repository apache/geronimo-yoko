package test.tnaming;

import java.util.Properties;

import org.apache.yoko.orb.CosNaming.tnaming2.NameServiceInitializer;

public class ServerWithIntegralNameService {
    public static final String REF_FILE_NAME = ServerWithIntegralNameService.class.getName() + ".ref";

    public static void main(String args[]) throws Exception {
        System.out.println("1");
        Properties props = new Properties();
        System.out.println("2");
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + NameServiceInitializer.class.getName(), "");
        System.out.println("3");
        props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port " + Util.NS_PORT);
        System.out.println("4");
        try (Server s = new Server(props)) {
            System.out.println("5");
            s.run(REF_FILE_NAME);
        }
    }
}
