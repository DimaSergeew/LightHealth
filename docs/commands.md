# Commands & permissions

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/lh` · `/lighthealth` · `/mhp` | — | Help |
| `/lh toggle` | `lighthealth.toggle` | Personal feedback on/off (actionbar, bossbar, look-at; your hits skip holograms & damage numbers) |
| `/lh reload` | `lighthealth.admin` | Reload config + messages |
| `/lh lang <code>` | `lighthealth.admin` | Set language |

Aliases: `lh`, `mhp`, `lighthealth`, `language`

### Languages

```text
/lh lang en
/lh lang ru
/lh lang es
/lh lang zh
```

Tab-complete is available for language codes.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `lighthealth.see` | `true` | Receive personal feedback and spawn holograms/numbers from your hits |
| `lighthealth.toggle` | `true` | Use `/lh toggle` |
| `lighthealth.admin` | `op` | Reload & language |

### LuckPerms example

```text
lp group default permission set lighthealth.see true
lp group default permission set lighthealth.toggle true
lp group admin permission set lighthealth.admin true
```
