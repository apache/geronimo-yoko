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
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

public final class Client {

    private static final QName SERVICE_NAME = new QName("http://schemas.apache.org/yoko/idl/HelloWorld", "HelloWorldCORBAService");

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlUrl = new URL("file:./HelloWorld-corba.wsdl");
    
        HelloWorldCORBAService ss = new HelloWorldCORBAService(wsdlUrl, SERVICE_NAME);
        HelloWorld port = ss.getHelloWorldCORBAPort();  
        

        System.out.println("Invoking greetMe... ");
        java.lang.String _greetMe_outparameter = port.greetMe("Hello There");
        System.out.println("greetMe.result=" + _greetMe_outparameter);
                
        
        System.exit(0);
    }

}
