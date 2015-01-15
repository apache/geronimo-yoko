package test.tnaming;

import static test.tnaming.Client.NameServiceAccessibility.READ_ONLY;

import java.util.Properties;

public class ClientForReadOnlyNameService {
    public static void main(String args[]) throws Exception {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        try (Client client = new Client(READ_ONLY, props, "-ORBInitRef", "NameService=" + Util.NS_LOC )) {
            client.run();
        }
    }

}
