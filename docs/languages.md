# Languages

Built-in locales:

| Code | Language |
|------|----------|
| `en` | English |
| `ru` | Русский |
| `es` | Español |
| `zh` | 简体中文 |

## Switch language

**Config**

```yaml
language: ru
```

**Command** (admin)

```text
/lh lang ru
```

Files are extracted to:

```text
plugins/LightHealth/lang/
  en.yml
  ru.yml
  es.yml
  zh.yml
```

Missing keys fall back to **English**.

Edit the YAML files to customize messages (MiniMessage supported).
