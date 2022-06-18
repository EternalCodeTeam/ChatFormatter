package com.eternalcode.formatter;

public final class ChatFormatterProvider {

    private static ChatFormatter chatFormatter;

    static void enable(ChatFormatter chatFormatter) {
        ChatFormatterProvider.chatFormatter = chatFormatter;
    }

    static void disable() {
        ChatFormatterProvider.chatFormatter = null;
    }

    public static ChatFormatter get() {
        if (chatFormatter == null) {
            throw new IllegalStateException();
        }

        return chatFormatter;
    }

}
