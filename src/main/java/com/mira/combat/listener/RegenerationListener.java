package com.mira.combat.listener;

import com.mira.combat.MiraCombatPlugin;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RegenerationListener implements Listener, Runnable {
    private final MiraCombatPlugin plugin;
    private final Map<UUID, Integer> counters = new HashMap<>();

    public RegenerationListener(MiraCombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent event) {
        if (!plugin.combatEnabled() || !plugin.legacySatiatedRegeneration()) return;
        if (event.getEntity() instanceof Player && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @Override
    public void run() {
        if (!plugin.combatEnabled() || !plugin.legacySatiatedRegeneration()) {
            counters.clear();
            return;
        }

        int interval = plugin.regenerationIntervalTicks();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!eligible(player)) {
                counters.remove(player.getUniqueId());
                continue;
            }

            int elapsed = counters.getOrDefault(player.getUniqueId(), 0) + 20;
            if (elapsed < interval) {
                counters.put(player.getUniqueId(), elapsed);
                continue;
            }
            counters.put(player.getUniqueId(), 0);

            AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttribute == null) continue;
            double maxHealth = maxHealthAttribute.getValue();
            if (player.getHealth() >= maxHealth) continue;

            player.setHealth(Math.min(maxHealth, player.getHealth() + plugin.regenerationHealAmount()));
            player.setExhaustion(Math.min(40.0F, player.getExhaustion() + (float) plugin.regenerationExhaustion()));
        }
    }

    private boolean eligible(Player player) {
        if (player.isDead()) return false;
        GameMode mode = player.getGameMode();
        if (mode != GameMode.SURVIVAL && mode != GameMode.ADVENTURE) return false;
        if (player.getFoodLevel() < plugin.regenerationMinimumFood()) return false;
        Boolean natural = player.getWorld().getGameRuleValue(GameRule.NATURAL_REGENERATION);
        return !Boolean.FALSE.equals(natural);
    }
}
