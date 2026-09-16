package me.oblivion.relics;

import me.oblivion.relics.command.TrustCommand;
import me.oblivion.relics.command.WithdrawCommand;
import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.listener.FlaskListener;
import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.oblivion.relics.command.RelicGiveCommand;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private EnergyManager energyManager;
    private EchoFlask echoFlask;
    private TrustManager trustManager;
    private RelicItem relicItem;
    private RelicManager relicManager;

    @Override
    public void onEnable() {

        playerDataManager = new PlayerDataManager(this);

        energyManager = new EnergyManager(
                playerDataManager
        );

        echoFlask = new EchoFlask(this);

        trustManager = new TrustManager(
                playerDataManager
        );

        relicItem = new RelicItem(this);

        relicManager = new RelicManager(
                playerDataManager,
                relicItem
        );

        getCommand("withdraw").setExecutor(
                new WithdrawCommand(
                        energyManager,
                        echoFlask
                )
        );

        getCommand("trust").setExecutor(
                new TrustCommand(
                        trustManager            
                )
        );

        getCommand("relicgive").setExecutor(
                new RelicGiveCommand(
                            relicManager
                )
        );

        getServer().getPluginManager().registerEvents(
                new FlaskListener(
                        echoFlask,
                        energyManager
                ),
                this
        );

        getLogger().info(
                "OblivionRelics has been enabled!"
        );

        getLogger().info(
                "Player data system loaded."
        );

        getLogger().info(
                "Energy system loaded."
        );

        getLogger().info(
                "Echo Flask system loaded."
        );

        getLogger().info(
                "Trust system loaded."
        );

        getLogger().info(
                "Relic system loaded."
        );
    }

    @Override
    public void onDisable() {
        getLogger().info(
                "OblivionRelics has been disabled!"
        );
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

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public RelicItem getRelicItem() {
        return relicItem;
    }

    public RelicManager getRelicManager() {
        return relicManager;
    }
}
