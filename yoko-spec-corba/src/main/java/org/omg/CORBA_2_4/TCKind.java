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
package org.omg.CORBA_2_4;

//
// This class is necessary because we need to add tk_local_interface
// but the JDK's version of TCKind can interfere
//
public class TCKind extends org.omg.CORBA.TCKind {
    public final static int _tk_local_interface = 33;

    public static TCKind tk_local_interface;

    //
    // The behavior of the constructor varies depending on whether
    // the JDK's version of org.omg.CORBA.TCKind is present. If it
    // is present, the first allocation will cause a BAD_PARAM
    // exception, in which case we allocate again using a dummy value.
    // If the ORBacus version of org.omg.CORBA.TCKind is present,
    // the first constructor will succeed.
    //
    static {
        try {
            tk_local_interface = new TCKind(_tk_local_interface);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            //
            // Must be using the JDK ORB
            //
            tk_local_interface = new TCKind(0);
        }
    }

    protected TCKind(int value) {
        super(value);
    }

    public int value() {
        return _tk_local_interface;
    }

    public static org.omg.CORBA.TCKind from_int(int value) {
        if (value == _tk_local_interface)
            return tk_local_interface;
        else
            return org.omg.CORBA.TCKind.from_int(value);
    }

    private java.lang.Object readResolve() throws java.io.ObjectStreamException {
        return from_int(value());
    }
}
