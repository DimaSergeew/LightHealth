# LightHealth

**Modern mob health feedback for Paper / Folia**  
Hologram · damage numbers · actionbar · bossbar · look-at

[Documentation](https://dimasergeew.github.io/LightHealth/) · [Modrinth](https://modrinth.com/plugin/lighthealth) · [Releases](https://github.com/DimaSergeew/LightHealth/releases)

---

**One job.** Show mob HP and damage — nothing else.  
Zero hard deps · Folia-ready · `en` `ru` `es` `zh`

| Channel | What you get |
|---------|----------------|
| **Hologram** | Bar above mob (TextDisplay) |
| **Numbers** | Color tiers + crit ✦ |
| **Action / Boss** | Green → yellow → red |
| **Look-at** | HP while aiming |

## Install

1. Drop jar into `plugins/` → restart  
2. Hit a mob (or look at one)  
3. Config: `plugins/LightHealth/config.yml` → `/lh reload`

```yaml
language: en
style: bar
display: { hologram: true, damage-numbers: true, actionbar: true, bossbar: true }
```

## Commands

| | |
|--|--|
| `/lh toggle` | Personal on/off |
| `/lh reload` | Reload (admin) |
| `/lh lang <en\|ru\|es\|zh>` | Language (admin) |

Aliases: `/lighthealth` · `/mhp`

Full reference → **[Wiki](https://dimasergeew.github.io/LightHealth/)**

## Build

```bash
./gradlew build
# build/libs/LightHealth-1.0.0.jar
```

Requires **Java 25+** · Paper **1.21+ / 26.x**

## License

[MIT](LICENSE)
