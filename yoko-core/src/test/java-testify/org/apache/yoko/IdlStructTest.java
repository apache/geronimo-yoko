/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.yoko;

import acme.Product;
import acme.RemoteFunction;
import org.junit.jupiter.api.Test;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.RemoteImpl;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ConfigureServer
public class IdlStructTest {
    interface Discount extends RemoteFunction<Product, Product>{}

    @RemoteImpl
    public static final Discount REMOTE = p-> new Product(p.name,p.price*0.9f);

    @Test
    public void sendProduct(Discount discount) throws RemoteException {
        final String productName = "ORB-Buster\u00ae";
        System.out.println(productName);
        Product result = discount.apply(new Product(productName, 50f));
        assertEquals(45f, result.price, "String should be transmitted and received correctly");
        assertEquals(productName, result.name, "String should be transmitted and received correctly");
    }


}
