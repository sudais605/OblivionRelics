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

public class RelicGiveCommand implements CommandExecutor {

    private final RelicManager relicManager;

    public RelicGiveCommand(
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

        if (args.length < 2) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Usage: /relicgive <player> <relic>"
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "Relics: "
                            + getRelicList()
            );

            return true;
        }

        Player target =
                Bukkit.getPlayerExact(args[0]);

        if (target == null) {

            sender.sendMessage(
                    ChatColor.RED
                            + "That player is not online."
            );

            return true;
        }

        RelicType newRelic;

        try {

            newRelic =
                    RelicType.valueOf(
                            args[1].toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Unknown Relic: "
                            + args[1]
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "Relics: "
                            + getRelicList()
            );

            return true;
        }

        // Remove every old Relic item.
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

        // Save the new Relic.
        relicManager.setRelic(
                target.getUniqueId(),
                newRelic
        );

        // Clear old ability state.
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

        // Give the new Relic item.
        ItemStack relicItem =
                relicManager.createRelicItem(
                        newRelic
                );

        if (relicItem != null) {

            target.getInventory()
                    .addItem(relicItem);
        }

        sender.sendMessage(
                ChatColor.GREEN
                        + "Gave "
                        + newRelic.getDisplayName()
                        + " to "
                        + target.getName()
                        + "."
        );

        target.sendMessage("");

        target.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        target.sendMessage(
                ChatColor.AQUA
                        + ChatColor.BOLD.toString()
                        + "       RELIC UPDATED"
        );

        target.sendMessage("");

        target.sendMessage(
                ChatColor.GRAY
                        + "Your Relic is now "
                        + ChatColor.AQUA
                        + ChatColor.BOLD.toString()
                        + newRelic.getDisplayName()
        );

        target.sendMessage(
                ChatColor.GRAY
                        + "Hold the Relic in your main hand "
                        + "to use its abilities."
        );

        target.sendMessage(
                ChatColor.GRAY
                        + "Use "
                        + ChatColor.AQUA
                        + "/relicinfo"
                        + ChatColor.GRAY
                        + " to view its abilities."
        );

        target.sendMessage(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        return true;
    }

    private String getRelicList() {

        StringBuilder list =
                new StringBuilder();

        for (RelicType relic :
                RelicType.values()) {

            if (list.length() > 0) {
                list.append(", ");
            }

            list.append(
                    relic.name().toLowerCase()
            );
        }

        return list.toString();
    }
}
