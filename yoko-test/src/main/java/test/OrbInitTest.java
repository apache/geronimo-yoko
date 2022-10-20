package test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static test.OrbInitTest.createOrb;

public class OrbInitTest {
    @BeforeClass
    public static void logWhereTheOrbClassComesFromAtRuntime() {
        System.out.println("ORB API class = " + ORB.class);
        System.out.println("ORB API class loader = " + ORB.class.getClassLoader());
        System.out.println("ORB impl class = " + org.apache.yoko.orb.CORBA.ORB.class);
        System.out.println("ORB impl class loader = " + org.apache.yoko.orb.CORBA.ORB.class.getClassLoader());
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

    @Test
    public void testORBSingletonIsTheSameInstance() {
        ORB orb1 = ORB.init();
        assertThat(orb1, is(notNullValue()));
        ORB orb2 = ORB.init();
        assertThat(orb2, is(orb1));
    }

    @Test
    public void testORBNoProps() {
        final ORB orb = createOrb();
        assertThat(orb, is(notNullValue()));
    }

    @Test(expected = NO_IMPLEMENT.class)
    public void testORBSingletonDestroy() {
        ORB.init().destroy();
    }

    @Test
    public void testORBExplicitClass() {
        final ORB orb = createOrb(props("org.omg.CORBA.ORBClass","org.apache.yoko.orb.CORBA.ORB"));
        assertThat(orb, is(notNullValue()));
    }
}
