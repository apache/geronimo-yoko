package org.apache.yoko.util.yasf;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum Yasf {
    ENUM_FIXED(0);

    // TODO - Get ids from OMG assigned for these values
    public static final int TAG_YOKO_AUXILLIARY_STREAM_FORMAT = 0xeeeeeeee;
    public static final int YOKO_AUXIllIARY_STREAM_FORMAT_SC = 0xeeeeeeee;

    public final int itemIndex;

    private Yasf(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public static Set<Yasf> supported() {
        return Collections.unmodifiableSet(EnumSet.of(ENUM_FIXED));
    }

    public static Set<Yasf> toSet(byte[] data) {
        if (data == null) return null;
        final EnumSet<Yasf> set = EnumSet.noneOf(Yasf.class);
        BitSet items = BitSet.valueOf(data);
        for (Yasf yasf : values()) {
            if (items.get(yasf.itemIndex)) set.add(yasf);
        }
        return Collections.unmodifiableSet(set);
    }

    public static byte[] toData(Set<Yasf> yasfSet) {
        if (null == yasfSet) return null;
        final BitSet bits = new BitSet();
        for (Yasf yasf : yasfSet) {
            bits.set(yasf.itemIndex);
        }
        return bits.toByteArray();
    }
}
