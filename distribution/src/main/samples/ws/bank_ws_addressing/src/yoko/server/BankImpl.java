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

package yoko.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.cxf.jaxb.JAXBUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl11.WSDLManagerImpl;

@javax.jws.WebService(portName = "BankCORBAPort", 
                      serviceName = "BankCORBAService",
                      targetNamespace = "http://schemas.apache.org/yoko/idl/Bank",
                      wsdlLocation = "file:./BankWS-corba.wsdl",
                      endpointInterface = "yoko.server.Bank")

public class BankImpl implements Bank {

    private static final QName ACCOUNT_SERVICE_NAME
        = new QName("http://schemas.apache.org/yoko/idl/Bank", "AccountCORBAService");
 
    private static final QName ACCOUNT_PORT_NAME
        = new QName("http://schemas.apache.org/yoko/idl/Bank", "AccountCORBAPort");

    private static final QName ACCOUNT_PORT_TYPE
        = new QName("http://schemas.apache.org/yoko/idl/Bank", "Account");

    private URL wsdlURL;
    private Map<String, EndpointReferenceType> accountList = 
        new HashMap<String, EndpointReferenceType>();
    private Map<String, Endpoint> endpointList = new HashMap<String, Endpoint>();

    public BankImpl(String wsdlLocation) {
        try {
            File wsdlFile = new File(wsdlLocation);
            if (wsdlFile.exists()) {
                wsdlURL = wsdlFile.toURL();
            } else {
                wsdlURL = new URL(wsdlLocation);
            }
        } catch (Exception ex) {
            System.out.println("Unable to resolve WSDL location");
            System.exit(1);
        }
    }
    
    public EndpointReferenceType createAccount(String accountName) {
        System.out.println("[Bank] Called createAccount( " + accountName + " )...");
        System.out.println();
        EndpointReferenceType ref = null;
        ref = createAccountReference(accountName);
        if (ref != null) {
            accountList.put(accountName, ref);
        }
        return ref;
    }

    public EndpointReferenceType createEprAccount(String accountName) {
        System.out.println("[Bank] Called createEprAccount( " + accountName + " )...");
        System.out.println();
        EndpointReferenceType ref = createAccountReference(accountName);
        if (ref != null) {
            accountList.put(accountName, ref);
        }
        return ref;
    }

    public EndpointReferenceType getAccount(String accountName) {
        System.out.println("[Bank] Called getAccount( " + accountName + " )...");
        System.out.println();
        return accountList.get(accountName);
    }

    public EndpointReferenceType getEprAccount(String accountName) {
        System.out.println("[Bank] Called getEprAccount( " + accountName + " )...");
        System.out.println();
        return accountList.get(accountName);
    }

    // TODO: What is the correct implementation for this operation?
    public EndpointReferenceType getAccountEprWithNoUseAttribute(String accountName) {
        return null;
    }
    
    // TODO: What is the correct implementation for this operation?
    public void findAccount(javax.xml.ws.Holder<Object> accountDetails) {
    }

    public void removeAccount(String accountName) {
        System.out.println("[Bank] Called removeAccount( " + accountName + " )...");
        System.out.println();
        accountList.remove(accountName);
        Endpoint ep = endpointList.remove(accountName);
        ep.stop();
    }

    private EndpointReferenceType createAccountReference(String accountName) {
        String corbaAddress = "corbaloc::localhost:60000/" + accountName;

        Object account = new AccountImpl();
        //Endpoint.publish(corbaAddress, account);
        Endpoint ep = Endpoint.publish(corbaAddress, account);
        endpointList.put(accountName, ep);
        
        // TODO: can we just use the EndpointReferenceUtils.getEndpointRef(manager, impl);
        
        EndpointReferenceType ref = 
            EndpointReferenceUtils.getEndpointReference(wsdlURL,
                                                        ACCOUNT_SERVICE_NAME,
                                                        ACCOUNT_PORT_NAME.getLocalPart());
        EndpointReferenceUtils.setInterfaceName(ref, ACCOUNT_PORT_TYPE);
        EndpointReferenceUtils.setAddress(ref, corbaAddress);
        
        return ref;
    }
}

