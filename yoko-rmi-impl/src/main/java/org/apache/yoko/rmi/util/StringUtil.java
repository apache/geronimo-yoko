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

package org.apache.yoko.rmi.util;

import java.util.ArrayList;

/**
 * 
 * @author Jeppe Sommer (jso@eos.dk)
 * @author Kim Harding Christensen (khc@eos.dk)
 */
public class StringUtil {
    public static String capitalize(String str) {
        StringBuffer sb = new StringBuffer(str);
        sb.setCharAt(0, Character.toUpperCase(str.charAt(0)));
        return sb.toString();
    }

    public static String join(String[] strings, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < strings.length; n++) {
            sb.append(strings[n]);
            if (n < strings.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String[] split(String str, char splitChar) {
        ArrayList al = new ArrayList(8);
        int i = -1;
        String s;
        String rest = str;
        while ((i = rest.indexOf(splitChar)) != -1) {
            s = rest.substring(0, i);
            al.add(s);
            rest = rest.substring(i + 1);
        }
        al.add(rest);
        String[] result = new String[al.size()];
        al.toArray(result);
        return result;
    }

    public static String replace(String str, String oldStr, String newStr) {
        int prevIndex = 0, nextIndex;
        StringBuffer result = new StringBuffer();

        while ((nextIndex = str.indexOf(oldStr, prevIndex)) != -1) {
            result.append(str.substring(prevIndex, nextIndex));
            result.append(newStr);
            prevIndex = nextIndex + oldStr.length();
        }

        result.append(str.substring(prevIndex));

        return result.toString();
    }
    
    public static final byte[] HEX_CHARS = {
        (byte)'0',
        (byte)'1',
        (byte)'2',
        (byte)'3',
        (byte)'4',
        (byte)'5',
        (byte)'6',
        (byte)'7',
        (byte)'8',
        (byte)'9',
        (byte)'A',
        (byte)'B',
        (byte)'C',
        (byte)'D',
        (byte)'E',
        (byte)'F',
    };

    public static final byte[] VALID_IDL_CHARS = {
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,1,0,
        1,1,1,1, 1,1,1,1, 1,1,0,0, 0,0,0,0,
        0,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1,
        1,1,1,1, 1,1,1,1, 1,1,1,0, 0,0,0,1,
        0,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1,
        1,1,1,1, 1,1,1,1, 1,1,1,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
        1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1,
        0,1,1,1, 1,1,1,0, 1,1,1,1, 1,0,0,1,
        1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1,
        0,1,1,1, 1,1,1,0, 1,1,1,1, 1,0,0,1,
    };

    public static String convertToValidIDLNames(String str) {

        int length = str.length();
        if (length == 0) {
            return str;
        }
        
        StringBuilder builder = new StringBuilder();
        
        for (char c : str.toCharArray()) {
            if (c > 255 || VALID_IDL_CHARS[c] == 0) {
                builder.append("\\U" +
                        (char)HEX_CHARS[(c & 0xF000) >>> 12] +
                        (char)HEX_CHARS[(c & 0x0F00) >>> 8] +
                        (char)HEX_CHARS[(c & 0x00F0) >>> 4] +
                        (char)HEX_CHARS[(c & 0x000F)] );
            } else {
                builder.append(c);
            }
        }
        
        if (builder.length() > 0) {
            str = builder.toString();
        }
        
        return str;
    }
    
}
