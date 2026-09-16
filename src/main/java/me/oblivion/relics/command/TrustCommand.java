package me.oblivion.relics.command;

import me.oblivion.relics.trust.TrustManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustCommand implements CommandExecutor {

    private final TrustManager trustManager;

    public TrustCommand(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("list")) {
            List<String> trusted = trustManager.getTrustedPlayers(
                    player.getUniqueId()
            );

            player.sendMessage(
                    ChatColor.AQUA + "§lOblivion §8» §7Trusted players:"
            );

            if (trusted.isEmpty()) {
                player.sendMessage(
                        ChatColor.GRAY + "None"
                );
                return true;
            }

            for (String uuidString : trusted) {
                try {
                    Player trustedPlayer = Bukkit.getPlayer(
                            java.util.UUID.fromString(uuidString)
                    );

                    String name = trustedPlayer != null
                            ? trustedPlayer.getName()
                            : uuidString;

                    player.sendMessage(
                            ChatColor.GRAY + "• " + ChatColor.AQUA + name
                    );

                } catch (IllegalArgumentException ignored) {
                    player.sendMessage(
                            ChatColor.GRAY + "• Unknown player"
                    );
                }
            }

            return true;
        }

        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null) {
            player.sendMessage(
                    "§b§lOblivion §8» §cThat player must be online."
            );
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(
                    "§b§lOblivion §8» §cYou cannot trust yourself."
            );
            return true;
        }

        if (action.equals("add")) {

            if (!trustManager.addTrust(
                    player.getUniqueId(),
                    target.getUniqueId()
            )) {
                player.sendMessage(
                        "§b§lOblivion §8» §eThat player is already trusted."
                );
                return true;
            }

            player.sendMessage(
                    "§b§lOblivion §8» §aYou now trust §b"
                            + target.getName() + "§a."
            );

            return true;
        }

        if (action.equals("remove")) {

            if (!trustManager.removeTrust(
                    player.getUniqueId(),
                    target.getUniqueId()
            )) {
                player.sendMessage(
                        "§b§lOblivion §8» §eThat player is not trusted."
                );
                return true;
            }

            player.sendMessage(
                    "§b§lOblivion §8» §cYou removed §b"
                            + target.getName() + " §cfrom your trust list."
            );

            return true;
        }

        sendUsage(player);
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("§b§lOblivion §8» §7Trust commands:");
        player.sendMessage("§f/trust add <player>");
        player.sendMessage("§f/trust remove <player>");
        player.sendMessage("§f/trust list");
    }
}
