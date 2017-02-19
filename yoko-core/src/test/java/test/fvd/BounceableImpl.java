package test.fvd;

import static test.fvd.Sets.format;
import static test.fvd.Sets.union;

import java.io.ObjectStreamField;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;

class NonSerializableSuper {
    final boolean isLocal;
    NonSerializableSuper() { // invoked by serialization
        isLocal = false;
    }
    NonSerializableSuper(String name) {
        isLocal = true;
    }
}

public class BounceableImpl extends NonSerializableSuper implements Bounceable {
    private static final long serialVersionUID = 1L;

    // this is the magic that makes this class marshal differently according to FieldMarshal Config
    private static final ObjectStreamField[] serialPersistentFields = Marshalling.computeSerialPersistentFields(BounceableImpl.class);

    private final Marshalling senderConfig = Marshalling.getCurrent();
    
    public BounceableImpl() {
        super(null); // avoid using the no-args parent constructor
    }

    private String a = "AAA";

    @Marshalling.SkipInDefaultVersion
    private transient String b = "BBB";

    @Marshalling.SkipInVersion1
    private String c = "CCC";

    @Marshalling.SkipInVersion2
    private String d = "DDD";

    private String e = "EEE";

    public static void main(String[] args) {
        System.out.println("success!");
    }

    @Override
    public Bounceable validateAndReplace() {
        System.out.println("Received from config " + senderConfig + " into " + Marshalling.getCurrent());
        Assert.assertEquals(createModelInstance(), this);
        return new BounceableImpl();
    }

    private BounceableImpl createModelInstance() throws Error {
        BounceableImpl expected = new BounceableImpl();
        if (isLocal) 
            return expected;
        try {
            Set<Field> unread = Marshalling.getCurrent().getSkippedFields(expected);
            Set<Field> unsent = senderConfig.getSkippedFields(expected);
            System.out.printf("Creating model instance:%nunsent fields: %s%nunread fields: %s%n", format(unsent), format(unread));
            for (Field f : union(unread, unsent))
                f.set(expected, null);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        return expected;
    }

    @Override
    public String toString() {
        return String.format("[%s|%s|%s|%s|%s]", a, b, c, d, e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d, e);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BounceableImpl))
            return false;
        BounceableImpl that = (BounceableImpl) obj;
        return Objects.equals(this.a, that.a) 
                && Objects.equals(this.b, that.b) 
                && Objects.equals(this.c, that.c) 
                && Objects.equals(this.d, that.d)
                && Objects.equals(this.e, that.e);
    }
}
