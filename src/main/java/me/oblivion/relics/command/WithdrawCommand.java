package me.oblivion.relics.command;

import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand implements CommandExecutor {

    private final EnergyManager energyManager;
    private final EchoFlask echoFlask;

    public WithdrawCommand(
            EnergyManager energyManager,
            EchoFlask echoFlask
    ) {
        this.energyManager = energyManager;
        this.echoFlask = echoFlask;
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

        int energy = energyManager.getEnergy(player.getUniqueId());

        if (energy <= 0) {
            player.sendMessage("§b§lOblivion §8» §cYou don't have any Energy to withdraw.");
            return true;
        }

        energyManager.removeEnergy(player.getUniqueId(), 1);

        player.getInventory().addItem(echoFlask.create());

        player.sendMessage(
                "§b§lOblivion §8» §bYou withdrew 1 Energy."
        );

        return true;
    }
}
