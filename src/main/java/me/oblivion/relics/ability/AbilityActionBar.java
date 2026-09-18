package me.oblivion.relics.ability;

import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AbilityActionBar {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final EnergyManager energyManager;
    private final RelicAbilityEngine abilityEngine;

    public AbilityActionBar(
            JavaPlugin plugin,
            RelicManager relicManager,
            EnergyManager energyManager,
            RelicAbilityEngine abilityEngine
    ) {
        this.plugin = plugin;
        this.relicManager = relicManager;
        this.energyManager = energyManager;
        this.abilityEngine = abilityEngine;

        start();
    }

    private void start() {

        plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {

                    for (Player player :
                            plugin.getServer()
                                    .getOnlinePlayers()) {

                        update(player);
                    }
                },
                2L,
                2L
        );
    }

    private void update(Player player) {

        RelicType relic =
                relicManager.getRelic(
                        player.getUniqueId()
                );

        if (relic == null) {

            send(
                    player,
                    "§7No Relic"
            );

            return;
        }

        int energy =
                energyManager.getEnergy(
                        player.getUniqueId()
                );

        if (!abilityEngine.isHoldingRelic(player)) {

            send(
                    player,
                    "§7Hold §b"
                            + relic.getDisplayName()
                            + " §7to use abilities"
                            + " §8│ §b⚡ "
                            + energy
                            + "/10"
            );

            return;
        }

        String message =
                "§8[§b"
                        + relic.getDisplayName()
                        + "§8] "
                        + format(
                        player,
                        relic,
                        1,
                        "[F]"
                )
                        + " §8│ "
                        + format(
                        player,
                        relic,
                        2,
                        "[SHIFT+F]"
                )
                        + " §8│ "
                        + format(
                        player,
                        relic,
                        3,
                        "[DOUBLE F]"
                )
                        + " §8│ §b⚡ "
                        + energy
                        + "/10";

        send(
                player,
                message
        );
    }

    private String format(
            Player player,
            RelicType relic,
            int slot,
            String key
    ) {

        String name =
                abilityEngine.getAbilityName(
                        relic,
                        slot
                );

        if (!abilityEngine.isAbilityUnlocked(
                player,
                slot
        )) {

            int required =
                    slot == 2 ? 8 : 10;

            return "§7"
                    + key
                    + " "
                    + name
                    + " §cLOCKED("
                    + required
                    + "E)";
        }

        int cooldown =
                abilityEngine.remainingCooldown(
                        player,
                        slot
                );

        if (cooldown > 0) {

            return "§f"
                    + key
                    + " §7"
                    + name
                    + " §c"
                    + cooldown
                    + "s";
        }

        return "§f"
                + key
                + " §b"
                + name
                + " §aREADY";
    }

    private void send(
            Player player,
            String message
    ) {

        player.sendActionBar(
                LegacyComponentSerializer
                        .legacySection()
                        .deserialize(message)
        );
    }
}
