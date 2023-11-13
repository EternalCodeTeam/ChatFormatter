package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.SimpleDeserializer;
import net.dzikoysk.cdn.serdes.SimpleSerializer;
import panda.std.Result;

public class TemplateComposer implements Composer<Template>, SimpleSerializer<Template>, SimpleDeserializer<Template> {

    @Override
    public Result<Template, Exception> deserialize(String source) {
        return Template.parse(source).mapErr(IllegalArgumentException::new);
    }

    @Override
    public Result<String, Exception> serialize(Template entity) {
        return Result.ok(entity.toString());
    }

}
