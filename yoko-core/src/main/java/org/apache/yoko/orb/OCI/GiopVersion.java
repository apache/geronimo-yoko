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
