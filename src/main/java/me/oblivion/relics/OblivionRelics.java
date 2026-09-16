package me.oblivion.relics.relic;

import me.oblivion.relics.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

public class RelicManager {

    private final PlayerDataManager playerDataManager;
    private final RelicItem relicItem;
    private final Random random = new Random();

    public RelicManager(
            PlayerDataManager playerDataManager,
            RelicItem relicItem
    ) {
        this.playerDataManager = playerDataManager;
        this.relicItem = relicItem;
    }

    public RelicType getRelic(UUID uuid) {

        String relicName = playerDataManager.getRelic(uuid);

        if (relicName == null || relicName.isBlank()) {
            return null;
        }

        try {
            return RelicType.valueOf(relicName.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public void setRelic(UUID uuid, RelicType relic) {

        if (relic == null) {
            playerDataManager.setRelic(uuid, "");
            return;
        }

        playerDataManager.setRelic(uuid, relic.name());
    }

    public RelicType giveRandomRelic(UUID uuid) {

        RelicType[] relics = RelicType.values();

        RelicType selected =
                relics[random.nextInt(relics.length)];

        setRelic(uuid, selected);

        return selected;
    }

    public ItemStack createRelicItem(UUID uuid) {

        RelicType relic = getRelic(uuid);

        if (relic == null) {
            return null;
        }

        return relicItem.create(relic);
    }

    public ItemStack createRelicItem(RelicType relic) {

        if (relic == null) {
            return null;
        }

        return relicItem.create(relic);
    }

    public boolean isRelicItem(ItemStack item) {
        return relicItem.isRelic(item);
    }

    public RelicType getRelicFromItem(ItemStack item) {
        return relicItem.getRelicType(item);
    }

    public boolean hasRelic(UUID uuid) {
        return getRelic(uuid) != null;
    }

    public boolean hasRelic(Player player) {
        return hasRelic(player.getUniqueId());
    }
}
