/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.INTERNAL;

public final class CorbaObjectWriter extends ObjectWriter {
    static Logger logger = Logger.getLogger(CorbaObjectWriter.class.getName());
    
    final org.omg.CORBA_2_3.portable.OutputStream out;

    CorbaObjectWriter(org.omg.CORBA.portable.OutputStream out,
            java.io.Serializable obj) throws java.io.IOException {
        super(obj);
        this.out = (org.omg.CORBA_2_3.portable.OutputStream) out;
    }

    public void write(int val) throws java.io.IOException {
        beforeWriteData();
        out.write_octet((byte) val);
    }

    public void write(byte[] val) throws java.io.IOException {
        beforeWriteData();
        write(val, 0, val.length);
    }

    public void write(byte[] arr, int off, int len) throws java.io.IOException {
        beforeWriteData();
        out.write_octet_array(arr, off, len);
    }

    public void writeBoolean(boolean val) throws java.io.IOException {
        beforeWriteData();
        out.write_boolean(val);
    }

    public void writeByte(int val) throws java.io.IOException {
        beforeWriteData();
        out.write_octet((byte) val);
    }

    public void writeShort(int val) throws java.io.IOException {
        beforeWriteData();
        out.write_short((short) val);
    }

    public void writeChar(int val) throws java.io.IOException {
        beforeWriteData();
        out.write_wchar((char) val);
    }

    public void writeInt(int val) throws java.io.IOException {
        beforeWriteData();
        out.write_long(val);
    }

    public void writeLong(long val) throws java.io.IOException {
        beforeWriteData();
        out.write_longlong(val);
    }

    public void writeFloat(float val) throws java.io.IOException {
        beforeWriteData();
        out.write_float(val);
    }

    public void writeDouble(double val) throws java.io.IOException {
        beforeWriteData();
        out.write_double(val);
    }

    public void writeBytes(java.lang.String val) throws java.io.IOException {
        for (int i = 0; i < val.length(); i++) {
            writeByte((int) val.charAt(i));
        }
    }

    public void writeChars(java.lang.String val) throws java.io.IOException {
        for (int i = 0; i < val.length(); i++) {
            writeChar((int) val.charAt(i));
        }
    }

    public void writeUTF(java.lang.String val) throws java.io.IOException {
        beforeWriteData();
        out.write_wstring(val);
    }

    public void writeObjectOverride(Object obj) throws IOException {
        beforeWriteData();
        try {
            javax.rmi.CORBA.Util.writeAbstractObject(out, obj);
        } catch (Error err) {
            logger.log(Level.FINE, "exception in writeObjectOverride", err);
            throw err;
        }
    }

    public void writeValueObject(Object obj) throws IOException {
        beforeWriteData();
        try {
            out.write_value((java.io.Serializable) obj);
        } catch (Error err) {
            logger.log(Level.FINE, "exception in writeValueObject", err);
            throw err;
        }

    }
    
    public void writeCorbaObject(Object obj) throws IOException {
	beforeWriteData();
	javax.rmi.CORBA.Util.writeRemoteObject(out, obj);
    }

    public void writeRemoteObject(Object obj) throws IOException {
        beforeWriteData();
        javax.rmi.CORBA.Util.writeRemoteObject(out, obj);
    }

    public void writeAny(Object obj) throws IOException {
        beforeWriteData();
        javax.rmi.CORBA.Util.writeAny(out, obj);
    }
    
    public ObjectReader getObjectReader(Object newObject) {
        throw new INTERNAL("cannot do this");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.ObjectWriter#_startValue()
     */
    protected void _startValue(String repID) throws IOException {
        org.omg.CORBA.portable.ValueOutputStream vout = (org.omg.CORBA.portable.ValueOutputStream) out;

        vout.start_value(repID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.ObjectWriter#_endValue()
     */
    protected void _endValue() throws IOException {
        org.omg.CORBA.portable.ValueOutputStream vout = (org.omg.CORBA.portable.ValueOutputStream) out;

        vout.end_value();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.yoko.rmi.impl.ObjectWriter#_nullValue()
     */
    protected void _nullValue() throws IOException {
        out.write_long(0);
    }

}
