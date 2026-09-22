# GTM Extra

Fabric mod for Minecraft 1.21.11.

## ModMenu integration

When [ModMenu](https://modrinth.com/mod/modmenu) is installed, select **Configure** next to GTM Extra. This opens the GTM Extra settings screen. It is intentionally ready for future option controls; ModMenu remains an optional runtime dependency.

## Features

- **Sneak Animation**: enabled by default, renders the local player's crouching pose while flying and holding sneak. It changes only the client-side render state, never the player hitbox or network state.
- **Outline Highlighter [Experimental]**: applies a configurable vanilla-style outline only when no non-air block lies between the camera and the player's center. Fences and transparent blocks suppress the outline too. Invisible players are excluded.

Settings are stored locally in `config/gtm-extra.json`.

## Build

Use the Gradle wrapper:

```powershell
.\\gradlew.bat build
```

The remapped mod JAR is written to `build/libs/`.
