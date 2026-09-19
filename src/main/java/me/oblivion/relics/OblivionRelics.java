package me.oblivion.relics;

import me.oblivion.relics.ability.AbilityActionBar;
import me.oblivion.relics.ability.RelicAbilityEngine;
import me.oblivion.relics.command.RelicGiveCommand;
import me.oblivion.relics.command.RelicRemoveCommand;
import me.oblivion.relics.command.RelicRerollGiveCommand;
import me.oblivion.relics.command.RelicStartCommand;
import me.oblivion.relics.command.TrustCommand;
import me.oblivion.relics.command.WithdrawCommand;
import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.gui.RelicInfoGUI;
import me.oblivion.relics.listener.AbilityDeathMessageListener;
import me.oblivion.relics.listener.DeathEnergyListener;
import me.oblivion.relics.listener.FlaskListener;
import me.oblivion.relics.listener.PlayerJoinRelicListener;
import me.oblivion.relics.listener.RelicProtectionListener;
import me.oblivion.relics.listener.RerollerListener;
import me.oblivion.relics.listener.UniversalAbilityListener;
import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RerollerItem;
import me.oblivion.relics.start.RelicStartManager;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private EnergyManager energyManager;
    private EchoFlask echoFlask;
    private TrustManager trustManager;

    private RelicItem relicItem;
    private RelicManager relicManager;

    private RelicAbilityEngine relicAbilityEngine;
    private AbilityActionBar abilityActionBar;

    private RelicStartManager relicStartManager;
    private RelicInfoGUI relicInfoGUI;

    private RerollerItem rerollerItem;

    @Override
    public void onEnable() {

        playerDataManager =
                new PlayerDataManager(this);

        energyManager =
                new EnergyManager(
                        playerDataManager
                );

        echoFlask =
                new EchoFlask(this);

        trustManager =
                new TrustManager(
                        playerDataManager
                );

        relicItem =
                new RelicItem(this);

        relicManager =
                new RelicManager(
                        playerDataManager,
                        relicItem
                );

        relicAbilityEngine =
                new RelicAbilityEngine(
                        this,
                        relicManager,
                        energyManager,
                        trustManager
                );

        abilityActionBar =
                new AbilityActionBar(
                        this,
                        relicManager,
                        energyManager,
                        relicAbilityEngine
                );

        relicStartManager =
                new RelicStartManager(this);

        relicInfoGUI =
                new RelicInfoGUI(
                        relicManager,
                        relicAbilityEngine,
                        energyManager
                );

        rerollerItem =
                new RerollerItem(this);

        if (getCommand("withdraw") != null) {
            getCommand("withdraw")
                    .setExecutor(
                            new WithdrawCommand(
                                    energyManager,
                                    echoFlask
                            )
                    );
        }

        if (getCommand("trust") != null) {
            getCommand("trust")
                    .setExecutor(
                            new TrustCommand(
                                    trustManager
                            )
                    );
        }

        if (getCommand("relicgive") != null) {
            getCommand("relicgive")
                    .setExecutor(
                            new RelicGiveCommand(
                                    relicManager
                            )
                    );
        }

        if (getCommand("relicremove") != null) {
            getCommand("relicremove")
                    .setExecutor(
                            new RelicRemoveCommand(
                                    relicManager
                            )
                    );
        }

        if (getCommand("relicstart") != null) {
            getCommand("relicstart")
                    .setExecutor(
                            new RelicStartCommand(
                                    this,
                                    relicManager,
                                    relicStartManager
                            )
                    );
        }

        if (getCommand("relicinfo") != null) {
            getCommand("relicinfo")
                    .setExecutor(
                            relicInfoGUI
                    );
        }

        if (getCommand("relicreroll") != null) {
            getCommand("relicreroll")
                    .setExecutor(
                            new RelicRerollGiveCommand(
                                    rerollerItem
                            )
                    );
        }

        if (getCommand("relictest") != null) {
            getCommand("relictest")
                    .setExecutor(
                            (sender, command, label, args) -> {

                                sender.sendMessage(
                                        "§b§l[OblivionRelics] "
                                                + "§aPlugin is working!"
                                );

                                return true;
                            }
                    );
        }

        getServer().getPluginManager().registerEvents(
                new FlaskListener(
                        echoFlask,
                        energyManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new UniversalAbilityListener(
                        relicAbilityEngine
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerJoinRelicListener(
                        relicManager,
                        relicStartManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new RelicProtectionListener(
                        relicManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new DeathEnergyListener(
                        energyManager,
                        echoFlask
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new AbilityDeathMessageListener(
                        relicAbilityEngine
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new RerollerListener(
                        relicManager,
                        rerollerItem,
                        relicItem
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                relicInfoGUI,
                this
        );

        getLogger().info(
                "================================"
        );

        getLogger().info(
                "OblivionRelics has been enabled!"
        );

        getLogger().info(
                "45 ability engine loaded."
        );

        getLogger().info(
                "Relic hold protection loaded."
        );

        getLogger().info(
                "Relic drop/container protection loaded."
        );

        getLogger().info(
                "Death energy system loaded."
        );

        getLogger().info(
                "Custom ability death messages loaded."
        );

        getLogger().info(
                "8 second AutoCrit loaded."
        );

        getLogger().info(
                "8 second Phase Shift loaded."
        );

        getLogger().info(
                "Relic reroller loaded."
        );

        getLogger().info(
                "================================"
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

    public RelicAbilityEngine getRelicAbilityEngine() {
        return relicAbilityEngine;
    }

    public AbilityActionBar getAbilityActionBar() {
        return abilityActionBar;
    }

    public RelicStartManager getRelicStartManager() {
        return relicStartManager;
    }

    public RelicInfoGUI getRelicInfoGUI() {
        return relicInfoGUI;
    }

    public RerollerItem getRerollerItem() {
        return rerollerItem;
    }
}
