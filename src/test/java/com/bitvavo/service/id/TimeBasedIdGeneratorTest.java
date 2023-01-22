package com.bitvavo.service.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeBasedIdGeneratorTest {
    TimeBasedIdGenerator TOT = new TimeBasedIdGenerator();

    @Test
    void shouldBeMonotonous() {
        long id = TOT.getNext();

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 30_000L) {
            long next = TOT.getNext();

            Assertions.assertTrue(id < next);
            id = next;
        }
    }
}