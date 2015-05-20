package test.rmi.exceptionhandling;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Thrower extends Remote {
    void throwAppException() throws RemoteException, MyAppException;
    void throwRuntimeException() throws RemoteException;
}