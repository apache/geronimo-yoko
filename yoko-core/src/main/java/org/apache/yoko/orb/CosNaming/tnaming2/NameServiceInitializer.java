package org.apache.yoko.orb.CosNaming.tnaming2;

import java.util.Arrays;

import org.apache.yoko.orb.OB.BootLocator;
import org.apache.yoko.orb.OB.BootManagerPackage.NotFound;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ObjectHolder;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NameServiceInitializer extends LocalObject implements ORBInitializer {
    abstract static class BootLocatorImpl extends LocalObject implements BootLocator {
    }

    private static final long serialVersionUID = 1L;

    boolean remoteReadOnly() {
        return false;
    }

    @Override
    public void pre_init(ORBInitInfo info) {
        try {
            final NamingContextImpl local = new NamingContextImpl();
            info.register_initial_reference("NameService", local);
        } catch (Exception e) {
            throw (INITIALIZE) (new INITIALIZE().initCause(e));
        }
    }

    @Override
    public void post_init(ORBInitInfo info) {
        try {
            final POA rootPOA = (POA) info.resolve_initial_references("RootPOA");
            final NamingContextImpl local = (NamingContextImpl) info.resolve_initial_references("NameService");
            final String serviceName = getServiceName(info);

            final org.apache.yoko.orb.OB.BootManager bootManager = org.apache.yoko.orb.OB.BootManagerHelper.narrow(info
                    .resolve_initial_references("BootManager"));
            final byte[] objectId = serviceName.getBytes();
            bootManager.set_locator(new BootLocatorImpl() {
                @Override
                public void locate(byte[] oid, ObjectHolder obj, BooleanHolder add) throws NotFound {
                    if (!!!Arrays.equals(oid, objectId))
                        throw new NotFound(new String(oid));

                    try {
                        rootPOA.the_POAManager().activate();
                        final Policy[] policies = new Policy[3];
                        policies[0] = rootPOA.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
                        policies[1] = rootPOA.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
                        policies[2] = rootPOA.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);

                        // create_POA must happen only once, so make sure to set
                        // add.value to true
                        final POA nameServicePOA = rootPOA.create_POA("TNameService", null, policies);
                        nameServicePOA.the_POAManager().activate();

                        final Servant nameServant = local.getServant(nameServicePOA, remoteReadOnly());

                        obj.value = nameServant._this_object();
                        // tell the boot manager to re-use this result so we
                        // only get called once
                        add.value = true;
                    } catch (Exception e) {
                        throw (NotFound) (new NotFound("Unexpected").initCause(e));
                    }
                }
            });
        } catch (Exception e) {
            throw (INITIALIZE) (new INITIALIZE().initCause(e));
        }
    }

    private String getServiceName(ORBInitInfo info) {
        for (String arg : info.arguments()) {
            if (arg.startsWith("ORBNameService=")) {
                return arg.substring("ORBNameService=".length());
            }
        }
        return "NameService";
    }
}
