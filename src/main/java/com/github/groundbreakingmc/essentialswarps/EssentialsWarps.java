package com.github.groundbreakingmc.essentialswarps;

import com.earth2me.essentials.Essentials;
import com.github.groundbreakingmc.essentialswarps.listeners.CommandListener;
import com.github.groundbreakingmc.essentialswarps.listeners.LimitManager;
import com.github.groundbreakingmc.essentialswarps.listeners.ServerWarpProtector;
import com.github.groundbreakingmc.essentialswarps.listeners.WarpProtector;
import com.github.groundbreakingmc.essentialswarps.utils.colorizer.Colorizer;
import com.github.groundbreakingmc.essentialswarps.utils.config.ConfigLoader;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

@Getter
public final class EssentialsWarps extends JavaPlugin {

    private Essentials essentials;
    private Permission perms;

    private Set<String> serverWarps;
    private String errorMessageServer;
    private String errorMessage;

    private LimitManager limitManager;

    @Override
    public void onEnable() {
        this.setupEssentials();
        this.setupPerms();
        this.limitManager = new LimitManager(this);
        this.setupValues();
        this.registerEvents();
    }

    public void setupEssentials() {
        final PluginManager pluginManager = super.getServer().getPluginManager();
        if (pluginManager.isPluginEnabled("Essentials")) {
            this.essentials = (Essentials) pluginManager.getPlugin("Essentials");
        } else {
            pluginManager.disablePlugin(this);
            throw new RuntimeException("Plugin cannot be loaded with disabled Essentials!");
        }
    }

    public void setupValues() {
        final FileConfiguration config = new ConfigLoader(this).get();
        this.serverWarps = ImmutableSet.copyOf(config.getStringList("server-warps"));
        final Colorizer colorizer = new Colorizer();
        this.errorMessageServer = colorizer.colorize(config.getString("server-owned"));
        this.errorMessage = colorizer.colorize(config.getString("not-yours"));
        this.limitManager.setupValues(config, colorizer);
    }

    private void setupPerms() {
        final ServicesManager servicesManager = super.getServer().getServicesManager();
        final RegisteredServiceProvider<Permission> permissionProvider = servicesManager.getRegistration(Permission.class);
        if (permissionProvider != null) {
            this.perms = permissionProvider.getProvider();
        }
    }

    private void registerEvents() {
        final PluginManager pluginManager = super.getServer().getPluginManager();
        pluginManager.registerEvents(new CommandListener(), this);
        pluginManager.registerEvents(new ServerWarpProtector(this), this);
        pluginManager.registerEvents(new WarpProtector(this), this);
    }

}
