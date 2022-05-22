package com.eternalcode.formatter.legacy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyTest {

    @Test
    void test() {
        String text = Legacy.decolor("§c SIEMA § test§a!");

        assertEquals("&c SIEMA § test&a!", text);
    }

}
