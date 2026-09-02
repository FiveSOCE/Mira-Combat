package com.mira.combat.listener;

import com.mira.combat.service.WorldPolicyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.AsyncStructureSpawnEvent;

public final class WorldPolicyListener implements Listener {
    private final WorldPolicyService policy;

    public WorldPolicyListener(WorldPolicyService policy) {
        this.policy = policy;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (policy.isBlockedMob(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStructureSpawn(AsyncStructureSpawnEvent event) {
        if (policy.isBlockedStructure(event.getStructure())) {
            event.setCancelled(true);
        }
    }
}
