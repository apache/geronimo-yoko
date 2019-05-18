package test.util;

import org.junit.Assert;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Skellington extends Servant implements Tie, Remote {
    private final Collection<Class<? extends Remote>> interfaceClasses;
    private final String[] ids;

    public Skellington() {
        Set<Class<? extends Remote>> ifaces = new HashSet<>();
        for (Class<?> c = this.getClass(); c != Object.class; c = c.getSuperclass()) {
            NEXT_CLASS: for (Class<?> iface: c.getInterfaces()) {
                if (Remote.class.isAssignableFrom(iface)) {
                    for (Method m : iface.getMethods()) {
                        if (Arrays.asList(m.getExceptionTypes()).contains(RemoteException.class))
                            continue;
                        continue NEXT_CLASS;
                    }
                    // there were no non-remote methods, so add the interface
                    ifaces.add((Class<? extends Remote>)iface);
                }
            }
        }
        final ValueHandler vh = Util.createValueHandler();
        this.interfaceClasses = Collections.unmodifiableSet(ifaces);
        this.ids = new String[interfaceClasses.size()];
        int index = 0;
        for (Class<?> c : interfaceClasses)
            this.ids[index++] = vh.getRMIRepositoryID(c);
    }

    public Skellington(Class<? extends Remote>... interfaces) {
        final ValueHandler vh = Util.createValueHandler();
        ids = new String[interfaces.length];
        List<Class<? extends Remote>> iflst = new ArrayList<>();
        for (int i = 0; i < interfaces.length; i++) {
            Assert.assertTrue(interfaces[i].isInterface());
            iflst.add(interfaces[i]);
            ids[i] = vh.getRMIRepositoryID(interfaces[i]);
        }
        this.interfaceClasses = Collections.unmodifiableList(iflst);
    }

    @Override
    public String[] _all_interfaces(POA poa, byte[] objectId) {
        return ids.clone();
    }

    @Override
    public org.omg.CORBA.Object thisObject() {
        return _this_object();
    }

    @Override
    public void deactivate() throws NoSuchObjectException {
        try{
            _poa().deactivate_object(_poa().servant_to_id(this));
        } catch (WrongPolicy |ObjectNotActive |ServantNotActive ignored){}
    }

    @Override
    public ORB orb() {return _orb();}

    @Override
    public void orb(ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        } catch(ClassCastException e) {
            throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
        }
    }

    @Override
    public void setTarget(Remote target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Remote getTarget() {
        return this;
    }

    @Override
    public OutputStream  _invoke(String method, InputStream _in, ResponseHandler reply) throws SystemException {
        try {
            return dispatch(method, (org.omg.CORBA_2_3.portable.InputStream) _in, reply);
        } catch (SystemException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnknownException(ex);
        }
    }

    public String publish(ORB serverORB) throws InvalidName, AdapterInactive, ServantAlreadyActive, WrongPolicy {
        POA rootPOA = POAHelper.narrow(serverORB.resolve_initial_references("RootPOA"));
        rootPOA.the_POAManager().activate();
        rootPOA.activate_object(this);
        return serverORB.object_to_string(thisObject());
    }

    protected abstract OutputStream dispatch(String method, org.omg.CORBA_2_3.portable.InputStream in, ResponseHandler reply) throws RemoteException;
}
