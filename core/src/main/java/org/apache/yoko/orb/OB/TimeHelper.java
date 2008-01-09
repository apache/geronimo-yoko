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

import org.omg.CORBA.*;
import org.omg.TimeBase.*;

//
// This is a simpler set of functions to work with TimeBase::UtcT, and
// TimeBase::TimeT values.
//
// The Time Service uses units of 100 nanoseconds and a base time
// of 15 October 1582 00:00:00.
//
public class TimeHelper {
    //
    // 100 nanoseconds = 10^-7 seconds and 1 millisecond = 10^-3 seconds,
    // hence there are 10^4 (10,000) milliseconds per 100 nanoseconds.
    //
    private final static long HNanosPerMilli = 10000L;

    //
    // 100 ns units from 15 October 1582 00:00:00 to 1 January 1970 00:00:00.
    //
    // Time difference in 100 ns units between DCE and POSIX time base.
    // 141427 days with 86400 seconds each.
    //
    private final static long DCEToPosix = 141427L * 86400L * HNanosPerMilli * 1000;

    //
    // Max TimeT value
    //
    public final static long MaxTimeT = 0xffffffffffffffffL;

    //
    // Max InaccuracyT value
    //
    public final static long MaxInaccuracyT = 0xffffffffffffL;

    public static UtcT utcNow(long inaccuracy) {
        if (Unsigned.gt(inaccuracy, MaxInaccuracyT))
            throw new BAD_PARAM();

        long time = Unsigned.add(Unsigned.multiply(System.currentTimeMillis(),
                HNanosPerMilli), DCEToPosix);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int offset = cal.get(java.util.Calendar.ZONE_OFFSET)
                + cal.get(java.util.Calendar.DST_OFFSET);

        return toUtcT(time, inaccuracy, (short) (offset / 60000));
    }

    public static UtcT utcMin() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int offset = cal.get(java.util.Calendar.ZONE_OFFSET)
                + cal.get(java.util.Calendar.DST_OFFSET);

        return toUtcT(0, 0, (short) (offset / 60000));
    }

    public static UtcT utcMax() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int offset = cal.get(java.util.Calendar.ZONE_OFFSET)
                + cal.get(java.util.Calendar.DST_OFFSET);

        return toUtcT(MaxTimeT, 0, (short) (offset / 60000));
    }

    public static long toJavaMillis(UtcT utc) {
        return toJavaMillis(utc.time);
    }

    public static long toJavaMillis(long time) {
        if (Unsigned.lt(time, DCEToPosix))
            return 0;

        return Unsigned.divide(Unsigned.subtract(time, DCEToPosix),
                HNanosPerMilli);
    }

    public static IntervalT toIntervalT(long time, long inaccuracy) {
        IntervalT inter = new IntervalT();

        if (Unsigned.lt(time, inaccuracy))
            inter.lower_bound = 0;
        else
            inter.lower_bound = Unsigned.subtract(time, inaccuracy);

        if (Unsigned.lt(Unsigned.subtract(MaxTimeT, time), inaccuracy))
            inter.upper_bound = MaxTimeT;
        else
            inter.upper_bound = Unsigned.add(time, inaccuracy);

        return inter;
    }

    public static UtcT toUtcT(long time, long inaccuracy, short tdf) {
        UtcT utc = new UtcT();

        utc.time = time;
        utc.tdf = tdf;
        utc.inacclo = (int) (inaccuracy & 0xffffffffL);
        utc.inacchi = (short) ((inaccuracy >> 32L) & 0xffffL);

        return utc;
    }

    public static UtcT toUtcT(long time, long inaccuracy) {
        return toUtcT(time, inaccuracy, (short) 0);
    }

    public static UtcT toUtcT(IntervalT inter) {
        //
        // Note that "time = (upper_ + lower_) / 2" may cause overflow.
        //
        long inaccuracy = Unsigned.divide(Unsigned.subtract(inter.upper_bound,
                inter.lower_bound), 2);
        long time = Unsigned.add(inter.lower_bound, inaccuracy);

        return toUtcT(time, inaccuracy);
    }

    public static String toString(UtcT time) {
        if (Unsigned.lt(time.time, DCEToPosix))
            return "Time less than 1 January 1970 00:00:00";

        //
        // Get local time
        //
        long milliTime = Unsigned.add(toJavaMillis(time), time.tdf * 60000);

        //
        // Convert to string
        //
        java.text.SimpleDateFormat date = new java.text.SimpleDateFormat(
                "MM/dd/yy HH:mm:ss:SSS");
        date.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return date.format(new java.util.Date(milliTime));
    }

    public static String toTimeString(UtcT time) {
        if (Unsigned.lt(time.time, DCEToPosix))
            return "Time less than 1 January 1970 00:00:00";

        //
        // Get local time
        //
        long milliTime = Unsigned.add(toJavaMillis(time), time.tdf * 60000);

        //
        // Convert to string
        //
        java.text.SimpleDateFormat date = new java.text.SimpleDateFormat(
                "HH:mm:ss:SSS");
        date.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return date.format(new java.util.Date(milliTime));
    }

    public static String toString(long time) {
        long milliTime = Unsigned.divide(time, 10000L);

        long sec = Unsigned.divide(milliTime, 1000);
        long msec = Unsigned.mod(milliTime, 1000);

        String ret = Long.toString(sec) + ":";
        if (msec < 10)
            ret = ret + "00";
        else if (msec < 100)
            ret = ret + "0";
        ret = ret + Long.toString(msec);

        return ret;
    }

    public static boolean lessThan(UtcT a, UtcT b) {
        return Unsigned.lt(a.time, b.time);
    }

    public static boolean lessThanEqual(UtcT a, UtcT b) {
        return Unsigned.lteq(a.time, b.time);
    }

    public static boolean greaterThan(UtcT a, UtcT b) {
        return Unsigned.gt(a.time, b.time);
    }

    public static boolean greaterThanEqual(UtcT a, UtcT b) {
        return Unsigned.gteq(a.time, b.time);
    }

    public static boolean equal(UtcT a, UtcT b) {
        return a.time == b.time;
    }

    public static boolean notEqual(UtcT a, UtcT b) {
        return a.time != b.time;
    }

    public static UtcT add(UtcT a, long t) {
        UtcT b = new UtcT(a.time, a.inacclo, a.inacchi, a.tdf);

        if (Unsigned.lt(Unsigned.subtract(MaxTimeT, b.time), t))
            b.time = MaxTimeT;
        else
            b.time = Unsigned.add(b.time, t);

        return b;
    }

    public static UtcT add(long t, UtcT a) {
        return add(a, t);
    }

    public static UtcT subtract(UtcT a, long t) {
        UtcT b = new UtcT(a.time, a.inacclo, a.inacchi, a.tdf);

        if (Unsigned.lt(b.time, t))
            b.time = 0;
        else
            b.time = Unsigned.subtract(b.time, t);

        return b;
    }
}
