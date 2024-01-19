<div align="center">
  <img src="https://raw.githubusercontent.com/SmylerMC/terramap/master/images/terramap_logo-x256.png" width=200>
  <h1>Terramap</h1>
  
## A dynamic world map within Minecraft
  
![Build status](https://img.shields.io/github/actions/workflow/status/SmylerMC/terramap/gradle.yml?style=flat-square)
![Discord link](https://img.shields.io/discord/713848917111996416?color=485eea&label=Discord&style=flat-square)
![CF Downloads](https://cf.way2muchnoise.eu/full_terramap_CurseForge%20downloads.svg?badge_style=flat)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/terramap?color=5ac96b&label=Modrinth+Downloads&style=flat-square&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8%2BCjxz%0AdmcKICAgdmlld0JveD0iMCAwIDE0MS43MDUwNiAxNDEuNzY1NjQiCiAgIGFyaWEtaGlkZGVuPSJ0%0AcnVlIgogICBjbGFzcz0idGV4dC1sb2dvIgogICB2ZXJzaW9uPSIxLjEiCiAgIGlkPSJzdmcyNiIK%0AICAgd2lkdGg9IjE0MS43MDUwNiIKICAgaGVpZ2h0PSIxNDEuNzY1NjQiCiAgIHhtbG5zPSJodHRw%0AOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c3ZnPSJodHRwOi8vd3d3LnczLm9yZy8y%0AMDAwL3N2ZyI%2BCiAgPGRlZnMKICAgICBpZD0iZGVmczMwIiAvPgogIDxnCiAgICAgaWQ9Imc2Igog%0AICAgIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAsMC4wMzk4NzIpIgogICAgIHN0eWxlPSJmaWxsOiM0%0AZWFhNWM7ZmlsbC1vcGFjaXR5OjEiPgogICAgPHBhdGgKICAgICAgIGQ9Ik0gMTU5LjA3LDg5LjI5%0AIEEgNzAuOTQsNzAuOTQgMCAxIDAgMjAsNjMuNTIgSCAzMiBBIDU4Ljc4LDU4Ljc4IDAgMCAxIDE0%0ANS4yMyw0OS45MyBsIC0xMS42NiwzLjEyIGEgNDYuNTQsNDYuNTQgMCAwIDAgLTI5LC0yNi41MiBs%0AIC0yLjE1LDEyLjEzIGEgMzQuMzEsMzQuMzEgMCAwIDEgMi43Nyw2My4yNiBsIDMuMTksMTEuOSBh%0AIDQ2LjUyLDQ2LjUyIDAgMCAwIDI4LjMzLC00OSBsIDExLjYyLC0zLjEgQSA1Ny45NCw1Ny45NCAw%0AIDAgMSAxNDcuMjcsODUgWiIKICAgICAgIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0xOS43OSkiCiAg%0AICAgICBmaWxsPSJ2YXIoLS1jb2xvci1icmFuZCkiCiAgICAgICBmaWxsLXJ1bGU9ImV2ZW5vZGQi%0ACiAgICAgICBpZD0icGF0aDIiCiAgICAgICBzdHlsZT0iZmlsbDojNGVhYTVjO2ZpbGwtb3BhY2l0%0AeToxIiAvPgogICAgPHBhdGgKICAgICAgIGQ9Ik0gMTA4LjkyLDEzOS4zIEEgNzAuOTMsNzAuOTMg%0AMCAwIDEgMTkuNzksNzYgaCAxMiBhIDU5LjQ4LDU5LjQ4IDAgMCAwIDEuNzgsOS45MSA1OC43Myw1%0AOC43MyAwIDAgMCAzLjYzLDkuOTEgbCAxMC42OCwtNi40MSBhIDQ2LjU4LDQ2LjU4IDAgMCAxIDQ0%0ALjcyLC02NSBMIDkwLjQzLDM2LjU0IEEgMzQuMzgsMzQuMzggMCAwIDAgNTcuMzYsNzkuNzUgQyA1%0ANy42Nyw4MC44OCA1OCw4MiA1OC40Myw4MyBMIDcyLjA5LDc0LjgxIDY4LDYzLjkzIDgwLjksNTAu%0ANjggOTcuMjEsNDcuMTcgMTAxLjksNTMgbCAtNy41Miw3LjYxIC02LjU1LDIuMDYgLTQuNjksNC44%0AMiAyLjMsNi4zOCBjIDAsMCA0LjY0LDQuOTQgNC42NSw0Ljk0IGwgNi41NywtMS43NCA0LjY3LC01%0ALjEzIDEwLjIsLTMuMjQgMyw2Ljg0IEwgMTA0LjA1LDg4LjQzIDg2LjQxLDk0IDc4LjQ5LDg1LjE5%0AIDY0LjcsOTMuNDggYSAzNC40NCwzNC40NCAwIDAgMCAyOC43MiwxMS41OSBMIDk2LjYxLDExNyBB%0AIDQ2LjYsNDYuNiAwIDAgMSA1NC4xMyw5OS44MyBsIC0xMC42NCw2LjM4IGEgNTguODEsNTguODEg%0AMCAwIDAgOTkuNiwtOS43NyBsIDExLjgsNC4yOSBhIDcwLjc3LDcwLjc3IDAgMCAxIC00NS45Nywz%0AOC41NyB6IgogICAgICAgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTE5Ljc5KSIKICAgICAgIGZpbGw9%0AInZhcigtLWNvbG9yLWJyYW5kKSIKICAgICAgIGlkPSJwYXRoNCIKICAgICAgIHN0eWxlPSJmaWxs%0AOiM0ZWFhNWM7ZmlsbC1vcGFjaXR5OjEiIC8%2BCiAgPC9nPgogIDxnCiAgICAgaWQ9ImcyNCIKICAg%0AICB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLDAuMDM5ODcyKSIgLz4KPC9zdmc%2BCg%3D%3D)
</div>

Terramap is an addon to the [Terraplusplus](https://www.curseforge.com/minecraft/mc-mods/terraplusplus) Minecraft mod. It renders real world maps inside of the game with extended features. It is still in beta and lacks intended features. 

## Useful links:
- [Terramap Discord server](https://discord.gg/zSMq3GN "Terramap Discord"): Report bugs, make suggestions and **get access to in-development builds**.
- [Terramap CurseForge page](https://www.curseforge.com/minecraft/mc-mods/terramap): Download the mod from there.
- [Terramap Modrinth page](https://modrinth.com/mod/terramap): An other way to download the mod.
- [Terramap wiki](https://github.com/SmylerMC/terramap/wiki): Advanced users can learn about more technical features there.
- [Terramap Trello](https://trello.com/b/pXex8eui/terramap): Discover what's being planed and worked on.

## Main Features:
- Display world maps directly in Minecraft, in a dedicated screen and as a minimal.
- Display a compass indicating the real geographic north directly on the in game HUD (rather than the Minecraft north)
- Multi layer maps: maps can display multiple sources on top of each other. Layer types include online raster tiled map, Minecraft regions and Terraplusplus generation preview.
- Easy world-wide teleportation through the maps' right-click menu or CTRL+click.
- Display player positions on the map, server or network-wide
- Quick access to technical information about a place, including distortion or region coordinates.
- Quick access to other map services. The default provider is OpenStreetMap.
- Configurable map providers, with server to client config synchronization.

## Planned features
- Vector maps
- Geocoding
- Support for more modding framework (Forge, Fabric, Bukkit)
- Support for more modern Minecraft versions

## Installation
Make sure you have [Minecraft Forge 1.12.2](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html) installed, as well as the necessary mod dependencies: [Terraplusplus](https://www.curseforge.com/minecraft/mc-mods/terraplusplus), [CubicChunks](https://www.curseforge.com/minecraft/mc-mods/opencubicchunks), and [CubicWorldGen](https://www.curseforge.com/minecraft/mc-mods/cubicworldgen). Installing a Forge mod is as simple as dropping the mod file in the mods folder in your Minecraft installation directory (I will not go into the details here, there are plenty of great guides online).
Alternatively, Terramap comes included in the [Build The Earth](https://buildtheearth.net/) modpack installer.

## Screenshots
Terramap on a solo world, with the vanilla osm style:
![terramap screenshot solo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap.png)

Custom map styles on BTE-France:
![terramap screenshot styles](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/custom_map_styles.png)

## How to use it:
In game, press m to open the full-screen map (by default, but it can be changed in the config)
Drag it, zoom in and out, and explore the world just like with any other digital map.
Right-click somwhere to get a menu with various useful tools (see the screenshots).
Additionally, you can: press p to toggle debug mod, press control to enable quick tp mode, change the map style in the map style menu, change entity display preferences, follow an entity or player by double clicking it, configure the minimap in the mod's config, add your own map styles, and more...
Checkout [the wiki](https://github.com/SmylerMC/terramap/wiki) for more technical information.

Terramap will work best if it is installed both on the server and the client. This is because it needs to synchronize the map projection used by the server to the client. It is still usable if installed only on the client, as long as you know and take the time to manually set the projection used.

## Contributing to the project:
In case you want to contribute, please make sure you join the Discord. You will get a contributor role there.

### Code
Future updates are already planned ahead, so please contact me before contributing code so we can discuss the changes you want to make first.
In any case, please fork this repository and create a pull request from your fork. You can clone your fork to your computer and work from your IDE there. Make sure you follow the [Minecraft Forge instructions to setup the development environment](https://github.com/MinecraftForge/Documentation/blob/1.12.x/docs/gettingstarted/index.md). 

### Translation
Contribution for translations are welcomed at any time. Please note however that the mod will still change quite a lot, and your translation will probably need to be updated to reflect theese changes. You don't have to stay available to update it personally, but your work may end up being modified by someone else if needs to be. If you are not comfortable using Github you can send me your translation file on the Discord or ask me anything there.
