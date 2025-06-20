package com.eternalcode.formatter.legacy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyTest {

    @Test
    void decolor() {
        assertEquals("&c SIEMA & test&a!", Legacy.clearSection("§c SIEMA § test§a!"));
    }

    @Test
    @DisplayName("Should convert legacy color codes to adventure")
    void testLegacyToAdventure() {
        String input = "&c SIEMA &#8376d3 &rtest&a!";

        String result = Legacy.legacyToAdventure(input);

        assertEquals("<red> SIEMA <#8376d3> <reset>test<green>!", result);
    }

    @Test
    @DisplayName("Conversion of legacy hex formatting used in plugins like HexNicks default format")
    void testLegacyHexToAdventure() {
        String input = "&x&c&c&d&d&7&7&lSIEMA &x&7&7&5&5&4&4test&a!";

        String result = Legacy.legacyToAdventure(input);

        assertEquals("<#ccdd77><bold>SIEMA <#775544>test<green>!", result);
    }

}
