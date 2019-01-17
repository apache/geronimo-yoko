package test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static test.util.OrbHelper.createOrb;
import static test.util.OrbHelper.getSingletonOrb;
import static test.util.OrbHelper.props;

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
        ORB orb1 = getSingletonOrb();
        assertThat(orb1, is(notNullValue()));
        ORB orb2 = getSingletonOrb();
        assertThat(orb2, is(orb1));
    }

    @Test
    public void testORBNoProps() {
        final ORB orb = createOrb();
        assertThat(orb, is(notNullValue()));
    }

    @Test(expected = NO_IMPLEMENT.class)
    public void testORBSingletonDestroy() {
        getSingletonOrb().destroy();
    }

    @Test
    public void testORBExplicitClass() {
        final ORB orb = createOrb(props("org.omg.CORBA.ORBClass","org.apache.yoko.orb.CORBA.ORB"));
        assertThat(orb, is(notNullValue()));
    }
}

