# LightHealth

![banner](https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/banner.png)

Modern, lightweight **mob health feedback** for Paper / Folia.

**One job:** show HP and damage — hologram, floating numbers, actionbar, or bossbar.

No hard dependencies. Clean config. Folia-ready.

## Features

- **Hologram** above mobs (TextDisplay — does not rewrite entity names)
- **Damage numbers** with color tiers by damage amount
- **Critical hits** — special symbol, gradient, larger scale
- **Action bar** — bar + HP + damage dealt
- **Boss bar** — progress = mob HP, color green → yellow → red
- **Look-at** — show HP while looking at a mob
- **Styles:** `bar` · `hearts` · `numeric` · `custom`
- **Locales:** English, Russian, Spanish, Chinese (`en` / `ru` / `es` / `zh`)

## Requirements

- **Paper / Purpur / Folia** 1.21+ (modern Paper 26.x)
- **Java 25+**
- **No** hard dependencies

## Install

1. Drop the jar into `plugins/`
2. Start the server
3. Edit `plugins/LightHealth/config.yml` if needed
4. `/lh reload`

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` · `/lighthealth` · `/mhp` | — | Help |
| `/lh toggle` | `lighthealth.toggle` | Personal on/off |
| `/lh reload` | `lighthealth.admin` | Reload config + messages |
| `/lh lang <code>` | `lighthealth.admin` | Language: `en` `ru` `es` `zh` |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | true | See displays |
| `lighthealth.toggle` | true | `/lh toggle` |
| `lighthealth.admin` | op | reload / lang |

## Quick config

```yaml
language: en
style: bar

display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true

look-at:
  enabled: true
  range: 12
  show:
    hologram: true
    actionbar: true
    bossbar: false
```

### Format placeholders

`<health>` `<max>` `<percent>` `<amount>` `<name>` `<hearts>` `<bar>` `<symbol>`

## Links

- [Documentation / Wiki](https://dimasergeew.github.io/LightHealth/)
- [Source (GitHub)](https://github.com/DimaSergeew/LightHealth)
- [Releases](https://github.com/DimaSergeew/LightHealth/releases)

## License

MIT
