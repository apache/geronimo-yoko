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

import java.math.BigInteger;

//
// Unsigned operations for the signed long type.  Underflow and overflow
// may occur.
//
public class Unsigned {
    // -----------------------------------------------------------------
    // Internal implementation
    // -----------------------------------------------------------------

    private static final long MIN_LONG = 0x8000000000000000L;

    private static BigInteger longToBigInteger(long l) {
        BigInteger b = BigInteger.valueOf(l & ~MIN_LONG);
        if (l < 0) {
            b = b.add(BigInteger.valueOf(0x7fffffffffffffffL));
            b = b.add(BigInteger.valueOf(0x0000000000000001L));
        }
        return b;
    }

    private static long bigIntegerToLong(BigInteger b) {
        long l = b.and(BigInteger.valueOf(0x7fffffffffffffffL)).longValue();
        if (b.compareTo(BigInteger.valueOf(0x7fffffffffffffffL)) > 0) {
            l |= MIN_LONG;
        }
        return l;
    }

    // -----------------------------------------------------------------
    // Public methods
    // -----------------------------------------------------------------

    public static boolean lt(long op1, long op2) {
        if ((op1 >= 0 && op2 >= 0) || (op1 < 0 && op2 < 0))
            return op1 < op2;
        else if (op1 >= 0)
            return true;
        else
            return false;
    }

    public static boolean lteq(long op1, long op2) {
        return op1 == op2 || lt(op1, op2);
    }

    public static boolean gt(long op1, long op2) {
        if ((op1 >= 0 && op2 >= 0) || (op1 < 0 && op2 < 0))
            return op1 > op2;
        else if (op1 >= 0)
            return false;
        else
            return true;
    }

    public static boolean gteq(long op1, long op2) {
        return op1 == op2 || gt(op1, op2);
    }

    public static boolean eq(long op1, long op2) {
        return op1 == op2;
    }

    public static boolean neq(long op1, long op2) {
        return op1 != op2;
    }

    public static long add(long op1, long op2) {
        BigInteger bop1 = longToBigInteger(op1);
        BigInteger bop2 = longToBigInteger(op2);
        long res = bop1.add(bop2).longValue();

        return res;
    }

    public static long subtract(long op1, long op2) {
        BigInteger bop1 = longToBigInteger(op1);
        BigInteger bop2 = longToBigInteger(op2);
        long res = bop1.subtract(bop2).longValue();

        return res;
    }

    public static long multiply(long op1, long op2) {
        BigInteger bop1 = longToBigInteger(op1);
        BigInteger bop2 = longToBigInteger(op2);
        long res = bop1.multiply(bop2).longValue();

        return res;
    }

    public static long divide(long op1, long op2) {
        BigInteger bop1 = longToBigInteger(op1);
        BigInteger bop2 = longToBigInteger(op2);
        long res = bop1.divide(bop2).longValue();

        return res;
    }

    public static long mod(long op1, long op2) {
        BigInteger bop1 = longToBigInteger(op1);
        BigInteger bop2 = longToBigInteger(op2);
        long res = bop1.mod(bop2).longValue();

        return res;
    }
}
