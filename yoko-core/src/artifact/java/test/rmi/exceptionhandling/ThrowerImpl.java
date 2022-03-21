package test.rmi.exceptionhandling;

import java.rmi.RemoteException;

import org.omg.CORBA.ORB;

public class ThrowerImpl implements Thrower {
    public static MyRuntimeException myRuntimeException;
    public static MyAppException myAppException;
    final ORB orb;

    public ThrowerImpl(ORB orb) {
        this.orb = orb;
    }

    @Override
    public void throwAppException() throws RemoteException, MyAppException {
        throw myAppException;
    }

    @Override
    public void throwRuntimeException() throws RemoteException {
        throw myRuntimeException;
    }
}
