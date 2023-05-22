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

public abstract class CodeSetReader {
    public final static int L_ENDIAN = 1;

    public final static int FIRST_CHAR = 2;

    abstract char read_char(ReadBuffer readBuffer) throws DATA_CONVERSION;

    abstract char read_wchar(ReadBuffer readBuffer, int len) throws DATA_CONVERSION;

    abstract int count_wchar(char value);

    abstract void set_flags(int flags);
}
