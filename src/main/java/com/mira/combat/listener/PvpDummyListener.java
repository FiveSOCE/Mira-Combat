package com.mira.combat.listener;

import com.mira.combat.service.PvpDummyService;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public final class PvpDummyListener implements Listener {
    private final PvpDummyService dummies;

    public PvpDummyListener(PvpDummyService dummies) {
        this.dummies = dummies;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof ArmorStand stand) || !dummies.isDummy(stand)) return;

        // Do not cancel the hit. Keeping the damage event alive lets MiraItems and other
        // combat mechanics observe repeated hits while zero damage makes the dummy immortal.
        event.setCancelled(false);
        event.setDamage(0.0D);
        stand.setFireTicks(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCombust(EntityCombustEvent event) {
        if (dummies.isDummy(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (dummies.isDummy(event.getRightClicked())) event.setCancelled(true);
    }
}
