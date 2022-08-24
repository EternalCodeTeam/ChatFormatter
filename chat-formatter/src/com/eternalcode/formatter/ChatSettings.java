package com.eternalcode.formatter;

public interface ChatSettings {

    boolean isPreFormatting();

    boolean isRelationalPlaceholdersEnabled();

    String getRawFormat(String rank);

}
