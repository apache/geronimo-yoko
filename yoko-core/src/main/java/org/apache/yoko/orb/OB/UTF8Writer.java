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

import org.apache.yoko.orb.OCI.BufferWriter;
import org.omg.CORBA.DATA_CONVERSION;

final class UTF8Writer extends CodeSetWriter {
    public void write_char(BufferWriter bufferWriter, char v) throws DATA_CONVERSION {
        if ((int) v < 0x80) {
            // Direct mapping (7 bits) for characters < 0x80
            bufferWriter.writeByte(v);
        } else if ((int) v <= 0x7ff) {
            // 5 free bits (%110xxxxx | %vvvvv)
            bufferWriter.writeByte((v >>> 6) | 0xc0);
            bufferWriter.writeByte((v & 0x3f) | 0x80);
        } else if ((int) v <= 0xffff) {
            // 4 free bits (%1110xxxx | %vvvv)
            bufferWriter.writeByte((v >>> 12) | 0xe0);
            bufferWriter.writeByte(((v >>> 6) & 0x3f) | 0x80);
            bufferWriter.writeByte((v & 0x3f) | 0x80);
        } else throw new DATA_CONVERSION();
    }

    public void write_wchar(BufferWriter bufferWriter, char v) throws DATA_CONVERSION {
        write_char(bufferWriter, v);
    }

    public int count_wchar(char value) {
        if (value < 0x80)
            return 1;
        else if (value < 0x7ff)
            return 2;
        else
            return 3;
    }

    public void set_flags(int flags) {
    }
}
