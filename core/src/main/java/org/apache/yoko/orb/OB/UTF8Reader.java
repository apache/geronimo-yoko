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
import org.omg.CORBA.DATA_CONVERSION;

final class UTF8Reader extends CodeSetReader {
    public char read_char(org.apache.yoko.orb.CORBA.InputStream in)
            throws org.omg.CORBA.DATA_CONVERSION {
        return utf8ToUnicode(in);
    }

    public char read_wchar(org.apache.yoko.orb.CORBA.InputStream in, int len)
            throws org.omg.CORBA.DATA_CONVERSION {
        return utf8ToUnicode(in);
    }

    public int count_wchar(char first) {
        if ((first & 0x80) == 0)
            return 1;
        else if ((first & 0xf8) == 0xc0)
            return 2;
        else if ((first & 0xf8) == 0xe0)
            return 3;

        throw new org.omg.CORBA.DATA_CONVERSION(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeDataConversion(org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Overflow),
                org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Overflow,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private char utf8ToUnicode(org.apache.yoko.orb.CORBA.InputStream in)
            throws org.omg.CORBA.DATA_CONVERSION {
        byte first = in.buf_.data_[in.buf_.pos_++];

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

            if ((in.buf_.data_[in.buf_.pos_] & 0xc0) != 0x80) {
                throw new org.omg.CORBA.DATA_CONVERSION(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeDataConversion(org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Encoding),
                        org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Encoding,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            value <<= 6;
            value |= in.buf_.data_[in.buf_.pos_++] & 0x3f;
        }
        //
        // 16 bit overflow
        //
        else {
            throw new org.omg.CORBA.DATA_CONVERSION(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeDataConversion(org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Overflow),
                    org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Overflow,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        if ((in.buf_.data_[in.buf_.pos_] & 0xc0) != 0x80) {
            throw new org.omg.CORBA.DATA_CONVERSION(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeDataConversion(org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Encoding),
                    org.apache.yoko.orb.OB.MinorCodes.MinorUTF8Encoding,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        value <<= 6;
        value |= in.buf_.data_[in.buf_.pos_++] & 0x3f;

        return value;
    }

    public void set_flags(int flags) {
    }
}
