package com.eternalcode.formatter.template;

import org.junit.jupiter.api.Test;
import panda.std.Result;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    void parseTest() {
        Result<Template, String> result = Template.parse("($arg1, $arg2, $arg3) -> \"SIEMA\"");

        assertTrue(result.isOk());

        Template template = result.get();
        List<String> arguments = template.getArguments();

        assertEquals(3, arguments.size());
        assertEquals("arg1", arguments.get(0));
        assertEquals("arg2", arguments.get(1));
        assertEquals("arg3", arguments.get(2));
        assertEquals("SIEMA", template.getContent());
    }

    @Test
    void parseTestWithIncorrectQuote() {
        Result<Template, String> result = Template.parse("($arg1, $arg2, $arg3) -> \"SIEMA\"\"");

        assertTrue(result.isOk());

        Template template = result.get();
        List<String> arguments = template.getArguments();

        assertEquals(3, arguments.size());
        assertEquals("arg1", arguments.get(0));
        assertEquals("arg2", arguments.get(1));
        assertEquals("arg3", arguments.get(2));
        assertEquals("SIEMA\"", template.getContent());
    }

    @Test
    void parseAndToStringCompareTest() {
        String original = "($arg1, $arg2, $arg3, $arg4) -> \"SIEMA\"";
        Result<Template, String> result = Template.parse(original);

        assertTrue(result.isOk());

        Template template = result.get();
        String deserialized = template.toString();

        assertEquals(original, deserialized);
    }

}
