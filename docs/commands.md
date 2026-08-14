# Commands & permissions

Aliases: `/lh`, `/lighthealth`, `/mhp`.

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` | — | Show help |
| `/lh toggle` | `lighthealth.toggle` | Turn your personal feedback on or off |
| `/lh status` | — | Show your personal setting, active channels, look-at, and style |
| `/lh reload` | `lighthealth.admin` | Reload config and messages |
| `/lh lang <code>` | `lighthealth.admin` | Set the plugin language |

`/lh language` is an alias of `/lh lang`. Language codes tab-complete.

```text
/lh lang en
/lh lang ru
/lh lang es
/lh lang zh
```

!!! note "What `/lh toggle` actually turns off"
    The preference is saved in `player-toggles.yml` and survives quit and restart.

    - Your action bar, boss bar, and look-at
    - Holograms and damage numbers **you would otherwise see**

    Other players with feedback enabled can still see displays from nearby combat. See [FAQ](faq.md).

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | `true` | See health displays (bars, holograms, numbers, look-at) |
| `lighthealth.toggle` | `true` | Use `/lh toggle` |
| `lighthealth.admin` | `op` | Reload and language |

### LuckPerms

```text
lp group default permission set lighthealth.see true
lp group default permission set lighthealth.toggle true
lp group admin permission set lighthealth.admin true
```
