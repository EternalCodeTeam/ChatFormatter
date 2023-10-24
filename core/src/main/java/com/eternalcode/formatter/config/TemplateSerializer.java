package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;
import panda.std.Result;
import panda.std.function.ThrowingFunction;

public class TemplateSerializer implements ObjectSerializer<Template> {

    @Override
    public boolean supports(@NotNull Class<? super Template> type) {
        return type.isAssignableFrom(Template.class);
    }

    @Override
    public void serialize(@NotNull Template object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(object.toString(), String.class);
    }

    @Override
    public Template deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        String value = data.getValue(String.class);

        Result<Template, String> templateResult = Template.parse(value);

        try {
            return templateResult.orThrow((ThrowingFunction<String, Exception, Exception>) IllegalArgumentException::new);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
