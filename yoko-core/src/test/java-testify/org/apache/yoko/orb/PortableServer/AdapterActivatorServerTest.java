package org.apache.yoko.orb.PortableServer;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureServer;

import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_SERVANT_MANAGER;
import static org.junit.Assert.fail;

@ConfigureServer
public class AdapterActivatorServerTest {

    @ConfigureOrb
    public static class AdapterActivatorTest {

        @Test
        void testAdapterActivator(POA root) throws Exception {
            POAManager rootMgr = root.the_POAManager();
            assertNotNull(rootMgr);

            TestAdapterActivator activator = new TestAdapterActivator();
            root.the_activator(activator);

            // Test: Activator and successful creation
            activator.reset("poa1", true);
            POA poa = root.find_POA("poa1", true);

            assertNotNull(poa);
            assertTrue(activator.invoked());
            String str = poa.the_name();
            assertEquals("poa1", str);
            POA parent = poa.the_parent();
            assertNotNull(parent);
            assertTrue(parent._is_equivalent(root));

            // Test: Activator and unsuccessful creation
            activator.reset("poa2", false);
            assertThrows(AdapterNonExistent.class, () -> root.find_POA("poa2", true));
            assertTrue(activator.invoked());

            // Test: Make sure activator isn't called when POA already exists
            activator.reset("poa1", true);
            poa = root.find_POA("poa1", true);

            assertTrue(!activator.invoked());

            // Test: Disable adapter activator and make sure it isn't invoked
            root.the_activator(null);
            activator.reset("poa2", false);
            assertThrows(AdapterNonExistent.class, () -> root.find_POA("poa2", true));
            assertFalse(activator.invoked());

            poa.destroy(true, true);
        }
    }

    @ConfigureServer.BeforeServer
    public static void setup(ORB orb, POA root, Bus bus) throws Exception {
        POAManager manager = root.the_POAManager();
        assertNotNull(manager);

        root.the_activator(new TestAdapterActivator());

        // First create an object-reference to the test POA. Then destroy the POA
        // so that the adapter activator will activate the POA when necessary.
        POA parentPoa = create_POA("parentPoa", root, manager, PERSISTENT, USER_ID, USE_SERVANT_MANAGER);
        POAManager poa3Manager = parentPoa.the_POAManager();
        POA childPoa = create_POA("childPoa", parentPoa, poa3Manager, PERSISTENT, USER_ID, USE_SERVANT_MANAGER);
        org.omg.CORBA.Object ref = childPoa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0");
        String ior = orb.object_to_string(ref);

        childPoa.destroy(true, true);
        parentPoa.destroy(true, true);

//        root.the_activator(new TestRemoteAdapterActivator(orb));
        bus.put("test1", ior);

    }

    @Test
    void test1(ORB orb, Bus bus) {
        TestHelper.narrow(orb.string_to_object(bus.get("test1"))).aMethod(); }


    // Classes for testing the adapter activator on a remote call.
    final static class TestServantActivator extends LocalObject implements ServantActivator {
        private final ORB orb;

        TestServantActivator(ORB orb) {
            this.orb  = orb;
        }

        public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            String oidString = new String(oid);
            // If the user is requesting the object "test1" then oblige
            Servant servant = null;
            if (oidString.equals("test1"))
                servant = new Test_impl(orb, "test1", false);
            if (servant != null) {
                // Verify that POA allows activator to explicitly activate a servant
                try {
                    poa.activate_object_with_id(oid, servant);
                    return servant;
                } catch (ObjectAlreadyActive | ServantAlreadyActive | WrongPolicy ex) {
                    throw new RuntimeException();
                }
            }
            // Fail
            throw new OBJECT_NOT_EXIST();
        }

        public void etherealize(byte[] oid, POA poa, Servant servant, boolean cleanup, boolean remaining) {
            if(remaining) return;
            String oidString = new String(oid);
            // If the user is requesting the object "test1" then oblige.
            if (new String(oid).equals("test1")) servant = null;
        }
    }

    final static class TestAdapterActivator extends LocalObject implements AdapterActivator {
        private String expectedName;
        private boolean create;
        private boolean invoked;

        TestAdapterActivator() {
            create = false;
            invoked = false;
        }

        void reset(String name, boolean create) {
            expectedName = name;
            this.create = create;
            invoked = false;
        }

        boolean invoked() { return invoked; }

        public boolean unknown_adapter(POA parent, String name) {
            assertEquals(name, expectedName);
            invoked = true;
            if(!create) return false;
            POAManager mgr = parent.the_POAManager();
            try {
                create_POA(name, parent, mgr);
            } catch (AdapterAlreadyExists | InvalidPolicy ex) {
                fail();
            }
            return true;
        }
    }

    final static class TestRemoteAdapterActivator extends LocalObject implements AdapterActivator {
        private final ORB orb;

        private TestServantActivator activator;

        TestRemoteAdapterActivator(ORB orb) {
            this.orb = orb;
            activator = new TestServantActivator(this.orb);
        }

        public boolean unknown_adapter(POA parent, String name) {
            if (! (name.equals("poa3") || name.equals("poa4"))) return false;
            POAManager mgr = parent.the_POAManager();
            POA poa = null;
            try {
                poa = create_POA(name, parent, mgr, PERSISTENT, USER_ID, USE_SERVANT_MANAGER);
            } catch (InvalidPolicy | AdapterAlreadyExists e) {
                throw new RuntimeException(e);
            }


            ServantActivator activator = this.activator;
            if (name.equals("poa3")) {
                AdapterActivator me = (AdapterActivator) this.orb;
                poa.the_activator(me);
            } else {
                try {
                    poa.set_servant_manager(activator);
                } catch (WrongPolicy ex) {
                    fail();
                }
            }
            return true;

        }
    }
}
