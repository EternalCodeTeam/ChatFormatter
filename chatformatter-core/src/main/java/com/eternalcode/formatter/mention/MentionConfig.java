package com.eternalcode.formatter.mention;

import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;

import java.io.Serializable;

@Contextual
public class MentionConfig implements Serializable {

    @Description("# Mention system configuration")
    @Description("# When a player mentions another player with @playername, the mentioned player will hear a sound")
    public boolean enabled = true;

    @Description("# The sound to play when a player is mentioned")
    @Description("# Available sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
    public String sound = "BLOCK_NOTE_BLOCK_PLING";

    @Description("# The volume of the mention sound (0.0 to 1.0)")
    public float volume = 1.0f;

    @Description({ " ", "# The pitch of the mention sound (0.5 to 2.0)" })
    public float pitch = 1.0f;
}
