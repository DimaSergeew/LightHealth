## LightHealth 1.1.0

This release makes LightHealth clearer for new players and less intrusive on a
fresh install.

### Player experience

- Added a one-time first-hit tip that introduces look-at inspect and `/lh toggle`
- Added `/lh status` to show personal feedback, active channels, look-at, and style
- Disabled the boss bar by default on new installs to reduce visual clutter
- Added localized onboarding and status messages in English, Russian, Spanish, and Chinese

### Project presentation

- Refocused the listing around mob health, damage indicators, and look-at inspect
- Added a clear comparison with nametag-based health plugins
- Updated configuration, command, FAQ, and installation documentation

Existing server configs are not overwritten. To use the quieter default on an
existing server, set `display.bossbar: false` manually.
