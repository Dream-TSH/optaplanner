package org.optaplanner.core.api.domain.value.buildin.bigdecimal;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class BigDecimalValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(10L, new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).getSize());
        assertEquals(200L, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).getSize());
        assertEquals(4007L, new BigDecimalValueRange(new BigDecimal("-15.00"), new BigDecimal("25.07")).getSize());
        assertEquals(0L, new BigDecimalValueRange(new BigDecimal("7.0"), new BigDecimal("7.0")).getSize());
        // IncrementUnit
        assertEquals(5L, new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).getSize());
        assertEquals(4L, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).getSize());
    }

    @Test
    public void get() {
        assertEquals(new BigDecimal("3"), new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).get(3L));
        assertEquals(new BigDecimal("100.3"), new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).get(3L));
        assertEquals(new BigDecimal("-4"), new BigDecimalValueRange(new BigDecimal("-5"), new BigDecimal("25")).get(1L));
        assertEquals(new BigDecimal("-4.94"), new BigDecimalValueRange(new BigDecimal("-5.00"), new BigDecimal("25.00")).get(6L));
        // IncrementUnit
        assertEquals(new BigDecimal("6.0"), new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).get(3L));
        assertEquals(new BigDecimal("115.3"), new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).get(3L));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createOriginalIterator(), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createOriginalIterator(), new BigDecimal("100.0"), new BigDecimal("100.1"), new BigDecimal("100.2"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createOriginalIterator(), new BigDecimal("-4.00"), new BigDecimal("-3.99"), new BigDecimal("-3.98"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .createOriginalIterator(), new BigDecimal("0.0"), new BigDecimal("2.0"), new BigDecimal("4.0"),
                new BigDecimal("6.0"), new BigDecimal("8.0"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .createOriginalIterator(), new BigDecimal("100.0"), new BigDecimal("105.1"), new BigDecimal("110.2"),
                new BigDecimal("115.3"));
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createRandomIterator(workingRandom), new BigDecimal("3"), new BigDecimal("0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createRandomIterator(workingRandom), new BigDecimal("100.3"), new BigDecimal("100.0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createRandomIterator(workingRandom), new BigDecimal("-3.97"), new BigDecimal("-4.00"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createRandomIterator(workingRandom));
        // IncrementUnit
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .createRandomIterator(workingRandom), new BigDecimal("6.0"), new BigDecimal("0.0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .createRandomIterator(workingRandom), new BigDecimal("115.3"), new BigDecimal("100.0"));
    }

}
