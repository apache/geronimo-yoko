/*
 * Copyright 2023 IBM Corporation and others.
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
package org.apache.yoko;

import acme.Product;
import acme.RemoteFunction;
import org.junit.jupiter.api.Test;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.RemoteImpl;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ConfigureServer
public class IdlStructTest {
    interface Register extends RemoteFunction<Product, Product>{}

    private static final String REGISTERED_TRADEMARK = "\u00ae";

    @RemoteImpl
    public static final Register REMOTE = p -> new Product(p.name,p.price);

    @Test
    public void sendProduct(Register register) throws RemoteException {
        final String productName = "ORB-Buster" + REGISTERED_TRADEMARK;
        System.out.println(productName);
        Product result = register.apply(new Product(productName, 50f));
        assertEquals(50f, result.price, "Float should be transmitted and received correctly");
        assertEquals(productName, result.name, "String should be transmitted and received correctly");
    }
}
