package com.mira.combat.service;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class BountyService {
    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration data;
    private final Economy economy;

    public BountyService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "bounties.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        this.economy = provider == null ? null : provider.getProvider();
    }

    public boolean economyReady() { return economy != null; }

    public double amount(UUID target) {
        return Math.max(0.0D, data.getDouble("bounties." + target + ".amount", 0.0D));
    }

    public String lastKnownName(UUID target) {
        return data.getString("bounties." + target + ".name", Bukkit.getOfflinePlayer(target).getName());
    }

    public synchronized boolean place(Player issuer, OfflinePlayer target, double amount) {
        if (economy == null || amount <= 0.0D || issuer.getUniqueId().equals(target.getUniqueId())) return false;
        if (!economy.has(issuer, amount)) return false;
        if (!economy.withdrawPlayer(issuer, amount).transactionSuccess()) return false;
        UUID id = target.getUniqueId();
        String path = "bounties." + id;
        data.set(path + ".amount", amount(id) + amount);
        data.set(path + ".name", target.getName() == null ? id.toString() : target.getName());
        data.set(path + ".last-issuer", issuer.getUniqueId().toString());
        data.set(path + ".updated-at", System.currentTimeMillis());
        save();
        return true;
    }

    public synchronized double claim(Player killer, Player victim) {
        if (economy == null || killer.getUniqueId().equals(victim.getUniqueId())) return 0.0D;
        double value = amount(victim.getUniqueId());
        if (value <= 0.0D) return 0.0D;
        if (!economy.depositPlayer(killer, value).transactionSuccess()) return 0.0D;
        data.set("bounties." + victim.getUniqueId(), null);
        data.set("history." + System.currentTimeMillis() + ".killer", killer.getUniqueId().toString());
        data.set("history." + System.currentTimeMillis() + ".victim", victim.getUniqueId().toString());
        data.set("history." + System.currentTimeMillis() + ".amount", value);
        save();
        return value;
    }

    public synchronized double clear(UUID target) {
        double previous = amount(target);
        data.set("bounties." + target, null);
        save();
        return previous;
    }

    public Map<UUID, Double> top(int limit) {
        Map<UUID, Double> values = new LinkedHashMap<>();
        if (!data.isConfigurationSection("bounties")) return values;
        data.getConfigurationSection("bounties").getKeys(false).stream()
                .map(this::parse)
                .filter(id -> id != null)
                .sorted(Comparator.<UUID>comparingDouble(this::amount).reversed())
                .limit(Math.max(1, limit))
                .forEach(id -> values.put(id, amount(id)));
        return values;
    }

    private UUID parse(String raw) {
        try { return UUID.fromString(raw); } catch (IllegalArgumentException ignored) { return null; }
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to save bounties.yml: " + exception.getMessage());
        }
    }
}
