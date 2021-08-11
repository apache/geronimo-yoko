package versioned;

import acme.Processor;

import java.rmi.RemoteException;

public interface VersionedProcessor extends Processor {
    String getVersion() throws RemoteException;
}
