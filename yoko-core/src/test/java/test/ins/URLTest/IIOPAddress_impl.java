/*
 * Copyright 2010 IBM Corporation and others.
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
package test.ins.URLTest;

//
// IDL:URLTest/IIOPAddress:1.0
//

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

//
//  class for testing CORBA URLs using iiop protocol
//

public class IIOPAddress_impl extends IIOPAddressPOA {
    private ORB orb_;

    private String host_;

    private int port_;

    private String key_;

    private String str_;

    // 
    // construct a test object with enough info to report its own
    // corbaloc parameters
    // 
    public IIOPAddress_impl(ORB orb, String host, int port, String key,
            String str) {
        orb_ = orb;
        host_ = host;
        port_ = port;
        key_ = key;
        str_ = str;
    }

    //
    // IDL:URLTest/IIOPAddress/getKey:1.0
    //
    public String getKey() {
        return key_;
    }

    //
    // IDL:URLTest/IIOPAddress/getPort:1.0
    //
    public short getPort() {
        short shortPort = (port_ > 0x8000) ? (short) (port_ - 0xffff - 1)
                : (short) port_;
        return shortPort;
    }

    //
    // IDL:URLTest/IIOPAddress/getHost:1.0
    //
    public String getHost() {
        return host_;
    }

    //
    // IDL:URLTest/IIOPAddress/getIIOPAddress:1.0
    //
    public String getIIOPAddress() {
        String result = "iiop:";
        result += host_;
        result += ":";
        result += Integer.toString(port_);
        return result;
    }

    //
    // IDL:URLTest/IIOPAddress/getCorbalocURL:1.0
    //
    public String getCorbalocURL() {
        String result = "corbaloc:" + getIIOPAddress();
        result += "/" + key_;
        return result;
    }

    //
    // IDL:URLTest/IIOPAddress/destroy:1.0
    //
    public void destroy() {
        // TODO: implement
    }

    //
    // IDL:URLTest/IIOPAddress/setString:1.0
    //
    public void setString(String textStr) {
        str_ = textStr;
    }

    //
    // IDL:URLTest/IIOPAddress/getString:1.0
    //
    public String getString() {
        return str_;
    }

    //
    // IDL:URLTest/IIOPAddress/deactivate:1.0
    //
    public void deactivate() {
        orb_.shutdown(false);
    }
}
