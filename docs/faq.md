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

## Holograms lag on farms

```yaml
hologram:
  only-when-damaged: true
  hide-after-ticks: 30
  view-distance: 12
```

Raise `damage-numbers` view-distance carefully; prefer lower.

## Folia

Supported. Scheduling uses entity/global region schedulers.

## Does it rewrite mob names?

No. Holograms use **TextDisplay**, not the entity custom name.

## Java version

Paper **26.x** needs **Java 25+**. Older Java will fail to load Paper API deps.

## Where to report issues

[GitHub Issues](https://github.com/DimaSergeew/LightHealth/issues)
