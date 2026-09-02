package com.mira.combat.service;

import com.mira.combat.MiraCombatPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class WorldPolicyService {
    private final MiraCombatPlugin plugin;
    private volatile Set<String> blockedMobNames = Set.of();
    private volatile Set<String> blockedStructureNames = Set.of();

    public WorldPolicyService(MiraCombatPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        blockedMobNames = normalize(plugin.getConfig().getStringList("combat.world-policy.blocked-mobs"));
        blockedStructureNames = normalize(plugin.getConfig().getStringList("combat.world-policy.blocked-structures"));
    }

    public boolean isBlockedMob(EntityType type) {
        return type != null && blockedMobNames.contains(type.name());
    }

    @SuppressWarnings("deprecation")
    public boolean isBlockedStructure(Structure structure) {
        if (structure == null) return false;
        String key = structure.getKey().getKey().toUpperCase(Locale.ROOT);
        return blockedStructureNames.contains(key);
    }

    public Set<String> blockedMobNames() {
        return blockedMobNames;
    }

    public Set<String> blockedStructureNames() {
        return blockedStructureNames;
    }

    private static Set<String> normalize(Iterable<String> values) {
        Set<String> normalized = new HashSet<>();
        for (String value : values) {
            if (value == null || value.isBlank()) continue;
            normalized.add(value.trim().toUpperCase(Locale.ROOT)
                    .replace('-', '_')
                    .replace(' ', '_'));
        }
        return Set.copyOf(normalized);
    }
}
