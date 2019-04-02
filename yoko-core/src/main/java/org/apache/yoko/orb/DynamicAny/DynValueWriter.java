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

package org.apache.yoko.orb.DynamicAny;

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OCI.Buffer;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;

import java.util.Hashtable;

final public class DynValueWriter {

    private final DynValueReader dynValueReader_;

    private final Hashtable instanceTable_;

    public DynValueWriter(ORBInstance orbInstance, DynAnyFactory factory) {
        instanceTable_ = new Hashtable(131);
        dynValueReader_ = new DynValueReader(orbInstance, factory, false);
    }

    public boolean writeIndirection(DynAny dv, OutputStream out) {
        Integer pos = (Integer) instanceTable_.get(dv);

        if (pos != null) {
            Buffer buf = out._OB_buffer();
            out.write_long(-1);
            int off = pos - buf.pos_;
            out.write_long(off);

            return true;
        }

        return false;
    }

    public void indexValue(DynAny dv, int startPos) {
        instanceTable_.put(dv, startPos);
        dynValueReader_.indexValue(startPos, dv);
    }

    public DynValueReader getReader() {
        return dynValueReader_;
    }

}
