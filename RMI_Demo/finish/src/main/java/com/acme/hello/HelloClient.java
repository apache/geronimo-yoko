package com.acme.hello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloClient
{
    public static void main(String arg[])
    {
        String message = "blank";

        // I download server's stubs ==> must set a SecurityManager
//        System.setSecurityManager(new RMISecurityManager());

        try
        {
            Registry registry = LocateRegistry.getRegistry();
            Hello server = (Hello) registry
                    .lookup("MessengerService");
            String responseMessage = server.sayHello();
            String expectedMessage = "Hello world!";
        }
        catch (Exception e)
        {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
