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
package ORBTest_ExceptionsExt_2_0;

//
// IDL:ORBTest_ExceptionsExt_2_0/Intf:1.0
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_PERSIST_STORE_Ex:1.0
    //
    public void
    op_PERSIST_STORE_Ex()
    {
        _ob_delegate_.op_PERSIST_STORE_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_FREE_MEM_Ex:1.0
    //
    public void
    op_FREE_MEM_Ex()
    {
        _ob_delegate_.op_FREE_MEM_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INV_IDENT_Ex:1.0
    //
    public void
    op_INV_IDENT_Ex()
    {
        _ob_delegate_.op_INV_IDENT_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INV_FLAG_Ex:1.0
    //
    public void
    op_INV_FLAG_Ex()
    {
        _ob_delegate_.op_INV_FLAG_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INTF_REPOS_Ex:1.0
    //
    public void
    op_INTF_REPOS_Ex()
    {
        _ob_delegate_.op_INTF_REPOS_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_BAD_CONTEXT_Ex:1.0
    //
    public void
    op_BAD_CONTEXT_Ex()
    {
        _ob_delegate_.op_BAD_CONTEXT_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_TRANSACTION_REQUIRED_Ex:1.0
    //
    public void
    op_TRANSACTION_REQUIRED_Ex()
    {
        _ob_delegate_.op_TRANSACTION_REQUIRED_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_TRANSACTION_ROLLEDBACK_Ex:1.0
    //
    public void
    op_TRANSACTION_ROLLEDBACK_Ex()
    {
        _ob_delegate_.op_TRANSACTION_ROLLEDBACK_Ex();
    }

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INVALID_TRANSACTION_Ex:1.0
    //
    public void
    op_INVALID_TRANSACTION_Ex()
    {
        _ob_delegate_.op_INVALID_TRANSACTION_Ex();
    }
}
