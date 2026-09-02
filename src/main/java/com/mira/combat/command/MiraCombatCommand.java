package com.mira.combat.command;

import com.mira.combat.MiraCombatPlugin;
import com.mira.combat.service.CombatProfileService;
import com.mira.combat.service.PvpDummyService;
import com.mira.core.api.MiraCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class MiraCombatCommand implements TabExecutor {
    private final MiraCombatPlugin plugin;
    private final MiraCore core;
    private final CombatProfileService profiles;
    private final PvpDummyService dummies;

    public MiraCombatCommand(MiraCombatPlugin plugin, MiraCore core, CombatProfileService profiles, PvpDummyService dummies) {
        this.plugin = plugin;
        this.core = core;
        this.profiles = profiles;
        this.dummies = dummies;
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
            case "dummy" -> dummy(sender, args);
            case "help" -> help(sender);
            default -> help(sender);
        }
        return true;
    }

    private void dummy(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            core.messages().send(sender, "&cDummy commands must be run by a player.");
            return;
        }
        if (args.length < 2) {
            core.messages().send(sender, "&f/mcombat dummy add &7- Place a PvP dummy on the block you are looking at");
            core.messages().send(sender, "&f/mcombat dummy remove &7- Remove the PvP dummy you are looking at");
            return;
        }

        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add" -> {
                ArmorStand dummy = dummies.add(player);
                if (dummy == null) {
                    core.messages().send(sender, "&cLook at a block within 12 blocks, then try again.");
                } else {
                    core.messages().send(sender, "&aPvP Dummy placed. &7It is persistent and cannot take damage.");
                }
            }
            case "remove" -> {
                if (dummies.removeLookedAt(player)) {
                    core.messages().send(sender, "&aPvP Dummy removed.");
                } else {
                    core.messages().send(sender, "&cLook directly at a PvP Dummy within 12 blocks, then try again.");
                }
            }
            default -> core.messages().send(sender, "&cUse /mcombat dummy <add|remove>.");
        }
    }

    private void status(CommandSender sender) {
        core.messages().send(sender, "&dMiraCombat v" + plugin.getPluginMeta().getVersion());
        core.messages().send(sender, "&7Enabled: " + yes(plugin.combatEnabled())
                + " &7Attack speed: &f" + plugin.attackSpeed()
                + " &7Max hurt ticks: &f" + plugin.maximumNoDamageTicks());
        core.messages().send(sender, "&7Sweep disabled: " + yes(plugin.disableSweepAttacks())
                + " &7Modern attack sounds: " + yes(!plugin.suppressModernAttackSounds())
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
        core.messages().send(sender, "&f/mcombat dummy add &7- Place an indestructible PvP dummy");
        core.messages().send(sender, "&f/mcombat dummy remove &7- Remove the dummy you are looking at");
    }

    private static String yes(boolean value) {
        return value ? "&aON" : "&cOFF";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("miracombat.admin")) return List.of();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return List.of("status", "test", "reload", "apply", "dummy", "help").stream()
                    .filter(value -> value.startsWith(prefix)).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("dummy")) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            return List.of("add", "remove").stream().filter(value -> value.startsWith(prefix)).toList();
        }
        return List.of();
    }
}
