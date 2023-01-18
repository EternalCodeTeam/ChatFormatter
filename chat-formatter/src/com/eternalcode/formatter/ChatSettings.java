package com.eternalcode.formatter;

public interface ChatSettings {

    boolean receiveUpdates();

    boolean isPreFormatting();

    String getRawFormat(String rank);

}
