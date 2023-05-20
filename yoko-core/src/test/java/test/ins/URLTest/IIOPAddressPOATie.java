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
public class IIOPAddressPOATie extends IIOPAddressPOA
{
    private IIOPAddressOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    IIOPAddressPOATie(IIOPAddressOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    IIOPAddressPOATie(IIOPAddressOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public IIOPAddressOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(IIOPAddressOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public org.omg.PortableServer.POA
    _default_POA()
    {
        if(_ob_poa_ != null)
            return _ob_poa_;
        else
            return super._default_POA();
    }

    //
    // IDL:URLTest/IIOPAddress/getKey:1.0
    //
    public String
    getKey()
    {
        return _ob_delegate_.getKey();
    }

    //
    // IDL:URLTest/IIOPAddress/getPort:1.0
    //
    public short
    getPort()
    {
        return _ob_delegate_.getPort();
    }

    //
    // IDL:URLTest/IIOPAddress/getHost:1.0
    //
    public String
    getHost()
    {
        return _ob_delegate_.getHost();
    }

    //
    // IDL:URLTest/IIOPAddress/getIIOPAddress:1.0
    //
    public String
    getIIOPAddress()
    {
        return _ob_delegate_.getIIOPAddress();
    }

    //
    // IDL:URLTest/IIOPAddress/getCorbalocURL:1.0
    //
    public String
    getCorbalocURL()
    {
        return _ob_delegate_.getCorbalocURL();
    }

    //
    // IDL:URLTest/IIOPAddress/destroy:1.0
    //
    public void
    destroy()
    {
        _ob_delegate_.destroy();
    }

    //
    // IDL:URLTest/IIOPAddress/setString:1.0
    //
    public void
    setString(String textStr)
    {
        _ob_delegate_.setString(textStr);
    }

    //
    // IDL:URLTest/IIOPAddress/getString:1.0
    //
    public String
    getString()
    {
        return _ob_delegate_.getString();
    }

    //
    // IDL:URLTest/IIOPAddress/deactivate:1.0
    //
    public void
    deactivate()
    {
        _ob_delegate_.deactivate();
    }
}
