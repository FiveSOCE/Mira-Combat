package com.mira.combat.listener;

import com.mira.combat.service.BountyService;
import com.mira.core.api.MiraCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

public final class BountyListener implements Listener {
    private final MiraCore core;
    private final BountyService bounties;

    public BountyListener(MiraCore core, BountyService bounties) {
        this.core = core;
        this.bounties = bounties;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) return;
        double paid = bounties.claim(killer, victim);
        if (paid <= 0.0D) return;
        String money = String.format("%,.2f", paid);
        Bukkit.broadcast(core.messages().prefix().append(core.messages().parse("&6[Bounty] &f" + killer.getName() + " &7claimed &a$" + money + " &7for eliminating &f" + victim.getName() + "&7!")));
        core.milestones().award(killer.getUniqueId(), "miracombat.bounty_claim", "MiraCombat", Map.of("victim", victim.getName(), "amount", Double.toString(paid)));
    }
}
