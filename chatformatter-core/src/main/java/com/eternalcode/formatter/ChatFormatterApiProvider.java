package com.eternalcode.formatter;

public final class ChatFormatterApiProvider {

    private static ChatFormatterApi chatFormatterAPI;

    static void enable(ChatFormatterApi chatFormatterAPI) {
        ChatFormatterApiProvider.chatFormatterAPI = chatFormatterAPI;
    }

    static void disable() {
        ChatFormatterApiProvider.chatFormatterAPI = null;
    }

    public static ChatFormatterApi get() {
        if (chatFormatterAPI == null) {
            throw new IllegalStateException();
        }

        return chatFormatterAPI;
    }

}
