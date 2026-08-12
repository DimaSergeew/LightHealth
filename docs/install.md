# Install

Get LightHealth running in a minute. No other plugins are required.

## Requirements

| | |
|--|--|
| Server | **Paper**, **Purpur**, or **Folia** **1.21+** (Paper **26.x** included) |
| Java | **25+** |
| Dependencies | None |

!!! warning "Not a Spigot plugin"
    LightHealth uses the Paper API (`paper-plugin.yml`, Brigadier, Folia schedulers). It will **not** load on CraftBukkit or Spigot.

## Steps

1. Download `LightHealth-x.y.z.jar` from [Releases](https://github.com/DimaSergeew/LightHealth/releases/latest), [Modrinth](https://modrinth.com/plugin/lighthealth), or the [SpigotMC listing](https://www.spigotmc.org/resources/lighthealth.137519/).
2. Put the jar into `plugins/`.
3. Start or restart the server.
4. Hit a mob — or look at one.
5. Optionally edit `plugins/LightHealth/config.yml` and run `/lh reload`.

!!! tip
    After an update, missing config keys are merged in automatically. You do not need to delete `config.yml`.

## First run

The default style is **bar**. Holograms, damage numbers, action bar, boss bar, and look-at are all on.

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: true
```

If nothing appears, check [FAQ](faq.md) — usually `/lh toggle` is off, or `lighthealth.see` is missing.

## Build from source

```bash
./gradlew build
# → build/libs/LightHealth-1.0.2.jar
```
