package me.oblivion.relics;

import me.oblivion.relics.ability.AbilityManager;
import me.oblivion.relics.ability.RelicAbilityEngine;
import me.oblivion.relics.ability.RiftAbilities;
import me.oblivion.relics.command.RelicGiveCommand;
import me.oblivion.relics.command.TrustCommand;
import me.oblivion.relics.command.WithdrawCommand;
import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.listener.AbilityListener;
import me.oblivion.relics.listener.FlaskListener;
import me.oblivion.relics.listener.UniversalAbilityListener;
import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class OblivionRelics extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private EnergyManager energyManager;
    private EchoFlask echoFlask;
    private TrustManager trustManager;
    private RelicItem relicItem;
    private RelicManager relicManager;

    private AbilityManager abilityManager;
    private RiftAbilities riftAbilities;
    private RelicAbilityEngine relicAbilityEngine;

    @Override
    public void onEnable() {

        // ==============================
        // CORE SYSTEMS
        // ==============================

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

        // ==============================
        // RELIC SYSTEM
        // ==============================

        relicItem =
                new RelicItem(this);

        relicManager =
                new RelicManager(
                        playerDataManager,
                        relicItem
                );

        // ==============================
        // ABILITY SYSTEM
        // ==============================

        abilityManager =
                new AbilityManager(
                        relicManager,
                        trustManager
                );

        riftAbilities =
                new RiftAbilities(
                        abilityManager
                );

        relicAbilityEngine =
                new RelicAbilityEngine(
                        this,
                        relicManager,
                        energyManager,
                        trustManager
                );

        // ==============================
        // COMMANDS
        // ==============================

        if (getCommand("withdraw") != null) {
            getCommand("withdraw").setExecutor(
                    new WithdrawCommand(
                            energyManager,
                            echoFlask
                    )
            );
        }

        if (getCommand("trust") != null) {
            getCommand("trust").setExecutor(
                    new TrustCommand(
                            trustManager
                    )
            );
        }

        if (getCommand("relicgive") != null) {
            getCommand("relicgive").setExecutor(
                    new RelicGiveCommand(
                            relicManager
                    )
            );
        }

        // ==============================
        // EVENT LISTENERS
        // ==============================

        getServer().getPluginManager().registerEvents(
                new FlaskListener(
                        echoFlask,
                        energyManager
                ),
                this
        );

        /*
         * Old Rift listener is still registered for now.
         * We will remove it later after the universal
         * ability system is fully tested.
         */
        getServer().getPluginManager().registerEvents(
                new AbilityListener(
                        riftAbilities
                ),
                this
        );

        /*
         * Universal ability system:
         * F = Ability I
         * SHIFT + F = Ability II
         * Double F = Ability III
         */
        getServer().getPluginManager().registerEvents(
                new UniversalAbilityListener(
                        relicAbilityEngine
                ),
                this
        );

        // ==============================
        // LOGGING
        // ==============================

        getLogger().info(
                "================================"
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

        getLogger().info(
                "Ability Manager loaded."
        );

        getLogger().info(
                "Relic Ability Engine loaded."
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

    // ==============================
    // GETTERS
    // ==============================

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

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public RiftAbilities getRiftAbilities() {
        return riftAbilities;
    }

    public RelicAbilityEngine getRelicAbilityEngine() {
        return relicAbilityEngine;
    }
}
