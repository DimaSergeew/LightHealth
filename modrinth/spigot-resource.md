# SpigotMC resource text (copy-paste)

Listed on SpigotMC for discovery. Built for the Paper family.

## Short description (line under the resource title)

```text
Mob health bar & damage indicator with look-at inspect, holograms and numbers. Paper / Purpur / Folia.
```

## Overview (BBCode)

Paste this into the **Overview** / Resource Description field in BBCode mode.

```bbcode
[CENTER][IMG]https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/banner.png[/IMG][/CENTER]

[CENTER][SIZE=5][B]LightHealth[/B][/SIZE]
[SIZE=4]Aim to inspect · Hit to see damage · No renamed mobs[/SIZE]
Lightweight mob health and damage indicator for Paper, Purpur and Folia[/CENTER]

LightHealth is a focused [B]mob health plugin and damage indicator[/B]. Aim at a mob to inspect its HP without attacking, then see a private health bar and floating damage numbers when combat starts.

Unlike nametag-based health plugins, LightHealth uses per-viewer [FONT=courier new]TextDisplay[/FONT] holograms. Mob names stay untouched, other players are not forced to see your UI, and there are no dependencies or gameplay changes.

[CENTER][IMG]https://raw.githubusercontent.com/DimaSergeew/LightHealth/main/assets/gallery.png[/IMG][/CENTER]

[SIZE=4][B]What you see[/B][/SIZE]
[LIST]
[*][B]Hologram[/B] — a health bar above the mob ([FONT=courier new]TextDisplay[/FONT], names stay untouched)
[*][B]Damage numbers[/B] — colored by how hard you hit, with a distinct crit style
[*][B]Action bar & boss bar[/B] — health and damage, shifting green → yellow → red
[*][B]Look-at[/B] — the same feedback while you aim at a mob, no hit required
[*][B]Styles[/B] — bar, hearts, numeric, or your own MiniMessage template
[*][B]Languages[/B] — English, Russian, Spanish, Chinese
[/LIST]

[SIZE=4][B]Requirements[/B][/SIZE]
[LIST]
[*][B]Paper / Purpur / Folia[/B] 1.21.4+ (Paper 26.x included)
[*]Java [B]21+[/B] on 1.21.x · [B]25+[/B] on 26.x (Minecraft itself requires 25)
[*][B]No[/B] other plugins
[/LIST]
[COLOR=#C0392B]This is a Paper plugin. It will not load on CraftBukkit or Spigot.[/COLOR]

[SIZE=4][B]Install[/B][/SIZE]
[LIST=1]
[*]Drop the jar into [B]plugins/[/B] and restart
[*]Hit a mob — or look at one
[*]Optionally edit [B]plugins/LightHealth/config.yml[/B] and run [B]/lh reload[/B]
[/LIST]

[SIZE=4][B]Commands[/B][/SIZE]
[LIST]
[*][B]/lh toggle[/B] — personal feedback on or off
[*][B]/lh status[/B] — show your active channels, look-at, and style
[*][B]/lh reload[/B] — reload the config (admin)
[*][B]/lh lang en|ru|es|zh[/B] — plugin language (admin)
[/LIST]
Aliases: [B]/lighthealth[/B], [B]/mhp[/B]

[SIZE=4][B]Links[/B][/SIZE]
[LIST]
[*]Docs: [URL]https://dimasergeew.github.io/LightHealth/[/URL]
[*]GitHub: [URL]https://github.com/DimaSergeew/LightHealth[/URL]
[*]Download: [URL]https://github.com/DimaSergeew/LightHealth/releases/latest[/URL]
[/LIST]

[I]Free · MIT · no dependencies[/I]
```
