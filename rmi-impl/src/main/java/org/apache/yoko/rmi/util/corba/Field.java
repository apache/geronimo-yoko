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

package org.apache.yoko.rmi.util.corba;

import java.lang.reflect.Modifier;

import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.Unsafe;

public class Field {
    final long fieldID;

    // static final Category log = Category.getInstance(Field.class);
    static final Unsafe unsafe = getUnsafe();
    
    static final Logger logger = Logger.getLogger(Field.class
            .getName());

    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class uc = Unsafe.class;
            java.lang.reflect.Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
            logger.log(Level.FINE, "exception getting unsafe", ignore); 
        }
        return unsafe;
    }

    public Field(java.lang.reflect.Field f) {
        if (Modifier.isStatic(f.getModifiers()))
            fieldID = unsafe.staticFieldOffset(f);
        else
            fieldID = unsafe.objectFieldOffset(f);
    }

    public void set(Object obj, Object val) throws IllegalArgumentException,
            IllegalAccessException {
        unsafe.putObject(obj, fieldID, val);
    }

    public void setByte(Object obj, byte val) throws IllegalArgumentException,
            IllegalAccessException {
        unsafe.putByte(obj, fieldID, val);
    }

    public void setBoolean(Object obj, boolean val)
            throws IllegalArgumentException, IllegalAccessException {
        unsafe.putBoolean(obj, fieldID, val);
    }

    public void setShort(Object obj, short val)
            throws IllegalArgumentException, IllegalAccessException {
        unsafe.putShort(obj, fieldID, val);
    }

    public void setChar(Object obj, char val) throws IllegalArgumentException,
            IllegalAccessException {
        unsafe.putChar(obj, fieldID, val);
    }

    public void setInt(Object obj, int val) throws IllegalArgumentException,
            IllegalAccessException {
        unsafe.putInt(obj, fieldID, val);
    }

    public void setLong(Object obj, long val) throws IllegalArgumentException,
            IllegalAccessException {
        unsafe.putLong(obj, fieldID, val);
    }

    public void setFloat(Object obj, float val)
            throws IllegalArgumentException, IllegalAccessException {
        unsafe.putFloat(obj, fieldID, val);
    }

    public void setDouble(Object obj, double val)
            throws IllegalArgumentException, IllegalAccessException {
        unsafe.putDouble(obj, fieldID, val);
    }

    public Object get(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getObject(obj, fieldID);
    }

    public byte getByte(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getByte(obj, fieldID);
    }

    public boolean getBoolean(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getBoolean(obj, fieldID);
    }

    public short getShort(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getShort(obj, fieldID);
    }

    public char getChar(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getChar(obj, fieldID);
    }

    public int getInt(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getInt(obj, fieldID);
    }

    public long getLong(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getLong(obj, fieldID);
    }

    public float getFloat(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getFloat(obj, fieldID);
    }

    public double getDouble(Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        return unsafe.getDouble(obj, fieldID);
    }

}
