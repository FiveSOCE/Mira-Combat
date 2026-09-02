package com.mira.combat.service;

import com.mira.combat.MiraCombatPlugin;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

public final class PvpDummyService {
    private final MiraCombatPlugin plugin;
    private final NamespacedKey dummyKey;

    public PvpDummyService(MiraCombatPlugin plugin) {
        this.plugin = plugin;
        this.dummyKey = new NamespacedKey(plugin, "pvp_dummy");
    }

    public ArmorStand add(Player player) {
        Block target = player.getTargetBlockExact(12);
        if (target == null) return null;

        Location location = target.getLocation().add(0.5D, 1.0D, 0.5D);
        ArmorStand stand = target.getWorld().spawn(location, ArmorStand.class, dummy -> {
            dummy.setCustomName("PvP Dummy");
            dummy.setCustomNameVisible(true);
            dummy.setArms(true);
            dummy.setBasePlate(true);
            dummy.setGravity(false);
            dummy.setVisible(true);
            dummy.setSmall(false);
            dummy.setPersistent(true);
            dummy.setInvulnerable(false);
            dummy.setCanPickupItems(false);
            dummy.setCollidable(true);
            dummy.getPersistentDataContainer().set(dummyKey, PersistentDataType.BYTE, (byte) 1);
            dummy.setRotation(player.getLocation().getYaw() + 180.0F, 0.0F);
        });
        return stand;
    }

    public boolean removeLookedAt(Player player) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                12.0D,
                0.35D,
                this::isDummy
        );
        if (result == null || result.getHitEntity() == null) return false;
        result.getHitEntity().remove();
        return true;
    }

    public boolean isDummy(Entity entity) {
        if (!(entity instanceof ArmorStand)) return false;
        Byte value = entity.getPersistentDataContainer().get(dummyKey, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }
}
