package com.mira.combat.listener;

import com.mira.combat.MiraCombatPlugin;
import com.mira.combat.service.ItemPolicyService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public final class ItemPolicyListener implements Listener {
    private final MiraCombatPlugin plugin;
    private final ItemPolicyService policy;

    public ItemPolicyListener(MiraCombatPlugin plugin, ItemPolicyService policy) {
        this.plugin = plugin;
        this.policy = policy;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        policy.clear(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem().getItemStack();

        if (!policy.hasBypass(player) && policy.isRestricted(stack)) {
            event.setCancelled(true);
            event.getItem().remove();
            policy.notifyDeleted(player);
            return;
        }

        policy.sanitizeTooltip(stack);
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (!policy.hasBypass(player) && policy.isRestricted(result)) {
            event.setCancelled(true);
            policy.notifyDeleted(player);
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (!policy.isRestricted(result)) return;

        for (HumanEntity viewer : event.getViewers()) {
            if (viewer instanceof Player player && !policy.hasBypass(player)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreative(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || policy.hasBypass(player)) return;

        ItemStack cursor = event.getCursor();
        if (policy.isRestricted(cursor)) {
            event.setCancelled(true);
            event.setCursor(new ItemStack(Material.AIR));
            policy.notifyDeleted(player);
        }

        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeldItemChange(PlayerItemHeldEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUseRestrictedItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (policy.hasBypass(player) || !policy.isRestricted(event.getItem())) return;

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> policy.enforce(player));
    }
}
