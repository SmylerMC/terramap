![Terramap logo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/terramap_logo-x256.png)

# Terramap
![Build status](https://img.shields.io/github/workflow/status/SmylerMC/terramap/Java%20CI%20with%20Gradle?style=flat-square) ![Discord link](https://img.shields.io/discord/713848917111996416?color=485eea&label=Discord&style=flat-square) ![Line count](https://img.shields.io/tokei/lines/github/SmylerMC/terramap?style=flat-square) ![Code size](https://img.shields.io/github/languages/code-size/SmylerMC/terramap?style=flat-square)

Terramap is an addon to the Terraplusplus Minecraft mod. It renders real world maps inside of the game with extended features to make full use of them. It is still in beta and lacks intended features.

## Useful links:
- [Terramap Discord server](https://discord.gg/zSMq3GN "Terramap Discord"): report bugs, make suggestions and get access to in-development builds.
- [Terramap CurseForge page](https://www.curseforge.com/minecraft/mc-mods/terramap): download the mod from there.
- [Terramap wiki](https://github.com/SmylerMC/terramap/wiki): Advanced users can learn more about more technical features there.
- [Terramap Trello](https://trello.com/b/pXex8eui/terramap): Discover what's being planed and worked on.

## Features:
- Fully fledged full-screen world map
- Minimap and compass with an intuituve configuration UI
- Teleport easily anywhere in the world using the maps
- Displays players on the maps, no matter how far away they are from you
- Quickly access technical information about a place from the map, including distortion or region coordinates
- Quickly open locations in other map services
- Supports customized map endpoints, and can synchronize them from server to clients

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

## Contributing:
In case you want to contribute, please make sure you join the Discord. You will get a contributor role there.

### Code
Future updates are already planned ahead, so please contact me before contributing code so we can discuss the changes you want to make first.
In any case, please fork this repository and create a pull request from your fork. You can clone your fork to your computer and work from your IDE there. Make sure you follow the [Minecraft Forge instructions to setup the development environment](https://github.com/MinecraftForge/Documentation/blob/1.12.x/docs/gettingstarted/index.md). 

### Translation
Contribution for translations are welcomed at any time. Please note however that the mod will still change quite a lot, and your translation will probably need to be updated to reflect theese changes. You don't have to stay available to update it personally, but your work may end up being modified by someone else if needs to be. If you are not comfortable using Github you can send me your translation file on the Discord or ask me anything there.
