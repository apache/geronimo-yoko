/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package yoko.client;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl11.WSDLManagerImpl;

public final class Client {

    private static final Logger LOG =
        Logger.getLogger(Client.class.getPackage().getName());

    private static final QName SERVICE_NAME 
        = new QName("http://schemas.apache.org/yoko/idl/Bank", "BankCORBAService");
    
    private Client() {
    }

    public static void main(String args[]) throws Exception {
        
        LOG.log(Level.INFO, "Resolving the bank object");
        URL wsdlUrl = new URL("file:./BankWS-corba.wsdl");
        BankCORBAService service = new BankCORBAService(wsdlUrl, SERVICE_NAME);
        Bank port = service.getBankCORBAPort();

        // Test the method Bank.createAccount()
        System.out.println("Creating account called \"Account1\"");
        EndpointReferenceType epr1 = port.createAccount("Account1");
        Account account1 = getAccountFromEPR(epr1);
        System.out.println("Depositing 100.00 into account \'Account1\"");
        account1.deposit(100.00f);
        System.out.println("Current balance of account \"Account1\" is " + account1.getBalance());
        System.out.println();

        /* Re-enable when we have a utility to manipulate the meta data stored 
           within the EPR. 
        // Test the method Bank.createEprAccount()
        System.out.println("Creating account called \"Account2\"");
        EndpointReferenceType epr2 = port.createEprAccount("Account2");
        Account account2 = getAccountFromEPR(epr2);
        System.out.println("Depositing 5.00 into account \'Account2\"");
        account2.deposit(5.00f);
        System.out.println("Current balance of account \"Account2\" is " + account2.getBalance());
        System.out.println();
        */

        // create two more accounts to use with the getAccount calls
        Account acc3 = getAccountFromEPR(port.createAccount("Account3"));
        acc3.deposit(200.00f);
        Account acc4 = getAccountFromEPR(port.createAccount("Account4"));
        acc4.deposit(400.00f);
        
        // Test the method Bank.getAccount()
        System.out.println("Retrieving account called \"Account3\"");
        EndpointReferenceType epr3 = port.getAccount("Account3");
        Account account3 = getAccountFromEPR(epr3);
        System.out.println("Current balance for \"Account3\" is " + account3.getBalance());
        System.out.println("Depositing 10.00 into account \"Account3\"");
        account3.deposit(10.00f);
        System.out.println("New balance for account \"Account3\" is " + account3.getBalance());
        System.out.println();

        /* Re-enable when we have a utility to manipulate the meta data stored 
           within the EPR. 
        // Test the method Bank.getEprAccount()
        System.out.println("Retrieving account called \"Account4\"");
        EndpointReferenceType epr4 = port.getEprAccount("Account4");
        Account account4 = getAccountFromEPR(epr4);
        System.out.println("Current balance for account \"Account4\" is " + account4.getBalance());
        System.out.println("Withdrawing 150.00 into account \"Account4\"");
        account4.deposit(-150.00f);
        System.out.println("New balance for account \"Account4\" is " + account4.getBalance());
        System.out.println();
        */

        port.removeAccount("Account1");
        port.removeAccount("Account3");
        port.removeAccount("Account4");

        System.exit(0);
    }

    private static Account getAccountFromEPR(EndpointReferenceType epr) {

        WSDLManager manager = null;
        try {
            manager = new WSDLManagerImpl();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unable to create WSDLManager");
            System.exit(1);
        }

        QName interfaceName = EndpointReferenceUtils.getInterfaceName(epr);
        String wsdlLocation = EndpointReferenceUtils.getWSDLLocation(epr);
        QName serviceName = EndpointReferenceUtils.getServiceName(epr);
        String portName = EndpointReferenceUtils.getPortName(epr);

        QName port = new QName(serviceName.getNamespaceURI(), portName);

        StringBuffer seiName = new StringBuffer();
        seiName.append("yoko.client.");
        seiName.append(JAXBUtils.nameToIdentifier(interfaceName.getLocalPart(),
                                                  JAXBUtils.IdentifierType.INTERFACE));

        Class<?> sei = null;
        try {
            sei = Class.forName(seiName.toString(), true, manager.getClass().getClassLoader());
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, "Unable to obtain SEI class");
            System.exit(1);
        }

        URL wsdlURL = null;
        try {
            wsdlURL = new URL(wsdlLocation);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unable to create URL for WSDL");
            System.exit(1);
        }

        Service service = Service.create(wsdlURL, serviceName);
        Account account = (Account)service.getPort(port, sei);

        Map<String, Object> requestContext = ((BindingProvider)account).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epr.getAddress().getValue());

        return account;
    }
}
