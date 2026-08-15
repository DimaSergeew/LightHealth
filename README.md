<p align="center">
  <img src="assets/github-hero.png" alt="LightHealth — See health. Feel every hit." width="960">
</p>

<p align="center">
  <strong>Private, lightweight combat feedback for modern Minecraft servers.</strong><br>
  Inspect a mob before the fight. See every hit when combat begins.
</p>

<p align="center">
  <a href="https://github.com/DimaSergeew/LightHealth/releases/latest"><img src="https://img.shields.io/github/v/release/DimaSergeew/LightHealth?style=for-the-badge&color=8CD950&label=Download" alt="Download latest release"></a>
  <a href="https://dimasergeew.github.io/LightHealth/"><img src="https://img.shields.io/badge/Documentation-1F6F54?style=for-the-badge&logo=gitbook&logoColor=white" alt="Documentation"></a>
  <a href="https://www.spigotmc.org/resources/lighthealth.137519/"><img src="https://img.shields.io/badge/SpigotMC-ED8106?style=for-the-badge&logo=spigotmc&logoColor=white" alt="SpigotMC"></a>
</p>

<p align="center">
  <a href="https://github.com/DimaSergeew/LightHealth/actions/workflows/build.yml"><img src="https://img.shields.io/github/actions/workflow/status/DimaSergeew/LightHealth/build.yml?branch=main&style=flat-square&label=build" alt="Build status"></a>
  <img src="https://img.shields.io/badge/Paper-1.21.4+-white?style=flat-square" alt="Paper 1.21.4+">
  <img src="https://img.shields.io/badge/Purpur-supported-A970FF?style=flat-square" alt="Purpur supported">
  <img src="https://img.shields.io/badge/Folia-ready-4CAF50?style=flat-square" alt="Folia ready">
  <img src="https://img.shields.io/badge/Java-21+-E76F00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21+">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-238636?style=flat-square" alt="MIT License"></a>
</p>

---

## Combat information without the clutter

LightHealth is a focused **mob health plugin and damage indicator** for Paper,
Purpur, and Folia. Aim at a mob to inspect its HP without attacking, then get
clear, responsive feedback as soon as combat starts.

Every display is drawn per viewer with `TextDisplay` entities, so mob names stay
untouched, nobody is forced to see your UI, and no other plugin is required.

<table>
  <tr>
    <td width="33%" align="center">
      <strong>Inspect</strong><br>
      Aim at a mob to see its health before attacking.
    </td>
    <td width="33%" align="center">
      <strong>Fight</strong><br>
      Get holograms, damage numbers, and personal bars.
    </td>
    <td width="33%" align="center">
      <strong>Stay in control</strong><br>
      Use <code>/lh toggle</code> and <code>/lh status</code> at any time.
    </td>
  </tr>
</table>

<p align="center">
  <img src="assets/gallery.png" alt="In-game preview: health bar and damage number above a wither skeleton" width="720">
</p>

## What players see

| Channel | What the player sees | Default |
|---------|----------------------|---------|
| **Hologram** | A health bar floating above the mob | On |
| **Damage numbers** | Rising amounts, colored by how hard you hit, with a distinct crit look | On |
| **Action bar** | Personal health and damage line, shifting green → yellow → red | On |
| **Boss bar** | The same feedback at the top of the screen, for bosses and arenas | Off |
| **Look-at inspect** | A mob's HP while you aim at it, no hit required | On |

## Not a nametag plugin

| | Nametag-based plugins | LightHealth |
|--|--|--|
| Mob names | Rewritten with HP text | Untouched |
| Visibility | Everyone sees the same tag | Per viewer, `/lh toggle` opt-out |
| Damage feedback | Usually none | Floating numbers with crit styling |
| Before the fight | Hit first, then read HP | Aim to inspect |
| Dependencies | Often ProtocolLib or similar | None |

## Pick a style

Set `style` in `config.yml`, or write your own MiniMessage template.

| Style | Example |
|-------|---------|
| `bar` | `[██████████░░] 17/20` |
| `hearts` | `❤❤❤❤❤❤❤❤❤❤ 17/20` (missing hearts dim to gray) |
| `numeric` | `❤ 17/20` |
| `custom` | `<bar> <white><health></white>/<gray><max></gray>` |

Placeholders: `<health>` `<max>` `<percent>` `<amount>` `<name>` `<hearts>` `<bar>` `<symbol>`.

## Get started

1. Download the latest jar from **[GitHub Releases](https://github.com/DimaSergeew/LightHealth/releases/latest)**.
2. Move it into your server's `plugins/` folder.
3. Restart the server, then look at or hit a mob.

<details>
<summary><strong>Default configuration</strong></summary>

<br>

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: false

look-at:
  enabled: true
  range: 12
  show:
    hologram: true
    actionbar: true
    bossbar: false
```

</details>

Damage tiers, timings, view distances, blacklists, and custom formats live in the
**[configuration guide](https://dimasergeew.github.io/LightHealth/config/)**.
Missing keys are merged in automatically after an update, so your `config.yml`
survives upgrades.

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
| Languages | English, Russian, Spanish, Chinese |

This is a Paper plugin. It will not load on CraftBukkit or Spigot.

## Build

You need **JDK 25** to compile (Paper 26.2 API). The published jar is Java 21 bytecode.

```bash
./gradlew build
# → build/libs/LightHealth-1.1.0.jar
```

Every push and pull request runs the same build and unit tests on
[GitHub Actions](https://github.com/DimaSergeew/LightHealth/actions/workflows/build.yml).

## Links

**[Documentation](https://dimasergeew.github.io/LightHealth/)** ·
**[Releases](https://github.com/DimaSergeew/LightHealth/releases)** ·
**[Report an issue](https://github.com/DimaSergeew/LightHealth/issues)** ·
**[SpigotMC](https://www.spigotmc.org/resources/lighthealth.137519/)**

## License

[MIT](LICENSE) · [bedepay](https://github.com/DimaSergeew)
