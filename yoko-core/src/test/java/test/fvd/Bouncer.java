package test.fvd;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bouncer extends Remote {
    Abstract bounceAbstract(Abstract obj) throws RemoteException;
    Object bounceObject(Object obj) throws RemoteException;
    Serializable bounceSerializable(Serializable obj) throws RemoteException;
    Value bounceValue(Value obj) throws RemoteException;
    void shutdown() throws RemoteException;
}
