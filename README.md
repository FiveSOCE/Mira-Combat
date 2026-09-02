# MiraCombat

MiraCombat is the combat engine for the Mira Minecraft plugin ecosystem. It targets **Paper 1.21.11** and **Java 21** and requires **MiraCore 0.1.0+**.

## Download

[**Download MiraCombat v0.1.3**](https://github.com/FiveSOCE/Mira-Combat/releases/download/v0.1.3/MiraCombat-0.1.3.jar)

[View the latest GitHub release](https://github.com/FiveSOCE/Mira-Combat/releases/latest)

Current release: **v0.1.3**

## Legacy presentation and restricted items

MiraCombat removes the modern `Attack Damage` and `Attack Speed` attribute lines from combat-capable tool tooltips while preserving the actual combat values.

Modern/restricted items configured by MiraCombat are blocked for normal players. The current default policy includes Trident, Mace, Shield, Spear, Wind Charge, Respawn Anchor, End Crystal, Totem of Undying, Heavy Core, Breeze Rod, Trial Key, Ominous Trial Key, Bundle and Recovery Compass.

Restricted items are blocked from normal acquisition paths and continuously removed if another command or plugin places one directly into a player's inventory.

Players are privately told:

```text
This item is disabled and has been deleted.
```

Administrators can bypass the restriction with:

```text
miracombat.restricted-items.bypass
```

This permission defaults to OP.

The restricted material list and delete message are configurable in `config.yml`.

## Combat foundation

MiraCombat provides a configurable **1.7-style PvP baseline** on a modern Paper server:

- removes the modern weapon attack cooldown by applying a high attack-speed profile
- restores each player's original attack speed when they quit or when the plugin disables
- configurable maximum hurt-resistance/no-damage ticks
- disables sweep attacks
- suppresses the modern player attack sound family for nearby listeners
- disables shield right-click blocking
- resets sprint after successful melee hits to preserve W-tap/sprint-reset gameplay
- configurable player-vs-player knockback
- sprint and Knockback-enchantment knockback bonuses
- fishing rod, snowball and egg PvP knockback
- configurable ender-pearl item cooldown, defaulting to 0 ticks
- legacy-style slow satiated regeneration, defaulting to 1 health every 80 ticks while food is at least 18
- blocks configured modern mobs and structures
- persistent indestructible PvP dummy ArmorStands for combo testing
- MiraCore module health registration
- public `MiraCombatApi`

The tuning is intentionally config-driven. The goal is to reproduce the responsive old-school 1.7 PvP feel on a modern server while keeping exact knockback and timing values easy to tune in-game.

## Commands

```text
/mcombat status
/mcombat test
/mcombat reload
/mcombat apply
/mcombat dummy add
/mcombat dummy remove
/mcombat help
```

All commands require `miracombat.admin` and default to OP.

`/mcombat dummy add` places a persistent PvP Dummy ArmorStand on top of the block the administrator is looking at. `/mcombat dummy remove` removes the Mira PvP Dummy being targeted. Dummy damage is reduced to zero rather than using the vanilla invulnerable flag, allowing normal damage events and combo/item testing to continue while the dummy survives indefinitely.

## Default tuning

```yaml
combat:
  attack-speed: 1024.0
  maximum-no-damage-ticks: 20
  disable-sweep-attacks: true
  disable-shields: true
  reset-sprint-on-hit: true

  legacy-tooltips:
    hide-attack-attributes: true

  restricted-items:
    enabled: true
    delete-message: "&cThis item is disabled and has been deleted."
    materials:
      - TRIDENT
      - MACE
      - SHIELD
      - SPEAR
      - WIND_CHARGE
      - RESPAWN_ANCHOR
      - END_CRYSTAL
      - TOTEM_OF_UNDYING
      - HEAVY_CORE
      - BREEZE_ROD
      - TRIAL_KEY
      - OMINOUS_TRIAL_KEY
      - BUNDLE
      - RECOVERY_COMPASS

  world-policy:
    blocked-mobs:
      - COPPER_GOLEM
      - BREEZE
      - WARDEN
      - HAPPY_GHAST
    blocked-structures:
      - ANCIENT_CITY
      - TRIAL_CHAMBERS
      - TRAIL_RUINS

  knockback:
    horizontal: 0.40
    vertical: 0.40
    vertical-limit: 0.40
    sprint-horizontal-bonus: 0.18
    sprint-vertical-bonus: 0.02
    enchantment-horizontal-bonus-per-level: 0.12

  projectiles:
    fishing-rod-knockback: true
    snowball-knockback: true
    egg-knockback: true
    horizontal: 0.32
    vertical: 0.28

  ender-pearls:
    cooldown-ticks: 0

  regeneration:
    legacy-satiated-regeneration: true
    interval-ticks: 80
    heal-amount: 1.0
    minimum-food-level: 18
```

## Public API

```java
MiraCombatApi api = core.services().require(MiraCombatApi.class);
```

The API currently exposes whether MiraCombat is enabled, the active attack-speed and hurt-resistance profile, and a method to refresh an individual player's combat profile.

## First in-game test pass

1. Install MiraCore 0.1.0 and MiraCombat.
2. Restart Paper 1.21.11.
3. Run `/mcombat test` and confirm all diagnostics pass.
4. Run `/miracore status` and confirm MiraCombat is HEALTHY.
5. Hover a sword or axe and confirm Attack Damage / Attack Speed lines are hidden.
6. Spam-click a sword and confirm there is no modern attack cooldown penalty.
7. Confirm sword sweep damage and modern attack audio are suppressed.
8. Fight another player and judge horizontal/vertical knockback and W-tap behaviour.
9. Hit another player with a fishing rod, snowball and egg.
10. Try to obtain a restricted modern item as a normal player and confirm it is deleted with the warning message.
11. Run `/mcombat dummy add`, repeatedly hit the dummy, then remove it with `/mcombat dummy remove`.
12. Run `/mcombat reload` after tuning `config.yml` and confirm changes apply without restarting.

## Building

```bash
gradle clean test build
```

Output:

```text
build/libs/MiraCombat-0.1.3.jar
```
