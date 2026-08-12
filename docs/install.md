# Install

## Requirements

| | |
|--|--|
| Server | **Paper / Purpur / Folia** **1.21+** (modern Paper **26.x** supported) |
| Java | **25+** |
| Dependencies | **None** |

## Steps

1. Download `LightHealth-x.y.z.jar` from [Releases](https://github.com/DimaSergeew/LightHealth/releases), [Modrinth](https://modrinth.com/plugin/lighthealth), or [SpigotMC](https://www.spigotmc.org/resources/lighthealth.137519/)
2. Put it into `plugins/`
3. Start the server
4. Optional: edit `plugins/LightHealth/config.yml`
5. `/lh reload`

## Build from source

```bash
./gradlew build
# → build/libs/LightHealth-1.0.2.jar
```

## First run

Default style is **bar**. Action bar + boss bar + look-at are on.

```yaml
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

Hit a mob or look at one — you should see the bar.
