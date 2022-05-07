package pl.eternalmc.chat.legacy;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Legacy {

    private Legacy() {
    }

    public final static LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public final static LegacyComponentSerializer LEGACY_AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

}
