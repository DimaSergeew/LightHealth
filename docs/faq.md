# FAQ

## Style / look-at changes do nothing

Reload config: `/lh reload`.  
If the data folder has an **old** `config.yml` without new keys, delete it once (or merge keys) and restart.

## Boss bar / action bar never show

They need a **viewer** — the attacking player (or look-at player). Environmental damage without a player damager won't open personal bars.

Also check:

```yaml
display:
  actionbar: true
  bossbar: true
```

And that you did not run `/lh toggle` off, and have `lighthealth.see`.

## `/lh toggle` still shows holograms from other players

Toggle is **per player**. It turns off:

- your actionbar / bossbar / look-at
- holograms and damage numbers **spawned by your hits**

If another player hits a mob, their hologram is a world `TextDisplay` and may still be visible. Environmental damage (fire, cactus) can still spawn a hologram with no player viewer.

## Holograms lag on farms

```yaml
hologram:
  only-when-damaged: true
  hide-after-ticks: 30
  view-distance: 12
```

Holograms ride the mob (no per-tick teleport). Raise `damage-numbers` view-distance carefully; prefer lower.

## Folia

Supported. Scheduling uses entity/global region schedulers. Floating damage numbers are cleaned up on entity death/remove (no orphan TextDisplays).

## Does it rewrite mob names?

No. Holograms use **TextDisplay**, not the entity custom name.

## Java version

Paper **26.x** needs **Java 25+**. Older Java will fail to load Paper API deps.

## Where to report issues

[GitHub Issues](https://github.com/DimaSergeew/LightHealth/issues)
