/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.orb.OB;

public final class ParseParams {
    //
    // Parse a parameter list. Parameters are separated by whitespace.
    // Quoted parameters are supported, as are escaped characters.
    // Parsing stops when the end of the string is reached, or when
    // an unescaped, unquoted comma character is encountered.
    //
    // The return value is -1 on end of string, or the position into the
    // input string marking the location where parsing should resume.
    //
    // For example, consider this input string:
    //
    // abc 123 'ABC 123' "abc,123", def ghi
    //
    // The first execution will return a list containing the elements
    // {"abc", "123", "ABC 123", "abc,123"}. The return value will point
    // to the string " def ghi".
    //
    // A BAD_PARAM exception will be raised if a formatting error
    // occurs.
    //
    public static int parse(String str, int start, java.util.Vector params) {
        int pos = start;
        final int strLen = str.length();
        boolean error = false;
        int result = -1;
        while (pos < strLen && result == -1 && !error) {
            //
            // Skip leading whitespace
            //
            char ch = str.charAt(pos);
            while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                pos++;
                ch = str.charAt(pos);
            }

            int startPos = pos;
            int endPos = 0;
            char quote = 0;
            boolean done = false;
            while (!done && !error) {
                if (pos == strLen) {
                    endPos = pos;
                    done = true;
                } else {
                    ch = str.charAt(pos);
                    switch (ch) {
                    case ',':
                        if (quote == 0) {
                            endPos = pos;
                            result = pos + 1;
                            done = true;
                        } else
                            pos++;
                        break;

                    case ' ':
                    case '\t':
                    case '\n':
                    case '\r':
                        if (quote == 0) {
                            endPos = pos;
                            done = true;
                        } else
                            pos++;
                        break;

                    case '\\':
                        pos++;
                        if (pos == strLen)
                            error = true;
                        else
                            pos++;
                        break;

                    case '\'':
                    case '"':
                        if (quote == 0) {
                            if (pos > startPos) {
                                done = true;
                                endPos = pos;
                            } else {
                                quote = ch;
                                pos++;
                            }
                        } else if (quote == ch) {
                            quote = 0;
                            done = true;
                            endPos = pos;
                            pos++;
                        } else
                            error = true; // mismatched quotes
                        break;

                    default:
                        pos++;
                        break;
                    }
                }
            }

            if (quote != 0)
                error = true;

            if (!error && pos > startPos) {
                ch = str.charAt(startPos);
                if (ch == '\'' || ch == '"')
                    startPos++;

                int len = endPos - startPos;
                StringBuffer buf = new StringBuffer(len);
                int s = startPos;
                while (s < endPos) {
                    ch = str.charAt(s);
                    if (ch != '\\')
                        buf.append(ch);
                    s++;
                }
                params.addElement(buf.toString());
            }
        }

        return result;
    }
}
