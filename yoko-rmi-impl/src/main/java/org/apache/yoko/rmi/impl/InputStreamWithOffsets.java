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
package org.apache.yoko.rmi.impl;

import org.omg.CORBA.portable.ValueInputStream;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

public abstract class InputStreamWithOffsets extends org.omg.CORBA_2_3.portable.InputStream implements ValueInputStream {
    private final Map<Integer, Serializable> offsetMap = new Hashtable<Integer, Serializable>(131);

    public Map<Integer, Serializable> getOffsetMap() {
        return offsetMap;
    }

    /**
     * @return the number of bytes read so far
     */
    public abstract long position();
}
