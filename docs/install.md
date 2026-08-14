# Install

Get LightHealth running in a minute. No other plugins are required.

## Requirements

| | |
|--|--|
| Server | **Paper**, **Purpur**, or **Folia** **1.21.4+** (Paper **26.x** included) |
| Java | **21+** on 1.21.x · **25+** on 26.x (Minecraft itself requires 25) |
| Dependencies | None |

!!! warning "Not a Spigot plugin"
    LightHealth uses the Paper API (`paper-plugin.yml`, Brigadier, Folia schedulers). It will **not** load on CraftBukkit or Spigot.

## Steps

1. Download `LightHealth-x.y.z.jar` from [GitHub Releases](https://github.com/DimaSergeew/LightHealth/releases/latest) or the [SpigotMC listing](https://www.spigotmc.org/resources/lighthealth.137519/).
2. Put the jar into `plugins/`.
3. Start or restart the server.
4. Hit a mob — or look at one.
5. Optionally edit `plugins/LightHealth/config.yml` and run `/lh reload`.

!!! tip
    After an update, missing config keys are merged in automatically. You do not need to delete `config.yml`.

## First run

The default style is **bar**. Holograms, damage numbers, action bar, and look-at
are on. The boss bar is opt-in to keep ordinary combat uncluttered.

```yaml
language: en
style: bar
display:
  hologram: true
  damage-numbers: true
  actionbar: true
  bossbar: false
```

If nothing appears, check [FAQ](faq.md) — usually `/lh toggle` is off, or `lighthealth.see` is missing.

## Build from source

You need **JDK 25** to compile (Paper 26.2 API). The published jar is Java 21 bytecode.

```bash
./gradlew build
# → build/libs/LightHealth-1.1.0.jar
```
