package com.eternalcode.formatter.template;

public class TemplateService {

    private final TemplateRepository repository;

    public TemplateService(TemplateRepository repository) {
        this.repository = repository;
    }

    public String applyTemplates(String text) {
        for (Template template : this.repository.getTemplates()) {
            text = template.apply(text);
        }

        return text;
    }

}
