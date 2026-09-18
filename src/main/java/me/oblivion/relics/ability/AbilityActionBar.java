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
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        update(player);
                    }
                },
                2L,
                2L
        );
    }

    private void update(Player player) {

        RelicType relic = relicManager.getRelic(player.getUniqueId());

        if (relic == null) {
            player.sendActionBar(
                    LegacyComponentSerializer.legacySection().deserialize(
                            "§7No Relic equipped"
                    )
            );
            return;
        }

        int energy = energyManager.getEnergy(player.getUniqueId());

        String ability1 = formatAbility(player, relic, 1, "[F]");
        String ability2 = formatAbility(player, relic, 2, "[SHIFT+F]");
        String ability3 = formatAbility(player, relic, 3, "[DOUBLE F]");

        String message =
                "§8[§b" + relic.getDisplayName() + "§8] "
                        + ability1
                        + " §8│ "
                        + ability2
                        + " §8│ "
                        + ability3
                        + " §8│ §b⚡ " + energy + "/10";

        player.sendActionBar(
                LegacyComponentSerializer.legacySection().deserialize(message)
        );
    }

    private String formatAbility(
            Player player,
            RelicType relic,
            int slot,
            String key
    ) {
        String name = abilityEngine.getAbilityName(relic, slot);

        if (!abilityEngine.isAbilityUnlocked(player, slot)) {

            int required = slot == 2 ? 8 : 10;

            return "§7" + key
                    + " §8" + name
                    + " §cLOCKED(" + required + "E)";
        }

        int cooldown = abilityEngine.remainingCooldown(player, slot);

        if (cooldown > 0) {
            return "§f" + key
                    + " §7" + name
                    + " §c" + cooldown + "s";
        }

        return "§f" + key
                + " §b" + name
                + " §aREADY";
    }
}
