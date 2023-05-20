/*
 * Copyright 2015 IBM Corporation and others.
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
package org.apache.yoko.rmi.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import org.omg.CORBA.portable.IndirectionException;

class ClosedObjectReader extends ObjectReader {
    private static final Logger LOGGER = Logger.getLogger(ClosedObjectReader.class.getName());
    public static final ClosedObjectReader INSTANCE = AccessController.doPrivileged(new PrivilegedAction<ClosedObjectReader>() {
        @Override
        public ClosedObjectReader run() {
            try {
                return new ClosedObjectReader();
            } catch (IOException e) {
                LOGGER.severe("Unable to create ClosedObjectInputStream singleton instance: " + e);
                return null;
            }
        }
    });

    private ClosedObjectReader() throws IOException {
    }

    private IllegalStateException newException() {
        return new IllegalStateException("Stream already closed");
    }

    public void close() {
    }

    //////////////////////////////////////
    // ONLY DELEGATE METHODS BELOW HERE //
    //////////////////////////////////////

    public int read(byte[] b) {
        throw newException();
    }
    public long skip(long n) {
        throw newException();
    }
    public void mark(int readlimit) {
        throw newException();
    }
    public void reset() {
        throw newException();
    }
    public boolean markSupported() {
        throw newException();
    }
    public Object readUnshared() {
        throw newException();
    }
    public void defaultReadObject() {
        throw newException();
    }
    public GetField readFields() {
        throw newException();
    }
    public void registerValidation(ObjectInputValidation obj, int prio) {
        throw newException();
    }
    public int read() {
        throw newException();
    }
    public int read(byte[] buf, int off, int len) {
        throw newException();
    }
    public int available() {
        throw newException();
    }
    public boolean readBoolean() {
        throw newException();
    }
    public byte readByte() {
        throw newException();
    }
    public int readUnsignedByte() {
        throw newException();
    }
    public char readChar() {
        throw newException();
    }
    public short readShort() {
        throw newException();
    }
    public int readUnsignedShort() {
        throw newException();
    }
    public int readInt() {
        throw newException();
    }
    public long readLong() {
        throw newException();
    }
    public float readFloat() {
        throw newException();
    }
    public double readDouble() {
        throw newException();
    }
    public void readFully(byte[] buf) {
        throw newException();
    }
    public void readFully(byte[] buf, int off, int len) {
        throw newException();
    }
    public int skipBytes(int len) {
        throw newException();
    }
    public String readLine() {
        throw newException();
    }
    public String readUTF() {
        throw newException();
    }

    // ObjectReader methods

    void _startValue() {
        throw newException();
    }
    void _endValue() {
        throw newException();
    }
    void setCurrentValueDescriptor(ValueDescriptor desc) {
        throw newException();
    }
    Object readAbstractObject() {
        throw newException();
    }
    Object readAny() {
        throw newException();
    }
    Object readValueObject() {
        throw newException();
    }
    Object readValueObject(Class<?> clz) {
        throw newException();
    }
    org.omg.CORBA.Object readCorbaObject(Class<?> type) {
        throw newException();
    }
    Remote readRemoteObject(Class<?> type) {
        throw newException();
    }
    void readExternal(Externalizable ext) {
        throw newException();
    }

    @Override
    protected final Object readObjectOverride() {
        throw newException();
    }
}
