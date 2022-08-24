package com.eternalcode.formatter;

public interface ChatSettings {

    boolean isPreFormatting();

    boolean isRelationalPlaceholders();

    String getRawFormat(String rank);

}
