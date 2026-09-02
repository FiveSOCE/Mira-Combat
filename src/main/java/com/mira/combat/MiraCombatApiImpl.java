package com.mira.combat;

import com.mira.combat.api.MiraCombatApi;
import com.mira.combat.service.CombatProfileService;
import org.bukkit.entity.Player;

final class MiraCombatApiImpl implements MiraCombatApi {
    private final MiraCombatPlugin plugin;
    private final CombatProfileService profiles;

    MiraCombatApiImpl(MiraCombatPlugin plugin, CombatProfileService profiles) {
        this.plugin = plugin;
        this.profiles = profiles;
    }

    @Override
    public boolean enabled() {
        return plugin.combatEnabled();
    }

    @Override
    public double attackSpeed() {
        return plugin.attackSpeed();
    }

    @Override
    public int maximumNoDamageTicks() {
        return plugin.maximumNoDamageTicks();
    }

    @Override
    public void refresh(Player player) {
        profiles.refresh(player);
    }
}
