package com.mira.combat;

import com.mira.combat.api.MiraCombatApi;
import com.mira.combat.service.BountyService;
import com.mira.combat.service.CombatProfileService;
import org.bukkit.entity.Player;

import java.util.UUID;

final class MiraCombatApiImpl implements MiraCombatApi {
    private final MiraCombatPlugin plugin;
    private final CombatProfileService profiles;
    private final BountyService bounties;

    MiraCombatApiImpl(MiraCombatPlugin plugin, CombatProfileService profiles, BountyService bounties) {
        this.plugin = plugin;
        this.profiles = profiles;
        this.bounties = bounties;
    }

    @Override public boolean enabled() { return plugin.combatEnabled(); }
    @Override public double attackSpeed() { return plugin.attackSpeed(); }
    @Override public int maximumNoDamageTicks() { return plugin.maximumNoDamageTicks(); }
    @Override public void refresh(Player player) { profiles.refresh(player); }
    @Override public double bounty(UUID player) { return bounties.amount(player); }
    @Override public boolean bountiesAvailable() { return bounties.economyReady(); }
}
