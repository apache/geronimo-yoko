package org.apache.yoko.orb.yasf;

import java.util.BitSet;

import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;

public enum Yasf {
    ;

    // TODO - Get ids from OMG assigned for these values
    public static final int TAG_YOKO_AUXILLIARY_STREAM_FORMAT = 0xeeeeeeee;
    public static final int YOKO_AUXIllIARY_STREAM_FORMAT_SC = 0xeeeeeeee;

    public final int itemIndex;

    private Yasf(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public boolean isSet(BitSet items) {
        return items.get(itemIndex);
    }

    public static Builder build() {
        return new BuilderImpl();
    }

    public static BitSet readData(byte[] data) {
        return BitSet.valueOf(data);
    }

    public interface Builder {
        public Builder set(Yasf... items);
        public Builder clear(Yasf... items);
        public TaggedComponent tc();
        public ServiceContext sc();
    }

    private static final class BuilderImpl implements Builder {
        private final BitSet bits = new BitSet();

        BuilderImpl() {}

        @Override
        public Builder set(Yasf... items) {
            for (Yasf item: items) {
                bits.set(item.itemIndex);
            }
            return this;
        }

        @Override
        public Builder clear(Yasf... items) {
            for (Yasf item: items) {
                bits.clear(item.itemIndex);
            }
            return this;
        }

        @Override
        public TaggedComponent tc() {
            return new TaggedComponent(TAG_YOKO_AUXILLIARY_STREAM_FORMAT, bits.toByteArray());
        }

        @Override
        public ServiceContext sc() {
            return new ServiceContext(YOKO_AUXIllIARY_STREAM_FORMAT_SC, bits.toByteArray());
        }
    }
}
