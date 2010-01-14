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

package org.apache.yoko.orb.CORBA;

final public class ContextList extends org.omg.CORBA.ContextList {
    private java.util.Vector stringVec_ = new java.util.Vector();

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int count() {
        return stringVec_.size();
    }

    public void add(String ctx) {
        stringVec_.addElement(ctx);
    }

    public String item(int index) throws org.omg.CORBA.Bounds {
        try {
            return (String) stringVec_.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.Bounds();
        }
    }

    public void remove(int index) throws org.omg.CORBA.Bounds {
        try {
            stringVec_.removeElementAt(index);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new org.omg.CORBA.Bounds();
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ContextList() {
    }
}
