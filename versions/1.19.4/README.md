# GTM Extra for Minecraft 1.19.4

This is a standalone Fabric build of GTM Extra for Minecraft 1.19.4. It keeps the Item Highlighter, grouped count/name labels, configurable color, per-item Glow/Name whitelist, and client command. Labels and glow are hidden behind blocks:

- `/gtmhighlighter true` enables the Item Highlighter.
- `/gtmhighlighter false` disables it.

Install Fabric Loader and Fabric API for Minecraft 1.19.4. ModMenu is optional and adds the settings screen.

Build with `gradlew.bat remapJar`; the distributable JAR is written to `build/libs/`.
