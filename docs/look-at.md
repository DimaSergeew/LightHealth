# Look-at

Show health while the player is **aiming at** a mob. No damage is required.

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
| `range` | Maximum look distance, in blocks |
| `interval-ticks` | How often to scan (lower is smoother, more work) |
| `show.*` | Which channels to use on look-at |

Look-at channels are independent of `display.*`. You can show a hologram on look-at even if damage uses a different set of channels.

Zero-damage look-at uses `look-at-actionbar` / `look-at-bossbar` templates, so the bar does not print `-0`. See [Config](config.md).

!!! note "Line of sight"
    The raycast hits **solid blocks**. You cannot read health through walls.

    Looking away hides look-at holograms and bars immediately. A **damage** hologram on the same mob stays until its own hide timer.

## Tips

- Keep `interval-ticks` around `4–8` on busy servers.
- Prefer hologram + action bar; leave the boss bar off unless you need it.
- Look-at respects `lighthealth.see` and `/lh toggle`.
