package com.eternalcode.formatter.rank;

import org.bukkit.entity.Player;

class EmptyRankProvider implements ChatRankProvider{

    @Override
    public String getRank(Player player) {
        return player.isOp() ? "op" : "default";
    }

}
