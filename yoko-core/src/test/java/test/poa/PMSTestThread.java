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

package test.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import java.io.*;

final class PMSTestThread extends Thread {
    private Test test_;

    private int state_;

    final static int NONE = 0;

    final static int CALL_STARTED = 1;

    final static int CALL_FAILURE = 2;

    final static int CALL_SUCCESS = 3;

    private synchronized void setState(int val) {
        state_ = val;
    }

    public void run() {
        setState(CALL_STARTED);
        try {
            test_.aMethod();
        } catch (org.omg.CORBA.TRANSIENT ex) {
            setState(CALL_FAILURE);
            return;
        } catch (org.omg.CORBA.SystemException ex) {
            System.err.println("Unexpected: " + ex);
        }
        setState(CALL_SUCCESS);
    }

    synchronized int callState() {
        return state_;
    }

    PMSTestThread(Test test) {
        test_ = test;
        state_ = NONE;
    }
}
