# GTM Extra

Fabric mod for Minecraft 1.21.11.

## ModMenu integration

When [ModMenu](https://modrinth.com/mod/modmenu) is installed, select **Configure** next to GTM Extra. This opens the GTM Extra settings screen, where Item Highlighter can be toggled and its color adjusted. ModMenu remains an optional runtime dependency.

## Features

- **Item Highlighter**: highlights selected dropped items and shows their grouped stack count and name when no block obstructs the view. Diamond swords are recognized only when their custom name is `Katana` or `Golf Club`. The whitelist settings let you toggle Glow and Name separately for each supported item. Toggle the global feature in the settings screen or with `/gtmhighlighter true` and `/gtmhighlighter false`.

A standalone Minecraft 1.19.4 project is available in `versions/1.19.4`.

Settings are stored locally in `config/gtm-extra.json`.

## Build

Use the Gradle wrapper:

```powershell
.\\gradlew.bat build
```

The remapped mod JAR is written to `build/libs/`.
