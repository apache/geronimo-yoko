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

public class SampleImpl  implements Sample {
	boolean bool = false;
	byte b = 0;
	short s = 0;
	int i = 0;
	long l = 0;
	float f;
	double d;
	char ch;
	int[] intArray = null;
	String str = "";
	Serializable serializable = null;
	Remote remote;
	SampleRemote sampleRemote = null;
	
	public boolean getBoolean() { return bool; }
	public void setBoolean(boolean bool) { this.bool = bool; }
	
	public byte getByte() {
		return b;
	}

	public void setByte(byte b) {
		this.b = b;
	}

	public short getShort() {
		return s;
	}

	public void setShort(short s) {
		this.s = s;
	}

	public int getInt() {
		return i;
	}

	public void setInt(int i) {
		this.i = i;
	}

	public long getLong() {
		return l;
	}

	public void setLong(long l) {
		this.l = l;
	}
	
	public float getFloat() { return f; }
	public void setFloat(float f) { this.f = f; }
	
	public double getDouble() { return d; }
	public void setDouble(double d) { this.d = d; }
	
	public char getChar() { return ch; }
	public void setChar(char ch) { this.ch = ch; }
	
	public int[] getIntArray() {
		return intArray;
	}
	
	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}

	public String getString() {
		return str;
	}

	public void setString(String str) {
		this.str = str;
	}
	
	public Serializable getSerializable() {return serializable; }
	public void setSerializable(Serializable s) {
	    System.out.println("received: " + s);
		if(serializable instanceof SampleSerializable) {
			SampleSerializable ser = (SampleSerializable) serializable;
			Object o = ser.getRemoteObject();
		     System.out.println("retrieved remote object: " + o);
		}
		this.serializable = s; 
	}
	
	public Remote getRemote() { return remote; }
	public void setRemote(Remote remote) { this.remote = remote; }
	public SampleRemote getSampleRemote() throws RemoteException {
		return sampleRemote;
	}
	public void setSampleRemote(SampleRemote sampleRemote) throws RemoteException {
		this.sampleRemote = sampleRemote;
	}

        public long sendReceiveLong(long l) {
            return l; 
        }

        public long[] sendReceiveLong(long[] l) {
            long temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public int sendReceiveInt(int l) {
            return l; 
        }

        public int[] sendReceiveInt(int[] l) {
            int temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public short sendReceiveShort(short l) {
            return l; 
        }

        public short[] sendReceiveShort(short[] l) {
            short temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public char sendReceiveChar(char l) {
            return l; 
        }

        public char[] sendReceiveChar(char[] l) {
            char temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public byte sendReceiveByte(byte l) {
            return l; 
        }

        public byte[] sendReceiveByte(byte[] l) {
            byte temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public boolean sendReceiveBoolean(boolean l) {
            return l; 
        }

        public boolean[] sendReceiveBoolean(boolean[] l) {
            boolean temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public String sendReceiveString(String l) {
            return l; 
        }

        public String[] sendReceiveString(String[] l) {
            String temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public float sendReceiveFloat(float l) {
            return l; 
        }

        public float[] sendReceiveFloat(float[] l) {
            float temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public double sendReceiveDouble(double l) {
            return l; 
        }

        public double[] sendReceiveDouble(double[] l) {
            double temp = l[0];
            l[0] = l[1];
            l[1] = temp;
            return l; 
        }

        public Remote sendReceiveRemote(Remote l) {
            return l; 
        }

        public Remote[] sendReceiveRemote(Remote[] l) {
            return l; 
        }

        public Serializable sendReceiveSerializable(Serializable l) {
            return l; 
        }

        public Serializable[] sendReceiveSerializable(Serializable[] l) {
            return l; 
        }
}
