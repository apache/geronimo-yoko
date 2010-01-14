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

final class UTF16Writer extends CodeSetWriter {
    private int Flags_ = 0;

    public void write_char(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        out.buf_.data_[out.buf_.pos_++] = (byte) (v & 0xff);
    }

    public void write_wchar(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        if (OB_Extras.COMPAT_WIDE_MARSHAL == true) {
            out.buf_.data_[out.buf_.pos_] = (byte) (v >> 8);
            out.buf_.data_[out.buf_.pos_ + 1] = (byte) v;
        } else {
            //
            // we don't support surrogate paired characters
            //
            org.apache.yoko.orb.OB.Assert._OB_assert(v < 0xD800 || v > 0xDFFF);

            //
            // if this character is the same character as the BOM, then we
            // need to escape it with the Big Endian BOM
            //
            if (((Flags_ & CodeSetWriter.FIRST_CHAR) != 0)
                    && (v == (char) 0xFEFF || v == (char) 0xFFFE)) {
                out.buf_.data_[out.buf_.pos_++] = (byte) 0xFE;
                out.buf_.data_[out.buf_.pos_++] = (byte) 0xFF;
            }

            //
            // we always write our UTF-16 characters in Big Endian format
            //
            out.buf_.data_[out.buf_.pos_++] = (byte) (v >>> 8);
            out.buf_.data_[out.buf_.pos_++] = (byte) (v & 0xff);

            //
            // turn off the FIRST_CHAR flag
            //
            Flags_ &= ~CodeSetWriter.FIRST_CHAR;
        }
    }

    public int count_wchar(char v) {
        if (OB_Extras.COMPAT_WIDE_MARSHAL == false) {
            //
            // we don't support surrogate paired characters
            //
            org.apache.yoko.orb.OB.Assert._OB_assert(v < (char) 0xD800
                    || v > (char) 0xDFFF);

            //
            // we need to escape the first character if its a BOM
            //
            if (((Flags_ & CodeSetWriter.FIRST_CHAR) != 0)
                    && (v == 0xFEFF || v == 0xFFFE))
                return 4;
        }

        return 2;
    }

    public void set_flags(int flags) {
        Flags_ = flags;
    }
}
