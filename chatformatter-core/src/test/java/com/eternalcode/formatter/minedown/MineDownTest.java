package com.eternalcode.formatter.minedown;

import com.eternalcode.formatter.adventure.TextColorTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MineDownTest {

    private static final TagResolver ALL = TagResolver.standard();
    private static final TagResolver NONE = TagResolver.empty();

    @Test
    @DisplayName("Should translate simple formatting to MiniMessage tags")
    void testSimpleFormatting() {
        String result = MineDown.mineDownToAdventure("**bold** ##italic## __under__ ~~strike~~ ??obf??", ALL);

        assertEquals("<bold>bold</bold> <italic>italic</italic> <underlined>under</underlined> "
            + "<strikethrough>strike</strikethrough> <obfuscated>obf</obfuscated>", result);
    }

    @Test
    @DisplayName("Should translate MineDown colors, gradients and rainbow")
    void testColors() {
        String result = MineDown.mineDownToAdventure("&red&a &#FF0000&b &#ff0000-#0000ff&c &rainbow&d", ALL);

        assertEquals("<red>a <#ff0000>b <gradient:#ff0000:#0000ff>c <rainbow>d", result);
    }

    @Test
    @DisplayName("Should leave text untouched without permissions")
    void testNoPermissions() {
        String input = "**bold** &red&a &rainbow&b [link](https://example.com) [x](red)";

        assertEquals(input, MineDown.mineDownToAdventure(input, NONE));
    }

    @Test
    @DisplayName("Should respect per-tag permissions")
    void testPartialPermissions() {
        TagResolver permitted = TagResolver.resolver(
            StandardTags.decorations(TextDecoration.BOLD),
            TextColorTagResolver.of(NamedTextColor.GOLD)
        );

        String result = MineDown.mineDownToAdventure("**a** ##b## &gold&c &red&d", permitted);

        assertEquals("<bold>a</bold> ##b## <gold>c &red&d", result);
    }

    @Test
    @DisplayName("Should translate links with shorthand and properties")
    void testLinks() {
        String result = MineDown.mineDownToAdventure(
            "[site](https://example.com/?a=b) [cmd](/spawn) [styled](red bold hover=It's me) [x](suggest_command=/msg Steve)",
            ALL
        );

        assertEquals(
            "<click:open_url:'https://example.com/?a=b'>site</click> "
                + "<click:run_command:'/spawn'>cmd</click> "
                + "<red><bold><hover:show_text:'It\\'s me'>styled</hover></bold></red> "
                + "<click:suggest_command:'/msg Steve'>x</click>",
            result
        );
    }

    @Test
    @DisplayName("Should leave the whole link untouched when any part is not permitted or unknown")
    void testLinkAllOrNothing() {
        TagResolver permitted = TagResolver.resolver(StandardTags.color(), StandardTags.hoverEvent());

        assertEquals("[x](red https://example.com)", MineDown.mineDownToAdventure("[x](red https://example.com)", permitted));
        assertEquals("[x](sprite=abc)", MineDown.mineDownToAdventure("[x](sprite=abc)", ALL));
        assertEquals("[x](font=Bad Key)", MineDown.mineDownToAdventure("[x](font=Bad Key)", ALL));
        assertEquals("<red><hover:show_text:'hi'>x</hover></red>", MineDown.mineDownToAdventure("[x](red hover=hi)", permitted));
    }

    @Test
    @DisplayName("Should mix MineDown with MiniMessage tags")
    void testMixedWithMiniMessage() {
        String translated = MineDown.mineDownToAdventure("**<red>hi</red>** [site](https://example.com)", ALL);
        Component component = MiniMessage.miniMessage().deserialize(translated);

        Component expected = Component.text()
            .append(Component.text("hi", NamedTextColor.RED, TextDecoration.BOLD))
            .append(Component.text(" "))
            .append(Component.text("site").clickEvent(ClickEvent.openUrl("https://example.com")))
            .build();

        assertEquals(expected.compact(), component.compact());
    }

    @Test
    @DisplayName("Should not break on malformed input")
    void testMalformedInput() {
        String input = "hi &foo& ** [x](  ) [](red) &&";

        assertEquals(input, MineDown.mineDownToAdventure(input, ALL));
    }

    @Test
    @DisplayName("Should not translate MineDown syntax inside URLs")
    void testUrlsArePreserved() {
        String result = MineDown.mineDownToAdventure(
            "**see** https://example.com/a__b__c?x=&red&y [docs](https://example.com/__init__)",
            ALL
        );

        assertEquals(
            "<bold>see</bold> https://example.com/a__b__c?x=&red&y "
                + "<click:open_url:'https://example.com/__init__'>docs</click>",
            result
        );
    }

    @Test
    @DisplayName("Should format text that contains a URL")
    void testFormattingAroundUrl() {
        String result = MineDown.mineDownToAdventure("**see https://example.com**", ALL);

        assertEquals("<bold>see https://example.com</bold>", result);
    }

    @Test
    @DisplayName("Should not translate simple formats inside quoted link arguments")
    void testQuotedLinkArgumentsArePreserved() {
        assertEquals(
            "<hover:show_text:'x**'>a</hover> b**",
            MineDown.mineDownToAdventure("[a](hover=x**) b**", ALL)
        );
        assertEquals(
            "<bold><hover:show_text:'**hi**'>a</hover></bold>",
            MineDown.mineDownToAdventure("**[a](hover=**hi**)**", ALL)
        );
    }

}
