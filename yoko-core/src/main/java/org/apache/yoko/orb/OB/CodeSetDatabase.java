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

final public class CodeSetDatabase {
    //
    // The codeset registry IDs for the supported codesets
    //
    public final static int ISOLATIN1 = 0x00010001;

    public final static int ISOLATIN2 = 0x00010002;

    public final static int ISOLATIN3 = 0x00010003;

    public final static int ISOLATIN4 = 0x00010004;

    public final static int ISOLATIN5 = 0x00010005;

    public final static int ISOLATIN7 = 0x00010007;

    public final static int ISOLATIN9 = 0x00010009;

    public final static int PCS = 0x00010020;

    public final static int UTF8 = 0x05010001;

    public final static int UTF16 = 0x00010109;

    public final static int UCS2 = 0x00010100;

    //
    // The CodeSetDatabase singleton
    //
    private static CodeSetDatabase instance_;

    //
    // Initialize database
    //
    private static CharMapDatabaseInit database_ = new CharMapDatabaseInit();

    protected void finalize() throws Throwable {
        super.finalize();
    }

    static public CodeSetDatabase instance() {
        if (instance_ == null) {
            instance_ = new CodeSetDatabase();

            //
            // Add locale specific char codesets
            //
            String locale = java.util.Locale.getDefault().getLanguage();

            if (locale.equals("C") || locale.equals("POSIX")) {
                CodeSetUtil.addCharCodeSet(ISOLATIN1);
            } else {
                String loc = locale.substring(0, 2);

                //
                // West European (C, POSIX, Germany, England, France,
                // Netherlands, Portugal)
                //
                if (loc.equals("de") || loc.equals("en") || loc.equals("fr")
                        || loc.equals("nl") || loc.equals("pt")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN1);
                }
                //
                // North European (Denmark, Finland, Island, Norway, Sweden)
                //
                else if (loc.equals("da") || loc.equals("fi")
                        || loc.equals("is") || loc.equals("no")
                        || loc.equals("sv")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN4);
                }
                //
                // South European (Italy)
                //
                else if (loc.equals("it")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN3);
                }
                //
                // East European (Czek, Hungary, Poland, Slovakia, Slovenia)
                //
                else if (loc.equals("cs") || loc.equals("hu")
                        || loc.equals("pl") || loc.equals("sk")
                        || loc.equals("sl")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN2);
                }
                //
                // Greek (Greece)
                //
                else if (loc.equals("el")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN7);
                }
                //
                // Cyrillic (Russia)
                //
                else if (loc.equals("ru")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN5);
                }
                //
                // Turkish (Turkey)
                //
                else if (loc.equals("tr")) {
                    CodeSetUtil.addCharCodeSet(ISOLATIN9);
                }
            }

            //
            // Always supported
            //
            CodeSetUtil.addCharCodeSet(PCS);

            //
            // Always supported, but only as transmission codeset
            //
            CodeSetUtil.addCharCodeSet(UTF8);

            //
            // Add the supported wchar codesets in the preferred order
            //
            CodeSetUtil.addWcharCodeSet(UTF16);
        }

        return instance_;
    }

    synchronized public CodeConverterBase getConverter(int to, int from) {
        CodeSetInfo toSet = getCodeSetInfo(to);
        CodeSetInfo fromSet = getCodeSetInfo(from);

        if (toSet != null && fromSet != null) {
            if (toSet.max_bytes == 1) {
                //
                // Optimization: Don't use converter for identical
                // narrow codesets
                //
                if (to == from)
                    return null;
            }
        }

        CodeConverterBase converter = null;

        //
        // Conversion possible at all?
        //
        if (fromSet == null || toSet == null) {
            converter = new CodeConverterNone(fromSet, toSet);
        } else {
            //
            // Shortcut for UTF-16 / UCS-2, and UTF-8 / ISOLATIN1
            //
            if ((toSet.rgy_value == UTF16 || toSet.rgy_value == UCS2)
                    && (fromSet.rgy_value == UTF16 || fromSet.rgy_value == UCS2)) {
                converter = new CodeConverterSimple(fromSet, toSet);
            } else if (((toSet.rgy_value == UTF8 || toSet.rgy_value == ISOLATIN1) && fromSet.rgy_value == UTF8)
                    || (toSet.rgy_value == UTF8 && fromSet.rgy_value == ISOLATIN1)) {
                converter = new CodeConverterSimple(fromSet, toSet);
            } else {
                //
                // Create new converter and add it to the converter list.
                // No conversion to/from ISOLATIN1 and to/from UCS2.
                //
                CharMapInfo fromMap = null;
                CharMapInfo toMap = null;

                int fromBase = fromSet.max_bytes == 1 ? ISOLATIN1 : UTF16;
                if (fromSet.rgy_value != fromBase)
                    fromMap = getCharMapInfo(fromSet.rgy_value);

                int toBase = toSet.max_bytes == 1 ? ISOLATIN1 : UTF16;
                if (toSet.rgy_value != toBase)
                    toMap = getCharMapInfo(toSet.rgy_value);

                if (fromMap != null && toMap != null) {
                    converter = new CodeConverterBoth(fromSet, toSet, fromMap,
                            toMap);
                } else if (fromMap != null) {
                    converter = new CodeConverterFrom(fromSet, toSet, fromMap);
                } else if (toMap != null) {
                    converter = new CodeConverterTo(fromSet, toSet, toMap);
                } else {
                    Assert._OB_assert(false);
                }
            }
        }

        return converter;
    }

    public CodeSetInfo getCodeSetInfo(int rgy_value) {
        //
        // Check if codeset id is listed in registry
        //
        for (int i = 0; i < CodeSetDatabaseInit.codeSetInfoArraySize_; i++)
            if (CodeSetDatabaseInit.codeSetInfoArray_[i].rgy_value == rgy_value)
                return CodeSetDatabaseInit.codeSetInfoArray_[i];

        return null;
    }

    int determineTCS(org.omg.CONV_FRAME.CodeSetComponent clientCS,
            org.omg.CONV_FRAME.CodeSetComponent serverCS, int fallback) {
        //
        // Check if native codesets are present
        //
        if (clientCS.native_code_set != 0 && serverCS.native_code_set != 0) {
            //
            // Check if the native codesets are identical
            // In case they are no conversion is required
            //
            if (clientCS.native_code_set == serverCS.native_code_set)
                return serverCS.native_code_set;

            //
            // Check if client can convert
            //
            if (checkCodeSetId(clientCS, serverCS.native_code_set))
                return serverCS.native_code_set;

            //
            // Check if server can convert
            //
            if (checkCodeSetId(serverCS, clientCS.native_code_set))
                return clientCS.native_code_set;
        }

        //
        // Check for common codeset that can be used for transmission
        // The server supported codesets have preference
        //
        for (int i = 0; i < serverCS.conversion_code_sets.length; i++) {
            if (checkCodeSetId(clientCS, serverCS.conversion_code_sets[i]))
                return serverCS.conversion_code_sets[i];
        }

        if (clientCS.native_code_set != 0 && serverCS.native_code_set != 0) {
            //
            // Check compatibility by using the OSF registry,
            // use fallback codeset if compatible
            //
            if (isCompatible(clientCS.native_code_set, serverCS.native_code_set))
                return fallback;
        }

        throw new org.omg.CORBA.CODESET_INCOMPATIBLE();
    }

    private boolean isCompatible(int id1, int id2) {
        CodeSetInfo cs1 = getCodeSetInfo(id1);
        CodeSetInfo cs2 = getCodeSetInfo(id2);

        if (cs1 == null || cs2 == null)
            return false;

        for (int i = 0; i < cs1.char_values_size; i++) {
            for (int j = 0; j < cs2.char_values_size; j++) {
                //
                // In order to be compatible in OSF terms, two codesets
                // must have a common character set
                //
                if (cs1.char_values[i] == cs2.char_values[j])
                    return true;
            }
        }

        return false;
    }

    private boolean checkCodeSetId(org.omg.CONV_FRAME.CodeSetComponent cs,
            int id) {
        for (int i = 0; i < cs.conversion_code_sets.length; i++) {
            if (cs.conversion_code_sets[i] == id)
                return true;
        }

        //
        // ID not found
        //
        return false;
    }

    public int nameToId(String name) {
        org.apache.yoko.orb.OB.Assert._OB_assert(name != null);

        //
        // Check if codeset name is listed in registry
        // Return first match so that shortcuts can be used
        //
        for (int i = 0; i < CodeSetDatabaseInit.codeSetInfoArraySize_; i++) {
            String s = CodeSetDatabaseInit.codeSetInfoArray_[i].description;
            if (s.indexOf(name) != -1)
                return CodeSetDatabaseInit.codeSetInfoArray_[i].rgy_value;
        }

        //
        // Name not found
        //
        return 0;
    }

    private CharMapInfo getCharMapInfo(int rgy_value) {
        String name = "";

        switch (rgy_value) {
        case ISOLATIN1:
            name = "ISO/IEC 8859-1:1998 to Unicode";
            break;

        case ISOLATIN2:
            name = "ISO 8859-2:1999 to Unicode";
            break;

        case ISOLATIN3:
            name = "ISO/IEC 8859-3:1999 to Unicode";
            break;

        case ISOLATIN4:
            name = "ISO/IEC 8859-4:1998 to Unicode";
            break;

        case ISOLATIN5:
            name = "ISO 8859-5:1999 to Unicode";
            break;

        case ISOLATIN7:
            name = "ISO 8859-7:1987 to Unicode";
            break;

        case ISOLATIN9:
            name = "ISO/IEC 8859-9:1999 to Unicode";
            break;

        case PCS:
            name = "PCS to Unicode";
            break;

        default:
            break;
        }

        for (int i = 0; i < CharMapDatabaseInit.charMapInfoArraySize_; i++) {
            if (CharMapDatabaseInit.charMapInfoArray_[i].name.equals(name))
                return CharMapDatabaseInit.charMapInfoArray_[i];
        }

        return null;
    }
}
