package me.oblivion.relics;

import me.oblivion.relics.ability.AbilityActionBar;
import me.oblivion.relics.ability.AbilityManager;
import me.oblivion.relics.ability.RiftAbilities;
import me.oblivion.relics.command.RelicGiveCommand;
import me.oblivion.relics.command.TrustCommand;
import me.oblivion.relics.command.WithdrawCommand;
import me.oblivion.relics.data.PlayerDataManager;
import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.listener.AbilityListener;
import me.oblivion.relics.listener.FlaskListener;
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
    private AbilityActionBar abilityActionBar;

    @Override
    public void onEnable() {

        playerDataManager =
                new PlayerDataManager(this);

        energyManager =
                new EnergyManager(playerDataManager);

        echoFlask =
                new EchoFlask(this);

        trustManager =
                new TrustManager(playerDataManager);

        relicItem =
                new RelicItem(this);

        relicManager =
                new RelicManager(
                        playerDataManager,
                        relicItem
                );

        abilityManager =
                new AbilityManager(
                        relicManager,
                        trustManager
                );

        riftAbilities =
                new RiftAbilities(
                        abilityManager
                );

        abilityActionBar =
                new AbilityActionBar(
                        this,
                        relicManager,
                        abilityManager
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

        getServer().getPluginManager().registerEvents(
                new AbilityListener(
                        riftAbilities
                ),
                this
        );

        abilityActionBar.start();

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
                "Ability system loaded."
        );

        getLogger().info(
                "Ability action bar loaded."
        );
    }

    @Override
    public void onDisable() {

        if (abilityActionBar != null) {
            abilityActionBar.stop();
        }

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

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public RiftAbilities getRiftAbilities() {
        return riftAbilities;
    }

    public AbilityActionBar getAbilityActionBar() {
        return abilityActionBar;
    }
}
