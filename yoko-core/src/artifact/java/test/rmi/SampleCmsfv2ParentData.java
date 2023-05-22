/*
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package test.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A class that writes more data than it reads in.
 * This is dangerous since a child class may not know where its own data begins.
 * This is addressed in custom marshaled stream format version 2, which boxes the 
 * parent data so the reading ORB can skip to the end of any custom marshaled 
 * parent data before reading in the child data.
 */
public class SampleCmsfv2ParentData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final byte value = (byte) 0xdd;

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(new Object[0]);
        oos.writeByte(0x77);
    }

    @Override
    public int hashCode() {
        return 31 + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SampleCmsfv2ParentData))
            return false;
        return (value == ((SampleCmsfv2ParentData) obj).value);
    }
    
    @Override
    public String toString() {
        return String.format("%s:%02x", super.toString(), value);
    }
}
