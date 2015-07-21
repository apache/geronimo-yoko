package test.fvd;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static test.fvd.Sets.difference;
import static test.fvd.Sets.format;
import static test.fvd.Sets.intersection;

import java.io.ObjectStreamField;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;

public enum Marshalling {
    DEFAULT_VERSION(SkipInDefaultVersion.class), 
    VERSION1(SkipInVersion1.class), 
    VERSION2(SkipInVersion2.class);

    private static final AtomicReference<Marshalling> IN_USE = new AtomicReference<>();
    private static final Set<Class<? extends Annotation>> VALID_ANNOTATIONS;

    static {
        HashSet<Class<? extends Annotation>> set = new HashSet<>();
        for (Marshalling mc : Marshalling.values())
            set.add(mc.skipAnnotationType);
        VALID_ANNOTATIONS = Collections.unmodifiableSet(set);
    }

    private final Class<? extends Annotation> skipAnnotationType;

    private Marshalling(Class<? extends Annotation> annotationType) {
        this.skipAnnotationType = annotationType;
    }

    public Marshalling select() {
        if (IN_USE.compareAndSet(null, this)) return this;
        Assert.fail("Attempt to select MarshallingConfiguration " + this + " when " + IN_USE.get() + " was already selected");
        throw new Error("Unreachable code");
    }

    static Marshalling getCurrent() {
        Marshalling current = IN_USE.get();
        Assert.assertNotNull("A " + Marshalling.class.getSimpleName() + " setting should have been selected.", current);
        return current;
    }

    public static ObjectStreamField[] computeSerialPersistentFields(Class<?> type) {
        validate(type);

        // check what type of marshalling is configured
        Marshalling currentConfig = getCurrent();
        System.out.println(currentConfig + " marshalling selected");
        if (currentConfig == DEFAULT_VERSION) {
            System.out.println("Using null for serialPersistentFields for type: " + type);
            // we can coerce default serialization semantics by making the serialPersistentFields null
            return null;
        }

        System.out.println("Constructing serialPersistentFields for type: " + type);

        // build an ordered set of object stream fields
        SortedSet<ObjectStreamField> result = new TreeSet<>(SerializationOrdering.INSTANCE);
        for(Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) ||
                    getAllAnnoTypes(f).contains(currentConfig.skipAnnotationType)) {
                System.out.println("Excluding field from serialPersistentFields: " + f);
            } else {
                System.out.println("Adding field to serialPersistentFields: "+ f);
                result.add(new ObjectStreamField(f.getName(), f.getType()));
            }
        }
        return result.toArray(new ObjectStreamField[result.size()]);
    }

    private static Set<Class<? extends Annotation>> getAllAnnoTypes(Field f) {
        HashSet<Class<? extends Annotation>> result = new HashSet<>();
        for (Annotation a : f.getAnnotations()) result.add(a.annotationType());
        System.out.println("anno types for field " + f + " = " + result);
        return result;
    }

    private static Set<Class<? extends Annotation>> getSkipAnnoTypes(Field f) {
        Set<Class<? extends Annotation>> result = getAllAnnoTypes(f);
        result.retainAll(VALID_ANNOTATIONS);
        return result;
    }

    private static void validate(Class<?> type) {
        Map<?, Set<Field>> fieldsByAnnoType = createAnnoTypeMap();

        checkForSerialPersistentFieldsField(type);

        Set<Field> nonStaticFields = getValidatedFields(type);

        for (Field f : nonStaticFields) {
            // find the relevant annotations for this field
            Set<?> annoTypes = getSkipAnnoTypes(f);
            for (Object annoType : annoTypes)
                fieldsByAnnoType.get(annoType).add(f);
            boolean isTransient = Modifier.isTransient(f.getModifiers());
            System.out.printf("Examining field %s: isTransient=%b anno types=%s%n", f, isTransient, annoTypes);
            // validate all and only default persisted fields have @Default
            Assert.assertEquals("@Marshalling.SkipInDefault should be used iff the field is transient", isTransient, annoTypes.contains(SkipInDefaultVersion.class));
            // validate all and only non-static fields should have at least one of the VALID_ANNOTATIONS
            Assert.assertNotEquals("No field should have all the Skip annotations", VALID_ANNOTATIONS, annoTypes);
        }
        checkForCommonFields(nonStaticFields, fieldsByAnnoType);
        checkForSkippedFields(fieldsByAnnoType);
    }

    private static Set<Field> getValidatedFields(Class<?> type) {
        Set<Field> result = new HashSet<>();
        for (Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                Assert.assertTrue("Static fields should not have any Skip... annotations", getSkipAnnoTypes(f).isEmpty());
                continue;
            }
            result.add(f);
        }
        // check there are some persistent fields
        for (Field f : result) {
            if (!!!Modifier.isTransient(f.getModifiers())) 
                return result; // found at least one persistent field!
        }
        Assert.fail("The class " + type + " should declare some non-transient, non-static fields");
        throw new Error("Unreachable code!");
    }

    private static void checkForSerialPersistentFieldsField(Class<?> type) {
        // first and foremost - the class must declare serialPersistentFields 
        try {
            Field spff = type.getDeclaredField("serialPersistentFields");
            // field must be private (probably)
            Assert.assertTrue(spff + " should be private", Modifier.isPrivate(spff.getModifiers()));
            // field must be static
            Assert.assertTrue(spff + " should be static", Modifier.isStatic(spff.getModifiers()));
            // field must be final
            Assert.assertTrue(spff + " should be final", Modifier.isFinal(spff.getModifiers()));
            // field must be of type ObjectStreamField[]
            Assert.assertEquals(spff + " should be of type ObjectStreamField[]", ObjectStreamField[].class, spff.getType());
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            Assert.fail("Class " + type + " must declare serialPersistentFields");
        }
    }

    private static void checkForCommonFields(Set<Field> allFields, Map<?, Set<Field>> fieldsByAnnoType) {
        // compare each combination of two configs
        Queue<Marshalling> configs = new LinkedList<>(Arrays.asList(Marshalling.values()));
        do {
            Marshalling leftConfig = configs.remove();
            Set<Field> leftFields = difference(allFields, fieldsByAnnoType.get(leftConfig.skipAnnotationType));
            for (Marshalling rightConfig: configs) {
                Set<Field> rightFields = difference(allFields, fieldsByAnnoType.get(rightConfig.skipAnnotationType));
                Set<Field> commonFields = intersection(leftFields, rightFields);
                System.out.printf("When communicating between %s and %s, the common fields are %s%n", leftConfig, rightConfig, format(commonFields));
                Assert.assertNotEquals("There should be some common fields between versions " + leftConfig + " and " + rightConfig , Collections.emptySet(), commonFields);
            }
        } while(!!!configs.isEmpty());
    }

    private static void checkForSkippedFields(Map<?, Set<Field>> fieldsByAnnoType) {
        // compare each permutation of two configs
        for (Marshalling sender : Marshalling.values()) {
            Set<Field> unsentFields = fieldsByAnnoType.get(sender.skipAnnotationType);
            for (Marshalling receiver : Marshalling.values()) {
                if (sender == receiver)
                    continue;
                Set<Field> skippedFields = difference(fieldsByAnnoType.get(receiver.skipAnnotationType), unsentFields);
                System.out.printf("When %s transmits to %s, the skipped fields are %s%n", sender, receiver, format(skippedFields));
                Assert.assertNotEquals("There should be some skipped fields when " + sender + " sends to " + receiver, 
                        Collections.emptySet(), skippedFields);
            }
        }
    }

    private static Map<Class<? extends Annotation>, Set<Field>> createAnnoTypeMap() {
        Map<Class<? extends Annotation>, Set<Field>> fieldsByAnnotationType = new HashMap<>();
        // fill the map with empty sets for each annotation we are considering
        for (Class<? extends Annotation> annotationType : VALID_ANNOTATIONS)
            fieldsByAnnotationType.put(annotationType, new HashSet<Field>());
        return fieldsByAnnotationType;
    }

    public Set<Field> getSkippedFields(Object expected) {
        Set<Field> result = new HashSet<>();
        for (Field f: getValidatedFields(expected.getClass()))
            if (getAllAnnoTypes(f).contains(skipAnnotationType))
                result.add(f);
        return result;
    }

    @Retention(RUNTIME)
    @Target(FIELD)
    @Documented
    public @interface SkipInDefaultVersion {}
    @Retention(RUNTIME)
    @Target(FIELD)
    @Documented
    public @interface SkipInVersion1 {}
    @Retention(RUNTIME)
    @Target(FIELD)
    @Documented
    public @interface SkipInVersion2 {}

    /**
     * The serialization spec dictates that primitive fields are serialized before Objects, 
     * and each group is sorted by the natural ordering of their names @see {@link String#compareTo(String)}
     */
    private static enum SerializationOrdering implements Comparator<ObjectStreamField> {
        INSTANCE;
        @Override
        public int compare(ObjectStreamField f1, ObjectStreamField f2) {
            boolean f1IsPrim = f1.getType().isPrimitive();
            boolean f2IsPrim = f2.getType().isPrimitive();
            if (f1IsPrim == f2IsPrim) 
                return f1.getName().compareTo(f2.getName());
            return f1IsPrim ? -1 : 1;
        }
    }
}
