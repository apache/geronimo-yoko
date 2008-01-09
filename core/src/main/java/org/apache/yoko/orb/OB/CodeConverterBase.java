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

abstract public class CodeConverterBase
// implements CodeSetReader, CodeSetWriter
{
    //
    // Source and destination code set
    //
    protected CodeSetInfo from_;

    protected CodeSetInfo to_;

    //
    // The UTF-8 or fixed width reader/writer
    //
    private CodeSetReader reader_;

    private CodeSetWriter writer_;

    private static CodeSetInfo noneInstance_;

    CodeConverterBase(CodeSetInfo from, CodeSetInfo to) {
        if ((from == null || to == null) && noneInstance_ == null) {
            noneInstance_ = new CodeSetInfo("none", 0, 0, null, (short) 2);
        }

        if (from == null)
            from = noneInstance_;
        if (to == null)
            to = noneInstance_;

        from_ = from;
        to_ = to;

        if (from.rgy_value == CodeSetDatabase.UTF8)
            reader_ = new UTF8Reader();
        else if (from.rgy_value == CodeSetDatabase.UTF16)
            reader_ = new UTF16Reader();
        else if (from.max_bytes <= 2)
            reader_ = new FixedWidth2Reader();
        else {
            //
            // Java doesn't support wide characters larger than 16 bit
            //
            Assert._OB_assert(false);
        }

        if (to.rgy_value == CodeSetDatabase.UTF8)
            writer_ = new UTF8Writer();
        else if (to.rgy_value == CodeSetDatabase.UTF16)
            writer_ = new UTF16Writer();
        else if (to.max_bytes <= 2)
            writer_ = new FixedWidth2Writer();
        else {
            //
            // Java doesn't support wide characters larger than 16 bit
            //
            Assert._OB_assert(false);
        }
    }

    final public boolean equals(java.lang.Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        CodeConverterBase b = (CodeConverterBase) obj;

        return (from_.rgy_value == b.from_.rgy_value && to_.rgy_value == b.to_.rgy_value);
    }

    final public int hashCode() {
        return from_.rgy_value + 29 * to_.rgy_value;
    }

    final public char read_char(org.apache.yoko.orb.CORBA.InputStream in)
            throws org.omg.CORBA.DATA_CONVERSION {
        return reader_.read_char(in);
    }

    public char read_wchar(org.apache.yoko.orb.CORBA.InputStream in, int len)
            throws org.omg.CORBA.DATA_CONVERSION {
        return reader_.read_wchar(in, len);
    }

    public void write_char(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        writer_.write_char(out, v);
    }

    public void write_wchar(org.apache.yoko.orb.CORBA.OutputStream out, char v)
            throws org.omg.CORBA.DATA_CONVERSION {
        writer_.write_wchar(out, v);
    }

    public int read_count_wchar(char v) {
        return reader_.count_wchar(v);
    }

    public int write_count_wchar(char v) {
        return writer_.count_wchar(v);
    }

    final public boolean readerRequired() {
        return (from_.rgy_value == CodeSetDatabase.UTF8)
                || (from_.rgy_value == CodeSetDatabase.UTF16);
    }

    final public boolean writerRequired() {
        return (to_.rgy_value == CodeSetDatabase.UTF8)
                || (to_.rgy_value == CodeSetDatabase.UTF16);
    }

    final public CodeSetInfo getFrom() {
        return from_;
    }

    final public CodeSetInfo getTo() {
        return to_;
    }

    final public void set_reader_flags(int flags) {
        reader_.set_flags(flags);
    }

    final public void set_writer_flags(int flags) {
        writer_.set_flags(flags);
    }

    //
    // Get conversion type
    //
    public abstract boolean conversionRequired();

    //
    // Convert narrow or wide character
    //
    public abstract char convert(char value);
}
