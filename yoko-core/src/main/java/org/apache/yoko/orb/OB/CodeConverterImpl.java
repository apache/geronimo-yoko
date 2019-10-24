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

import java.util.Objects;

import static org.apache.yoko.orb.OB.CharMapInfo.CM_IDENTITY;

final class CodeConverterImpl extends CodeConverterBase {
    private final CharMapInfo fromMap;
    private final CharMapInfo toMap;
    private final boolean conversionRequired;

    CodeConverterImpl(CodeSetInfo fromSet, CodeSetInfo toSet) {
        super(fromSet, toSet);
        fromMap = fromSet.charMap;
        toMap = toSet.charMap;
        Objects.requireNonNull(fromMap);
        Objects.requireNonNull(toMap);
        conversionRequired = (fromMap == CM_IDENTITY) && (toMap == CM_IDENTITY);
    }

    public boolean conversionRequired() {
        return conversionRequired;
    }

    public char convert(char v) {
        return convertFromJava(convertToJava(v));
    }

    private char convertFromJava(char v) {
        return toMap.convertFromJava(v);
    }

    private char convertToJava(char v) {
        return fromMap.convertToJava(v);
    }
}
