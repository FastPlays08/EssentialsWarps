package com.github.groundbreakingmc.essentialswarps.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class WarpProtector implements Listener {

    private final EssentialsWarps plugin;
    private final Essentials essentials;

    public WarpProtector(final EssentialsWarps plugin) {
        this.plugin = plugin;
        this.essentials = plugin.getEssentials();
    }

    @EventHandler
    public void onDelete(final EssentialsWarpDeleteEvent event) {
        final Player sender = event.getPlayer();

        try {
            if (sender.hasPermission("essentils.delwarp.all")
                    || event.getLastOwner(essentials).equals(sender.getUniqueId())) {
                return;
            }
        } catch (final WarpNotFoundException ignored) {
            return;
        }

        event.setCancelled(true);
        sender.sendMessage(this.plugin.getErrorMessage());
    }
}
