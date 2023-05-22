/*
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
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
