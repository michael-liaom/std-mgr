package com.weijie.stdmgr;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassDataTest {

    @Test
    public void toDomain() {
        assertEquals("class.name", ClassData.toDomain("name"));
    }

    @Test
    public void toDomainAs() {
        assertEquals("class.name AS class_name", ClassData.toDomainAs("name"));
    }

    @Test
    public void getAsCol() {
        assertEquals("class_name", ClassData.getAsCol("name"));
    }
}