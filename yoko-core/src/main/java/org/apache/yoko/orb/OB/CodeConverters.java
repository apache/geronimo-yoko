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

final public class CodeConverters {
    // This class may look immutable, but CodeConverterBase holds reader and writer objects that are stateful and mutable
    public static final CodeConverters NULL_CONVERTER = new CodeConverters(null, null, null, null);

    public final CodeConverterBase inputCharConverter;
    public final CodeConverterBase outputCharConverter;
    public final CodeConverterBase inputWcharConverter;
    public final CodeConverterBase outputWcharConverter;

    private CodeConverters(CodeConverterBase charIn, CodeConverterBase charOut, CodeConverterBase wcharIn, CodeConverterBase wcharOut) {
        inputCharConverter = charIn;
        outputCharConverter = charOut;
        inputWcharConverter = wcharIn;
        outputWcharConverter = wcharOut;
    }

    private CodeConverters(CodeConverters c) {
        this(c.inputCharConverter, c.outputCharConverter, c.inputWcharConverter, c.outputWcharConverter);
    }

    public static CodeConverters createCopy(CodeConverters template) {
        if (template == null) return NULL_CONVERTER;
        if (template == NULL_CONVERTER) return NULL_CONVERTER;
        return new CodeConverters(template);
    }

    public static CodeConverters create(CodeConverterBase charIn, CodeConverterBase charOut, CodeConverterBase wcharIn, CodeConverterBase wcharOut) {
        if (charIn == null && charOut == null && wcharIn == null && wcharOut == null) return NULL_CONVERTER;
        return new CodeConverters(charIn, charOut, wcharIn, wcharOut);
    }

    public boolean equals(Object obj) {
        CodeConverters conv = (CodeConverters) obj;
        if (conv == null)
            return false;

        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;

        if (inputCharConverter == null && conv.inputCharConverter == null)
            a = true;
        else if (inputCharConverter != null
                && inputCharConverter.equals(conv.inputCharConverter))
            a = true;

        if (outputCharConverter == null && conv.outputCharConverter == null)
            b = true;
        else if (outputCharConverter != null
                && outputCharConverter.equals(conv.outputCharConverter))
            b = true;

        if (inputWcharConverter == null && conv.inputWcharConverter == null)
            c = true;
        else if (inputWcharConverter != null
                && inputWcharConverter.equals(conv.inputWcharConverter))
            c = true;

        if (outputWcharConverter == null && conv.outputWcharConverter == null)
            d = true;
        else if (outputWcharConverter != null
                && outputWcharConverter.equals(conv.outputWcharConverter))
            d = true;

        return a && b && c && d;
    }

    public int hashCode() {
        int result;
        result = (inputCharConverter != null ? inputCharConverter.hashCode() : 0);
        result = 29 * result + (outputCharConverter != null ? outputCharConverter.hashCode() : 0);
        result = 29 * result + (inputWcharConverter != null ? inputWcharConverter.hashCode() : 0);
        result = 29 * result + (outputWcharConverter != null ? outputWcharConverter.hashCode() : 0);
        return result;
    }
}
