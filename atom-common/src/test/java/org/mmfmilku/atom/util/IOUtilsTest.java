package org.mmfmilku.atom.util;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IOUtilsTest {

    @Test
    public void testCaseOne() {
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", false);
        map.put("list", Collections.singletonList("aa"));
        byte[] serialize = IOUtils.serialize(map);
        Object deserialize = IOUtils.deserialize(serialize);
        System.out.println(deserialize);
        assertTrue(deserialize instanceof Map);
        Map<String, Object> deserializeMap = (Map<String, Object>) deserialize;
        assertEquals(3, deserializeMap.size());
        assertEquals("aa", ((List) deserializeMap.get("list")).get(0));
    }

    @Test
    public void testCaseTwo() {
        byte[] serialize = IOUtils.serialize(null);
        System.out.println(serialize);
    }

}