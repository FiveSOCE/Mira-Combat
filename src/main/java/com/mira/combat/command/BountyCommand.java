package com.mira.combat.command;

import com.mira.combat.service.BountyService;
import com.mira.core.api.MiraCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BountyCommand implements CommandExecutor, TabCompleter {
    private final MiraCore core;
    private final BountyService bounties;

    public BountyCommand(MiraCore core, BountyService bounties) {
        this.core = core;
        this.bounties = bounties;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                core.messages().send(sender, "&eUse /bounty <player> [amount] or /bounty top");
                return true;
            }
            double current = bounties.amount(player.getUniqueId());
            core.messages().send(player, "&6Your bounty: &a$" + String.format("%,.2f", current));
            return true;
        }
        if (args[0].equalsIgnoreCase("top")) {
            core.messages().send(sender, "&6&lTop Bounties");
            int rank = 1;
            for (Map.Entry<UUID, Double> entry : bounties.top(10).entrySet()) {
                core.messages().send(sender, "&e#" + rank++ + " &f" + bounties.lastKnownName(entry.getKey()) + " &7- &a$" + String.format("%,.2f", entry.getValue()));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("miracombat.bounty.admin")) {
                core.messages().send(sender, "&cYou do not have permission.");
                return true;
            }
            if (args.length < 2) return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double cleared = bounties.clear(target.getUniqueId());
            core.messages().send(sender, "&aCleared &f" + target.getName() + "&a's bounty (&f$" + String.format("%,.2f", cleared) + "&a).");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (args.length == 1) {
            double current = bounties.amount(target.getUniqueId());
            core.messages().send(sender, "&6Bounty on &f" + (target.getName() == null ? args[0] : target.getName()) + "&6: &a$" + String.format("%,.2f", current));
            return true;
        }
        if (!(sender instanceof Player player)) {
            core.messages().send(sender, "&cOnly players can fund bounties.");
            return true;
        }
        if (!player.hasPermission("miracombat.bounty.place")) {
            core.messages().send(player, "&cYou do not have permission to place bounties.");
            return true;
        }
        double amount;
        try { amount = Double.parseDouble(args[1]); }
        catch (NumberFormatException ex) { core.messages().send(player, "&cInvalid amount."); return true; }
        if (amount < 100.0D) {
            core.messages().send(player, "&cMinimum bounty contribution is $100.");
            return true;
        }
        if (!bounties.place(player, target, amount)) {
            core.messages().send(player, "&cCould not place that bounty. Check your balance and target.");
            return true;
        }
        Bukkit.broadcast(core.messages().prefix().append(core.messages().parse("&6[Bounty] &f" + player.getName() + " &7added &a$" + String.format("%,.2f", amount) + " &7to &f" + target.getName() + "&7. Total: &a$" + String.format("%,.2f", bounties.amount(target.getUniqueId())))));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            result.add("top");
            if (sender.hasPermission("miracombat.bounty.admin")) result.add("clear");
            Bukkit.getOnlinePlayers().forEach(p -> result.add(p.getName()));
            return result.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("clear")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}
