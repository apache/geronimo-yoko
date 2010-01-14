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

//
// This class parses the fields of an CORBANAME stringified name. Pass
// as string or octet sequence to the constructor. The isValid()
// method determines if the path is a valid stringified name. The
// getContents() method returns a string sequence of {id, kind} pairs.
//
public class CORBANameParser {
    byte[] path_;

    int curr_ = 0;

    byte terminator_ = 0;

    String[] contents_;

    boolean valid_ = true;

    private String next() {
        String field = new String();
        while (curr_ < path_.length && valid_) {
            byte ch = path_[curr_];
            if (ch == '.' || ch == '/') {
                ++curr_;
                terminator_ = ch;
                return field;
            }

            //
            // Escape character? '.', '/' and '\' are
            // permitted to follow.
            //
            if (ch == '\\') {
                ++curr_;
                if (curr_ >= path_.length) {
                    valid_ = false;
                    continue;
                }

                ch = path_[curr_];
                if (ch != '.' && ch != '/' && ch != '\\') {
                    valid_ = false;
                    continue;
                }
            }
            field += (char) ch;
            ++curr_;
        }

        terminator_ = 0;
        return field;
    }

    private boolean atEnd() {
        return curr_ >= path_.length;
    }

    private byte terminator() {
        return terminator_;
    }

    private void parse() {
        java.util.Vector vec = new java.util.Vector();
        while (!atEnd() && valid_) {
            String id = next();
            String kind;

            //
            // If the terminator is a '.' then the next field is the
            // kind.
            //
            if (terminator() == '.') {
                kind = next();
                //
                // The kind field is not permitted to end with a '.'.
                //
                if (terminator() == '.')
                    valid_ = false;
            } else
                kind = new String();
            if (valid_) {
                vec.addElement(id);
                vec.addElement(kind);
            }
        }

        if (valid_) {
            contents_ = new String[vec.size()];
            for (int i = 0; i < vec.size(); i++)
                contents_[i] = (String) vec.elementAt(i);
        }
    }

    public CORBANameParser(byte[] path) {
        path_ = path;

        parse();
    }

    public CORBANameParser(String path) {
        path_ = new byte[path.length()];
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) > 255) {
                valid_ = false;
                break;
            }
            path_[i] = (byte) path.charAt(i);
        }

        parse();
    }

    public boolean isValid() {
        return valid_;
    }

    public String[] getContents() {
        return contents_;
    }
}
