package com.eternalcode.formatter;

import org.bukkit.Bukkit;

public class IntegrationTestE2E {

    {
        System.out.println("test");
    }

    @PaperTest
    void test() {
        System.out.println("WORK " + Bukkit.getMaxPlayers());
    }

}
