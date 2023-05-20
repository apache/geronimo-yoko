/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import org.apache.yoko.io.ReadBuffer;
import org.omg.CORBA.DATA_CONVERSION;

final class FixedWidth2Reader extends CodeSetReader {
    public char read_char(ReadBuffer readBuffer) {
        return readBuffer.readByteAsChar();
    }

    public char read_wchar(ReadBuffer readBuffer, int len) throws DATA_CONVERSION {
        if (len == 2) {
            return (char) ((readBuffer.readByte() << 8) | (readBuffer.readByte() & 0xff));
        } else
            throw new DATA_CONVERSION();
    }

    public int count_wchar(char value) {
        return 2;
    }

    public void set_flags(int flags) {
    }
}
