package com.mira.combat.api;

import org.bukkit.entity.Player;

public interface MiraCombatApi {
    boolean enabled();
    double attackSpeed();
    int maximumNoDamageTicks();
    void refresh(Player player);
}
