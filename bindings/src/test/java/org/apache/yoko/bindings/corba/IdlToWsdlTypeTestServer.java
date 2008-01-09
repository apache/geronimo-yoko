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

package org.apache.yoko.bindings.corba;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Endpoint;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.schemas.yoko.idl.idltowsdl_type_test.*;

public class IdlToWsdlTypeTestServer {

    boolean serverReady = false;

    public void _start() {
        Object implementor = new IdlToWsdlTypeTestImpl();
        String address = "corbaloc::localhost:40010/idltowsdl_type_test";
        Endpoint ep = Endpoint.create("http://schemas.apache.org/yoko/bindings/corba", implementor);
        String wsdlLocation = getClass().getResource("/wsdl/type_test/idltowsdl_type_test.wsdl").toString();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("javax.xml.ws.wsdl.description", wsdlLocation);
        ep.setProperties(props);
        ep.publish(address);
        serverReady = true;
    }

    public static void main(String[] args) {
        try {
            IdlToWsdlTypeTestServer s = new IdlToWsdlTypeTestServer();
            s._start();
            System.out.println("server ready...");
            // wait for a key press then shut 
            // down the server
            //
            System.in.read();
            System.out.println("server passed...");
            System.out.println("server stopped..."); 
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("server startup failed (not a log message)");
            System.exit(-1);
        } finally { 
            System.out.println("done!");
        }
    }

    public boolean isServerReady() {
        return serverReady;
    }

}
