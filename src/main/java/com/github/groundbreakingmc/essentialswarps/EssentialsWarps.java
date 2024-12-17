package com.github.groundbreakingmc.essentialswarps;

import com.earth2me.essentials.Essentials;
import com.github.groundbreakingmc.essentialswarps.listeners.CommandListener;
import com.github.groundbreakingmc.essentialswarps.listeners.ServerWarpProtector;
import com.github.groundbreakingmc.essentialswarps.listeners.WarpProtector;
import com.github.groundbreakingmc.essentialswarps.utils.colorizer.Colorizer;
import com.github.groundbreakingmc.essentialswarps.utils.config.ConfigLoader;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

@Getter
public final class EssentialsWarps extends JavaPlugin {

    private Essentials essentials;
    private Set<String> serverWarps;
    private String errorMessageServer;
    private String errorMessage;

    @Override
    public void onEnable() {
        this.setupEssentials();
        this.setupErrorMessage();
    }

    public void setupEssentials() {
        final PluginManager pluginManager = super.getServer().getPluginManager();
        if (pluginManager.isPluginEnabled("Essentials")) {
            this.essentials = (Essentials) pluginManager.getPlugin("Essentials");
            pluginManager.registerEvents(new CommandListener(), this);
            pluginManager.registerEvents(new ServerWarpProtector(this), this);
            pluginManager.registerEvents(new WarpProtector(this), this);
        } else {
            pluginManager.disablePlugin(this);
            throw new RuntimeException("Plugin cannot be loaded with disabled Essentials!");
        }
    }

    public void setupErrorMessage() {
        final FileConfiguration config = new ConfigLoader(this).get();
        this.serverWarps = ImmutableSet.copyOf(config.getStringList("server-warps"));
        final Colorizer colorizer = new Colorizer();
        this.errorMessageServer = colorizer.colorize(config.getString("error-message-server"));
        this.errorMessage = colorizer.colorize(config.getString("error-message"));
    }
}
