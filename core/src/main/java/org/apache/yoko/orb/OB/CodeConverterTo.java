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

final class CodeConverterTo extends CodeConverterBase {
    //
    // Unicode character mapping table
    //
    private CharMapInfo toMap_;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    CodeConverterTo(CodeSetInfo fromSet, CodeSetInfo toSet, CharMapInfo toMap) {
        super(fromSet, toSet);

        toMap_ = toMap;
    }

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public boolean conversionRequired() {
        return true;
    }

    public char convert(char v) {
        for (int i = 0; i <= toMap_.upper_bound; i++) {
            if (toMap_.map_values[i] == v)
                return (char) i;
        }

        return v;
    }
}
