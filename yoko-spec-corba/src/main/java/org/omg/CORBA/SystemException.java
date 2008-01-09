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
package org.omg.CORBA;

import org.omg.CORBA.OMGVMCID;
import org.apache.yoko.ApacheVMCID;

abstract public class SystemException extends java.lang.RuntimeException {
    public int minor;

    public CompletionStatus completed;

    protected SystemException(String reason, int minor, CompletionStatus status) {
        super(reason);
        this.minor = minor;
        this.completed = status;
    }

    public String toString() {

        // the first part of the message code is the issuing authority, so split it off.
        int vmcid = minor & 0xFFFFF000;

        String vmcidName = null;

        // is this an OMG defined major code?
        if (vmcid == OMGVMCID.value) {
            vmcidName = "OMG";
        }
        // this is a Yoko one, but the VMCID is registered as Apache.
        else if (vmcid == ApacheVMCID.value) {
            vmcidName = "Apache";
        }
        else {
            // Just display the numeric value.
            vmcidName = "0x" + Integer.toHexString(vmcid);
        }

        String s = super.toString() + ":  vmcid: " + vmcidName + " minor code: 0x"
                + Integer.toHexString(minor & 0x00000FFF);

        switch (completed.value()) {
        case CompletionStatus._COMPLETED_YES:
            s += "  completed: Yes";
            break;

        case CompletionStatus._COMPLETED_NO:
            s += "  completed: No";
            break;

        case CompletionStatus._COMPLETED_MAYBE:
        default:
            s += "  completed: Maybe";
            break;
        }

        return s;
    }
}
