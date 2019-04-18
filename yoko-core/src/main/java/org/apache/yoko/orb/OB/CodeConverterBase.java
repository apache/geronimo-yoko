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
import org.apache.yoko.orb.OCI.WriteBuffer;
import org.omg.CORBA.DATA_CONVERSION;

import java.util.Objects;

import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_16;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_8;

abstract public class CodeConverterBase {
    private final CodeSetInfo sourceCodeSet;

    private final CodeSetInfo destinationCodeSet;

    //
    // The UTF-8 or fixed width reader/writer
    //
    private final CodeSetReader reader_;

    private final CodeSetWriter writer_;

    CodeConverterBase(CodeSetInfo source, CodeSetInfo destination) {
        if (source == null)
            source = CodeSetInfo.NONE;
        if (destination == null)
            destination = CodeSetInfo.NONE;

        sourceCodeSet = source;
        destinationCodeSet = destination;

        if (source == UTF_8)
            reader_ = new UTF8Reader();
        else if (source == UTF_16)
            reader_ = new UTF16Reader();
        else if (source.max_bytes <= 2)
            reader_ = new FixedWidth2Reader();
        else {
            //
            // Java doesn't support wide characters larger than 16 bit
            //
            Assert._OB_assert(false);
            throw new Error("unreachable");
        }

        if (destination == UTF_8)
            writer_ = new UTF8Writer();
        else if (destination == UTF_16)
            writer_ = new UTF16Writer();
        else if (destination.max_bytes <= 2)
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
        return this.sourceCodeSet == that.sourceCodeSet && this.destinationCodeSet == that.destinationCodeSet;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCodeSet, destinationCodeSet);
    }

    final public char read_char(ReadBuffer readBuffer) throws DATA_CONVERSION {
        return reader_.read_char(readBuffer);
    }

    public char read_wchar(ReadBuffer readBuffer, int len) throws DATA_CONVERSION {
        return reader_.read_wchar(readBuffer, len);
    }

    public void write_char(WriteBuffer writeBuffer, char v) throws DATA_CONVERSION {
        writer_.write_char(writeBuffer, v);
    }

    public void write_wchar(WriteBuffer writeBuffer, char v) throws DATA_CONVERSION {
        writer_.write_wchar(writeBuffer, v);
    }

    public int read_count_wchar(char v) {
        return reader_.count_wchar(v);
    }

    public int write_count_wchar(char v) {
        return writer_.count_wchar(v);
    }

    final public boolean readerRequired() {
        return (sourceCodeSet == UTF_8) || (sourceCodeSet == UTF_16);
    }

    final public boolean writerRequired() {
        return (destinationCodeSet == UTF_8) || (destinationCodeSet == UTF_16);
    }

    final public CodeSetInfo getSourceCodeSet() {
        return sourceCodeSet;
    }

    final public CodeSetInfo getDestinationCodeSet() {
        return destinationCodeSet;
    }

    final public void set_reader_flags(int flags) {
        reader_.set_flags(flags);
    }

    final public void set_writer_flags(int flags) {
        writer_.set_flags(flags);
    }

    public abstract boolean conversionRequired();

    public abstract char convert(char value);

}
