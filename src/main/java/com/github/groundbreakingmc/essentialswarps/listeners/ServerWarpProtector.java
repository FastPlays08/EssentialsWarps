package com.github.groundbreakingmc.essentialswarps.listeners;

import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerWarpProtector implements Listener {

    private final EssentialsWarps plugin;

    public ServerWarpProtector(final EssentialsWarps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDelete(final EssentialsWarpDeleteEvent event) {
        final Player sender = event.getPlayer();
        if (sender.hasPermission("essentils.delwarp.server")
                || !this.plugin.getServerWarps().contains(event.getWarpName())) {
            return;
        }

        event.setCancelled(true);
        sender.sendMessage(this.plugin.getErrorMessageServer());
    }
}
