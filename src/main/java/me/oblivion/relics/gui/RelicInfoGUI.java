package me.oblivion.relics.gui;

import me.oblivion.relics.ability.RelicAbilityEngine;
import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RelicInfoGUI implements org.bukkit.command.CommandExecutor, Listener {

    private static final String TITLE = ChatColor.DARK_AQUA + "Oblivion Relics";

    private final RelicManager relicManager;
    private final RelicAbilityEngine abilityEngine;
    private final EnergyManager energyManager;

    public RelicInfoGUI(
            RelicManager relicManager,
            RelicAbilityEngine abilityEngine,
            EnergyManager energyManager
    ) {
        this.relicManager = relicManager;
        this.abilityEngine = abilityEngine;
        this.energyManager = energyManager;
    }

    @Override
    public boolean onCommand(
            org.bukkit.command.CommandSender sender,
            org.bukkit.command.Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        open(player);
        return true;
    }

    public void open(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                TITLE
        );

        RelicType currentRelic = relicManager.getRelic(player.getUniqueId());
        int energy = energyManager.getEnergy(player.getUniqueId());

        RelicType[] relics = RelicType.values();

        for (int i = 0; i < relics.length; i++) {

            RelicType relic = relics[i];

            ItemStack item = new ItemStack(getMaterial(relic));
            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                continue;
            }

            boolean current = relic == currentRelic;

            meta.setDisplayName(
                    (current ? ChatColor.GREEN : ChatColor.AQUA)
                            + ChatColor.BOLD
                            + relic.getDisplayName()
            );

            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━━━━");
            lore.add(ChatColor.GRAY + relic.getDescription());
            lore.add("");
            lore.add(ChatColor.WHITE + "Abilities:");
            lore.add(
                    ChatColor.AQUA + "F "
                            + ChatColor.WHITE
                            + abilityEngine.getAbilityName(relic, 1)
                            + ChatColor.GRAY
                            + " • READY"
            );
            lore.add(
                    ChatColor.AQUA + "SHIFT+F "
                            + ChatColor.WHITE
                            + abilityEngine.getAbilityName(relic, 2)
                            + ChatColor.GRAY
                            + " • 8 Energy"
            );
            lore.add(
                    ChatColor.AQUA + "DOUBLE F "
                            + ChatColor.WHITE
                            + abilityEngine.getAbilityName(relic, 3)
                            + ChatColor.GRAY
                            + " • 10 Energy"
            );
            lore.add("");
            lore.add(
                    ChatColor.GRAY + "Your Energy: "
                            + ChatColor.AQUA
                            + energy
                            + ChatColor.GRAY
                            + "/10"
            );

            if (current) {
                lore.add("");
                lore.add(ChatColor.GREEN + "✔ Your current Relic");
            }

            lore.add(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━━━━");

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            item.setItemMeta(meta);

            inventory.setItem(getSlot(i), item);
        }

        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta infoMeta = info.getItemMeta();

        if (infoMeta != null) {
            infoMeta.setDisplayName(
                    ChatColor.LIGHT_PURPLE
                            + ChatColor.BOLD
                            + "Energy & Controls"
            );

            List<String> infoLore = new ArrayList<>();

            infoLore.add("");
            infoLore.add(ChatColor.GRAY + "Energy:");
            infoLore.add(ChatColor.AQUA + "3 " + ChatColor.GRAY + "starting Energy");
            infoLore.add(ChatColor.AQUA + "8 " + ChatColor.GRAY + "unlocks Ability II");
            infoLore.add(ChatColor.AQUA + "10 " + ChatColor.GRAY + "unlocks Ability III");
            infoLore.add("");
            infoLore.add(ChatColor.GRAY + "Controls:");
            infoLore.add(ChatColor.WHITE + "F " + ChatColor.GRAY + "→ Ability I");
            infoLore.add(ChatColor.WHITE + "SHIFT+F " + ChatColor.GRAY + "→ Ability II");
            infoLore.add(ChatColor.WHITE + "DOUBLE F " + ChatColor.GRAY + "→ Ability III");
            infoLore.add("");
            infoLore.add(
                    ChatColor.GRAY
                            + "Use "
                            + ChatColor.AQUA
                            + "/withdraw"
                            + ChatColor.GRAY
                            + " to withdraw Energy."
            );
            infoLore.add("");

            infoMeta.setLore(infoLore);
            info.setItemMeta(infoMeta);
        }

        inventory.setItem(49, info);

        player.openInventory(inventory);
    }

    private int getSlot(int index) {

        int[] slots = {
                10, 11, 12, 13, 14,
                15, 16, 19, 20, 21,
                22, 23, 24, 25, 28
        };

        return slots[index];
    }

    private Material getMaterial(RelicType relic) {

        return switch (relic) {
            case RIFT -> Material.ECHO_SHARD;
            case GRAVITY -> Material.HEAVY_CORE;
            case VOID -> Material.ENDER_PEARL;
            case STORM -> Material.LIGHTNING_ROD;
            case FROST -> Material.POWDER_SNOW_BUCKET;
            case INFERNO -> Material.BLAZE_POWDER;
            case SHADOW -> Material.INK_SAC;
            case TIME -> Material.CLOCK;
            case PHANTOM -> Material.PHANTOM_MEMBRANE;
            case SOUL -> Material.SOUL_LANTERN;
            case CHAOS -> Material.AMETHYST_SHARD;
            case CELESTIAL -> Material.NETHER_STAR;
            case AEGIS -> Material.SHIELD;
            case FORCE -> Material.PISTON;
            case ARCANE -> Material.ENCHANTED_BOOK;
        };
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals(TITLE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!event.getView().getTitle().equals(TITLE)) {
            return;
        }

        event.setCancelled(true);
    }
}
