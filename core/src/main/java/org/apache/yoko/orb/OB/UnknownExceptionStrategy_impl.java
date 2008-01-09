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

import org.apache.yoko.orb.OB.Logger;
import org.apache.yoko.orb.OB.UnknownExceptionInfo;
import org.apache.yoko.orb.OB.UnknownExceptionStrategy;

//
// An UnknownExceptionStrategy will be called by the ORB when a servant
// raises an unexpected exception
//
public class UnknownExceptionStrategy_impl extends org.omg.CORBA.LocalObject
        implements UnknownExceptionStrategy {
    protected org.omg.CORBA.ORB orb_;

    public UnknownExceptionStrategy_impl(org.omg.CORBA.ORB orb) {
        orb_ = orb;
    }

    //
    // Handle an unknown exception. If this method doesn't throw
    // a SystemException, the ORB will return CORBA::UNKNOWN to
    // the client.
    //
    public void unknown_exception(UnknownExceptionInfo info) {
        String msg = "Servant method raised a non-CORBA exception";

        if (info.response_expected())
            msg += "\nClient receives this exception as CORBA::UNKNOWN";

        msg += "\noperation name: \"";
        msg += info.operation();
        msg += '"';

        org.apache.yoko.orb.OCI.TransportInfo transportInfo = info
                .transport_info();
        if (transportInfo != null) {
            String desc = transportInfo.describe();
            msg += '\n';
            msg += desc;
        } else {
            msg += "\nCollocated method call";
        }

        msg += "\n";
        msg += info.describe_exception();

        Logger logger = ((org.apache.yoko.orb.CORBA.ORB) orb_).logger();
        logger.warning(msg);
    }

    public void destroy() {
        orb_ = null;
    }
}
