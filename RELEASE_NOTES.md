## LightHealth 1.1.0

This release makes LightHealth clearer for new players, less intrusive on a
fresh install, and stricter about the servers it agrees to run on.

### Player experience

- Added a one-time first-hit tip that introduces look-at inspect and `/lh toggle`
- Added `/lh status` to show personal feedback, active channels, look-at, and style
- Disabled the boss bar by default on new installs to reduce visual clutter
- Added localized onboarding and status messages in English, Russian, Spanish, and Chinese

### Compatibility and reliability

- The jar now refuses to load below 1.21.4 instead of loading and then failing on the first hit
- The first-hit tip no longer writes player data while handling a damage event
- Leaving the server clears leftover holograms and damage numbers
- `/lh reload` re-reads stored `/lh toggle` choices

### Project presentation

- Refocused the listing around mob health, damage indicators, and look-at inspect
- Added a clear comparison with nametag-based health plugins
- Updated configuration, command, FAQ, and installation documentation
- Added a build workflow so every change is compiled and tested before release

Existing server configs are not overwritten. To use the quieter default on an
existing server, set `display.bossbar: false` manually.
