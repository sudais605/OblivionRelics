package me.oblivion.relics.listener;

import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.relic.RerollerItem;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
    public void onUse(PlayerInteractEvent event) {

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();

        if (!rerollerItem.isReroller(item)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

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

        RelicType newRelic = getDifferentRelic(current);

        // Save the new Relic
        relicManager.setRelic(
                player.getUniqueId(),
                newRelic
        );

        // Remove old Relic item(s)
        removeRelicItems(player);

        // Give new Relic item
        ItemStack newItem =
                relicManager.createRelicItem(newRelic);

        if (newItem != null) {
            player.getInventory().addItem(newItem);
        }

        // Consume one reroller
        if (item.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItemInMainHand(item);
        }

        // Visual effects
        player.getWorld().spawnParticle(
                Particle.ENCHANT,
                player.getLocation().add(0, 1, 0),
                45,
                0.7,
                1.0,
                0.7,
                0.05
        );

        player.getWorld().playSound(
                player.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                1.4f,
                1.3f
        );

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + ChatColor.BOLD.toString()
                        + "        RELIC REROLLED"
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
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        player.sendTitle(
                ChatColor.LIGHT_PURPLE
                        + ChatColor.BOLD.toString()
                        + newRelic.getDisplayName(),
                ChatColor.GRAY + "Your Relic has been rerolled",
                10,
                45,
                15
        );
    }

    private RelicType getDifferentRelic(RelicType current) {

        RelicType[] relics =
                RelicType.values();

        RelicType result;

        do {
            result = relics[
                    ThreadLocalRandom.current()
                            .nextInt(relics.length)
            ];
        } while (result == current);

        return result;
    }

    private void removeRelicItems(Player player) {

        for (int slot = 0;
             slot < player.getInventory().getSize();
             slot++) {

            ItemStack stack =
                    player.getInventory().getItem(slot);

            if (stack == null) {
                continue;
            }

            if (relicItem.isRelic(stack)) {
                player.getInventory()
                        .setItem(slot, null);
            }
        }
    }
}
