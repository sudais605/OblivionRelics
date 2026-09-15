package me.oblivion.relics;

import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("OblivionRelics has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OblivionRelics has been disabled!");
    }
}
