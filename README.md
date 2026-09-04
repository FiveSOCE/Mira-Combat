# MiraCombat

MiraCombat is the PvP/combat engine for the Mira Paper server suite. It recreates a configurable old-school 1.7-style combat feel on modern Paper, controls restricted modern combat items and world content, and provides combat diagnostics plus persistent PvP test dummies.

## Download

[**Download MiraCombat v0.1.5**](https://github.com/FiveSOCE/Mira-Combat/releases/download/v0.1.5/MiraCombat-0.1.5.jar)

## Requirements / Dependencies

- Paper 1.21.11
- Java 21
- MiraCore 0.2.0 or newer

## How MiraCombat Works

MiraCombat applies a configurable legacy PvP profile to players, including effectively removing the modern attack cooldown, disabling sweep attacks and shield blocking, suppressing modern player attack sounds, resetting sprint after successful melee hits, and applying configurable player and projectile knockback. It also supports configurable hurt-resistance timing, zero/low ender-pearl cooldowns and legacy-style slow satiated regeneration.

The plugin can hide modern Attack Damage/Attack Speed tooltip lines while preserving the actual attributes. Configured restricted modern items are blocked from normal acquisition and continuously removed from non-bypassed player inventories. The default policy includes items such as Tridents, Maces, Shields, Wind Charges, Respawn Anchors, End Crystals, Totems and other modern-content items. Configured modern mobs and structures can also be blocked.

Administrators can place persistent indestructible PvP dummy ArmorStands for combo testing. MiraCombat registers its health with MiraCore and exposes a public `MiraCombatApi` focused strictly on combat-profile behavior. v0.1.5 removes the duplicate built-in bounty subsystem completely; MiraBounties is now the single bounty authority for the suite. Combat tagging is intentionally not implemented here and is reserved for the future standalone MiraCombatTag plugin.

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

Alias: `/miracombat` for `/mcombat`.

## Permissions

| Permission | Default | What it does |
| --- | --- | --- |
| `miracombat.admin` | OP | Allows combat administration, diagnostics and PvP dummy management. |
| `miracombat.restricted-items.bypass` | OP | Allows possession/use of items disabled by MiraCombat's restricted-item policy. |
