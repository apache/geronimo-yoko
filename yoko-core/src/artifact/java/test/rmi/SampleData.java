package test.rmi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by nrichard on 11/03/16.
 */
public class SampleData implements Serializable {
    private static class Data2 implements Serializable {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data2)) return false;
            return true;
        }
    }

    private static class MrBoom implements Serializable {
        private void writeObject(ObjectOutputStream oos) throws IOException {
            throw new IOException("*BOOM*!");
        }
    }

    public static enum DataEnum {
        E1(new Data2()), E2(new MrBoom());
        public final Serializable s;

        private DataEnum(Serializable s) {
            this.s = s;
        }
    }

    public final Serializable f1 = DataEnum.E1;
    public final Serializable f2 = DataEnum.E2;
    public final Serializable f3 = DataEnum.E1.s;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SampleData)) return false;
        SampleData sd = (SampleData)o;
        if (f1 != sd.f1) return false;
        if (f2 != sd.f2) return false;
        return f3.equals(sd.f3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f1, f2);
    }
}
