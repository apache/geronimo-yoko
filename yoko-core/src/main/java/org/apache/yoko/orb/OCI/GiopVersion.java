/*
 * Copyright 2015 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OCI;

public enum GiopVersion {
    GIOP1_0(1,0), GIOP1_1(1,1), GIOP1_2(1,2);

    public final byte major;
    public final byte minor;

    private GiopVersion(int major, int minor) {
        this.major = (byte)(major & 0xff);
        this.minor = (byte)(minor & 0xff);
    }

    public static GiopVersion get(byte major, byte minor) {
        if (major < 1) return GIOP1_0;
        if (major > 1) return GIOP1_2;
        switch (minor) {
            case 0: return GIOP1_0;
            case 1: return GIOP1_1;
            default: return GIOP1_2;
        }
    }
}
