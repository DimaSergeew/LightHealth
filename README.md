# LightHealth

Modern, lightweight **mob health feedback** for Paper / Folia.

**One job:** show HP and damage — hologram, floating numbers, actionbar, or bossbar.

No hard dependencies. Clean config. Folia-ready.

---

## Features

- **Hologram** above mobs (TextDisplay — does not rewrite entity names)
- **Damage numbers** with color tiers by damage amount
- **Critical hits** — special symbol, gradient, larger scale
- **Action bar** — bar + HP + damage dealt
- **Boss bar** — progress = mob HP, green → yellow → red
- **Look-at** — show HP while looking at a mob
- **Styles:** `bar` · `hearts` · `numeric` · `custom`
- **Locales:** English, Russian, Spanish, Chinese (`en` / `ru` / `es` / `zh`)

---

## Requirements

| | |
|--|--|
| Server | Paper / Purpur / Folia **1.21+** (modern Paper 26.x) |
| Java | **25+** (Paper 26.x) |
| Dependencies | **None** |

---

## Install

1. Drop `LightHealth-x.y.z.jar` into `plugins/`
2. Start the server
3. Edit `plugins/LightHealth/config.yml` if needed
4. `/lh reload`

---

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` · `/lighthealth` · `/mhp` | — | Help |
| `/lh toggle` | `lighthealth.toggle` | Personal on/off |
| `/lh reload` | `lighthealth.admin` | Reload config + messages |
| `/lh lang <code>` | `lighthealth.admin` | Set language (`en` `ru` `es` `zh`) |

---

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | true | See displays |
| `lighthealth.toggle` | true | `/lh toggle` |
| `lighthealth.admin` | op | reload / lang |

---

## Quick config

```yaml
language: en          # en | ru | es | zh
style: bar            # bar | hearts | numeric | custom

display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true

look-at:
  enabled: true
  range: 12
  interval-ticks: 4
  show:
    hologram: true
    actionbar: true
    bossbar: false
```

### Placeholders (MiniMessage formats)

`<health>` `<max>` `<percent>` `<amount>` `<name>` `<hearts>` `<bar>` `<symbol>`

Locales live in `plugins/LightHealth/lang/{en,ru,es,zh}.yml`.

---

## Build

```bash
./gradlew build
```

Output: `build/libs/LightHealth-1.0.0.jar`

---

## License

MIT — see [LICENSE](LICENSE).
