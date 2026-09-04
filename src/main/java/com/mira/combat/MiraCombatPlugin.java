package com.mira.combat;

import com.mira.combat.api.MiraCombatApi;
import com.mira.combat.command.MiraCombatCommand;
import com.mira.combat.listener.CombatListener;
import com.mira.combat.listener.ItemPolicyListener;
import com.mira.combat.listener.PvpDummyListener;
import com.mira.combat.listener.RegenerationListener;
import com.mira.combat.listener.WorldPolicyListener;
import com.mira.combat.service.CombatProfileService;
import com.mira.combat.service.ItemPolicyService;
import com.mira.combat.service.PvpDummyService;
import com.mira.combat.service.WorldPolicyService;
import com.mira.combat.util.CombatNumbers;
import com.mira.core.api.MiraCore;
import com.mira.core.api.MiraCoreProvider;
import com.mira.core.api.ModuleHealth;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiraCombatPlugin extends JavaPlugin {
    private MiraCore core;
    private CombatProfileService profiles;
    private ItemPolicyService itemPolicy;
    private WorldPolicyService worldPolicy;
    private PvpDummyService dummies;
    private MiraCombatApi api;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        core = MiraCoreProvider.require();
        profiles = new CombatProfileService(this);
        itemPolicy = new ItemPolicyService(this, core);
        worldPolicy = new WorldPolicyService(this);
        dummies = new PvpDummyService(this);
        api = new MiraCombatApiImpl(this, profiles);

        core.modules().register(this, "MiraCombat");
        core.services().register(MiraCombatApi.class, api);

        getServer().getPluginManager().registerEvents(new CombatListener(this, profiles), this);
        getServer().getPluginManager().registerEvents(new ItemPolicyListener(this, itemPolicy), this);
        getServer().getPluginManager().registerEvents(new WorldPolicyListener(worldPolicy), this);
        getServer().getPluginManager().registerEvents(new PvpDummyListener(dummies), this);

        RegenerationListener regeneration = new RegenerationListener(this);
        getServer().getPluginManager().registerEvents(regeneration, this);
        getServer().getScheduler().runTaskTimer(this, regeneration, 20L, 20L);

        getServer().getScheduler().runTaskTimer(this, itemPolicy::sweepAll, 5L, 5L);

        MiraCombatCommand command = new MiraCombatCommand(this, core, profiles, dummies);
        PluginCommand pluginCommand = getCommand("miracombat");
        if (pluginCommand == null) {
            core.modules().setHealth(this, ModuleHealth.UNHEALTHY, "miracombat command missing from plugin.yml");
            throw new IllegalStateException("miracombat command missing from plugin.yml");
        }
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);

        for (Player player : getServer().getOnlinePlayers()) {
            profiles.apply(player);
            itemPolicy.enforce(player);
        }

        core.modules().setHealth(this, ModuleHealth.HEALTHY,
                "Legacy combat, PvP dummies, item policy and world policy ready");
        getLogger().info("MiraCombat v" + getPluginMeta().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (profiles != null) profiles.restoreAll();
        if (core != null) {
            if (api != null) core.services().unregister(MiraCombatApi.class, api);
            core.modules().unregister(this);
        }
    }

    public void reloadPluginConfiguration() {
        reloadConfig();
        profiles.refreshAll();
        if (itemPolicy != null) {
            itemPolicy.reload();
            itemPolicy.sweepAll();
        }
        if (worldPolicy != null) worldPolicy.reload();
    }

    public boolean combatEnabled() { return getConfig().getBoolean("combat.enabled", true); }
    public double attackSpeed() { return CombatNumbers.clamp(getConfig().getDouble("combat.attack-speed", 1024.0D), 4.0D, 1024.0D); }
    public int maximumNoDamageTicks() { return CombatNumbers.clamp(getConfig().getInt("combat.maximum-no-damage-ticks", 20), 0, 40); }
    public boolean disableSweepAttacks() { return getConfig().getBoolean("combat.disable-sweep-attacks", true); }
    public boolean suppressModernAttackSounds() { return getConfig().getBoolean("combat.suppress-modern-attack-sounds", true); }
    public boolean disableShields() { return getConfig().getBoolean("combat.disable-shields", true); }
    public boolean resetSprintOnHit() { return getConfig().getBoolean("combat.reset-sprint-on-hit", true); }
    public boolean knockbackEnabled() { return getConfig().getBoolean("combat.knockback.enabled", true); }
    public double knockbackHorizontal() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.horizontal", 0.40D), 0.0D, 2.0D); }
    public double knockbackVertical() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.vertical", 0.40D), 0.0D, 2.0D); }
    public double knockbackVerticalLimit() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.vertical-limit", 0.40D), 0.0D, 2.0D); }
    public double sprintHorizontalBonus() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.sprint-horizontal-bonus", 0.18D), 0.0D, 1.0D); }
    public double sprintVerticalBonus() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.sprint-vertical-bonus", 0.02D), 0.0D, 1.0D); }
    public double enchantmentHorizontalBonus() { return CombatNumbers.clamp(getConfig().getDouble("combat.knockback.enchantment-horizontal-bonus-per-level", 0.12D), 0.0D, 1.0D); }
    public boolean fishingRodKnockback() { return getConfig().getBoolean("combat.projectiles.fishing-rod-knockback", true); }
    public boolean snowballKnockback() { return getConfig().getBoolean("combat.projectiles.snowball-knockback", true); }
    public boolean eggKnockback() { return getConfig().getBoolean("combat.projectiles.egg-knockback", true); }
    public double projectileHorizontal() { return CombatNumbers.clamp(getConfig().getDouble("combat.projectiles.horizontal", 0.32D), 0.0D, 2.0D); }
    public double projectileVertical() { return CombatNumbers.clamp(getConfig().getDouble("combat.projectiles.vertical", 0.28D), 0.0D, 2.0D); }
    public double projectileVerticalLimit() { return CombatNumbers.clamp(getConfig().getDouble("combat.projectiles.vertical-limit", 0.38D), 0.0D, 2.0D); }
    public int enderPearlCooldownTicks() { return CombatNumbers.clamp(getConfig().getInt("combat.ender-pearls.cooldown-ticks", 0), 0, 1200); }
    public boolean legacySatiatedRegeneration() { return getConfig().getBoolean("combat.regeneration.legacy-satiated-regeneration", true); }
    public int regenerationIntervalTicks() { return CombatNumbers.clamp(getConfig().getInt("combat.regeneration.interval-ticks", 80), 20, 1200); }
    public double regenerationHealAmount() { return CombatNumbers.clamp(getConfig().getDouble("combat.regeneration.heal-amount", 1.0D), 0.1D, 20.0D); }
    public int regenerationMinimumFood() { return CombatNumbers.clamp(getConfig().getInt("combat.regeneration.minimum-food-level", 18), 0, 20); }
    public double regenerationExhaustion() { return CombatNumbers.clamp(getConfig().getDouble("combat.regeneration.exhaustion-per-heal", 3.0D), 0.0D, 40.0D); }
}
