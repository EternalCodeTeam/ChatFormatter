package pl.eternalmc.chat.hook;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import pl.eternalmc.chat.ChatRankProvider;

public class VaultRankProvider implements ChatRankProvider {

    private final Permission permission;

    public VaultRankProvider(Server server) {
        RegisteredServiceProvider<Permission> provider = server.getServicesManager().getRegistration(Permission.class);

        if (provider == null) {
            throw new IllegalStateException("Vault not found!");
        }

        this.permission = provider.getProvider();
    }

    @Override
    public String getRank(Player player) {
        return this.permission.getPrimaryGroup(player.getWorld().getName(), player);
    }

}
