package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface BiPlayerPlaceholderStack {

    String apply(String text, Player target, @Nullable Player otherTarget);
}
