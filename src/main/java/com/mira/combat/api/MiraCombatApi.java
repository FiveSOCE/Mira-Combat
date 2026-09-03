package com.mira.combat.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface MiraCombatApi {
    boolean enabled();
    double attackSpeed();
    int maximumNoDamageTicks();
    void refresh(Player player);
    double bounty(UUID player);
    boolean bountiesAvailable();
}
