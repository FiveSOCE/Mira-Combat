# MiraCombat

MiraCombat is the combat engine for the Mira Minecraft plugin ecosystem. It targets **Paper 1.21.11** and **Java 21** and requires **MiraCore 0.1.0+**.

## Download

[**Download MiraCombat v0.1.0**](https://github.com/FiveSOCE/Mira-Combat/releases/download/v0.1.0/MiraCombat-0.1.0.jar)

Current release: **v0.1.0**

## v0.1.0 foundation

MiraCombat provides a configurable old-school PvP baseline on a modern Paper server:

- removes the modern weapon attack cooldown by applying a high attack-speed profile
- restores each player's original attack speed when they quit or when the plugin disables
- configurable maximum hurt-resistance/no-damage ticks
- disables sweep attacks
- disables shield right-click blocking
- resets sprint after successful melee hits to preserve W-tap/sprint-reset gameplay
- configurable player-vs-player knockback
- sprint and Knockback-enchantment knockback bonuses
- fishing rod, snowball and egg PvP knockback
- configurable ender-pearl item cooldown, defaulting to 0 ticks
- legacy-style slow satiated regeneration, defaulting to 1 health every 80 ticks while food is at least 18
- MiraCore module health registration
- public `MiraCombatApi`

The tuning is intentionally config-driven. The goal is to get the server mechanically close to the 1.8 PvP feel, then tune exact knockback and timing values through in-game testing rather than hardcoding one profile forever.

## Commands

```text
/mcombat status
/mcombat test
/mcombat reload
/mcombat apply
/mcombat help
```

All commands require `miracombat.admin` and default to OP.

## Default tuning

```yaml
combat:
  attack-speed: 1024.0
  maximum-no-damage-ticks: 20
  disable-sweep-attacks: true
  disable-shields: true
  reset-sprint-on-hit: true

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
3. Run `/mcombat test` and expect `7/7 passed`.
4. Run `/miracore status` and confirm MiraCombat is HEALTHY.
5. Spam-click a sword and confirm there is no modern attack cooldown penalty.
6. Confirm sword sweep damage is disabled.
7. Confirm shields cannot be used to block.
8. Fight another player and judge horizontal/vertical knockback and W-tap behaviour.
9. Hit another player with a fishing rod, snowball and egg.
10. Throw consecutive ender pearls and confirm the configured cooldown.
11. With hunger at 18+, confirm natural healing uses the slower legacy cadence.
12. Run `/mcombat reload` after tuning `config.yml` and confirm changes apply without restarting.

## Not yet claimed as exact 1.8 parity

v0.1.0 is the combat foundation. We still need real player-vs-player testing before locking the final knockback profile, and later passes can add deeper compatibility such as legacy armor/damage formula tuning, potion-strength differences, sword-block simulation if practical on modern clients, and any factions-specific combat rules we decide to support.

## Building

```bash
gradle clean test build
```

Output:

```text
build/libs/MiraCombat-0.1.0.jar
```
