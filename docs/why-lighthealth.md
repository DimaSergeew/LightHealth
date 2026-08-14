# Why LightHealth?

Many mob health plugins put numbers directly into an entity's custom name.
LightHealth takes a different approach: it adds a clear, private combat layer
without changing the mob itself.

## Aim before you fight

Look at a mob to inspect its health without attacking. The raycast respects solid
blocks, and the range and display channels are configurable.

## Keep nametags untouched

Health holograms use per-viewer `TextDisplay` entities instead of custom names.
That means named pets, NPCs, and other nametag-based plugins keep their own text.

## Show only what you need

Holograms, floating damage numbers, the action bar, and the boss bar can be
configured separately. The boss bar is opt-in on new installs to keep ordinary
combat uncluttered.

## Give players control

`/lh toggle` saves a player's choice across restarts. `/lh status` shows the
personal setting, active server channels, look-at state, and current style.

## Run on modern Paper servers

LightHealth targets Paper, Purpur, and Folia 1.21.4+ with Folia-aware scheduling,
no hard dependencies, and Java 21 bytecode.
