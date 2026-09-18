package me.oblivion.relics.command;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.start.RelicStartManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelicStartCommand
        implements org.bukkit.command.CommandExecutor {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final RelicStartManager startManager;

    private final Map<Player, ItemDisplay> displays =
            new HashMap<>();

    public RelicStartCommand(
            JavaPlugin plugin,
            RelicManager relicManager,
            RelicStartManager startManager
    ) {
        this.plugin = plugin;
        this.relicManager = relicManager;
        this.startManager = startManager;
    }

    @Override
    public boolean onCommand(
            org.bukkit.command.CommandSender sender,
            org.bukkit.command.Command command,
            String label,
            String[] args
    ) {

        if (!sender.isOp()) {
            sender.sendMessage(
                    "§cOnly OP players can use this command."
            );
            return true;
        }

        if (startManager.isStarted()) {
            sender.sendMessage(
                    "§cThe Relic system has already started."
            );
            return true;
        }

        List<Player> players =
                new ArrayList<>(
                        plugin.getServer()
                                .getOnlinePlayers()
                );

        if (players.isEmpty()) {
            sender.sendMessage(
                    "§cThere are no players online."
            );
            return true;
        }

        startManager.setStarted(true);

        // ==============================
        //          ANNOUNCEMENT
        // ==============================

        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        plugin.getServer().broadcastMessage(
                "§b§l             OBLIVION SMP"
        );
        plugin.getServer().broadcastMessage(
                "§7             §lRELIC AWAKENING"
        );
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage(
                "§bThe Relics are awakening..."
        );
        plugin.getServer().broadcastMessage(
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        plugin.getServer().broadcastMessage("");

        // ==============================
        //       PREPARE PLAYERS
        // ==============================

        for (Player player : players) {

            // Random relic
            RelicType relic =
                    relicManager.giveRandomRelic(
                            player.getUniqueId()
                    );

            // Remove any old relic item
            removeOldRelicItems(player);

            // Freeze
            player.setGravity(false);
            player.setFallDistance(0);
            player.setVelocity(
                    new Vector(0, 0, 0)
            );

            // Lift slightly
            Location lifted =
                    player.getLocation().clone();

            lifted.add(0, 1.0, 0);

            player.teleport(
                    lifted,
                    PlayerTeleportEvent.TeleportCause.PLUGIN
            );

            // ==========================
            //       RELIC DISPLAY
            // ==========================

            ItemStack item =
                    relicManager.createRelicItem(
                            relic
                    );

            if (item != null) {

                ItemDisplay display =
                        player.getWorld().spawn(
                                lifted.clone()
                                        .add(0, 1.2, 0),
                                ItemDisplay.class
                        );

                display.setItemStack(item);

                display.setBillboard(
                        ItemDisplay.Billboard.CENTER
                );

                displays.put(
                        player,
                        display
                );
            }

            player.getWorld().playSound(
                    player.getLocation(),
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    1.2f,
                    0.7f
            );
        }

        // ==============================
        //        AWAKENING ANIMATION
        // ==============================

        new BukkitRunnable() {

            int ticks = 0;
            int lastSecond = -1;

            @Override
            public void run() {

                ticks += 2;

                int secondsRemaining =
                        5 - (ticks / 20);

                if (secondsRemaining != lastSecond
                        && secondsRemaining > 0) {

                    lastSecond = secondsRemaining;

                    for (Player player : players) {

                        if (!player.isOnline()) {
                            continue;
                        }

                        player.sendActionBar(
                                "§b§lRELIC AWAKENING §8• §f"
                                        + secondsRemaining
                        );

                        player.getWorld().playSound(
                                player.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_CHIME,
                                0.8f,
                                0.7f
                                        + (
                                        (5 - secondsRemaining)
                                                * 0.12f
                                )
                        );
                    }
                }

                // ==========================
                //      ORBIT DISPLAY
                // ==========================

                for (int index = 0;
                     index < players.size();
                     index++) {

                    Player player =
                            players.get(index);

                    if (!player.isOnline()) {
                        continue;
                    }

                    player.setGravity(false);
                    player.setFallDistance(0);
                    player.setVelocity(
                            new Vector(0, 0, 0)
                    );

                    Location center =
                            player.getLocation();

                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            center.clone()
                                    .add(0, 1, 0),
                            5,
                            0.25,
                            0.5,
                            0.25,
                            0.01
                    );

                    ItemDisplay display =
                            displays.get(player);

                    if (display != null
                            && !display.isDead()) {

                        double angle =
                                (ticks * 0.13)
                                        + (index * 0.65);

                        double radius = 1.15;

                        double x =
                                Math.cos(angle) * radius;

                        double z =
                                Math.sin(angle) * radius;

                        double y =
                                1.8
                                        + Math.sin(
                                        angle * 1.5
                                ) * 0.22;

                        Location orbit =
                                center.clone().add(
                                        x,
                                        y,
                                        z
                                );

                        display.teleport(
                                orbit,
                                PlayerTeleportEvent.TeleportCause.PLUGIN
                        );

                        display.setRotation(
                                (float)
                                        Math.toDegrees(angle),
                                0
                        );
                    }
                }

                // ==========================
                //           FINISH
                // ==========================

                if (ticks >= 100) {

                    for (Player player : players) {

                        if (!player.isOnline()) {
                            continue;
                        }

                        ItemDisplay display =
                                displays.remove(player);

                        if (display != null
                                && !display.isDead()) {
                            display.remove();
                        }

                        player.setGravity(true);
                        player.setFallDistance(0);
                        player.setVelocity(
                                new Vector(0, 0, 0)
                        );

                        RelicType relic =
                                relicManager.getRelic(
                                        player.getUniqueId()
                                );

                        if (relic == null) {
                            relic =
                                    relicManager.giveRandomRelic(
                                            player.getUniqueId()
                                    );
                        }

                        removeOldRelicItems(player);

                        ItemStack item =
                                relicManager.createRelicItem(
                                        relic
                                );

                        if (item != null) {
                            player.getInventory()
                                    .addItem(item);
                        }

                        Location location =
                                player.getLocation();

                        player.getWorld().spawnParticle(
                                Particle.END_ROD,
                                location.clone()
                                        .add(0, 1, 0),
                                45,
                                0.7,
                                1.0,
                                0.7,
                                0.04
                        );

                        player.getWorld().playSound(
                                location,
                                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                                1.5f,
                                1.3f
                        );

                        player.sendTitle(
                                "§b§l"
                                        + relic.getDisplayName(),
                                "§7Your Relic has awakened",
                                10,
                                50,
                                15
                        );
                    }

                    plugin.getServer().broadcastMessage("");
                    plugin.getServer().broadcastMessage(
                            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );
                    plugin.getServer().broadcastMessage(
                            "§b§l            RELICS AWAKENED"
                    );
                    plugin.getServer().broadcastMessage("");
                    plugin.getServer().broadcastMessage(
                            "§7Every player has received a Relic."
                    );
                    plugin.getServer().broadcastMessage(
                            "§bGood luck."
                    );
                    plugin.getServer().broadcastMessage(
                            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );
                    plugin.getServer().broadcastMessage("");

                    cancel();
                }
            }
        }.runTaskTimer(
                plugin,
                2L,
                2L
        );

        return true;
    }

    private void removeOldRelicItems(Player player) {

        for (int slot = 0;
             slot < player.getInventory().getSize();
             slot++) {

            ItemStack item =
                    player.getInventory()
                            .getItem(slot);

            if (item == null) {
                continue;
            }

            if (relicManager.isRelicItem(item)) {
                player.getInventory()
                        .setItem(slot, null);
            }
        }
    }
}
