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
package org.apache.yoko.orb.OCI;

public interface BufferReader {
    void align2();

    void align4();

    void align8();

    void align(int n);

    void align(AlignmentBoundary boundary);

    void skipBytes(int n);

    void skipToEnd();

    void rewindToStart();

    byte peekByte();

    byte readByte();

    char readByteAsChar();

    void readBytes(byte[] value, int offset, int length);

    char peekChar();

    char readChar();

    char readChar_LE();

    String dumpPosition();

    String dumpRemainingData();

    String dumpAllData();

    String dumpAllDataWithPosition();

    /**
     * Copy the available data to the provided writer
     * @param writer
     */
    void appendAvailableDataTo(Buffer.Writer writer);

    byte[] copyRemainingBytes();
}
