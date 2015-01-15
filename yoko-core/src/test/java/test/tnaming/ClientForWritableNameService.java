package test.tnaming;

import java.util.Properties;
import static test.tnaming.Client.NameServiceAccessibility.*;

public class ClientForWritableNameService {
    public static void main(String args[]) throws Exception {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        try (Client client = new Client(WRITABLE, props, "-ORBInitRef", "NameService=" + Util.NS_LOC)) {
            Util.createBindingsOverWhichToIterate(client.orb, client.rootNamingContext);
            client.run();
        }
    }

}
