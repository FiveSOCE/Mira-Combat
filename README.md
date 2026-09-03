# MiraCombat

MiraCombat is the PvP/combat engine for the Mira Paper server suite. It recreates a configurable old-school 1.7-style combat feel on modern Paper, controls restricted modern combat items and world content, and provides combat diagnostics plus persistent PvP test dummies.

## Download

[**Download MiraCombat v0.1.3**](https://github.com/FiveSOCE/Mira-Combat/releases/download/v0.1.3/MiraCombat-0.1.3.jar)

## Requirements / Dependencies

- Paper 1.21.11
- Java 21
- MiraCore 0.1.0 or newer
- Vault optional for the legacy built-in bounty subsystem

## How MiraCombat Works

MiraCombat applies a configurable legacy PvP profile to players, including effectively removing the modern attack cooldown, disabling sweep attacks and shield blocking, suppressing modern player attack sounds, resetting sprint after successful melee hits, and applying configurable player and projectile knockback. It also supports configurable hurt-resistance timing, zero/low ender-pearl cooldowns and legacy-style slow satiated regeneration.

The plugin can hide modern Attack Damage/Attack Speed tooltip lines while preserving the actual attributes. Configured restricted modern items are blocked from normal acquisition and continuously removed from non-bypassed player inventories. The default policy includes items such as Tridents, Maces, Shields, Wind Charges, Respawn Anchors, End Crystals, Totems and other modern-content items. Configured modern mobs and structures can also be blocked.

Administrators can place persistent indestructible PvP dummy ArmorStands for combo testing. MiraCombat registers its health with MiraCore and exposes a public `MiraCombatApi`. Current source also retains a built-in bounty command surface; the standalone MiraBounties plugin is the dedicated bounty module for the wider suite.

## Commands

| Command | Permission | What it does |
| --- | --- | --- |
| `/mcombat status` | `miracombat.admin` | Shows the active combat profile/runtime state. |
| `/mcombat test` | `miracombat.admin` | Runs MiraCombat diagnostics/self-tests. |
| `/mcombat reload` | `miracombat.admin` | Reloads combat configuration. |
| `/mcombat apply` | `miracombat.admin` | Reapplies the configured combat profile to players. |
| `/mcombat dummy add` | `miracombat.admin` | Places a persistent PvP test dummy at the targeted location. |
| `/mcombat dummy remove` | `miracombat.admin` | Removes the targeted Mira PvP dummy. |
| `/mcombat help` | `miracombat.admin` | Shows MiraCombat command help. |
| `/bounty <player>` | None required for viewing | Shows a player's built-in MiraCombat bounty. |
| `/bounty <player> <amount>` | `miracombat.bounty.place` | Adds/funds a bounty on a player through the built-in bounty subsystem. |
| `/bounty top` | None required for viewing | Shows the built-in bounty leaderboard. |
| `/bounty ...` administrative clear flow | `miracombat.bounty.admin` | Allows administrative clearing/management of built-in bounties. |

Aliases: `/miracombat` for `/mcombat`, `/bounties` for `/bounty`.

## Permissions

| Permission | Default | What it does |
| --- | --- | --- |
| `miracombat.admin` | OP | Allows combat administration, diagnostics and PvP dummy management. |
| `miracombat.restricted-items.bypass` | OP | Allows possession/use of items disabled by MiraCombat's restricted-item policy. |
| `miracombat.bounty.place` | Everyone | Allows funding built-in MiraCombat bounties. |
| `miracombat.bounty.admin` | OP | Allows administrative management/clearing of built-in bounties. |
