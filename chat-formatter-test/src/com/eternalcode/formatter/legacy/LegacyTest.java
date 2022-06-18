package com.eternalcode.formatter.legacy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyTest {

    @Test
    void decolor() {
        String text = Legacy.deColor("§c SIEMA § test§a!");

        assertEquals("&c SIEMA § test&a!", text);
    }

    @Test
    void testShadow() {
        String text = Legacy.shadow("&c SIEMA & test&a!");

        assertEquals("&&c SIEMA & test&&a!", text);
    }

    @Test
    void testManyShadow() {
        String text = Legacy.shadow("&c SIEMA & test&a &&c yoo&a sieema&f!");

        assertEquals("&&c SIEMA & test&&a &&&c yoo&&a sieema&&f!", text);
    }

    @Test
    void testColorShadow() {
        String text = Legacy.colorShadow("&c SIEMA & test&a &&c yoo&a sieema&f!");

        assertEquals("§c SIEMA & test§a &c yoo§a sieema§f!", text);
    }


}
