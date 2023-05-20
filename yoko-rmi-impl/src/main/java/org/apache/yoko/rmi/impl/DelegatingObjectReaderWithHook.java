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
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.rmi.Remote;

import org.omg.CORBA.portable.IndirectionException;

abstract class DelegatingObjectReaderWithBeforeReadHook extends DelegatingObjectReader {
    private final ObjectReader delegate;

    DelegatingObjectReaderWithBeforeReadHook(ObjectReader delegate) throws IOException {
        super.delegateTo(delegate);
        this.delegate = delegate;
    }

    abstract void beforeRead();

    @Override
    final void delegateTo(ObjectReader delegate) {
        throw new UnsupportedOperationException();
    }

    //////////////////////////////////////
    // ONLY DELEGATE METHODS BELOW HERE //
    //////////////////////////////////////

    public int read(byte[] b) throws IOException {
        beforeRead();
        return delegate.read(b);
    }
    public long skip(long n) throws IOException {
        beforeRead();
        return delegate.skip(n);
    }
    public void mark(int readlimit) {
        delegate.mark(readlimit);
    }
    public void reset() throws IOException {
        delegate.reset();
    }
    public boolean markSupported() {
        return delegate.markSupported();
    }
    public Object readUnshared() throws IOException, ClassNotFoundException {
        beforeRead();
        return delegate.readUnshared();
    }

    public abstract void defaultReadObject() throws IOException, ClassNotFoundException;
    public abstract GetField readFields() throws IOException, ClassNotFoundException;

    public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException, InvalidObjectException {
        delegate.registerValidation(obj, prio);
    }
    public int read() throws IOException {
        beforeRead();
        return delegate.read();
    }
    public int read(byte[] buf, int off, int len) throws IOException {
        beforeRead();
        return delegate.read(buf, off, len);
    }
    public int available() throws IOException {
        return delegate.available();
    }
    public void close() throws IOException {
        delegate.close();
    }
    public boolean readBoolean() throws IOException {
        beforeRead();
        return delegate.readBoolean();
    }
    public byte readByte() throws IOException {
        beforeRead();
        return delegate.readByte();
    }
    public int readUnsignedByte() throws IOException {
        beforeRead();
        return delegate.readUnsignedByte();
    }
    public char readChar() throws IOException {
        beforeRead();
        return delegate.readChar();
    }
    public short readShort() throws IOException {
        beforeRead();
        return delegate.readShort();
    }
    public int readUnsignedShort() throws IOException {
        beforeRead();
        return delegate.readUnsignedShort();
    }
    public int readInt() throws IOException {
        beforeRead();
        return delegate.readInt();
    }
    public long readLong() throws IOException {
        beforeRead();
        return delegate.readLong();
    }
    public float readFloat() throws IOException {
        beforeRead();
        return delegate.readFloat();
    }
    public double readDouble() throws IOException {
        beforeRead();
        return delegate.readDouble();
    }
    public void readFully(byte[] buf) throws IOException {
        beforeRead();
        delegate.readFully(buf);
    }
    public void readFully(byte[] buf, int off, int len) throws IOException {
        beforeRead();
        delegate.readFully(buf, off, len);
    }
    public int skipBytes(int len) throws IOException {
        beforeRead();
        return delegate.skipBytes(len);
    }
    @SuppressWarnings("deprecation")
    public String readLine() throws IOException {
        beforeRead();
        return delegate.readLine();
    }
    public String readUTF() throws IOException {
        beforeRead();
        return delegate.readUTF();
    }
    @Override
    protected final Object readObjectOverride() throws ClassNotFoundException ,IOException {
        beforeRead();
        return delegate.readObjectOverride();
    };

    ///////////////////////////////////////
    // delegate methods for ObjectReader //
    ///////////////////////////////////////
    Object readAbstractObject() throws IndirectionException {
        beforeRead();
        return delegate.readAbstractObject();
    }
    Object readAny() throws IndirectionException {
        beforeRead();
        return delegate.readAny();
    }
    Object readValueObject() throws IndirectionException {
        beforeRead();
        return delegate.readValueObject();
    }
    Object readValueObject(Class<?> clz) throws IndirectionException {
        beforeRead();
        return delegate.readValueObject(clz);
    }
    org.omg.CORBA.Object readCorbaObject(Class<?> type) {
        beforeRead();
        return delegate.readCorbaObject(type);
    }
    Remote readRemoteObject(Class<?> type) {
        beforeRead();
        return delegate.readRemoteObject(type);
    }
    void readExternal(Externalizable ext) throws IOException, ClassNotFoundException {
        beforeRead();
        delegate.readExternal(ext);
    }
}
