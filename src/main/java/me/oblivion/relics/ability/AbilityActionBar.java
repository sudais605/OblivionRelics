package me.oblivion.relics.ability;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AbilityActionBar {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final AbilityManager abilityManager;

    private BukkitTask task;

    public AbilityActionBar(
            JavaPlugin plugin,
            RelicManager relicManager,
            AbilityManager abilityManager
    ) {
        this.plugin = plugin;
        this.relicManager = relicManager;
        this.abilityManager = abilityManager;
    }

    public void start() {

        if (task != null) {
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::updateAllPlayers,
                0L,
                2L
        );
    }

    public void stop() {

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void updateAllPlayers() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            RelicType relic =
                    relicManager.getRelic(player.getUniqueId());

            if (relic == null) {
                continue;
            }

            if (relic != RelicType.RIFT) {
                continue;
            }

            updateRiftBar(player);
        }
    }

    private void updateRiftBar(Player player) {

        long cooldown =
                abilityManager.getRemainingCooldown(
                        player,
                        "RIFT_PULL"
                );

        String abilityOne;

        if (cooldown > 0) {

            double seconds =
                    Math.ceil(cooldown / 100.0) / 10.0;

            abilityOne =
                    "§cRift Pull " +
                    String.format("%.1fs", seconds);

        } else {

            abilityOne =
                    "§aRift Pull READY";
        }

        String abilityTwo =
                "§eRift Dash 8E";

        String abilityThree =
                "§eRift Break 10E";

        String message =
                "§b[F] §f" + abilityOne +
                " §8│ " +
                "§b[SHIFT+F] §f" + abilityTwo +
                " §8│ " +
                "§b[CTRL+F] §f" + abilityThree;

        player.sendActionBar(
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacySection()
                        .deserialize(message)
        );
    }
}
