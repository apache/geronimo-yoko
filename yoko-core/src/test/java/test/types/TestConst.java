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

package test.types;

import java.math.*;
import org.omg.CORBA.*;

public class TestConst extends test.common.TestBase {
    public TestConst() {
        {
            final Measurement constEnum = Measurement.FURLONGS;
            TEST(constEnum == ConstEnum.value);

            final int constLong = -1234;
            TEST(constLong == ConstLong.value);

            final long constLongLong = constLong * constLong;
            TEST(constLongLong == ConstLongLong.value);

            final long constLongLongMin = -9223372036854775807L - 1L;
            TEST(constLongLongMin == ConstLongLongMin.value);

            final long constLongLongMax = 9223372036854775807L;
            TEST(constLongLongMax == ConstLongLongMax.value);

            final int constULong = (5432 + constLong) * 100 % 124;
            TEST(constULong == ConstULong.value);

            final long constULongLong = constULong + (constULong << 20);
            TEST(constULongLong == ConstULongLong.value);

            //
            // 18446744073709551615 is represented as -1
            //
            final long constULongLongMax = -1L;
            TEST(constULongLongMax == ConstULongLongMax.value);

            final double constDouble = 50.23 - 1532.718 * 0.029;
            TEST(constDouble == ConstDouble.value);

            final float constFloat = (float) (constDouble * 1.11);
            TEST(constFloat == ConstFloat.value);

            final short constShort = (short) (-23 % 10);
            TEST(constShort == ConstShort.value);

            final boolean constBoolean = true;
            TEST(constBoolean == ConstBoolean.value);

            final byte constOctet = (byte) 254;
            TEST(constOctet == ConstOctet.value);

            final BigDecimal constFixed = new BigDecimal("275.189").multiply(
                    new BigDecimal("1.163")).add(new BigDecimal("48.0093"));
            TEST(constFixed.equals(ConstFixed.value));

            final String constString = "This is ConstString";
            TEST(constString.equals(ConstString.value));

            final String constEmptyString = "";
            TEST(constEmptyString.equals(ConstEmptyString.value));

            final String constWString = "This is ConstWString";
            TEST(constWString.equals(ConstWString.value));

            final String constEmptyWString = "";
            TEST(constEmptyWString.equals(ConstEmptyWString.value));
        }

        {
            final int constLong = 12345678;
            TEST(constLong == test.types.TestConstModule.ConstLong.value);

            final double constDouble = ConstDouble.value / 2.0;
            TEST(constDouble == test.types.TestConstModule.ConstDouble.value);

            final boolean constBoolean = false;
            TEST(constBoolean == test.types.TestConstModule.ConstBoolean.value);

            final String constString = "This is ConstString in a module";
            TEST(constString
                    .equals(test.types.TestConstModule.ConstString.value));

            final String constWString = "This is ConstWString in a module";
            TEST(constWString
                    .equals(test.types.TestConstModule.ConstWString.value));

            final Measurement constEnum = Measurement.METERS;
            TEST(constEnum == test.types.TestConstModule.ConstEnum.value);
        }

        {
            final int constLong = (0xf | 0xf000) & 0xfffe;
            TEST(constLong == TestConstInterface.ConstLong);

            final int constULong = (int) 0xFFFFFFFF;
            TEST(constULong == TestConstInterface.ConstULong);

            final char constChar0 = (char) 0;
            TEST(constChar0 == TestConstInterface.ConstChar0);

            final char constChar1 = 'c';
            TEST(constChar1 == TestConstInterface.ConstChar1);

            final char constChar2 = '\n';
            TEST(constChar2 == TestConstInterface.ConstChar2);

            final char constChar3 = '\377';
            TEST(constChar3 == TestConstInterface.ConstChar3);

            final char constChar4 = '\210';
            TEST(constChar4 == TestConstInterface.ConstChar4);

            final char constChar5 = '\'';
            TEST(constChar5 == TestConstInterface.ConstChar5);

            final char constWChar = 'Z';
            TEST(constWChar == TestConstInterface.ConstWChar);

            final String constString = "\n\t\013\b\r\f\007\\?\'\"\377\377\007";
            TEST(constString.equals(TestConstInterface.ConstString));

            final String constWString = "\n\t\013\b\r\f\007\\?\'\"\377\377\007";
            TEST(constWString.equals(TestConstInterface.ConstWString));
        }
    }

    public static void main(String args[]) {
        int status = 0;

        try {
            //
            // Run tests
            //
            System.out.print("Testing constants... ");
            System.out.flush();
            new TestConst();
            System.out.println("Done!");
        } catch (org.omg.CORBA.SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        System.exit(status);
    }
}
