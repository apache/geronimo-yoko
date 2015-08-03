package test.fvd;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public enum Sets {
    ;
    static <T> Set<T> union(Set<T> left, Set<T> right) {
        HashSet<T> result = new HashSet<>(left);
        result.addAll(right);
        return result;
    }

    static <T> Set<T> intersection(Set<T> left, Set<T> right) {
        HashSet<T> result = new HashSet<>(left);
        result.retainAll(right);
        return result;
    }

    static <T> Set<T> difference(Set<T> minuend, Set<T> subtrahend) {
        HashSet<T> result = new HashSet<>(minuend);
        result.removeAll(subtrahend);
        return result;
    }

    static String format(Set<Field> fields) {
        String result = "{";
        for (Field field : fields)
            result += (result.length() == 1 ? "" : ",") + field.getName();
        return result + "}";
    }
}
