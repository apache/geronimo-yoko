package org.apache.yoko.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.yoko.util.CollectionExtras.allOf;
import static org.apache.yoko.util.CollectionExtras.filterByType;
import static org.apache.yoko.util.CollectionExtras.removeInReverse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static testify.matchers.Matchers.consistsOf;
import static testify.matchers.Matchers.isEmpty;

@SuppressWarnings("unchecked")
public class CollectionExtrasTest {
    @Test
    public void testRemoveInReverseEmptyList() {
        List<Object> emptyList = emptyList();
        assertThat(removeInReverse(emptyList), isEmpty());
    }

    @Test
    public void testRemoveInReverseNonEmptyList() {
        List<Integer> ints = new ArrayList<>(asList(1, 2, 3, 4, 5));
        final Iterable<Integer> integers = removeInReverse(ints);
        assertThat(integers, consistsOf(5, 4, 3, 2, 1));
        assertThat(ints, is(Collections.<Integer>emptyList()));
    }

    @Test
    public void testAllOfNothing() {
        assertThat(allOf(), isEmpty());
    }

    @Test
    public void testAllOfSomeEmptyLists() {
        final Iterable<Object> iterable = allOf(emptyList(), emptyList(), emptyList());
        assertThat(iterable, isEmpty());
    }

    @Test
    public void testAllOfSeveralLists() {
        final List<Integer> emptyList = emptyList();
        final Iterable<Integer> iterable = allOf(asList(1, 2, 3), emptyList, asList(4, 5));
        assertThat(iterable, consistsOf(1, 2, 3, 4, 5));
    }

    @Test
    public void testAllOfDisparateLists() {
        final List<Integer> ints = asList(1, 2);
        final List<Double> doubles = asList(3D, 4D);
        final List<Float> floats = Collections.singletonList(5F);
        final List<Short> shorts = emptyList();
        assertThat(allOf(ints, doubles, floats, shorts), consistsOf(1, 2, 3D, 4D, 5F));
    }

    @Test
    public void testFilterByTypeEmptyList() {
        assertThat(filterByType(emptyList(), Integer.class), isEmpty());
    }

    @Test
    public void testFilterByTypeListOfNumbers() {
        final List<Number> numbers = new ArrayList<>(asList(1, (short)2, 3D, 4F, 5L, 6, (short)7, 8D, 9F, 10L));
        assertThat(filterByType(numbers, Integer.class), consistsOf(1, 6));
        assertThat(filterByType(numbers, Short.class), consistsOf((short)2, (short)7));
        assertThat(filterByType(numbers, Double.class), consistsOf(3D, 8D));
        assertThat(filterByType(numbers, Float.class), consistsOf(4F, 9F));
        assertThat(filterByType(numbers, Long.class), consistsOf(5L, 10L));
        assertThat(filterByType(numbers, Byte.class), isEmpty());
    }
}
