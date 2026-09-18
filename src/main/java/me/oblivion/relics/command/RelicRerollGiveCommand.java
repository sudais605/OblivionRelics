package me.oblivion.relics.command;

import me.oblivion.relics.relic.RerollerItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RelicRerollGiveCommand implements CommandExecutor {

    private final RerollerItem rerollerItem;

    public RelicRerollGiveCommand(RerollerItem rerollerItem) {
        this.rerollerItem = rerollerItem;
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

        if (args.length < 1) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Usage: /relicreroll <player>"
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

        target.getInventory().addItem(
                rerollerItem.create()
        );

        sender.sendMessage(
                ChatColor.GREEN
                        + "Gave a Relic Reroller to "
                        + target.getName()
                        + "."
        );

        target.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "You received a Relic Reroller."
        );

        return true;
    }
}
