# MiraCombat

## Version-specific JAR downloads

| Paper version | Java | Filename | Download |
| --- | --- | --- | --- |
| **1.18.2** | 17 | `MiraCombat_1.18.2.jar` | [Download legacy JAR](https://github.com/FiveSOCE/Mira-Combat/releases/download/dev-1.18.2/MiraCombat_1.18.2.jar) |
| **1.21.11–26.2** | 21 baseline; newest server may require Java 25 | `MiraCombat_1.21.11-26.2.jar` | [Download modern JAR](https://github.com/FiveSOCE/Mira-Combat/releases/download/compat-modern-1.21.11-26.2/MiraCombat_1.21.11-26.2.jar) |

Both named JAR assets are verified as published after successful CI builds. **Live PvP regression tests and Paper 1.19–1.21.10 verification are still outstanding.** Do not install both variants.

[Legacy source](https://github.com/FiveSOCE/Mira-Combat/tree/compat/1.18.2) · [Modern source](https://github.com/FiveSOCE/Mira-Combat) · [All releases](https://github.com/FiveSOCE/Mira-Combat/releases)

MiraCombat provides configurable old-school Factions combat: no modern attack cooldown, sprint-reset/knockback tuning, projectile and pearl controls, restricted modern items, and protected testing dummies.

## Dependencies and permissions

- **Required:** Matching Paper/Java runtime and version-matched **MiraCore**.
- **Not required:** Vault, LuckPerms, EssentialsX or MiraFactions.
- `miracombat.admin` (OP): Admin diagnostics, apply/reload combat settings and manage PvP dummies.
- `miracombat.restricted-items.bypass` (OP): Bypass configured restricted-item policy.

## Commands

| Command | Permission | Purpose |
| --- | --- | --- |
| `/mcombat status`, `/mcombat test` | `miracombat.admin` | View status and run diagnostics |
| `/mcombat reload`, `/mcombat apply` | `miracombat.admin` | Reload and apply combat configuration |
| `/mcombat dummy add`, `/mcombat dummy remove` | `miracombat.admin` | Manage protected PvP training dummies |
| `/mcombat help` | `miracombat.admin` | Show administration help |

Alias: `/miracombat`. Back up `plugins/MiraCombat/` before switching versions.

## Paper 1.18.2 implementation notes

The 1.18.2 API lacks modern `EntityKnockbackByEntityEvent` and `AsyncStructureSpawnEvent`. This legacy variant applies custom melee knockback on the next server tick following uncancelled damage. This can differ from the newer immediate knockback event, so PvP knockback needs gameplay verification. Mob spawn restriction still uses `CreatureSpawnEvent`, but **structure-generation restriction is not implemented on the legacy target**; do not rely on the `blocked-structures` setting to prevent terrain structures in 1.18.2. All combat configuration, player item restrictions and training dummies remain compiled into this variant.

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
