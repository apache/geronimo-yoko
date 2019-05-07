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

import org.apache.yoko.orb.OB.PIArgsDowncall;
import org.apache.yoko.orb.OB.PIDIIDowncall;
import org.apache.yoko.orb.OB.PIVoidDowncall;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;

public abstract class ArgumentStrategy {
    public static ArgumentStrategy create(ORB orb, PIVoidDowncall downcall) {
        return new ArgumentStrategyNull(orb);
    }

    public static ArgumentStrategy create(ORB orb, PIArgsDowncall pad) {
        return new ArgumentStrategySII(orb, pad.argDesc_, pad.retDesc_, pad.exceptionTC_);
    }

    public static ArgumentStrategy create(ORB orb, PIDIIDowncall pdd) {
        return new ArgumentStrategyDII(orb, pdd.args_, pdd.result_, pdd.exceptionList_);
    }

    protected final ORB orb_;

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

    ArgumentStrategy(ORB orb) {
        orb_ = orb;
        argsAvail_ = false;
        resultAvail_ = false;
        exceptAvail_ = false;
        exceptNeverAvail_ = false;
    }

    //
    // Get the arguments
    //
    abstract Parameter[] arguments();

    //
    // Get the exceptions
    //
    abstract TypeCode[] exceptions();

    //
    // Get the result
    //
    abstract Any result();

    //
    // Set the result (server side only)
    //
    abstract void setResult(Any any);

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
