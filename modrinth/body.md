<p style="text-align:center">
  <img src="https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/banner.png" alt="LightHealth — mob health feedback">
</p>

<p style="text-align:center">
  <strong>A lightweight mob health and damage indicator for Paper, Purpur, and Folia.</strong><br>
  Aim to inspect · Hit to see damage · No renamed mobs
</p>

LightHealth is a focused **mob health plugin and damage indicator**. Aim at a mob
to inspect its HP without attacking, then see a private health bar and floating
damage numbers when combat starts.

Unlike nametag-based health plugins, LightHealth uses per-viewer `TextDisplay`
holograms. Mob names stay untouched, other players are not forced to see your UI,
and there are no dependencies or gameplay changes.

Works on **Paper**, **Purpur**, and **Folia** (**1.21.4+** / Paper **26.x**). Languages: English, Russian, Spanish, Chinese.

| Channel | What you see |
|---------|----------------|
| **Hologram** | A health bar above the mob (`TextDisplay`) |
| **Numbers** | Floating damage, colored by amount, with a distinct crit style |
| **Action / boss bar** | Health and damage, shifting green → yellow → red |
| **Look-at inspect** | Check a mob's HP before choosing to fight |

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
  bossbar: false
```

Styles: `bar` · `hearts` · `numeric` · `custom`.  
Full options: **[documentation](https://dimasergeew.github.io/LightHealth/)**.

## Commands

Aliases: `/lh`, `/lighthealth`, `/mhp`

| Command | Description |
|---------|-------------|
| `/lh toggle` | Personal feedback on or off |
| `/lh status` | Show your active channels, look-at, and style |
| `/lh reload` | Reload config (admin) |
| `/lh lang en` / `ru` / `es` / `zh` | Plugin language (admin) |

## Requirements

| | |
|--|--|
| Server | Paper, Purpur, or Folia **1.21.4+** (Paper **26.x** included) |
| Java | **21+** on 1.21.x · **25+** on 26.x (Minecraft itself requires 25) |
| Dependencies | None |

This is a Paper plugin. It will **not** load on CraftBukkit or Spigot.

## Links

- [Documentation](https://dimasergeew.github.io/LightHealth/)
- [GitHub](https://github.com/DimaSergeew/LightHealth)
- [SpigotMC](https://www.spigotmc.org/resources/lighthealth.137519/)

MIT · no dependencies
