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

import org.apache.yoko.orb.OB.UnknownExceptionInfo;

public class UnknownExceptionInfo_impl extends org.omg.CORBA.LocalObject
        implements UnknownExceptionInfo {
    private String operation_;

    private boolean responseExpected_;

    private org.apache.yoko.orb.OCI.TransportInfo transportInfo_;

    private RuntimeException ex_;

    // ------------------------------------------------------------------
    // UnknownExceptionInfo_impl constructor
    // ------------------------------------------------------------------

    public UnknownExceptionInfo_impl(String operation,
            boolean responseExpected,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo,
            RuntimeException ex) {
        operation_ = operation;
        responseExpected_ = responseExpected;
        transportInfo_ = transportInfo;
        ex_ = ex;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String operation() {
        return operation_;
    }

    public boolean response_expected() {
        return responseExpected_;
    }

    public org.apache.yoko.orb.OCI.TransportInfo transport_info() {
        return transportInfo_;
    }

    public String describe_exception() {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex_.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public void raise_exception() {
        throw ex_;
    }
}
