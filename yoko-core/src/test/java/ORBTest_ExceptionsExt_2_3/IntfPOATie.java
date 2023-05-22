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
package ORBTest_ExceptionsExt_2_3;

//
// IDL:ORBTest_ExceptionsExt_2_3/Intf:1.0
//
public class IntfPOATie extends IntfPOA
{
    private IntfOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    IntfPOATie(IntfOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    IntfPOATie(IntfOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public IntfOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(IntfOperations delegate)
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
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_CODESET_INCOMPATIBLE_Ex:1.0
    //
    public void
    op_CODESET_INCOMPATIBLE_Ex()
    {
        _ob_delegate_.op_CODESET_INCOMPATIBLE_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_REBIND_Ex:1.0
    //
    public void
    op_REBIND_Ex()
    {
        _ob_delegate_.op_REBIND_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TIMEOUT_Ex:1.0
    //
    public void
    op_TIMEOUT_Ex()
    {
        _ob_delegate_.op_TIMEOUT_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TRANSACTION_UNAVAILABLE_Ex:1.0
    //
    public void
    op_TRANSACTION_UNAVAILABLE_Ex()
    {
        _ob_delegate_.op_TRANSACTION_UNAVAILABLE_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_TRANSACTION_MODE_Ex:1.0
    //
    public void
    op_TRANSACTION_MODE_Ex()
    {
        _ob_delegate_.op_TRANSACTION_MODE_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_3/Intf/op_BAD_QOS_Ex:1.0
    //
    public void
    op_BAD_QOS_Ex()
    {
        _ob_delegate_.op_BAD_QOS_Ex();
    }
}
