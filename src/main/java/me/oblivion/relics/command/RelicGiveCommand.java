package me.oblivion.relics.command;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendMessage(
                    "§b§lOblivion §8» §cYou don't have permission."
            );
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(
                    "§b§lOblivion §8» §7Usage: /relicgive <player> <relic>"
            );
            sender.sendMessage(
                    "§7Example: §f/relicgive Steve RIFT"
            );
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(
                    "§b§lOblivion §8» §cThat player must be online."
            );
            return true;
        }

        RelicType relic;

        try {
            relic = RelicType.valueOf(
                    args[1].toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(
                    "§b§lOblivion §8» §cUnknown Relic."
            );

            sender.sendMessage(
                    "§7Available Relics:"
            );

            for (RelicType type : RelicType.values()) {
                sender.sendMessage(
                        "§8• §b" + type.name()
                );
            }

            return true;
        }

        // Save Relic to player data
        relicManager.setRelic(
                target.getUniqueId(),
                relic
        );

        // Give the actual Relic item
        target.getInventory().addItem(
                relicManager.createRelicItem(relic)
        );

        sender.sendMessage(
                "§b§lOblivion §8» §aGave §b"
                        + relic.getDisplayName()
                        + " §ato §f"
                        + target.getName()
                        + "§a."
        );

        target.sendMessage(
                "§b§lOblivion §8» §aYou received "
                        + relic.getDisplayName()
                        + "§a."
        );

        return true;
    }
}
