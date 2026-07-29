# Config

File: `plugins/LightHealth/config.yml`

## Core

```yaml
language: en          # en | ru | es | zh
style: bar            # bar | hearts | numeric | custom

display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

| `style` | Effect |
|---------|--------|
| `bar` | `[████░░░░] HP` colored by % |
| `hearts` | Colored hearts |
| `numeric` | Simple numbers |
| `custom` | Uses `format.*` only |

## Channels

### Hologram

```yaml
hologram:
  only-when-damaged: true
  hide-after-ticks: 40
  view-distance: 16
  y-offset: 0.35
```

### Damage numbers

```yaml
damage-numbers:
  duration-ticks: 28
  rise-per-tick: 0.045
  base-scale: 1.15
  crit-scale: 1.5
  tiers:
    - max: 2
      format: "<#B0B0B0>-<amount></#B0B0B0>"
    # … more tiers
  crit:
    enabled: true
    symbol: "✦"
    format: "<gradient:#FFE082:#FF6D00><bold><symbol> <amount></bold></gradient>"
```

First tier where `amount <= max` wins.

### Action bar / Boss bar

```yaml
actionbar:
  duration-ticks: 40

bossbar:
  hide-after-ticks: 70
  min-max-health: 0      # 0 = all mobs
  dynamic-color: true
  high-percent: 50       # >50% green
  mid-percent: 25        # >25% yellow, else red
  overlay: NOTCHED_10
```

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
```

When `style: bar` / `hearts` / `numeric`, the matching `styles.*` templates are used.

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

After edits: `/lh reload`.
