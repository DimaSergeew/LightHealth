# FAQ

??? question "Style or look-at changes do nothing"

    Run `/lh reload`.

    On startup and reload, missing keys from newer plugin versions are merged into `config.yml` without overwriting your values.

??? question "The boss bar or action bar never shows"

    They need a **viewer** — the attacking player, or the player looking at the mob. Environmental damage with no player damager will not open personal bars.

    Also check:

    ```yaml
    display:
      actionbar: true
      bossbar: true
    ```

    And that `/lh toggle` is on, and the player has `lighthealth.see`.

??? question "`/lh toggle` still shows holograms from other players"

    Toggle is **per observer** and is saved in `player-toggles.yml`. It turns off **your** action bar, boss bar, look-at, holograms, and damage numbers.

    Holograms and floating numbers are `TextDisplay`s hidden by default. They are shown only to players who have toggle on and `lighthealth.see`. Other players' hits can still create a display, but you will not see it while toggled off.

    Fire, cactus, and other environmental damage can still spawn a display for nearby players who have feedback enabled.

??? question "Holograms lag on farms"

    ```yaml
    hologram:
      only-when-damaged: true
      hide-after-ticks: 30
      view-distance: 12
    ```

    Ordinary mobs carry the hologram as a passenger (no per-tick teleport). Rideable mobs and players are followed in world space instead, so you can still mount them.

    Keep `damage-numbers` view-distance modest on large farms. Environmental numbers are throttled by `env-interval-ticks` (default `10`).

??? question "Which servers are supported?"

    **Paper, Purpur, and Folia** (**1.21.4+** / Paper **26.x**). No hard dependencies.

    Paper **1.21.0 / 1.21.1** is not supported.

    This is a Paper plugin (`paper-plugin.yml`, Brigadier, Folia schedulers). It will **not** load on CraftBukkit or Spigot.

??? question "Does Folia work?"

    Yes. Entity work uses the entity scheduler; delayed action/boss bars run on the viewer's scheduler. `TextDisplay`s are removed on the display's own scheduler so they should not leak across regions.

??? question "Does it rewrite mob names?"

    No. Holograms use a **TextDisplay**, not the entity custom name.

??? question "Which Java version?"

    The plugin jar is **Java 21** bytecode.

    | Server | Java to run |
    |--------|-------------|
    | Paper **1.21.4+** (before 26.x) | **21+** |
    | Paper **26.x** | **25+** (required by Minecraft, not only this plugin) |

??? question "Where do I report issues?"

    [GitHub Issues](https://github.com/DimaSergeew/LightHealth/issues)
