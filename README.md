<p align="center">
  <img src="assets/banner.png" alt="LightHealth — mob health feedback" width="800">
</p>

<h1 align="center">LightHealth</h1>

<p align="center">
  <b>A lightweight mob health and damage indicator for Paper, Purpur, and Folia.</b><br>
  Aim to inspect · Hit to see damage · No renamed mobs
</p>

<p align="center">
  <a href="https://dimasergeew.github.io/LightHealth/"><img src="https://img.shields.io/badge/docs-wiki-1DB954?style=flat-square&logo=gitbook&logoColor=white" alt="Documentation"></a>
  <a href="https://www.spigotmc.org/resources/lighthealth.137519/"><img src="https://img.shields.io/badge/spigotmc-listing-ED8106?style=flat-square" alt="SpigotMC listing"></a>
  <a href="https://github.com/DimaSergeew/LightHealth/releases/latest"><img src="https://img.shields.io/github/v/release/DimaSergeew/LightHealth?style=flat-square&color=2D6A4F" alt="Latest release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-238636?style=flat-square" alt="MIT License"></a>
</p>

<p align="center">
  <a href="https://dimasergeew.github.io/LightHealth/"><b>Documentation</b></a>
  ·
  <a href="https://www.spigotmc.org/resources/lighthealth.137519/">SpigotMC</a>
  ·
  <a href="https://github.com/DimaSergeew/LightHealth/releases">Releases</a>
</p>

---

LightHealth is a focused **mob health plugin and damage indicator**. Aim at a mob
to inspect its HP without attacking, then see a private health bar and floating
damage numbers when combat starts.

Unlike nametag-based health plugins, LightHealth uses per-viewer `TextDisplay`
holograms. Mob names stay untouched, other players are not forced to see your UI,
and no dependencies or gameplay changes are added.

Works on **Paper**, **Purpur**, and **Folia** (**1.21.4+** / Paper **26.x**). Locales: English, Russian, Spanish, Chinese.

| Channel | What you see |
|---------|----------------|
| **Hologram** | A health bar above the mob (`TextDisplay`) |
| **Numbers** | Floating damage, colored by amount, with a distinct crit style |
| **Action / boss bar** | Health and damage, shifting green → yellow → red |
| **Look-at inspect** | Aim at a mob to check HP before choosing to fight |

<p align="center">
  <img src="assets/gallery.png" alt="In-game preview: health bar and damage number above a wither skeleton" width="720">
</p>

## Features

- **Aim to inspect** — raycast HP feedback without hitting; range and channels configurable
- **Four display channels** — hologram, damage numbers, action bar, and optional boss bar
- **Private by design** — per-viewer displays do not rewrite mob nametags
- **Styles** — `bar`, `hearts`, `numeric`, or `custom` formats with MiniMessage placeholders
- **Locales** — `en`, `ru`, `es`, `zh` out of the box (`/lh lang`)
- **Folia-ready** — no soft-dependencies; Paper API only
- **Player control** — `/lh toggle` persists; `/lh status` explains what is active

## Install

1. Download `LightHealth-x.y.z.jar` from [GitHub Releases](https://github.com/DimaSergeew/LightHealth/releases/latest) or [SpigotMC](https://www.spigotmc.org/resources/lighthealth.137519/).
2. Put the jar in `plugins/` and restart the server.
3. Hit a mob — or look at one. Optionally edit `plugins/LightHealth/config.yml` and run `/lh reload`.

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: false
```

More options (look-at, hologram timing, damage-number tiers, custom formats): **[configuration docs](https://dimasergeew.github.io/LightHealth/config/)**.

## Commands

Aliases: `/lh`, `/lighthealth`, `/mhp`.

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` | — | Show help |
| `/lh toggle` | `lighthealth.toggle` | Turn personal feedback on or off |
| `/lh status` | — | Show personal setting, active channels, look-at, and style |
| `/lh reload` | `lighthealth.admin` | Reload config and messages |
| `/lh lang <en\|ru\|es\|zh>` | `lighthealth.admin` | Set the plugin language |

Full reference: **[commands & permissions](https://dimasergeew.github.io/LightHealth/commands/)**.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | `true` | See health displays (bars, holograms, numbers, look-at) |
| `lighthealth.toggle` | `true` | Use `/lh toggle` |
| `lighthealth.admin` | `op` | Reload and language |

## Requirements

| | |
|--|--|
| Server | Paper, Purpur, or Folia **1.21.4+** (Paper **26.x** included) |
| Java | **21+** on 1.21.x · **25+** on 26.x (Minecraft itself requires 25) |
| Dependencies | None |

This is a Paper plugin. It will not load on CraftBukkit or Spigot.

## Build

You need **JDK 25** to compile (Paper 26.2 API). The published jar is Java 21 bytecode.

```bash
./gradlew build
# → build/libs/LightHealth-1.1.0.jar
```

## License

[MIT](LICENSE) · [bedepay](https://github.com/DimaSergeew)
