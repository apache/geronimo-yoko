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
// IDL:orb.yoko.apache.org/OB/RetryPolicy:1.0
//
/**
 *
 * The retry policy. This policy is used to specify retry behavior after
 * communication failures (i.e., <code>CORBA::TRANSIENT</code> and
 * <code>CORBA::COMM_FAILURE</code> exceptions).
 *
 **/

public interface RetryPolicyOperations extends org.omg.CORBA.PolicyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/RetryPolicy/retry_mode:1.0
    //
    /**
     *
     * For retry_mode <code>RETRY_NEVER</code> indicates that requests
     * should never be retried, and the exception is re-thrown to the
     * application. <code>RETRY_STRICT</code> will retry once if the
     * exception completion status is <code>COMPLETED_NO</code>, in
     * order to guarantee at-most-once semantics. <code>RETRY_ALWAYS</code>
     * will retry once, regardless of the exception completion
     * status. The default value is <code>RETRY_STRICT</code>.
     * 
     * retry_interval is the time in milliseconds between reties. The
     * default is 0.
     * 
     * retry_max is the maximum number of retries. The default is 1.
     * 
     * retry_remote determines whether or not to retry on exceptions
     * received over-the-wire. The default is false: only retry on
     * locally generated exceptions.
     *
     * <B>Note:</B> Many TCP/IP stacks do not provide a reliable
     * indication of communication failure when sending smaller
     * requests, therefore the failure may not be detected until the
     * ORB attempts to read the reply.  In this case, the ORB must
     * assume that the remote end has received the request, in order
     * to guarantee at-most-once semantics for the request. The
     * implication is that when using the default setting of
     * <code>RETRY_STRICT</code>, most communication failures will not
     * cause a retry. This behavior can be relaxed using
     * <code>RETRY_ALWAYS</code>.
     *
     **/

    short
    retry_mode();

    //
    // IDL:orb.yoko.apache.org/OB/RetryPolicy/retry_interval:1.0
    //
    /***/

    int
    retry_interval();

    //
    // IDL:orb.yoko.apache.org/OB/RetryPolicy/retry_max:1.0
    //
    /***/

    int
    retry_max();

    //
    // IDL:orb.yoko.apache.org/OB/RetryPolicy/retry_remote:1.0
    //
    /***/

    boolean
    retry_remote();
}
