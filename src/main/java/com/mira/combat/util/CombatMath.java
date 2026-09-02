package com.mira.combat.util;

import org.bukkit.util.Vector;

public final class CombatMath {
    private CombatMath() {
    }

    public static Vector knockback(Vector direction, double horizontal, double vertical, double verticalLimit) {
        Vector flat = direction.clone().setY(0.0D);
        if (flat.lengthSquared() < 1.0E-8D) {
            flat = new Vector(0.0D, 0.0D, 1.0D);
        } else {
            flat.normalize();
        }

        double cappedVertical = Math.min(Math.max(0.0D, verticalLimit), Math.max(0.0D, vertical));
        return new Vector(flat.getX() * Math.max(0.0D, horizontal), cappedVertical,
                flat.getZ() * Math.max(0.0D, horizontal));
    }

    public static Vector projectileVelocity(Vector current, Vector direction, double horizontal,
                                            double vertical, double verticalLimit) {
        Vector base = current.clone().multiply(0.5D);
        Vector impulse = knockback(direction, horizontal, vertical, verticalLimit);
        base.setX(base.getX() + impulse.getX());
        base.setZ(base.getZ() + impulse.getZ());
        base.setY(Math.min(Math.max(0.0D, verticalLimit), base.getY() + impulse.getY()));
        return base;
    }
}
