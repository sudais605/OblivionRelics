package me.oblivion.relics.command;

import me.oblivion.relics.OblivionRelics;
import me.oblivion.relics.relic.RelicManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RelicRemoveCommand
        implements CommandExecutor {

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

        if (!relicManager.hasRelic(target)) {

            sender.sendMessage(
                    ChatColor.RED
                            + target.getName()
                            + " does not have a Relic."
            );

            return true;
        }

        var relic =
                relicManager.getRelic(
                        target.getUniqueId()
                );

        String oldRelic =
                relic.getDisplayName();

        relicManager.setRelic(
                target.getUniqueId(),
                null
        );

        if (sender instanceof Player) {

            OblivionRelics plugin =
                    OblivionRelics.getPlugin(
                            OblivionRelics.class
                    );

            if (plugin != null) {

                plugin.getRelicAbilityEngine()
                        .clearPlayerState(
                                target.getUniqueId()
                        );
            }
        }

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
                        + oldRelic
                        + " from "
                        + target.getName()
                        + "."
        );

        target.sendMessage(
                ChatColor.RED
                        + "Your Relic has been removed."
        );

        return true;
    }
}
