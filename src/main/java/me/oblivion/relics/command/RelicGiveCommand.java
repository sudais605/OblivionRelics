package me.oblivion.relics.command;

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

    public RelicGiveCommand(RelicManager relicManager) {
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
            sender.sendMessage(ChatColor.RED + "Only OP players can use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /relicgive <player> <relic>");
            sender.sendMessage(ChatColor.GRAY + "Available: " + formatRelics());
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        RelicType relic;
        try {
            relic = RelicType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + "Unknown Relic: " + args[1]);
            sender.sendMessage(ChatColor.GRAY + "Available: " + formatRelics());
            return true;
        }

        // Remove any previous Relic items so the player always has one clean Relic item.
        for (int slot = 0; slot < target.getInventory().getSize(); slot++) {
            ItemStack item = target.getInventory().getItem(slot);
            if (relicManager.isRelicItem(item)) {
                target.getInventory().setItem(slot, null);
            }
        }

        relicManager.setRelic(
                target.getUniqueId(),
                relic
        );

        ItemStack item = relicManager.createRelicItem(relic);
        if (item != null) {
            target.getInventory().addItem(item);
        }

        sender.sendMessage(
                ChatColor.GREEN + "Gave "
                        + ChatColor.AQUA + relic.getDisplayName()
                        + ChatColor.GREEN + " to "
                        + target.getName() + "."
        );

        target.sendMessage(
                ChatColor.AQUA + "Your Relic is now "
                        + ChatColor.BOLD + relic.getDisplayName() + ChatColor.RESET
                        + ChatColor.AQUA + "."
        );

        return true;
    }

    private String formatRelics() {
        StringBuilder builder = new StringBuilder();

        for (RelicType relic : RelicType.values()) {
            if (builder.length() > 0) {
                builder.append(ChatColor.GRAY).append(", ");
            }
            builder.append(ChatColor.AQUA).append(relic.name());
        }

        return builder.toString();
    }
}
