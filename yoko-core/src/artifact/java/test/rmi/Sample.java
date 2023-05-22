/*
 * Copyright 2022 IBM Corporation and others.
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
package test.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface Sample extends Remote {
	public boolean getBoolean() throws RemoteException;
	public void setBoolean(boolean b) throws RemoteException;
	public byte getByte() throws RemoteException;
	public void setByte(byte b) throws RemoteException;
	public short getShort() throws RemoteException;
	public void setShort(short s) throws RemoteException;
	public int getInt() throws RemoteException;
	public void setInt(int i) throws RemoteException;
	public long getLong() throws RemoteException;
	public void setLong(long l) throws RemoteException;
	public float getFloat() throws RemoteException;
	public void setFloat(float f) throws RemoteException;
	public double getDouble() throws RemoteException;
	public void setDouble(double d) throws RemoteException;
	public void setChar(char ch) throws RemoteException;
	public char getChar() throws RemoteException;
	public int[] getIntArray() throws RemoteException;
	public void setIntArray(int[] intArray) throws RemoteException;
	public String getString() throws RemoteException;
	public void setString(String s) throws RemoteException;
	public Serializable getSerializable() throws RemoteException;
	public void setSerializable(Serializable s) throws RemoteException;
	public Remote getRemote() throws RemoteException;
	public void setRemote(Remote remote) throws RemoteException;
	public SampleRemote getSampleRemote() throws RemoteException;
	public void setSampleRemote(SampleRemote sampleRemote) throws RemoteException;
        public long sendReceiveLong(long l) throws RemoteException;
        public long[] sendReceiveLong(long[] l) throws RemoteException;
        public int sendReceiveInt(int l) throws RemoteException;
        public int[] sendReceiveInt(int[] l) throws RemoteException;
        public short sendReceiveShort(short l) throws RemoteException;
        public short[] sendReceiveShort(short[] l) throws RemoteException;
        public char sendReceiveChar(char l) throws RemoteException;
        public char[] sendReceiveChar(char[] l) throws RemoteException;
        public byte sendReceiveByte(byte l) throws RemoteException;
        public byte[] sendReceiveByte(byte[] l) throws RemoteException;
        public boolean sendReceiveBoolean(boolean l) throws RemoteException;
        public boolean [] sendReceiveBoolean(boolean[] l) throws RemoteException;
        public String sendReceiveString(String l) throws RemoteException;
        public String [] sendReceiveString(String[] l) throws RemoteException;
        public float sendReceiveFloat(float l) throws RemoteException;
        public float [] sendReceiveFloat(float[] l) throws RemoteException;
        public double sendReceiveDouble(double l) throws RemoteException;
        public double [] sendReceiveDouble(double[] l) throws RemoteException;
        public Remote sendReceiveRemote(Remote l) throws RemoteException;
        public Remote [] sendReceiveRemote(Remote[] l) throws RemoteException;
        public Serializable sendReceiveSerializable(Serializable l) throws RemoteException;
        public Serializable [] sendReceiveSerializable(Serializable[] l) throws RemoteException;
}                                               
