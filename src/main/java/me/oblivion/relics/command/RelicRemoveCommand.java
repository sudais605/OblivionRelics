package me.oblivion.relics.command;

import me.oblivion.relics.OblivionRelics;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RelicRemoveCommand implements CommandExecutor {

    private final RelicManager relicManager;

    public RelicRemoveCommand(
            RelicManager relicManager
    ) {
        this.relicManager = relicManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.isOp()) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Only OP players can use this command."
            );

            return true;
        }

        Player target;

        if (args.length == 0) {

            if (!(sender instanceof Player player)) {

                sender.sendMessage(
                        ChatColor.RED
                                + "Usage: /relicremove <player>"
                );

                return true;
            }

            target = player;

        } else {

            target =
                    Bukkit.getPlayerExact(
                            args[0]
                    );

            if (target == null) {

                sender.sendMessage(
                        ChatColor.RED
                                + "That player is not online."
                );

                return true;
            }
        }

        RelicType currentRelic =
                relicManager.getRelic(
                        target.getUniqueId()
                );

        if (currentRelic == null) {

            sender.sendMessage(
                    ChatColor.RED
                            + target.getName()
                            + " does not have a Relic."
            );

            return true;
        }

        // Remove stored Relic.
        relicManager.setRelic(
                target.getUniqueId(),
                null
        );

        // Clear all active ability state.
        OblivionRelics plugin =
                OblivionRelics.getPlugin(
                        OblivionRelics.class
                );

        if (plugin != null
                && plugin.getRelicAbilityEngine() != null) {

            plugin.getRelicAbilityEngine()
                    .clearPlayerState(
                            target.getUniqueId()
                    );
        }

        // Remove every Relic item from inventory.
        for (int slot = 0;
             slot < target.getInventory().getSize();
             slot++) {

            ItemStack item =
                    target.getInventory()
                            .getItem(slot);

            if (item != null
                    && relicManager.isRelicItem(item)) {

                target.getInventory()
                        .setItem(slot, null);
            }
        }

        sender.sendMessage(
                ChatColor.GREEN
                        + "Removed "
                        + currentRelic.getDisplayName()
                        + " from "
                        + target.getName()
                        + "."
        );

        target.sendMessage("");

        target.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        target.sendMessage(
                ChatColor.RED
                        + ChatColor.BOLD.toString()
                        + "        RELIC REMOVED"
        );

        target.sendMessage("");

        target.sendMessage(
                ChatColor.GRAY
                        + "Your "
                        + ChatColor.AQUA
                        + currentRelic.getDisplayName()
                        + ChatColor.GRAY
                        + " has been removed."
        );

        target.sendMessage(
                ChatColor.GRAY
                        + "All Relic abilities are now disabled."
        );

        target.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        return true;
    }
}
