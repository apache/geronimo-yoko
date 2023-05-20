/*
 * Copyright 2010 IBM Corporation and others.
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
package ORBTest;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class TestIntfWChar_impl extends ORBTest_WChar.IntfPOA {
    private POA m_poa;

    private char m_aWChar;

    private String m_aWString;

    public TestIntfWChar_impl(POA poa) {
        m_poa = poa;
    }

    public synchronized char attrWChar() {
        return m_aWChar;
    }

    public synchronized void attrWChar(char value) {
        m_aWChar = value;
    }

    public synchronized char opWChar(char a0, CharHolder a1, CharHolder a2) {
        m_aWChar = (char) (a0 + a1.value);
        a1.value = a2.value = m_aWChar;
        return m_aWChar;
    }

    public synchronized char opWCharEx(char a0, CharHolder a1, CharHolder a2)
            throws ORBTest_WChar.ExWChar {
        m_aWChar = (char) (a0 + a1.value);
        throw new ORBTest_WChar.ExWChar(m_aWChar);
    }

    public synchronized String attrWString() {
        return m_aWString;
    }

    public synchronized void attrWString(String value) {
        m_aWString = value;
    }

    public synchronized String opWString(String a0, StringHolder a1,
            StringHolder a2) {
        m_aWString = a0 + a1.value;
        a1.value = a2.value = m_aWString;
        return m_aWString;
    }

    public synchronized String opWStringEx(String a0, StringHolder a1,
            StringHolder a2) throws ORBTest_WChar.ExWString {
        m_aWString = a0 + a1.value;
        throw new ORBTest_WChar.ExWString(m_aWString);
    }

    public org.omg.PortableServer.POA _default_POA() {
        return m_poa;
    }
}
