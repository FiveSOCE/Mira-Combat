package com.mira.combat.listener;

import com.mira.combat.MiraCombatPlugin;
import com.mira.combat.service.CombatProfileService;
import com.mira.combat.util.CombatMath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public final class CombatListener implements Listener {
    private static final List<Sound> MODERN_ATTACK_SOUNDS = List.of(
            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
            Sound.ENTITY_PLAYER_ATTACK_STRONG,
            Sound.ENTITY_PLAYER_ATTACK_CRIT,
            Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK,
            Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
            Sound.ENTITY_PLAYER_ATTACK_WEAK
    );

    private final MiraCombatPlugin plugin;
    private final CombatProfileService profiles;

    public CombatListener(MiraCombatPlugin plugin, CombatProfileService profiles) {
        this.plugin = plugin;
        this.profiles = profiles;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        profiles.apply(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        profiles.restore(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> profiles.refresh(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!plugin.combatEnabled()) return;

        if (plugin.disableSweepAttacks() && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && event.getDamager() instanceof Player attacker) {
            if (plugin.suppressModernAttackSounds()) {
                suppressModernAttackSounds(attacker);
                Bukkit.getScheduler().runTask(plugin, () -> suppressModernAttackSounds(attacker));
            }

            if (plugin.resetSprintOnHit() && event.getEntity() instanceof Player) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (attacker.isOnline()) attacker.setSprinting(false);
                });
            }
        }
    }

    private void suppressModernAttackSounds(Player attacker) {
        if (!attacker.isOnline()) return;
        for (Player listener : attacker.getWorld().getPlayers()) {
            if (listener.getLocation().distanceSquared(attacker.getLocation()) > 2304.0D) continue;
            for (Sound sound : MODERN_ATTACK_SOUNDS) {
                listener.stopSound(sound);
            }
        }
    }

    @SuppressWarnings("removal")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKnockback(EntityKnockbackByEntityEvent event) {
        if (!plugin.combatEnabled() || !plugin.knockbackEnabled()) return;
        if (!(event.getEntity() instanceof Player victim) || !(event.getSourceEntity() instanceof Player attacker)) return;

        double horizontal = plugin.knockbackHorizontal();
        double vertical = plugin.knockbackVertical();

        if (attacker.isSprinting()) {
            horizontal += plugin.sprintHorizontalBonus();
            vertical += plugin.sprintVerticalBonus();
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        int knockbackLevel = weapon.getEnchantmentLevel(Enchantment.KNOCKBACK);
        horizontal += knockbackLevel * plugin.enchantmentHorizontalBonus();

        Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector());
        event.setFinalKnockback(CombatMath.knockback(direction, horizontal, vertical, plugin.knockbackVerticalLimit()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.combatEnabled() || !plugin.disableShields()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.SHIELD) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!plugin.combatEnabled() || !(event.getEntity() instanceof EnderPearl pearl)
                || !(pearl.getShooter() instanceof Player player)) return;

        Bukkit.getScheduler().runTask(plugin,
                () -> player.setCooldown(Material.ENDER_PEARL, plugin.enderPearlCooldownTicks()));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!plugin.combatEnabled() || !(event.getHitEntity() instanceof Player victim)) return;
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player attacker) || attacker.equals(victim)) return;

        boolean enabled = projectile instanceof FishHook && plugin.fishingRodKnockback()
                || projectile instanceof Snowball && plugin.snowballKnockback()
                || projectile instanceof Egg && plugin.eggKnockback();
        if (!enabled) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!victim.isOnline() || victim.isDead()) return;
            Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector());
            victim.setVelocity(CombatMath.projectileVelocity(victim.getVelocity(), direction,
                    plugin.projectileHorizontal(), plugin.projectileVertical(), plugin.projectileVerticalLimit()));
        });
    }
}
