# SpigotMC resource text (copy-paste)

Listed on SpigotMC for discovery. Built for the Paper family.

## Short description (line under the resource title)

```text
Show mob health and damage — hologram, numbers, action bar, boss bar, and look-at. No other plugins required.
```

## Overview (BBCode, no images)

Paste this into the **Overview** / Resource Description field in BBCode mode.

```bbcode
[CENTER][SIZE=6][B]LightHealth[/B][/SIZE]
[SIZE=4]Mob health and damage — and nothing else[/SIZE]
[/CENTER]

LightHealth does one job, quietly: when you hit a mob, or simply look at one, you see its [B]health[/B] and the [B]damage you dealt[/B].

It does not rewrite mob names. It needs no other plugins. Styles and languages live in YAML.

[SIZE=4][B]What you see[/B][/SIZE]
[LIST]
[*][B]Hologram[/B] — a health bar above the mob (TextDisplay, names stay untouched)
[*][B]Damage numbers[/B] — colored by how hard you hit, with a distinct crit style
[*][B]Action bar & boss bar[/B] — health and damage, shifting green → yellow → red
[*][B]Look-at[/B] — the same feedback while you aim at a mob, no hit required
[*][B]Styles[/B] — bar, hearts, numeric, or your own template
[*][B]Languages[/B] — English, Russian, Spanish, Chinese
[/LIST]

[SIZE=4][B]Requirements[/B][/SIZE]
[LIST]
[*]Minecraft [B]1.21+[/B] (including 26.x)
[*]Java [B]25+[/B]
[*][B]No[/B] other plugins
[/LIST]

[SIZE=4][B]Install[/B][/SIZE]
[LIST=1]
[*]Drop the jar into [B]plugins/[/B] and restart the server
[*]Hit a mob — or look at one
[*]Optionally edit [B]plugins/LightHealth/config.yml[/B] and run [B]/lh reload[/B]
[/LIST]

[SIZE=4][B]Commands[/B][/SIZE]
[LIST]
[*][B]/lh toggle[/B] — turn personal feedback on or off
[*][B]/lh reload[/B] — reload the config (admin)
[*][B]/lh lang en|ru|es|zh[/B] — plugin language (admin)
[/LIST]
Aliases: [B]/lighthealth[/B], [B]/mhp[/B]

[SIZE=4][B]Links[/B][/SIZE]
[LIST]
[*]Docs: [URL]https://dimasergeew.github.io/LightHealth/[/URL]
[*]GitHub: [URL]https://github.com/DimaSergeew/LightHealth[/URL]
[*]Modrinth: [URL]https://modrinth.com/plugin/lighthealth[/URL]
[/LIST]

[I]Free · MIT · no dependencies[/I]
```
