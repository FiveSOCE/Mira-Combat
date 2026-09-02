package com.mira.combat.command;

import com.mira.combat.MiraCombatPlugin;
import com.mira.combat.service.CombatProfileService;
import com.mira.core.api.MiraCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class MiraCombatCommand implements TabExecutor {
    private final MiraCombatPlugin plugin;
    private final MiraCore core;
    private final CombatProfileService profiles;

    public MiraCombatCommand(MiraCombatPlugin plugin, MiraCore core, CombatProfileService profiles) {
        this.plugin = plugin;
        this.core = core;
        this.profiles = profiles;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("miracombat.admin")) {
            core.messages().send(sender, "&cYou do not have permission to administer MiraCombat.");
            return true;
        }

        String action = args.length == 0 ? "status" : args[0].toLowerCase(Locale.ROOT);
        switch (action) {
            case "status" -> status(sender);
            case "test" -> test(sender);
            case "reload" -> {
                plugin.reloadPluginConfiguration();
                core.messages().send(sender, "&aMiraCombat configuration reloaded and online players refreshed.");
            }
            case "apply" -> {
                if (!(sender instanceof Player player)) {
                    core.messages().send(sender, "&cThis command must be run by a player.");
                    return true;
                }
                profiles.refresh(player);
                core.messages().send(sender, "&aReapplied your MiraCombat profile.");
            }
            case "help" -> help(sender);
            default -> help(sender);
        }
        return true;
    }

    private void status(CommandSender sender) {
        core.messages().send(sender, "&dMiraCombat v" + plugin.getPluginMeta().getVersion());
        core.messages().send(sender, "&7Enabled: " + yes(plugin.combatEnabled())
                + " &7Attack speed: &f" + plugin.attackSpeed()
                + " &7Max hurt ticks: &f" + plugin.maximumNoDamageTicks());
        core.messages().send(sender, "&7Sweep disabled: " + yes(plugin.disableSweepAttacks())
                + " &7Shields disabled: " + yes(plugin.disableShields())
                + " &7Sprint reset: " + yes(plugin.resetSprintOnHit()));
        core.messages().send(sender, "&7KB H/V: &f" + plugin.knockbackHorizontal() + "/" + plugin.knockbackVertical()
                + " &7Rod/Snowball/Egg: " + yes(plugin.fishingRodKnockback()) + "/"
                + yes(plugin.snowballKnockback()) + "/" + yes(plugin.eggKnockback()));
        core.messages().send(sender, "&7Pearl cooldown: &f" + plugin.enderPearlCooldownTicks() + " ticks"
                + " &7Legacy regen: " + yes(plugin.legacySatiatedRegeneration()));
    }

    private void test(CommandSender sender) {
        int passed = 0;
        if (plugin.isEnabled()) passed++;
        if (plugin.attackSpeed() >= 4.0D) passed++;
        if (plugin.maximumNoDamageTicks() >= 0) passed++;
        if (plugin.knockbackHorizontal() >= 0.0D && plugin.knockbackVertical() >= 0.0D) passed++;
        if (plugin.projectileHorizontal() >= 0.0D && plugin.projectileVertical() >= 0.0D) passed++;
        if (plugin.enderPearlCooldownTicks() >= 0) passed++;
        if (plugin.regenerationIntervalTicks() >= 20) passed++;

        if (passed == 7) core.messages().send(sender, "&aMiraCombat Self-Test: 7/7 passed.");
        else core.messages().send(sender, "&cMiraCombat Self-Test: " + passed + "/7 passed.");
    }

    private void help(CommandSender sender) {
        core.messages().send(sender, "&dMiraCombat &fcommands");
        core.messages().send(sender, "&f/mcombat status &7- Show active combat tuning");
        core.messages().send(sender, "&f/mcombat test &7- Run runtime diagnostics");
        core.messages().send(sender, "&f/mcombat reload &7- Reload tuning and refresh online players");
        core.messages().send(sender, "&f/mcombat apply &7- Reapply your combat profile");
    }

    private static String yes(boolean value) {
        return value ? "&aON" : "&cOFF";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("miracombat.admin") || args.length != 1) return List.of();
        String prefix = args[0].toLowerCase(Locale.ROOT);
        return List.of("status", "test", "reload", "apply", "help").stream()
                .filter(value -> value.startsWith(prefix)).toList();
    }
}
