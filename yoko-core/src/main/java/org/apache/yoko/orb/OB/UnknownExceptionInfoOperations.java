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

package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo:1.0
//
/**
 *
 * Information about the context in which the unknown exception occurred.
 *
 **/

public interface UnknownExceptionInfoOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo/operation:1.0
    //
    /**
     *
     * The name of the operation that the servant was executing.
     *
     * @return The operation name.
     *
     **/

    String
    operation();

    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo/response_expected:1.0
    //
    /**
     *
     * Indicates whether the client is expecting a response to
     * this invocation.
     *
     * @return TRUE if the client expects a response, FALSE otherwise.
     *
     **/

    boolean
    response_expected();

    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo/transport_info:1.0
    //
    /**
     *
     * Obtains information about the transport on which this request
     * was received.
     *
     * @return The transport information, or NULL if the request
     * was made on a collocated servant.
     *
     **/

    org.apache.yoko.orb.OCI.TransportInfo
    transport_info();

    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo/describe_exception:1.0
    //
    /**
     *
     * Obtains a description of the exception. For example, in
     * Java this returns the exception stack trace. Calling
     * this operation in C++ will raise CORBA::NO_IMPLEMENT.
     *
     * @return The exception description.
     *
     **/

    String
    describe_exception();

    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionInfo/raise_exception:1.0
    //
    /**
     *
     * Raises the unknown exception.
     *
     **/

    void
    raise_exception();
}
