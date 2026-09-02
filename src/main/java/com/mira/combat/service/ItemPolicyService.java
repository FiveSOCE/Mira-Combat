package com.mira.combat.service;

import com.mira.combat.MiraCombatPlugin;
import com.mira.core.api.MiraCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemPolicyService {
    public static final String BYPASS_PERMISSION = "miracombat.restricted-items.bypass";

    private final MiraCombatPlugin plugin;
    private final MiraCore core;
    private final Set<String> restrictedMaterialNames = new HashSet<>();
    private final ConcurrentHashMap<UUID, Long> lastWarning = new ConcurrentHashMap<>();

    public ItemPolicyService(MiraCombatPlugin plugin, MiraCore core) {
        this.plugin = plugin;
        this.core = core;
        reload();
    }

    public void reload() {
        restrictedMaterialNames.clear();
        for (String material : plugin.getConfig().getStringList("combat.restricted-items.materials")) {
            if (material == null || material.isBlank()) continue;
            restrictedMaterialNames.add(material.trim().toUpperCase(Locale.ROOT));
        }
    }

    public boolean restrictedItemsEnabled() {
        return plugin.getConfig().getBoolean("combat.restricted-items.enabled", true);
    }

    public boolean hideAttackAttributes() {
        return plugin.getConfig().getBoolean("combat.legacy-tooltips.hide-attack-attributes", true);
    }

    public boolean hasBypass(Player player) {
        return player.hasPermission(BYPASS_PERMISSION);
    }

    public boolean isRestricted(ItemStack item) {
        return item != null && isRestricted(item.getType());
    }

    public boolean isRestricted(Material material) {
        return restrictedItemsEnabled()
                && material != null
                && restrictedMaterialNames.contains(material.name());
    }

    public void sweepAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            enforce(player);
        }
    }

    public void enforce(Player player) {
        boolean removed = false;
        PlayerInventory inventory = player.getInventory();

        ItemStack[] storage = inventory.getStorageContents();
        removed |= processArray(player, storage);
        inventory.setStorageContents(storage);

        ItemStack[] armor = inventory.getArmorContents();
        removed |= processArray(player, armor);
        inventory.setArmorContents(armor);

        ItemStack offHand = inventory.getItemInOffHand();
        if (shouldDelete(player, offHand)) {
            inventory.setItemInOffHand(new ItemStack(Material.AIR));
            removed = true;
        } else {
            sanitizeTooltip(offHand);
        }

        ItemStack cursor = player.getItemOnCursor();
        if (shouldDelete(player, cursor)) {
            player.setItemOnCursor(new ItemStack(Material.AIR));
            removed = true;
        } else {
            sanitizeTooltip(cursor);
        }

        if (removed) notifyDeleted(player);
    }

    public void sanitizeTooltip(ItemStack item) {
        if (!hideAttackAttributes() || item == null || item.getType().isAir() || !isCombatTool(item.getType())) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) return;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public void notifyDeleted(Player player) {
        long now = System.currentTimeMillis();
        Long previous = lastWarning.put(player.getUniqueId(), now);
        if (previous != null && now - previous < 500L) return;

        String message = plugin.getConfig().getString(
                "combat.restricted-items.delete-message",
                "&cThis item is disabled and has been deleted."
        );
        core.messages().send(player, message);
    }

    public void clear(Player player) {
        lastWarning.remove(player.getUniqueId());
    }

    private boolean processArray(Player player, ItemStack[] contents) {
        boolean removed = false;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (shouldDelete(player, item)) {
                contents[i] = null;
                removed = true;
            } else {
                sanitizeTooltip(item);
            }
        }
        return removed;
    }

    private boolean shouldDelete(Player player, ItemStack item) {
        return item != null && !item.getType().isAir() && !hasBypass(player) && isRestricted(item);
    }

    private boolean isCombatTool(Material material) {
        String name = material.name();
        return name.endsWith("_SWORD")
                || name.endsWith("_AXE")
                || name.endsWith("_PICKAXE")
                || name.endsWith("_SHOVEL")
                || name.endsWith("_HOE")
                || name.equals("TRIDENT")
                || name.equals("MACE")
                || name.equals("SPEAR");
    }
}
