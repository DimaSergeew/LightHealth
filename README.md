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

LightHealth is a small Paper plugin with a single purpose: when you hit a mob — or look at one — you see its health and the damage you dealt.

It does not rewrite mob names, add extra gameplay, or pull in other plugins. Styles and languages are configured in YAML.

Works on **Paper**, **Purpur**, and **Folia** (1.21+ / Paper 26.x). Locales: English, Russian, Spanish, Chinese.

| Channel | What you see |
|---------|----------------|
| **Hologram** | A health bar above the mob, rendered as a `TextDisplay` |
| **Numbers** | Floating damage, colored by amount, with a distinct crit style |
| **Action / boss bar** | Health and damage, shifting green → yellow → red |
| **Look-at** | The same feedback while you aim at a mob, without dealing damage |

<p align="center">
  <img src="assets/gallery.png" alt="In-game preview: health bar and damage number above a wither skeleton" width="720">
</p>

## Install

1. Download `LightHealth-x.y.z.jar` from [Releases](https://github.com/DimaSergeew/LightHealth/releases/latest) and put it in `plugins/`.
2. Restart the server, then hit a mob — or look at one.
3. Optionally edit `plugins/LightHealth/config.yml` and run `/lh reload`.

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

## Commands

| Command | Description |
|---------|-------------|
| `/lh toggle` | Turn personal feedback on or off |
| `/lh reload` | Reload the config (admin) |
| `/lh lang <en\|ru\|es\|zh>` | Set the plugin language (admin) |

Aliases: `/lighthealth`, `/mhp`.

Full reference: **[documentation](https://dimasergeew.github.io/LightHealth/)**.

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
