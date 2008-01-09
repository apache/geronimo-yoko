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

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebResult;

@javax.jws.WebService(portName = "HelloWorldCORBAPort", serviceName = "HelloWorldCORBAService", 
                      targetNamespace = "http://schemas.apache.org/yoko/idl/HelloWorld",
                      wsdlLocation = "file:./HelloWorld-corba.wsdl",
                      endpointInterface = "yoko.server.HelloWorld")
                      
public class HelloWorldImpl implements HelloWorld {

    private static final Logger LOG = 
        Logger.getLogger(HelloWorldImpl.class.getPackage().getName());

    /* (non-Javadoc)
     * @see yoko.server.HelloWorld#greetMe(java.lang.String  inparameter )*
     */
    public java.lang.String greetMe(
        java.lang.String inparameter
    )
    { 
       LOG.info("Executing operation greetMe");
       return new java.lang.String("Hi " + inparameter);
    }

}
