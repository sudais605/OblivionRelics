package me.oblivion.relics;

import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EnergyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private EnergyManager energyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        playerDataManager = new PlayerDataManager(this);
        energyManager = new EnergyManager(playerDataManager);

        getLogger().info("OblivionRelics has been enabled!");
        getLogger().info("Player data system loaded.");
        getLogger().info("Energy system loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OblivionRelics has been disabled.");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EnergyManager getEnergyManager() {
        return energyManager;
    }
}
