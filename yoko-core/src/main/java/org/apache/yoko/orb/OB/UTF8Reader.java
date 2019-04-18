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

import org.apache.yoko.orb.OCI.ReadBuffer;
import org.omg.CORBA.DATA_CONVERSION;

import static org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Encoding;
import static org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Overflow;
import static org.apache.yoko.orb.OB.MinorCodes.describeDataConversion;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final class UTF8Reader extends CodeSetReader {
    public char read_char(ReadBuffer readBuffer) throws DATA_CONVERSION {
        byte first = readBuffer.readByte();

        //
        // Direct mapping for characters < 0x80
        //
        if ((first & 0x80) == 0)
            return (char) first;

        char value;

        if ((first & 0xf8) == 0xc0) {
            // 5 free bits
            value = (char) (first & 0x1f);
        } else if ((first & 0xf8) == 0xe0) {
            // 4 free bits
            value = (char) (first & 0x0f);

            if ((readBuffer.peekByte() & 0xc0) != 0x80) {
                throw new DATA_CONVERSION(describeDataConversion(MinorUTF8Encoding), MinorUTF8Encoding, COMPLETED_NO);
            }

            value <<= 6;
            value |= readBuffer.readByte() & 0x3f;
        }
        //
        // 16 bit overflow
        //
        else {
            throw new DATA_CONVERSION(describeDataConversion(MinorUTF8Overflow), MinorUTF8Overflow, COMPLETED_NO);
        }

        if ((readBuffer.peekByte() & 0xc0) != 0x80) {
            throw new DATA_CONVERSION(describeDataConversion(MinorUTF8Encoding), MinorUTF8Encoding, COMPLETED_NO);
        }

        value <<= 6;
        value |= readBuffer.readByte() & 0x3f;

        return value;
    }

    public char read_wchar(ReadBuffer readBuffer, int len) throws DATA_CONVERSION {
        return read_char(readBuffer);
    }

    public int count_wchar(char first) {
        if ((first & 0x80) == 0)
            return 1;
        else if ((first & 0xf8) == 0xc0)
            return 2;
        else if ((first & 0xf8) == 0xe0)
            return 3;

        throw new DATA_CONVERSION(describeDataConversion(MinorUTF8Overflow), MinorUTF8Overflow, COMPLETED_NO);
    }

    public void set_flags(int flags) {
    }
}
