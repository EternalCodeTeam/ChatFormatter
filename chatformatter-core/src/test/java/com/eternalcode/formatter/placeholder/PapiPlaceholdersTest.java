package com.eternalcode.formatter.placeholder;

import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.template.TemplateService;
import me.clip.placeholderapi.PlaceholderAPI;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

class PapiPlaceholdersTest {

    private static MockedStatic<PlaceholderAPI> mockStatic;

    private static PlaceholderRegistry registry;
    private static TemplateService templateService;
    private static PluginConfig pluginConfig;

    @BeforeAll
    static void setUpBeforeClass() {
        pluginConfig = new PluginConfig();
        registry = new PlaceholderRegistry();
        templateService = new TemplateService(pluginConfig);

        registry.playerStack(new ConfiguredPlaceholderAPIStack(pluginConfig));
        registry.playerStack(new PlaceholderAPIStack());
    }

    @BeforeEach
    void setUp() {        // mock the PlaceholderAPI plugin (staic methods)
        mockStatic = mockStatic(PlaceholderAPI.class);
        mockStatic.when(() -> PlaceholderAPI.setPlaceholders(isNull(Player.class), anyString())).thenAnswer(invocation -> invocation.<String>getArgument(1)
            .replace("%vault_group%", "--VIP--")
            .replace("%player_first_join_date%", "2021-01-01")
            .replace("%player_health%", "19")
            .replace("%player_level%", "420"));
    }

    @Test
    void test() {
        String text = "Hello %player_name%, you joined on %player_first_join_date% and you are in the %vault_group% group. Your health is %player_health% and your level is %player_level%.";
        String result = registry.format(text, null);

        assertThat(result)
            .isEqualTo("Hello %player_name%, you joined on 2021-01-01 and you are in the --VIP-- group. Your health is 19 and your level is 420.");
    }


    @ParameterizedTest
    @ValueSource(strings = {"default", "vip", "mod", "admin", "owner"})
    void testFormatFromConfig(String rank) {
        String rawFormat = pluginConfig.getRawFormat(rank);
        String formattedString = templateService.applyTemplates(rawFormat);
        String result = registry.format(formattedString, null);

        assertThat(result)
            .contains("19", "420", "2021-01-01", "--VIP--")
            .doesNotContain("%", "{", "}");
    }


    @Test
    void testFormatFromConfigUnknown() {
        String rawFormat = pluginConfig.getRawFormat("unknown");
        String formattedString = templateService.applyTemplates(rawFormat);
        String result = registry.format(formattedString, null);

        assertThat(result)
            .doesNotContain("%", "{", "}");
    }

    @AfterEach
    void tearDownAfterClass() {
        mockStatic.close();
    }

}
