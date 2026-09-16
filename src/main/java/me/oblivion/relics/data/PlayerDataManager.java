package me.oblivion.relics;

import me.oblivion.relics.data.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        playerDataManager = new PlayerDataManager(this);

        getLogger().info("OblivionRelics has been enabled!");
        getLogger().info("Player data system loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OblivionRelics has been disabled!");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
