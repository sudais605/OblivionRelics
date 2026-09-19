package me.oblivion.relics.listener;

import me.oblivion.relics.OblivionRelics;
import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.relic.RerollerItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class RerollerListener implements Listener {

    private final RelicManager relicManager;
    private final RerollerItem rerollerItem;
    private final RelicItem relicItem;

    public RerollerListener(
            RelicManager relicManager,
            RerollerItem rerollerItem,
            RelicItem relicItem
    ) {
        this.relicManager = relicManager;
        this.rerollerItem = rerollerItem;
        this.relicItem = relicItem;
    }

    @EventHandler
    public void onUse(
            PlayerInteractEvent event
    ) {

        Action action =
                event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack held =
                event.getItem();

        if (!rerollerItem.isReroller(held)) {
            return;
        }

        event.setCancelled(true);

        Player player =
                event.getPlayer();

        RelicType current =
                relicManager.getRelic(
                        player.getUniqueId()
                );

        if (current == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "You do not have a Relic to reroll."
            );

            return;
        }

        RelicType newRelic =
                getDifferentRelic(current);

        Plugin plugin =
                Bukkit.getPluginManager()
                        .getPlugin("OblivionRelics");

        if (plugin == null) {
            return;
        }

        // Consume one reroller.
        consumeOne(player);

        // Clear old ability cooldowns/state.
        if (plugin instanceof OblivionRelics oblivionRelics) {
            oblivionRelics
                    .getRelicAbilityEngine()
                    .clearPlayerState(
                            player.getUniqueId()
                    );
        }

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + ChatColor.BOLD.toString()
                        + "         RELIC REROLLING"
        );

        player.sendMessage("");

        player.sendMessage(
                ChatColor.GRAY
                        + "Current Relic: "
                        + ChatColor.RED
                        + current.getDisplayName()
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "Searching for a new Relic..."
        );

        player.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        // Reroll animation.
        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                ticks += 2;

                player.getWorld().spawnParticle(
                        Particle.ENCHANT,
                        player.getLocation()
                                .add(0, 1, 0),
                        14,
                        0.45,
                        0.8,
                        0.45,
                        0.04
                );

                if (ticks % 10 == 0) {

                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            0.9f,
                            0.65f
                                    + (ticks * 0.01f)
                    );
                }

                if (ticks >= 50) {

                    // Replace stored Relic.
                    relicManager.setRelic(
                            player.getUniqueId(),
                            newRelic
                    );

                    // Remove every old Relic item.
                    removeRelicItems(player);

                    // Give the new Relic item.
                    ItemStack newItem =
                            relicManager.createRelicItem(
                                    newRelic
                            );

                    if (newItem != null) {

                        player.getInventory()
                                .addItem(newItem);
                    }

                    // Final effect.
                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            player.getLocation()
                                    .add(0, 1, 0),
                            50,
                            0.7,
                            1.0,
                            0.7,
                            0.04
                    );

                    player.getWorld().spawnParticle(
                            Particle.ENCHANT,
                            player.getLocation()
                                    .add(0, 1, 0),
                            60,
                            0.9,
                            1.1,
                            0.9,
                            0.05
                    );

                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            1.5f,
                            1.35f
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            ChatColor.DARK_GRAY
                                    + "━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    player.sendMessage(
                            ChatColor.LIGHT_PURPLE
                                    + ChatColor.BOLD.toString()
                                    + "          RELIC REROLLED"
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            ChatColor.GRAY
                                    + "Previous: "
                                    + ChatColor.RED
                                    + current.getDisplayName()
                    );

                    player.sendMessage(
                            ChatColor.GRAY
                                    + "New Relic: "
                                    + ChatColor.AQUA
                                    + ChatColor.BOLD.toString()
                                    + newRelic.getDisplayName()
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            ChatColor.GRAY
                                    + "Use "
                                    + ChatColor.AQUA
                                    + "/relicinfo"
                                    + ChatColor.GRAY
                                    + " to view its abilities."
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            ChatColor.DARK_GRAY
                                    + "━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    player.sendTitle(
                            ChatColor.AQUA
                                    + ChatColor.BOLD.toString()
                                    + newRelic.getDisplayName(),
                            ChatColor.GRAY
                                    + "Your Relic has changed",
                            10,
                            45,
                            15
                    );

                    cancel();
                }
            }

        }.runTaskTimer(
                plugin,
                0L,
                2L
        );
    }

    private RelicType getDifferentRelic(
            RelicType current
    ) {

        RelicType[] relics =
                RelicType.values();

        RelicType result;

        do {

            result =
                    relics[
                            ThreadLocalRandom.current()
                                    .nextInt(
                                            relics.length
                                    )
                    ];

        } while (result == current);

        return result;
    }

    private void consumeOne(
            Player player
    ) {

        ItemStack item =
                player.getInventory()
                        .getItemInMainHand();

        if (!rerollerItem.isReroller(item)) {
            return;
        }

        if (item.getAmount() <= 1) {

            player.getInventory()
                    .setItemInMainHand(null);

        } else {

            item.setAmount(
                    item.getAmount() - 1
            );

            player.getInventory()
                    .setItemInMainHand(item);
        }
    }

    private void removeRelicItems(
            Player player
    ) {

        for (int slot = 0;
             slot < player.getInventory().getSize();
             slot++) {

            ItemStack item =
                    player.getInventory()
                            .getItem(slot);

            if (relicItem.isRelic(item)) {

                player.getInventory()
                        .setItem(slot, null);
            }
        }
    }
}
