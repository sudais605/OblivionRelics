package me.oblivion.relics.listener;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.start.RelicStartManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinRelicListener
        implements Listener {

    private final RelicManager relicManager;
    private final RelicStartManager startManager;

    public PlayerJoinRelicListener(
            RelicManager relicManager,
            RelicStartManager startManager
    ) {
        this.relicManager = relicManager;
        this.startManager = startManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // SMP has not started yet
        if (!startManager.isStarted()) {
            return;
        }

        Player player =
                event.getPlayer();

        // Already has a relic
        if (relicManager.hasRelic(player)) {
            return;
        }

        RelicType relic =
                relicManager.giveRandomRelic(
                        player.getUniqueId()
                );

        ItemStack item =
                relicManager.createRelicItem(
                        relic
                );

        if (item != null) {
            player.getInventory()
                    .addItem(item);
        }

        player.sendMessage("");
        player.sendMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        player.sendMessage(
                "§b§l        YOUR RELIC HAS AWAKENED"
        );
        player.sendMessage("");
        player.sendMessage(
                "§7Relic: §b§l"
                        + relic.getDisplayName()
        );
        player.sendMessage(
                "§7Use §f/relicinfo §7to view your abilities."
        );
        player.sendMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        player.sendMessage("");

        player.sendTitle(
                "§b§l"
                        + relic.getDisplayName(),
                "§7Your Relic has awakened",
                10,
                50,
                15
        );

        player.getWorld().spawnParticle(
                org.bukkit.Particle.END_ROD,
                player.getLocation()
                        .add(0, 1, 0),
                30,
                0.6,
                0.8,
                0.6,
                0.03
        );

        player.getWorld().playSound(
                player.getLocation(),
                org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                1.2f,
                1.2f
        );
    }
}
