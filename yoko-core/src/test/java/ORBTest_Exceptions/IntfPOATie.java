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
package ORBTest_Exceptions;

//
// IDL:ORBTest_Exceptions/Intf:1.0
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
    // IDL:ORBTest_Exceptions/Intf/op_UNKNOWN_Ex:1.0
    //
    public void
    op_UNKNOWN_Ex()
    {
        _ob_delegate_.op_UNKNOWN_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_PARAM_Ex:1.0
    //
    public void
    op_BAD_PARAM_Ex()
    {
        _ob_delegate_.op_BAD_PARAM_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_MEMORY_Ex:1.0
    //
    public void
    op_NO_MEMORY_Ex()
    {
        _ob_delegate_.op_NO_MEMORY_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_IMP_LIMIT_Ex:1.0
    //
    public void
    op_IMP_LIMIT_Ex()
    {
        _ob_delegate_.op_IMP_LIMIT_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_COMM_FAILURE_Ex:1.0
    //
    public void
    op_COMM_FAILURE_Ex()
    {
        _ob_delegate_.op_COMM_FAILURE_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INV_OBJREF_Ex:1.0
    //
    public void
    op_INV_OBJREF_Ex()
    {
        _ob_delegate_.op_INV_OBJREF_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_PERMISSION_Ex:1.0
    //
    public void
    op_NO_PERMISSION_Ex()
    {
        _ob_delegate_.op_NO_PERMISSION_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INTERNAL_Ex:1.0
    //
    public void
    op_INTERNAL_Ex()
    {
        _ob_delegate_.op_INTERNAL_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_MARSHAL_Ex:1.0
    //
    public void
    op_MARSHAL_Ex()
    {
        _ob_delegate_.op_MARSHAL_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INITIALIZE_Ex:1.0
    //
    public void
    op_INITIALIZE_Ex()
    {
        _ob_delegate_.op_INITIALIZE_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_IMPLEMENT_Ex:1.0
    //
    public void
    op_NO_IMPLEMENT_Ex()
    {
        _ob_delegate_.op_NO_IMPLEMENT_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_TYPECODE_Ex:1.0
    //
    public void
    op_BAD_TYPECODE_Ex()
    {
        _ob_delegate_.op_BAD_TYPECODE_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_OPERATION_Ex:1.0
    //
    public void
    op_BAD_OPERATION_Ex()
    {
        _ob_delegate_.op_BAD_OPERATION_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_RESOURCES_Ex:1.0
    //
    public void
    op_NO_RESOURCES_Ex()
    {
        _ob_delegate_.op_NO_RESOURCES_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_RESPONSE_Ex:1.0
    //
    public void
    op_NO_RESPONSE_Ex()
    {
        _ob_delegate_.op_NO_RESPONSE_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_INV_ORDER_Ex:1.0
    //
    public void
    op_BAD_INV_ORDER_Ex()
    {
        _ob_delegate_.op_BAD_INV_ORDER_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_TRANSIENT_Ex:1.0
    //
    public void
    op_TRANSIENT_Ex()
    {
        _ob_delegate_.op_TRANSIENT_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_OBJ_ADAPTER_Ex:1.0
    //
    public void
    op_OBJ_ADAPTER_Ex()
    {
        _ob_delegate_.op_OBJ_ADAPTER_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_DATA_CONVERSION_Ex:1.0
    //
    public void
    op_DATA_CONVERSION_Ex()
    {
        _ob_delegate_.op_DATA_CONVERSION_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_OBJECT_NOT_EXIST_Ex:1.0
    //
    public void
    op_OBJECT_NOT_EXIST_Ex()
    {
        _ob_delegate_.op_OBJECT_NOT_EXIST_Ex();
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INV_POLICY_Ex:1.0
    //
    public void
    op_INV_POLICY_Ex()
    {
        _ob_delegate_.op_INV_POLICY_Ex();
    }
}
