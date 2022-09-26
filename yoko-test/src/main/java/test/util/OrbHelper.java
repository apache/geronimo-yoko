package test.util;

import org.omg.CORBA.ORB;

import java.util.Properties;

public enum OrbHelper {
    ;
    /** Get the Singleton orb */
    public static ORB getSingletonOrb() {
        return ORB.init();
    }

    /** Create a non-singleton orb without specifying any properties */
    public static ORB createOrb(String...params){
        return createOrb(null, params);
    }

    /** Create a non-singleton orb */
    public static ORB createOrb(Properties props, String...params){
        return ORB.init(params, props);
    }

    public static Properties props(String...props) {
        Properties result = new Properties();
        String key = null;
        for (String s: props) {
            if (key == null) {
                key = s;
            } else {
                result.setProperty(key, s);
                key = null;
            }
        }
        return result;
    }

}
