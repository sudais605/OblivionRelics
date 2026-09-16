package me.oblivion.relics;

import me.oblivion.relics.command.WithdrawCommand;
import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.listener.FlaskListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private EnergyManager energyManager;
    private EchoFlask echoFlask;

    @Override
    public void onEnable() {

        playerDataManager = new PlayerDataManager(this);
        energyManager = new EnergyManager(playerDataManager);
        echoFlask = new EchoFlask(this);

        getCommand("withdraw").setExecutor(
                new WithdrawCommand(energyManager, echoFlask)
        );

        getServer().getPluginManager().registerEvents(
                new FlaskListener(echoFlask, energyManager),
                this
        );

        getLogger().info("OblivionRelics has been enabled!");
        getLogger().info("Player data system loaded.");
        getLogger().info("Energy system loaded.");
        getLogger().info("Echo Flask system loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OblivionRelics has been disabled!");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EnergyManager getEnergyManager() {
        return energyManager;
    }

    public EchoFlask getEchoFlask() {
        return echoFlask;
    }
}
