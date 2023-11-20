package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

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
        try {
            return Template.parse(value);
        }
        catch (NoSuchElementException exception) {
            throw new IllegalArgumentException("Unable to parse template", exception);
        }
    }
}