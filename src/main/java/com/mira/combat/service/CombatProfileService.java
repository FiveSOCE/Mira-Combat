package com.mira.combat.service;

import com.mira.combat.MiraCombatPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CombatProfileService {
    private final MiraCombatPlugin plugin;
    private final Map<UUID, OriginalState> original = new HashMap<>();

    public CombatProfileService(MiraCombatPlugin plugin) {
        this.plugin = plugin;
    }

    public void apply(Player player) {
        if (!plugin.combatEnabled()) {
            restore(player);
            return;
        }

        AttributeInstance attackSpeed = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attackSpeed == null) return;

        original.computeIfAbsent(player.getUniqueId(), ignored ->
                new OriginalState(attackSpeed.getBaseValue(), player.getMaximumNoDamageTicks()));

        attackSpeed.setBaseValue(plugin.attackSpeed());
        player.setMaximumNoDamageTicks(plugin.maximumNoDamageTicks());
    }

    public void refresh(Player player) {
        OriginalState state = original.get(player.getUniqueId());
        if (state != null) restoreValues(player, state);
        original.remove(player.getUniqueId());
        apply(player);
    }

    public void refreshAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            refresh(player);
        }
    }

    public void restore(Player player) {
        OriginalState state = original.remove(player.getUniqueId());
        if (state != null) restoreValues(player, state);
    }

    public void restoreAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            restore(player);
        }
        original.clear();
    }

    private void restoreValues(Player player, OriginalState state) {
        AttributeInstance attackSpeed = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attackSpeed != null) attackSpeed.setBaseValue(state.attackSpeed());
        player.setMaximumNoDamageTicks(state.maximumNoDamageTicks());
    }

    private record OriginalState(double attackSpeed, int maximumNoDamageTicks) {
    }
}
