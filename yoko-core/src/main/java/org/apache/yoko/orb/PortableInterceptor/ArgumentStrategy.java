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

package org.apache.yoko.orb.PortableInterceptor;

abstract class ArgumentStrategy {
    protected org.omg.CORBA.ORB orb_; // Java only

    //
    // Are arguments available?
    //
    protected boolean argsAvail_;

    //
    // Is the result available?
    //
    protected boolean resultAvail_;

    //
    // Are exceptions available?
    //
    protected boolean exceptAvail_;

    //
    // Are exceptions never available?
    //
    protected boolean exceptNeverAvail_;

    ArgumentStrategy(org.omg.CORBA.ORB orb) {
        orb_ = orb;
        argsAvail_ = false;
        resultAvail_ = false;
        exceptAvail_ = false;
        exceptNeverAvail_ = false;
    }

    //
    // Get the arguments
    //
    abstract org.omg.Dynamic.Parameter[] arguments();

    //
    // Get the exceptions
    //
    abstract org.omg.CORBA.TypeCode[] exceptions();

    //
    // Get the result
    //
    abstract org.omg.CORBA.Any result();

    //
    // Set the result (server side only)
    //
    abstract void setResult(org.omg.CORBA.Any any);

    //
    // Are the args available?
    //
    void setArgsAvail(boolean v) {
        argsAvail_ = v;
    }

    //
    // Are the exceptions available?
    //
    void setExceptAvail(boolean v) {
        exceptAvail_ = v;
    }

    //
    // Exceptions are never available
    //
    void setExceptNeverAvail() {
        exceptNeverAvail_ = true;
    }

    //
    // Is the result available?
    //
    void setResultAvail(boolean v) {
        resultAvail_ = v;
    }
}
