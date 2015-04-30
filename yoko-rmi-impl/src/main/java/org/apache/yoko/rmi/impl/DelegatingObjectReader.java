package org.apache.yoko.rmi.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.rmi.Remote;

import org.omg.CORBA.portable.IndirectionException;

abstract class DelegatingObjectReader extends ObjectReader {
    private ObjectReader delegate;

    public DelegatingObjectReader() throws IOException {}

    void delegateTo(ObjectReader delegate) {
        this.delegate = delegate;
    }

    //////////////////////////////////////
    // ONLY DELEGATE METHODS BELOW HERE //
    //////////////////////////////////////

    protected final Object readObjectOverride() throws ClassNotFoundException, IOException {
        return delegate.readObjectOverride();
    }

    public int read(byte[] b) throws IOException {
        return delegate.read(b);
    }
    public long skip(long n) throws IOException {
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
        return delegate.readUnshared();
    }
    public void defaultReadObject() throws IOException, ClassNotFoundException {
        delegate.defaultReadObject();
    }
    public GetField readFields() throws IOException, ClassNotFoundException {
        return delegate.readFields();
    }
    public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException, InvalidObjectException {
        delegate.registerValidation(obj, prio);
    }
    public int read() throws IOException {
        return delegate.read();
    }
    public int read(byte[] buf, int off, int len) throws IOException {
        return delegate.read(buf, off, len);
    }
    public int available() throws IOException {
        return delegate.available();
    }
    public void close() throws IOException {
        delegate.close();
    }
    public boolean readBoolean() throws IOException {
        return delegate.readBoolean();
    }
    public byte readByte() throws IOException {
        return delegate.readByte();
    }
    public int readUnsignedByte() throws IOException {
        return delegate.readUnsignedByte();
    }
    public char readChar() throws IOException {
        return delegate.readChar();
    }
    public short readShort() throws IOException {
        return delegate.readShort();
    }
    public int readUnsignedShort() throws IOException {
        return delegate.readUnsignedShort();
    }
    public int readInt() throws IOException {
        return delegate.readInt();
    }
    public long readLong() throws IOException {
        return delegate.readLong();
    }
    public float readFloat() throws IOException {
        return delegate.readFloat();
    }
    public double readDouble() throws IOException {
        return delegate.readDouble();
    }
    public void readFully(byte[] buf) throws IOException {
        delegate.readFully(buf);
    }
    public void readFully(byte[] buf, int off, int len) throws IOException {
        delegate.readFully(buf, off, len);
    }
    public int skipBytes(int len) throws IOException {
        return delegate.skipBytes(len);
    }
    @Deprecated
    public String readLine() throws IOException {
        return delegate.readLine();
    }
    public String readUTF() throws IOException {
        return delegate.readUTF();
    }

    ///////////////////////////////////////
    // delegate methods for ObjectReader //
    ///////////////////////////////////////

    void _startValue() {
        delegate._startValue();
    }
    void _endValue() {
        delegate._endValue();
    }
    void setCurrentValueDescriptor(ValueDescriptor desc) {
        delegate.setCurrentValueDescriptor(desc);
    }
    Object readAbstractObject() throws IndirectionException {
        return delegate.readAbstractObject();
    }
    Object readAny() throws IndirectionException {
        return delegate.readAny();
    }
    Object readValueObject() throws IndirectionException {
        return delegate.readValueObject();
    }
    Object readValueObject(Class<?> clz) throws IndirectionException {
        return delegate.readValueObject(clz);
    }
    org.omg.CORBA.Object readCorbaObject(Class<?> type) {
        return delegate.readCorbaObject(type);
    }
    Remote readRemoteObject(Class<?> type) {
        return delegate.readRemoteObject(type);
    }
    void readExternal(Externalizable ext) throws IOException, ClassNotFoundException {
        delegate.readExternal(ext);
    }
}
