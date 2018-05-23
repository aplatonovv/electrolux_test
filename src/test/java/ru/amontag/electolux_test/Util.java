package ru.amontag.electolux_test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Util {
    public  static <K,V> void checkMaps(Map<K, V> expected, Map<K, V> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach((k, v) -> assertEquals(v, actual.get(k)));
    }
}
