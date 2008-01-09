/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.omg.IOP;

//
// IDL:omg.org/IOP/Codec:1.0
//
/***/

public interface CodecOperations
{
    //
    // IDL:omg.org/IOP/Codec/encode:1.0
    //
    /***/

    byte[]
    encode(org.omg.CORBA.Any data)
        throws org.omg.IOP.CodecPackage.InvalidTypeForEncoding;

    //
    // IDL:omg.org/IOP/Codec/decode:1.0
    //
    /***/

    org.omg.CORBA.Any
    decode(byte[] data)
        throws org.omg.IOP.CodecPackage.FormatMismatch;

    //
    // IDL:omg.org/IOP/Codec/encode_value:1.0
    //
    /***/

    byte[]
    encode_value(org.omg.CORBA.Any data)
        throws org.omg.IOP.CodecPackage.InvalidTypeForEncoding;

    //
    // IDL:omg.org/IOP/Codec/decode_value:1.0
    //
    /***/

    org.omg.CORBA.Any
    decode_value(byte[] data,
                 org.omg.CORBA.TypeCode tc)
        throws org.omg.IOP.CodecPackage.FormatMismatch,
               org.omg.IOP.CodecPackage.TypeMismatch;
}
