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

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.complementOf;
import static org.apache.yoko.orb.OB.CharMapInfo.IDENTITY;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_2;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_3;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_4;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_5;
import static org.apache.yoko.orb.OB.CharMapInfo.LATIN_9;

public enum CodeSetInfo {
    NONE("none", 0x00000000, 2, new short[0]),
    ISO_LATIN_1("ISO 8859-1:1987; Latin Alphabet No. 1", 0x00010001, 1, new short[]{0x0011}, IDENTITY),
    ISO_LATIN_2("ISO 8859-2:1987; Latin Alphabet No. 2", 0x00010002, 1, new short[]{0x0012}, LATIN_2),
    ISO_LATIN_3("ISO 8859-3:1988; Latin Alphabet No. 3", 0x00010003, 1, new short[]{0x0013}, LATIN_3),
    ISO_LATIN_4("ISO 8859-4:1988; Latin Alphabet No. 4", 0x00010004, 1, new short[]{0x0014}, LATIN_4),
    ISO_LATIN_5("ISO/IEC 8859-5:1988; Latin-Cyrillic Alphabet", 0x00010005, 1, new short[]{0x0015}, LATIN_5),
    ISO_8859_6_1987("ISO 8859-6:1987; Latin-Arabic Alphabet", 0x00010006, 1, new short[]{0x0016}),
    ISO_LATIN_7("ISO 8859-7:1987; Latin-Greek Alphabet", 0x00010007, 1, new short[]{0x0017}, LATIN_9),
    ISO_8859_8_1988("ISO 8859-8:1988; Latin-Hebrew Alphabet", 0x00010008, 1, new short[]{0x0018}),
    ISO_LATIN_9("ISO/IEC 8859-9:1989; Latin Alphabet No. 5", 0x00010009, 1, new short[]{0x0019}, LATIN_9),
    ISO_IEC_8859_10_1992("ISO/IEC 8859-10:1992; Latin Alphabet No. 6", 0x0001000a, 1, new short[]{0x001a}),
    ISO_IEC_8859_15_1999("ISO/IEC 8859-15:1999; Latin Alphabet No. 9", 0x0001000f, 1, new short[]{0x0011}),
    ISO_646_IRV("ISO 646:1991 IRV (International Reference Version)", 0x00010020, 1, new short[]{0x0001}, IDENTITY),
    UCS_2("ISO/IEC 10646-1:1993; UCS-2, Level 1", 0x00010100, 2, new short[]{0x1000}, IDENTITY),
    ISO_IEC_10646_1_1993__UCS2_L2("ISO/IEC 10646-1:1993; UCS-2, Level 2", 0x00010101, 2, new short[]{0x1000}),
    ISO_IEC_10646_1_1993__UCS2_L3("ISO/IEC 10646-1:1993; UCS-2, Level 3", 0x00010102, 2, new short[]{0x1000}),
    ISO_IEC_10646_1_1993__USC4_L1("ISO/IEC 10646-1:1993; UCS-4, Level 1", 0x00010104, 4, new short[]{0x1000}),
    ISO_IEC_10646_1_1993__USC4_L2("ISO/IEC 10646-1:1993; UCS-4, Level 2", 0x00010105, 4, new short[]{0x1000}),
    ISO_IEC_10646_1_1993__USC4_L3("ISO/IEC 10646-1:1993; UCS-4, Level 3", 0x00010106, 4, new short[]{0x1000}),
    ISO_IEC_10646_1_1993__UTF1("ISO/IEC 10646-1:1993; UTF-1, UCS Transformation Format 1", 0x00010108, 5, new short[]{0x1000}),
    UTF_16("ISO/IEC 10646-1:1993; UTF-16, UCS Transformation Format 16-bit form", 0x00010109, 2, new short[]{0x1000}, IDENTITY),
    JIS_X0201_1976("JIS X0201:1976; Japanese phonetic characters", 0x00030001, 1, new short[]{0x0080}),
    JIS_X0208_1978("JIS X0208:1978 Japanese Kanji Graphic Characters", 0x00030004, 2, new short[]{0x0081}),
    JIS_X0208_1983("JIS X0208:1983 Japanese Kanji Graphic Characters", 0x00030005, 2, new short[]{0x0081}),
    JIS_X0208_1990("JIS X0208:1990 Japanese Kanji Graphic Characters", 0x00030006, 2, new short[]{0x0081}),
    JIS_X0212_1990("JIS X0212:1990; Supplementary Japanese Kanji Graphic Chars", 0x0003000a, 2, new short[]{0x0082}),
    JIS_eucJP_1993("JIS eucJP:1993; Japanese EUC", 0x00030010, 3, new short[]{0x0011, 0x0080, 0x0081, 0x0082}),
    KS_C5601_1987("KS C5601:1987; Korean Hangul and Hanja Graphic Characters", 0x00040001, 2, new short[]{0x0100}),
    KS_C5657_1991("KS C5657:1991; Supplementary Korean Graphic Characters", 0x00040002, 2, new short[]{0x0101}),
    KS_eucKR_1991("KS eucKR:1991; Korean EUC", 0x0004000a, 2, new short[]{0x0011, 0x0100, 0x0101}),
    CNS_11643_1986("CNS 11643:1986; Taiwanese Hanzi Graphic Characters", 0x00050001, 2, new short[]{0x0180}),
    CNS_11643_1992("CNS 11643:1992; Taiwanese Extended Hanzi Graphic Chars", 0x00050002, 4, new short[]{0x0181}),
    CNS_eucTW_1991("CNS eucTW:1991; Taiwanese EUC", 0x0005000a, 4, new short[]{0x0001, 0x0180}),
    CNS_eucTW_1993("CNS eucTW:1993; Taiwanese EUC", 0x00050010, 4, new short[]{0x0001, 0x0181}),
    TIS_620_2529("TIS 620-2529, Thai characters", 0x000b0001, 1, new short[]{0x0200}),
    TTB_CCDC_1984("TTB CCDC:1984; Chinese Code for Data Communications", 0x000d0001, 2, new short[]{0x0180}),
    OSF_Japanese_UJIS("OSF Japanese UJIS", 0x05000010, 2, new short[]{0x0001, 0x0080, 0x0081}),
    OSF_Japanese_SJIS_1("OSF Japanese SJIS-1", 0x05000011, 2, new short[]{0x0001, 0x0080, 0x0081}),
    OSF_Japanese_SJIS_2("OSF Japanese SJIS-2", 0x05000012, 2, new short[]{0x0001, 0x0080, 0x0081}),
    UTF_8("X/Open UTF-8; UCS Transformation Format 8 (UTF-8)", 0x05010001, 6, new short[]{0x1000}),
    JVC_eucJP("JVC_eucJP", 0x05020001, 3, new short[]{0x0001, 0x0080, 0x0081, 0x0082}),
    JVC_SJIS("JVC_SJIS", 0x05020002, 2, new short[]{0x0001, 0x0080, 0x0081}),
    DEC_Kanji("DEC Kanji", 0x10000001, 2, new short[]{0x0011, 0x0080, 0x0081}),
    Super_DEC_Kanji("Super DEC Kanji", 0x10000002, 3, new short[]{0x0011, 0x0080, 0x0081, 0x0082}),
    DEC_Shift_JIS("DEC Shift JIS", 0x10000003, 2, new short[]{0x0011, 0x0080, 0x0081}),
    HP_roman8("HP roman8; English and Western European languages", 0x10010001, 1, new short[]{0x0011}),
    HP_kana8("HP kana8; Japanese katakana (incl JIS X0201:1976)", 0x10010002, 1, new short[]{0x0080}),
    HP_arabic8("HP arabic8; Arabic", 0x10010003, 1, new short[]{0x0016}),
    HP_greek8("HP greek8; Greek", 0x10010004, 1, new short[]{0x0017}),
    HP_hebrew8("HP hebrew8; Hebrew", 0x10010005, 1, new short[]{0x0018}),
    HP_turkish8("HP turkish8; Turkish", 0x10010006, 1, new short[]{0x0013, 0x0019}),
    HP15CN("HP15CN; encoding method for Simplified Chinese", 0x10010007, 2, new short[]{0x0001, 0x0300}),
    HP_big5("HP big5; encoding method for Traditional Chinese", 0x10010008, 2, new short[]{0x0001, 0x0180}),
    HP_japanese15__sjis("HP japanese15 (sjis); Shift-JIS for mainframe (incl JIS X0208:1990)", 0x10010009, 2, new short[]{0x0001, 0x0080, 0x0081}),
    HP_sjishi("HP sjishi; Shift-JIS for HP user (incl JIS X0208:1990)", 0x1001000a, 2, new short[]{0x0001, 0x0080, 0x0081}),
    HP_sjispc("HP sjispc; Shift-JIS for PC (incl JIS X0208:1990)", 0x1001000b, 2, new short[]{0x0001, 0x0080, 0x0081}),
    HP_ujis("HP ujis; EUC (incl JIS X0208:1990)", 0x1001000c, 2, new short[]{0x0001, 0x0080, 0x0081}),
    IBM_037__CCSID_00037("IBM-037 (CCSID 00037); CECP for USA, Canada, NL, Ptgl, Brazil, Australia, NZ", 0x10020025, 1, new short[]{0x0011}),
    IBM_273__CCSID_00273("IBM-273 (CCSID 00273); CECP for Austria, Germany", 0x10020111, 1, new short[]{0x0011}),
    IBM_277__CCSID_00277("IBM-277 (CCSID 00277); CECP for Denmark, Norway", 0x10020115, 1, new short[]{0x0011}),
    IBM_278__CCSID_00278("IBM-278 (CCSID 00278); CECP for Finland, Sweden", 0x10020116, 1, new short[]{0x0011}),
    IBM_280__CCSID_00280("IBM-280 (CCSID 00280); CECP for Italy", 0x10020118, 1, new short[]{0x0011}),
    IBM_282__CCSID_00282("IBM-282 (CCSID 00282); CECP for Portugal", 0x1002011a, 1, new short[]{0x0011}),
    IBM_284__CCSID_00284("IBM-284 (CCSID 00284); CECP for Spain, Latin America (Spanish)", 0x1002011c, 1, new short[]{0x0011}),
    IBM_285__CCSID_00285("IBM-285 (CCSID 00285); CECP for United Kingdom", 0x1002011d, 1, new short[]{0x0011}),
    IBM_290__CCSID_00290("IBM-290 (CCSID 00290); Japanese Katakana Host Ext SBCS", 0x10020122, 1, new short[]{0x0080}),
    IBM_297__CCSID_00297("IBM-297 (CCSID 00297); CECP for France", 0x10020129, 1, new short[]{0x0011}),
    IBM_300__CCSID_00300("IBM-300 (CCSID 00300); Japanese Host DBCS incl 4370 UDC", 0x1002012c, 2, new short[]{0x0081}),
    IBM_301__CCSID_00301("IBM-301 (CCSID 00301); Japanese PC Data DBCS incl 1880 UDC", 0x1002012d, 2, new short[]{0x0081}),
    IBM_420__CCSID_00420("IBM-420 (CCSID 00420); Arabic (presentation shapes)", 0x100201a4, 1, new short[]{0x0016}),
    IBM_424__CCSID_00424("IBM-424 (CCSID 00424); Hebrew", 0x100201a8, 1, new short[]{0x0018}),
    IBM_437__CCSID_00437("IBM-437 (CCSID 00437); PC USA", 0x100201b5, 1, new short[]{0x0011}),
    IBM_500__CCSID_00500("IBM-500 (CCSID 00500); CECP for Belgium, Switzerland", 0x100201f4, 1, new short[]{0x0011}),
    IBM_833__CCSID_00833("IBM-833 (CCSID 00833); Korean Host Extended SBCS", 0x10020341, 1, new short[]{0x0001}),
    IBM_834__CCSID_00834("IBM-834 (CCSID 00834); Korean Host DBCS incl 1227 UDC", 0x10020342, 2, new short[]{0x0100}),
    IBM_835__CCSID_00835("IBM-835 (CCSID 00835); T-Ch Host DBCS incl 6204 UDC", 0x10020343, 2, new short[]{0x0180}),
    IBM_836__CCSID_00836("IBM-836 (CCSID 00836); S-Ch Host Extended SBCS", 0x10020344, 1, new short[]{0x0001}),
    IBM_837__CCSID_00837("IBM-837 (CCSID 00837); S-Ch Host DBCS incl 1880 UDC", 0x10020345, 2, new short[]{0x0300}),
    IBM_838__CCSID_00838("IBM-838 (CCSID 00838); Thai Host Extended SBCS", 0x10020346, 1, new short[]{0x0200}),
    IBM_839__CCSID_00839("IBM-839 (CCSID 00839); Thai Host DBCS incl 374 UDC", 0x10020347, 2, new short[]{0x0200}),
    IBM_850__CCSID_00850("IBM-850 (CCSID 00850); Multilingual IBM PC Data-MLP 222", 0x10020352, 1, new short[]{0x0011}),
    IBM_852__CCSID_00852("IBM-852 (CCSID 00852); Multilingual Latin-2", 0x10020354, 1, new short[]{0x0012}),
    IBM_855__CCSID_00855("IBM-855 (CCSID 00855); Cyrillic PC Data", 0x10020357, 1, new short[]{0x0015}),
    IBM_856__CCSID_00856("IBM-856 (CCSID 00856); Hebrew PC Data (extensions)", 0x10020358, 1, new short[]{0x0018}),
    IBM_857__CCSID_00857("IBM-857 (CCSID 00857); Turkish Latin-5 PC Data", 0x10020359, 1, new short[]{0x0019}),
    IBM_861__CCSID_00861("IBM-861 (CCSID 00861); PC Data Iceland", 0x1002035d, 1, new short[]{0x0011}),
    IBM_862__CCSID_00862("IBM-862 (CCSID 00862); PC Data Hebrew", 0x1002035e, 1, new short[]{0x0018}),
    IBM_863__CCSID_00863("IBM-863 (CCSID 00863); PC Data Canadian French", 0x1002035f, 1, new short[]{0x0011}),
    IBM_864__CCSID_00864("IBM-864 (CCSID 00864); Arabic PC Data", 0x10020360, 1, new short[]{0x0016}),
    IBM_866__CCSID_00866("IBM-866 (CCSID 00866); PC Data Cyrillic 2", 0x10020362, 1, new short[]{0x0015}),
    IBM_868__CCSID_00868("IBM-868 (CCSID 00868); Urdu PC Data", 0x10020364, 1, new short[]{0x0016}),
    IBM_869__CCSID_00869("IBM-869 (CCSID 00869); Greek PC Data", 0x10020365, 1, new short[]{0x0017}),
    IBM_870__CCSID_00870("IBM-870 (CCSID 00870); Multilingual Latin-2 EBCDIC", 0x10020366, 1, new short[]{0x0012}),
    IBM_871__CCSID_00871("IBM-871 (CCSID 00871); CECP for Iceland", 0x10020367, 1, new short[]{0x0011}),
    IBM_874__CCSID_00874("IBM-874 (CCSID 00874); Thai PC Display Extended SBCS", 0x1002036a, 1, new short[]{0x0200}),
    IBM_875__CCSID_00875("IBM-875 (CCSID 00875); Greek", 0x1002036b, 1, new short[]{0x0017}),
    IBM_880__CCSID_00880("IBM-880 (CCSID 00880); Multilingual Cyrillic", 0x10020370, 1, new short[]{0x0015}),
    IBM_891__CCSID_00891("IBM-891 (CCSID 00891); Korean PC Data SBCS", 0x1002037b, 1, new short[]{0x0001}),
    IBM_896__CCSID_00896("IBM-896 (CCSID 00896); Japanese Katakana characters; superset of JIS X0201:1976", 0x10020380, 1, new short[]{0x0080}),
    IBM_897__CCSID_00897("IBM-897 (CCSID 00897); PC Data Japanese SBCS (use with CP 00301)", 0x10020381, 1, new short[]{0x0080}),
    IBM_903__CCSID_00903("IBM-903 (CCSID 00903); PC Data Simplified Chinese SBCS (use with  DBCS)", 0x10020387, 1, new short[]{0x0001}),
    IBM_904__CCSID_00904("IBM-904 (CCSID 00904); PC Data Traditional Chinese SBCS (use with  DBCS)", 0x10020388, 1, new short[]{0x0001}),
    IBM_918__CCSID_00918("IBM-918 (CCSID 00918); Urdu", 0x10020396, 1, new short[]{0x0016}),
    IBM_921__CCSID_00921("IBM-921 (CCSID 00921); Baltic 8-Bit", 0x10020399, 1, new short[]{0x001a}),
    IBM_922__CCSID_00922("IBM-922 (CCSID 00922); Estonia 8-Bit", 0x1002039a, 1, new short[]{0x001a}),
    IBM_926__CCSID_00926("IBM-926 (CCSID 00926); Korean PC Data DBCS incl 1880 UDC", 0x1002039e, 2, new short[]{0x0100}),
    IBM_927__CCSID_00927("IBM-927 (CCSID 00927); T-Ch PC Data DBCS incl 6204 UDC", 0x1002039f, 2, new short[]{0x0180}),
    IBM_928__CCSID_00928("IBM-928 (CCSID 00928); S-Ch PC Data DBCS incl 1880 UDC", 0x100203a0, 2, new short[]{0x0300}),
    IBM_929__CCSID_00929("IBM-929 (CCSID 00929); Thai PC Data DBCS incl 374 UDC", 0x100203a1, 2, new short[]{0x0200}),
    IBM_930__CCSID_00930("IBM-930 (CCSID 00930); Kat-Kanji Host MBCS Ext-SBCS", 0x100203a2, 2, new short[]{0x0080, 0x0081}),
    IBM_932__CCSID_00932("IBM-932 (CCSID 00932); Japanese PC Data Mixed", 0x100203a4, 2, new short[]{0x0080, 0x0081}),
    IBM_933__CCSID_00933("IBM-933 (CCSID 00933); Korean Host Extended SBCS", 0x100203a5, 2, new short[]{0x0001, 0x0100}),
    IBM_934__CCSID_00934("IBM-934 (CCSID 00934); Korean PC Data Mixed", 0x100203a6, 2, new short[]{0x0001, 0x0100}),
    IBM_935__CCSID_00935("IBM-935 (CCSID 00935); S-Ch Host Mixed", 0x100203a7, 2, new short[]{0x0001, 0x0300}),
    IBM_936__CCSID_00936("IBM-936 (CCSID 00936); PC Data S-Ch MBCS", 0x100203a8, 2, new short[]{0x0001, 0x0300}),
    IBM_937__CCSID_00937("IBM-937 (CCSID 00937); T-Ch Host Mixed", 0x100203a9, 2, new short[]{0x0001, 0x0180}),
    IBM_938__CCSID_00938("IBM-938 (CCSID 00938); PC Data T-Ch MBCS", 0x100203aa, 2, new short[]{0x0001, 0x0180}),
    IBM_939__CCSID_00939("IBM-939 (CCSID 00939); Latin-Kanji Host MBCS", 0x100203ab, 2, new short[]{0x0080, 0x0081}),
    IBM_941__CCSID_00941("IBM-941 (CCSID 00941); Japanese PC DBCS for Open", 0x100203ad, 2, new short[]{0x0081}),
    IBM_942__CCSID_00942("IBM-942 (CCSID 00942); Japanese PC Data Mixed", 0x100203ae, 2, new short[]{0x0080, 0x0081}),
    IBM_943__CCSID_00943("IBM-943 (CCSID 00943); Japanese PC MBCS for Open", 0x100203af, 2, new short[]{0x0080, 0x0081}),
    IBM_946__CCSID_00946("IBM-946 (CCSID 00946); S-Ch PC Data Mixed", 0x100203b2, 2, new short[]{0x0001, 0x0300}),
    IBM_947__CCSID_00947("IBM-947 (CCSID 00947); T-Ch PC Data DBCS incl 6204 UDC", 0x100203b3, 2, new short[]{0x0180}),
    IBM_948__CCSID_00948("IBM-948 (CCSID 00948); T-Ch PC Data Mixed", 0x100203b4, 2, new short[]{0x0001, 0x0180}),
    IBM_949__CCSID_00949("IBM-949 (CCSID 00949); IBM KS PC Data Mixed", 0x100203b5, 2, new short[]{0x0001, 0x0100}),
    IBM_950__CCSID_00950("IBM-950 (CCSID 00950); T-Ch PC Data Mixed", 0x100203b6, 2, new short[]{0x0001, 0x0180}),
    IBM_951__CCSID_00951("IBM-951 (CCSID 00951); IBM KS PC Data DBCS incl 1880 UDC", 0x100203b7, 2, new short[]{0x0100}),
    IBM_955__CCSID_00955("IBM-955 (CCSID 00955); Japan Kanji characters; superset of JIS X0208:1978", 0x100203bb, 2, new short[]{0x0081}),
    IBM_964__CCSID_00964("IBM-964 (CCSID 00964); T-Chinese EUC CNS1163 plane 1,2", 0x100203c4, 4, new short[]{0x0001, 0x0180}),
    IBM_970__CCSID_00970("IBM-970 (CCSID 00970); Korean EUC", 0x100203ca, 2, new short[]{0x0011, 0x0100, 0x0101}),
    IBM_1006__CCSID_01006("IBM-1006 (CCSID 01006); Urdu 8-bit", 0x100203ee, 1, new short[]{0x0016}),
    IBM_1025__CCSID_01025("IBM-1025 (CCSID 01025); Cyrillic Multilingual", 0x10020401, 1, new short[]{0x0015}),
    IBM_1026__CCSID_01026("IBM-1026 (CCSID 01026); Turkish Latin-5", 0x10020402, 1, new short[]{0x0019}),
    IBM_1027__CCSID_01027("IBM-1027 (CCSID 01027); Japanese Latin Host Ext SBCS", 0x10020403, 1, new short[]{0x0080}),
    IBM_1040__CCSID_01040("IBM-1040 (CCSID 01040); Korean PC Data Extended SBCS", 0x10020410, 1, new short[]{0x0001}),
    IBM_1041__CCSID_01041("IBM-1041 (CCSID 01041); Japanese PC Data Extended SBCS", 0x10020411, 1, new short[]{0x0080}),
    IBM_1043__CCSID_01043("IBM-1043 (CCSID 01043); T-Ch PC Data Extended SBCS", 0x10020413, 1, new short[]{0x0001}),
    IBM_1046__CCSID_01046("IBM-1046 (CCSID 01046); Arabic PC Data", 0x10020416, 1, new short[]{0x0016}),
    IBM_1047__CCSID_01047("IBM-1047 (CCSID 01047); Latin-1 Open System", 0x10020417, 1, new short[]{0x0011}),
    IBM_1088__CCSID_01088("IBM-1088 (CCSID 01088); IBM KS Code PC Data SBCS", 0x10020440, 1, new short[]{0x0001}),
    IBM_1097__CCSID_01097("IBM-1097 (CCSID 01097); Farsi", 0x10020449, 1, new short[]{0x0016}),
    IBM_1098__CCSID_01098("IBM-1098 (CCSID 01098); Farsi PC Data", 0x1002044a, 1, new short[]{0x0016}),
    IBM_1112__CCSID_01112("IBM-1112 (CCSID 01112); Baltic Multilingual", 0x10020458, 1, new short[]{0x001a}),
    IBM_1114__CCSID_01114("IBM-1114 (CCSID 01114); T-Ch PC Data SBCS (IBM BIG-5)", 0x1002045a, 1, new short[]{0x0001}),
    IBM_1115__CCSID_01115("IBM-1115 (CCSID 01115); S-Ch PC Data SBCS (IBM GB)", 0x1002045b, 1, new short[]{0x0001}),
    IBM_1122__CCSID_01122("IBM-1122 (CCSID 01122); Estonia", 0x10020462, 1, new short[]{0x001a}),
    IBM_1250__CCSID_01250("IBM-1250 (CCSID 01250); MS Windows Latin-2", 0x100204e2, 1, new short[]{0x0012}),
    IBM_1251__CCSID_01251("IBM-1251 (CCSID 01251); MS Windows Cyrillic", 0x100204e3, 1, new short[]{0x0015}),
    IBM_1252__CCSID_01252("IBM-1252 (CCSID 01252); MS Windows Latin-1", 0x100204e4, 1, new short[]{0x0011}),
    IBM_1253__CCSID_01253("IBM-1253 (CCSID 01253); MS Windows Greek", 0x100204e5, 1, new short[]{0x0017}),
    IBM_1254__CCSID_01254("IBM-1254 (CCSID 01254); MS Windows Turkey", 0x100204e6, 1, new short[]{0x0019}),
    IBM_1255__CCSID_01255("IBM-1255 (CCSID 01255); MS Windows Hebrew", 0x100204e7, 1, new short[]{0x0018}),
    IBM_1256__CCSID_01256("IBM-1256 (CCSID 01256); MS Windows Arabic", 0x100204e8, 1, new short[]{0x0016}),
    IBM_1257__CCSID_01257("IBM-1257 (CCSID 01257); MS Windows Baltic", 0x100204e9, 1, new short[]{0x001a}),
    IBM_1380__CCSID_01380("IBM-1380 (CCSID 01380); S-Ch PC Data DBCS incl 1880 UDC", 0x10020564, 2, new short[]{0x0300}),
    IBM_1381__CCSID_01381("IBM-1381 (CCSID 01381); S-Ch PC Data Mixed incl 1880 UDC", 0x10020565, 2, new short[]{0x0001, 0x0300}),
    IBM_1383__CCSID_01383("IBM-1383 (CCSID 01383); S-Ch EUC GB 2312-80 set (1382)", 0x10020567, 3, new short[]{0x0001, 0x0300}),
    IBM_300__CCSID_04396("IBM-300 (CCSID 04396); Japanese Host DBCS incl 1880 UDC", 0x1002112c, 2, new short[]{0x0081}),
    IBM_850__CCSID_04946("IBM-850 (CCSID 04946); Multilingual IBM PC Data-190", 0x10021352, 1, new short[]{0x0011}),
    IBM_852__CCSID_04948("IBM-852 (CCSID 04948); Latin-2 Personal Computer", 0x10021354, 1, new short[]{0x0012}),
    IBM_855__CCSID_04951("IBM-855 (CCSID 04951); Cyrillic Personal Computer", 0x10021357, 1, new short[]{0x0015}),
    IBM_856__CCSID_04952("IBM-856 (CCSID 04952); Hebrew PC Data", 0x10021358, 1, new short[]{0x0018}),
    IBM_857__CCSID_04953("IBM-857 (CCSID 04953); Turkish Latin-5 PC Data", 0x10021359, 1, new short[]{0x0019}),
    IBM_864__CCSID_04960("IBM-864 (CCSID 04960); Arabic PC Data (all shapes)", 0x10021360, 1, new short[]{0x0016}),
    IBM_868__CCSID_04964("IBM-868 (CCSID 04964); PC Data for Urdu", 0x10021364, 1, new short[]{0x0016}),
    IBM_869__CCSID_04965("IBM-869 (CCSID 04965); Greek PC Data", 0x10021365, 1, new short[]{0x0017}),
    IBM_5026__CCSID_05026("IBM-5026 (CCSID 05026); Japanese Katakana-Kanji Host Mixed", 0x100213a2, 2, new short[]{0x0080, 0x0081}),
    IBM_5031__CCSID_05031("IBM-5031 (CCSID 05031); S-Ch Host MBCS", 0x100213a7, 2, new short[]{0x0001, 0x0300}),
    IBM_1027_and__300__CCSID_05035("IBM-1027 and -300 (CCSID 05035); Japanese Latin-Kanji Host Mixed", 0x100213ab, 2, new short[]{0x0080, 0x0081}),
    IBM_5048__CCSID_05048("IBM-5048 (CCSID 05048); Japanese Kanji characters; superset of JIS X0208:1990 (and 1983)", 0x100213b8, 2, new short[]{0x0081}),
    IBM_5049__CCSID_05049("IBM-5049 (CCSID 05049); Japanese Kanji characters; superset of JIS X0212:1990", 0x100213b9, 2, new short[]{0x0082}),
    IBM_5067__CCSID_05067("IBM-5067 (CCSID 05067); Korean Hangul and Hanja; superset of KS C5601:1987", 0x100213cb, 2, new short[]{0x0100}),
    IBM_420__CCSID_08612("IBM-420 (CCSID 08612); Arabic (base shapes only)", 0x100221a4, 1, new short[]{0x0016}),
    IBM_833__CCSID_09025("IBM-833 (CCSID 09025); Korean Host SBCS", 0x10022341, 1, new short[]{0x0001}),
    IBM_834__CCSID_09026("IBM-834 (CCSID 09026); Korean Host DBCS incl 1880 UDC", 0x10022342, 2, new short[]{0x0100}),
    IBM_838__CCSID_09030("IBM-838 (CCSID 09030); Thai Host Extended SBCS", 0x10022346, 1, new short[]{0x0200}),
    IBM_864__CCSID_09056("IBM-864 (CCSID 09056); Arabic PC Data (unshaped)", 0x10022360, 1, new short[]{0x0016}),
    IBM_874__CCSID_09066("IBM-874 (CCSID 09066); Thai PC Display Extended SBCS", 0x1002236a, 1, new short[]{0x0200}),
    IBM_9125__CCSID_09125("IBM-9125 (CCSID 09125); Korean Host Mixed incl 1880 UDC", 0x100223a5, 2, new short[]{0x0001, 0x0100}),
    IBM_850__CCSID_25426("IBM-850 (CCSID 25426); Multilingual IBM PC Display-MLP", 0x10026352, 1, new short[]{0x0011}),
    IBM_856__CCSID_25432("IBM-856 (CCSID 25432); Hebrew PC Display (extensions)", 0x10026358, 1, new short[]{0x0018}),
    IBM_1042__CCSID_25618("IBM-1042 (CCSID 25618); S-Ch PC Display Ext SBCS", 0x10026412, 1, new short[]{0x0001}),
    IBM_037__CCSID_28709("IBM-037 (CCSID 28709); T-Ch Host Extended SBCS", 0x10027025, 1, new short[]{0x0001}),
    IBM_856__CCSID_33624("IBM-856 (CCSID 33624); Hebrew PC Display", 0x10028358, 1, new short[]{0x0018}),
    IBM33722__CCSID_33722("IBM33722 (CCSID 33722); Japanese EUC JISx201,208,212", 0x100283ba, 3, new short[]{0x0080, 0x0081, 0x0082}),
    HTCsjis("HTCsjis; Hitachi SJIS 90-1", 0x10030001, 2, new short[]{0x0001, 0x0080, 0x0081}),
    HTCujis("HTCujis; Hitachi eucJP 90-1", 0x10030002, 2, new short[]{0x0001, 0x0080, 0x0081}),
    Fujitsu_U90("Fujitsu U90; Japanese EUC", 0x10040001, 3, new short[]{0x0001, 0x0080, 0x0081}),
    Fujitsu_S90("Fujitsu S90; Japanese EUC", 0x10040002, 3, new short[]{0x0001, 0x0080, 0x0081}),
    Fujitsu_R90("Fujitsu R90; Fujitsu Shift JIS", 0x10040003, 2, new short[]{0x0001, 0x0080, 0x0081}),
    EBCDIC_ASCII__and_JEF("EBCDIC(ASCII) and JEF; Japanese encoding method for mainframe", 0x10040004, 3, new short[]{0x0001, 0x0081}),
    EBCDIC_Katakana__and_JEF("EBCDIC(Katakana) and JEF; Japanese encoding method for mainframe", 0x10040005, 3, new short[]{0x0001, 0x0080, 0x0081}),
    EBCDIC_Japanese_English__and_JEF("EBCDIC(Japanese English) and JEF; Japanese encoding method for mainframe", 0x10040006, 3, new short[]{0x0001, 0x0081}),
    ;

    public static final Set<CodeSetInfo> REGISTRY = unmodifiableSet(complementOf(EnumSet.of(NONE)));

    public final short max_bytes;

    final String description;
    public final int id;
    final CharMapInfo charMap;

    private final short[] charsets;

    CodeSetInfo(String desc, int reg_id, int max_width, short[] charsets, CharMapInfo charMap) {
        this.description = desc;
        this.id = reg_id;
        this.max_bytes = (short)max_width;
        this.charsets = charsets;
        this.charMap = charMap;
    }

    CodeSetInfo(String desc, int reg_id, int max_width, short[] charsets) {
        this(desc, reg_id, max_width, charsets, null);
    }

    public static int getRegistryIdForName(String name) {
        Objects.requireNonNull(name);

        //
        // Check if codeset name is listed in registry
        // Return first match so that shortcuts can be used
        //
        for (final CodeSetInfo value: REGISTRY) {
            if (value.description.contains(name)) return value.id;
        }

        //
        // Name not found
        //
        return NONE.id;
    }

    public static CodeSetInfo forRegistryId(int rgy_value) {
        //
        // Check if codeset id is listed in registry
        //
        for (final CodeSetInfo value: REGISTRY) {
            if (value.id == rgy_value) return value;
        }

        return null;
    }

    boolean isCompatibleWith(CodeSetInfo that) {
        if (that == null) return false;

        for (short thisCharset: this.charsets) {
            for (short thatCharset: that.charsets) {
                // In order to be compatible in OSF terms, two codesets
                // must have a common character set
                if (thisCharset == thatCharset) return true;
            }
        }

        return false;
    }

}
