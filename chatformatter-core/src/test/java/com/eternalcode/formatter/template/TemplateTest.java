package com.eternalcode.formatter.template;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateTest {

    private final TemplateService templateService = new TemplateService(() -> List.of(
        Template.of("two", List.of("arg1", "arg2"), "> $arg1 + $arg2 <"),
        Template.of("three", List.of("arg1", "arg2", "arg3"), "< $arg1 + $arg2 + $arg3 >")
    ));

    @Test
    void parseTest() {
        Optional<Template> result = Template.parse("$test($arg1, $arg2, $arg3) -> 'SIEMA'");

        assertTrue(result.isPresent());

        Template template = result.get();
        List<String> arguments = template.getArguments();

        assertEquals("test", template.getName());
        assertEquals(3, arguments.size());
        assertEquals("arg1", arguments.get(0));
        assertEquals("arg2", arguments.get(1));
        assertEquals("arg3", arguments.get(2));
        assertEquals("SIEMA", template.getContent());
    }

    @Test
    void parseTestWithIncorrectQuote() {
        Optional<Template> result = Template.parse("$test($arg1, $arg2, $arg3) -> 'SIEMA''");

        assertTrue(result.isPresent());

        Template template = result.get();
        List<String> arguments = template.getArguments();

        assertEquals("test", template.getName());
        assertEquals(3, arguments.size());
        assertEquals("arg1", arguments.get(0));
        assertEquals("arg2", arguments.get(1));
        assertEquals("arg3", arguments.get(2));
        assertEquals("SIEMA'", template.getContent());
    }

    @Test
    void parseAndToStringCompareTest() {
        String original = "$test($arg1, $arg2, $arg3, $arg4) -> 'SIEMA'";
        Optional<Template> result = Template.parse(original);

        assertTrue(result.isPresent());

        Template template = result.get();
        String deserialized = template.toString();

        assertEquals(original, deserialized);
    }

    @Test
    void testApplyTemplate() {
        Template template = Template.of("test", List.of("arg"), "- $arg -");
        String text = template.apply("% $test(SIEMA) %");

        assertEquals("% - SIEMA - %", text);
    }


    @Test
    void testApplyTemplateService() {
        String text = templateService.applyTemplates("% $two(1, 2) $three(1, 2, 3) %");

        assertEquals("% > 1 + 2 < < 1 + 2 + 3 > %", text);
    }

}