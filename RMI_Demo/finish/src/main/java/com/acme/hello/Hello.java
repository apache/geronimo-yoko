package com.acme.hello;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {
    String sayHello() throws RemoteException;
    void setGreeting(String greeting) throws RemoteException;
}
