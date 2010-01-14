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

final class FixedWidth2Writer extends CodeSetWriter {
    public void write_char(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        out.buf_.data_[out.buf_.pos_++] = (byte) v;
    }

    public void write_wchar(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        out.buf_.data_[out.buf_.pos_] = (byte) (v >> 8);
        out.buf_.data_[out.buf_.pos_ + 1] = (byte) v;
    }

    public int count_wchar(char v) {
        return 2;
    }

    public void set_flags(int flags) {
    }
}
