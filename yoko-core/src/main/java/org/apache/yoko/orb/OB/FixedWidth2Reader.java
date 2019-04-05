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

import org.apache.yoko.orb.OCI.BufferReader;
import org.omg.CORBA.DATA_CONVERSION;

final class FixedWidth2Reader extends CodeSetReader {
    public char read_char(BufferReader bufferReader) {
        return bufferReader.readByteAsChar();
    }

    public char read_wchar(BufferReader bufferReader, int len) throws DATA_CONVERSION {
        if (len == 2) {
            return (char) ((bufferReader.readByte() << 8) | (bufferReader.readByte() & 0xff));
        } else
            throw new DATA_CONVERSION();
    }

    public int count_wchar(char value) {
        return 2;
    }

    public void set_flags(int flags) {
    }
}
