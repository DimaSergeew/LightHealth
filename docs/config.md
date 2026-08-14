# Config

File: `plugins/LightHealth/config.yml`

After edits, run `/lh reload`. New keys from a plugin update are merged in automatically.

## Core

```yaml
language: en          # en | ru | es | zh
style: bar            # bar | hearts | numeric | custom

display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: false

onboarding:
  enabled: true
```

The boss bar is opt-in on new installs to keep ordinary combat uncluttered. Existing
servers keep their current value when upgrading.

`onboarding.enabled` controls the one-time tip shown after a player's first
successful hit. The tip explains look-at and `/lh toggle`; seen state is stored in
`player-toggles.yml`.

| `style` | Effect |
|---------|--------|
| `bar` | `[████░░░░]` colored by remaining health |
| `hearts` | Colored hearts |
| `numeric` | Simple numbers |
| `custom` | Uses `format.*` only |

## Channels

### Hologram

```yaml
hologram:
  only-when-damaged: true  # skips 0-damage hits for hologram only
  hide-after-ticks: 40
  view-distance: 16
  y-offset: 0.35           # extra height above the attachment / head
```

!!! note
    Rideable mobs (horses, pigs, striders, camels, …) and players are **not** mounted. The hologram follows in world space so you can still ride them.

    A killing-blow bar can stay until hide or until the entity is removed.

### Damage numbers

```yaml
damage-numbers:
  duration-ticks: 28
  rise-per-tick: 0.045
  base-scale: 1.15
  crit-scale: 1.5
  env-interval-ticks: 10   # throttle fire / poison / cactus numbers; 0 = off
  tiers:
    - max: 2
      format: "<#B0B0B0>-<amount></#B0B0B0>"
    # … more tiers
  crit:
    enabled: true
    symbol: "✦"
    format: "<gradient:#FFE082:#FF6D00><bold><symbol> <amount></bold></gradient>"
```

The first tier where `amount <= max` wins. Player hits always show; `env-interval-ticks` only throttles environmental damage.

### Action bar / boss bar

```yaml
actionbar:
  duration-ticks: 40

bossbar:
  hide-after-ticks: 70
  min-max-health: 0      # 0 = all mobs
  dynamic-color: true
  high-percent: 50       # above this: green
  mid-percent: 25        # above this: yellow, else red
  overlay: NOTCHED_10
```

These need a **viewer** (the attacker, or the player using look-at).

## Styles & formats

Placeholders (MiniMessage):

`<health>` · `<max>` · `<percent>` · `<amount>` · `<name>` · `<hearts>` · `<bar>` · `<symbol>`

```yaml
styles:
  bar:
    filled: "█"
    empty: "░"
    length: 12
    hologram: "<bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>"
    actionbar: "<white><name></white> <bar> … <red>-<amount></red>"
    bossbar: "<white><name></white>  …  <red>-<amount></red>"
    look-at-actionbar: "<white><name></white> <bar> …"   # no -<amount>
    look-at-bossbar: "<white><name></white>  …"
```

When `style` is `bar`, `hearts`, or `numeric`, the matching `styles.*` templates are used.  
Look-at (zero damage) uses `look-at-actionbar` / `look-at-bossbar`.

## Blacklist

```yaml
blacklist:
  entities:
    - ARMOR_STAND
    - VILLAGER
    # …
  worlds: []
  worlds-as-whitelist: false

players: false   # show on players
```
