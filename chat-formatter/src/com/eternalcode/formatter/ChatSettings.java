package com.eternalcode.formatter;

public interface ChatSettings {

    boolean isReceiveUpdates();

    boolean isPreFormatting();

    String getRawFormat(String rank);

}
