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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.omg.CORBA.DATA_CONVERSION;

import java.util.Objects;

import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_16;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_8;

abstract public class CodeConverterBase
// implements CodeSetReader, CodeSetWriter
{
    //
    // Source and destination code set
    //
    private final CodeSetInfo from_;

    private final CodeSetInfo to_;

    //
    // The UTF-8 or fixed width reader/writer
    //
    private final CodeSetReader reader_;

    private final CodeSetWriter writer_;

    CodeConverterBase(CodeSetInfo from, CodeSetInfo to) {
        if (from == null)
            from = CodeSetInfo.NONE;
        if (to == null)
            to = CodeSetInfo.NONE;

        from_ = from;
        to_ = to;

        if (from == UTF_8)
            reader_ = new UTF8Reader();
        else if (from == UTF_16)
            reader_ = new UTF16Reader();
        else if (from.max_bytes <= 2)
            reader_ = new FixedWidth2Reader();
        else {
            //
            // Java doesn't support wide characters larger than 16 bit
            //
            Assert._OB_assert(false);
            throw new Error("unreachable");
        }

        if (to == UTF_8)
            writer_ = new UTF8Writer();
        else if (to == UTF_16)
            writer_ = new UTF16Writer();
        else if (to.max_bytes <= 2)
            writer_ = new FixedWidth2Writer();
        else {
            //
            // Java doesn't support wide characters larger than 16 bit
            //
            Assert._OB_assert(false);
            throw new Error("unreachable");
        }
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) return true;
        return (other instanceof CodeConverterBase) && this.equals((CodeConverterBase) other);
    }

    private boolean equals(CodeConverterBase that) {
        return this.from_ == that.from_ && this.to_ == that.to_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from_, to_);
    }

    final public char read_char(InputStream in) throws DATA_CONVERSION {
        return reader_.read_char(in);
    }

    public char read_wchar(InputStream in, int len) throws DATA_CONVERSION {
        return reader_.read_wchar(in, len);
    }

    public void write_char(OutputStream out, char v) throws DATA_CONVERSION {
        writer_.write_char(out, v);
    }

    public void write_wchar(OutputStream out, char v) throws DATA_CONVERSION {
        writer_.write_wchar(out, v);
    }

    public int read_count_wchar(char v) {
        return reader_.count_wchar(v);
    }

    public int write_count_wchar(char v) {
        return writer_.count_wchar(v);
    }

    final public boolean readerRequired() {
        return (from_ == UTF_8) || (from_ == UTF_16);
    }

    final public boolean writerRequired() {
        return (to_ == UTF_8) || (to_ == UTF_16);
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
