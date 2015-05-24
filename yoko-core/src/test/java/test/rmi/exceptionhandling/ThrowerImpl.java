package test.rmi.exceptionhandling;

import java.rmi.RemoteException;

public class ThrowerImpl implements Thrower {
    @Override
    public void throwAppException() throws RemoteException, MyAppException {
        throw new MyAppException();
    }

    @Override
    public void throwRuntimeException() throws RemoteException {
        throw new MyRuntimeException();
    }
}