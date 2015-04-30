package test.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A class that writes more data than it reads in.
 * This is dangerous since a child class may not know where its own data begins.
 * This is addressed in custom marshaled stream format version 2, which boxes the 
 * parent data so the reading ORB can skip to the end of any custom marshaled 
 * parent data before reading in the child data.
 */
public class SampleCmsfv2ParentData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final byte value = (byte) 0xdd;

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(new Object[0]);
        oos.writeByte(0x77);
    }

    @Override
    public int hashCode() {
        return 31 + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SampleCmsfv2ParentData))
            return false;
        return (value == ((SampleCmsfv2ParentData) obj).value);
    }
    
    @Override
    public String toString() {
        return String.format("%s:%02x", super.toString(), value);
    }
}
