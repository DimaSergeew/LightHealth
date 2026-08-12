<p align="center">
  <img src="assets/banner.png" alt="LightHealth — mob health feedback" width="800">
</p>

<h1 align="center">LightHealth</h1>

<p align="center">
  Show mob health and damage — clearly, and nothing else.<br>
  Holograms · floating numbers · action bar · boss bar · look-at
</p>

<p align="center">
  <a href="https://dimasergeew.github.io/LightHealth/"><img src="https://img.shields.io/badge/docs-wiki-1DB954?style=flat-square&logo=gitbook&logoColor=white" alt="Documentation"></a>
  <a href="https://modrinth.com/plugin/lighthealth"><img src="https://img.shields.io/badge/modrinth-lighthealth-1BD96A?style=flat-square&logo=modrinth&logoColor=white" alt="Modrinth"></a>
  <a href="https://www.spigotmc.org/resources/lighthealth.137519/"><img src="https://img.shields.io/badge/spigotmc-listing-ED8106?style=flat-square" alt="SpigotMC listing"></a>
  <a href="https://github.com/DimaSergeew/LightHealth/releases/latest"><img src="https://img.shields.io/github/v/release/DimaSergeew/LightHealth?style=flat-square&color=2D6A4F" alt="Latest release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-238636?style=flat-square" alt="MIT License"></a>
</p>

<p align="center">
  <a href="https://dimasergeew.github.io/LightHealth/"><b>Documentation</b></a>
  ·
  <a href="https://modrinth.com/plugin/lighthealth">Modrinth</a>
  ·
  <a href="https://www.spigotmc.org/resources/lighthealth.137519/">SpigotMC</a>
  ·
  <a href="https://github.com/DimaSergeew/LightHealth/releases">Releases</a>
</p>

---

LightHealth is a small Paper plugin with one job: when you hit a mob — or look at one — you see its health and the damage you dealt.

It does **not** rewrite mob names, add extra gameplay, or require other plugins. Styles and languages live in YAML.

Works on **Paper**, **Purpur**, and **Folia** (1.21+ / Paper 26.x). Locales: English, Russian, Spanish, Chinese.

| Channel | What you see |
|---------|----------------|
| **Hologram** | A health bar above the mob (`TextDisplay`) |
| **Numbers** | Floating damage, colored by amount, with a distinct crit style |
| **Action / boss bar** | Health and damage, shifting green → yellow → red |
| **Look-at** | The same feedback while you aim at a mob, without dealing damage |

<p align="center">
  <img src="assets/gallery.png" alt="In-game preview: health bar and damage number above a wither skeleton" width="720">
</p>

## Features

- **Four display channels** — hologram, damage numbers, action bar, boss bar (toggle each in config)
- **Look-at** — raycast HP feedback without hitting; range and channels configurable
- **Styles** — `bar`, `hearts`, `numeric`, or `custom` formats with MiniMessage placeholders
- **Locales** — `en`, `ru`, `es`, `zh` out of the box (`/lh lang`)
- **Folia-ready** — no soft-dependencies; Paper API only
- **Per-player toggle** — `/lh toggle` persists across restarts

## Install

1. Download `LightHealth-x.y.z.jar` from [Releases](https://github.com/DimaSergeew/LightHealth/releases/latest) or [Modrinth](https://modrinth.com/plugin/lighthealth).
2. Put the jar in `plugins/` and restart the server.
3. Hit a mob — or look at one. Optionally edit `plugins/LightHealth/config.yml` and run `/lh reload`.

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

More options (look-at, hologram timing, damage-number tiers, custom formats): **[configuration docs](https://dimasergeew.github.io/LightHealth/config/)**.

## Commands

Aliases: `/lh`, `/lighthealth`, `/mhp`.

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` | — | Show help |
| `/lh toggle` | `lighthealth.toggle` | Turn personal feedback on or off |
| `/lh reload` | `lighthealth.admin` | Reload config and messages |
| `/lh lang <en\|ru\|es\|zh>` | `lighthealth.admin` | Set the plugin language |

Full reference: **[commands & permissions](https://dimasergeew.github.io/LightHealth/commands/)**.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | `true` | See health displays / spawn holograms and numbers from your hits |
| `lighthealth.toggle` | `true` | Use `/lh toggle` |
| `lighthealth.admin` | `op` | Reload and language |

## Requirements

| | |
|--|--|
| Server | Paper, Purpur, or Folia **1.21+** (Paper **26.x** included) |
| Java | **25+** |
| Dependencies | None |

This is a Paper plugin. It will not load on CraftBukkit or Spigot.

## Build

```bash
./gradlew build
# → build/libs/LightHealth-1.0.2.jar
```

## License

[MIT](LICENSE) · [bedepay](https://github.com/DimaSergeew)
