package test.osgi;

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class OrbInitTest {
    @BeforeClass
    public static void logWhereTheOrbClassComesFrom() {
        System.out.println("ORB API class = " + ORB.class);
        System.out.println("ORB API class loader = " + ORB.class.getClassLoader());
        System.out.println("ORB impl class = " + org.apache.yoko.orb.CORBA.ORB.class);
        System.out.println("ORB impl class loader = " + org.apache.yoko.orb.CORBA.ORB.class.getClassLoader());
    }

    @Test
    public void testORBSingleton() {
        ORB orb1 = ORB.init();
        assertThat(orb1, is(notNullValue()));
        ORB orb2 = ORB.init();
        assertThat(orb2, is(orb1));
    }

    @Test
    public void testORBNoProps() {
        final ORB orb = ORB.init((String[]) null, null);
        assertThat(orb, is(notNullValue()));
    }

    @Test(expected = NO_IMPLEMENT.class)
    public void testORBSingletonDestroy() {
        ORB.init().destroy();
    }

    @Test
    public void testORBExplicitClass() {
        final ORB orb = ORB.init((String[]) null, new Properties() {{
            put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        }});
        assertThat(orb, is(notNullValue()));
    }
}

