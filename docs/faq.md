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

    Toggle is **per player** and is saved in `player-toggles.yml`. It turns off:

    - your action bar, boss bar, and look-at
    - holograms and damage numbers **spawned by your hits**

    If another player hits a mob, their hologram is a world `TextDisplay` and may still be visible. Fire, cactus, and other environmental damage can still spawn a hologram with no player viewer.

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

    **Paper, Purpur, and Folia** (1.21+ / Paper 26.x). No hard dependencies.

    This is a Paper plugin (`paper-plugin.yml`, Brigadier, Folia schedulers). It will **not** load on CraftBukkit or Spigot.

??? question "Does Folia work?"

    Yes. Scheduling uses entity and global region schedulers. Floating damage numbers are cleaned up when the entity is removed, so they should not leave orphan `TextDisplay`s.

??? question "Does it rewrite mob names?"

    No. Holograms use a **TextDisplay**, not the entity custom name.

??? question "Which Java version?"

    Minecraft **1.21+ / Paper 26.x** needs **Java 25+**. Older Java will fail to load the plugin.

??? question "Where do I report issues?"

    [GitHub Issues](https://github.com/DimaSergeew/LightHealth/issues)
