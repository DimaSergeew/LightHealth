# Languages

Built-in locales for plugin messages (`/lh` help, reload, toggle, errors).

| Code | Language |
|------|----------|
| `en` | English |
| `ru` | Русский |
| `es` | Español |
| `zh` | 简体中文 |

## Switch language

Set it in `config.yml`:

```yaml
language: ru
```

Or as an admin:

```text
/lh lang ru
```

`/lh lang` writes only the `language:` line, so the rest of your config comments stay intact.

## Customize messages

Files are extracted to:

```text
plugins/LightHealth/lang/
  en.yml
  ru.yml
  es.yml
  zh.yml
```

Edit those YAML files — MiniMessage is supported. Missing keys fall back to **English**.

On startup or `/lh reload`, new keys from a plugin update are merged in without overwriting your custom lines.
