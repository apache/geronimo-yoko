package com.acme.hello;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class HelloServer {
    static {
        // allow the registry to hold acme objects
        System.setProperty("sun.rmi.registry.registryFilter", "java.**;com.acme.**");
    }
    static HelloImpl impl;
    static Remote stub;
    static Registry registry;
    static int lastUsedPort = -1;
    static volatile String bindPoint;

    enum SocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory {
        INSTANCE;

        public Socket createSocket(String host, int port) throws IOException {
            return new Socket(host, port);
        }

        public ServerSocket createServerSocket(int port) throws IOException {
            ServerSocket serverSocket = new ServerSocket(port);
            lastUsedPort = serverSocket.getLocalPort();
            return serverSocket;
        }
    }

    public static void main(String[] args) {
        int requestedPort = Optional.of(args)
                .filter(arr -> arr.length > 0)
                .map(arr -> arr[0])
                .map(Integer::parseInt)
                .orElse(0);
        int port = start(requestedPort);
        System.out.println("HelloServer started on port " + port);
    }

    public static synchronized int start(int port) {
        try {
            impl = new HelloImpl();
            stub = UnicastRemoteObject.exportObject(impl, port, SocketFactory.INSTANCE, SocketFactory.INSTANCE);
            registry = LocateRegistry.createRegistry(lastUsedPort, SocketFactory.INSTANCE, SocketFactory.INSTANCE);
            bindPoint = "//localhost:" + lastUsedPort + "/MessengerService";
            Naming.rebind(bindPoint, stub);
            System.out.println("Stub and registry Created and bound.");
        } catch (Exception e) {
            System.out.println("HelloServer err: " + e.getMessage());
            e.printStackTrace();
        }
        return lastUsedPort;
    }

    public static synchronized void stop() {
        try {
            System.out.println(lastUsedPort);
            System.out.println(bindPoint);
            Naming.unbind(bindPoint);
            UnicastRemoteObject.unexportObject(impl, true);
            System.out.println("Impl stopped");
            UnicastRemoteObject.unexportObject(registry, true);
            System.out.println("Registry stopped");
        } catch (Exception e) {
            System.out.println("HelloServer err: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
