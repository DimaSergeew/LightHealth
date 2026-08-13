<p style="text-align:center">
  <img src="https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/banner.png" alt="LightHealth — mob health feedback">
</p>

<p style="text-align:center">
  <strong>Show mob health and damage — clearly, and nothing else.</strong><br>
  Holograms · floating numbers · action bar · boss bar · look-at
</p>

LightHealth is a small **Paper** plugin with one job: when you hit a mob — or look at one — you see its health and the damage you dealt.

It does **not** rewrite mob names, add extra gameplay, or need other plugins. Styles and languages live in YAML.

Works on **Paper**, **Purpur**, and **Folia** (1.21+ / Paper 26.x). Languages: English, Russian, Spanish, Chinese.

| Channel | What you see |
|---------|----------------|
| **Hologram** | A health bar above the mob (`TextDisplay`) |
| **Numbers** | Floating damage, colored by amount, with a distinct crit style |
| **Action / boss bar** | Health and damage, shifting green → yellow → red |
| **Look-at** | The same feedback while you aim, without dealing damage |

<p style="text-align:center">
  <img src="https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/gallery.png" alt="In-game preview: health bar and damage number above a wither skeleton">
</p>

## Install

1. Download the jar from this page
2. Put it in `plugins/` and restart
3. Hit a mob — or look at one

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

Styles: `bar` · `hearts` · `numeric` · `custom`.  
Full options: **[documentation](https://dimasergeew.github.io/LightHealth/)**.

## Commands

Aliases: `/lh`, `/lighthealth`, `/mhp`

| Command | Description |
|---------|-------------|
| `/lh toggle` | Personal feedback on or off |
| `/lh reload` | Reload config (admin) |
| `/lh lang en` / `ru` / `es` / `zh` | Plugin language (admin) |

## Requirements

| | |
|--|--|
| Server | Paper, Purpur, or Folia **1.21+** (Paper **26.x** included) |
| Java | **25+** |
| Dependencies | None |

This is a Paper plugin. It will **not** load on CraftBukkit or Spigot.

## Links

- [Documentation](https://dimasergeew.github.io/LightHealth/)
- [GitHub](https://github.com/DimaSergeew/LightHealth)
- [SpigotMC](https://www.spigotmc.org/resources/lighthealth.137519/)

MIT · no dependencies
