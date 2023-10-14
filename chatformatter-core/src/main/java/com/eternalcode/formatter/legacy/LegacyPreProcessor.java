package com.eternalcode.formatter.legacy;

import java.util.function.UnaryOperator;

public final class LegacyPreProcessor implements UnaryOperator<String> {

    @Override
    public String apply(String component) {
        return Legacy.clearSection(component);
    }

}
