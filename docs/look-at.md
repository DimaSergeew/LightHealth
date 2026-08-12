# Look-at

Show HP while the player is **looking at** a mob (raycast). No damage required.

## Config

```yaml
look-at:
  enabled: true
  range: 12
  interval-ticks: 4
  show:
    hologram: true
    actionbar: true
    bossbar: false
```

| Key | Meaning |
|-----|---------|
| `enabled` | Master switch |
| `range` | Max look distance (blocks) |
| `interval-ticks` | How often to scan (lower = smoother, more work) |
| `show.*` | Which channels to use on look-at |

Look-at channels are independent of `display.*` for damage — you can show a hologram on look-at even if you use different damage channels.

The raycast hits **blocks**. You cannot read HP through walls. Looking away hides look-at holograms / bars immediately (a damage hologram on the same mob is left until its own hide timer).

## Tips

- Keep `interval-ticks` around `4–8` for busy servers
- Prefer `hologram` + `actionbar`; leave bossbar off unless needed
- Respects `lighthealth.see` and `/lh toggle`
