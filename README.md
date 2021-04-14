![Terramap logo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/terramap_logo-x256.png)

# Terramap

An addon mod to Terra1:1 providing an map in-game of the world with various perks, including easy teleportation without having to copy-paste the GPS coordinates from a web browser, or open various map websites at the desired location directly from the game.

Terramap will work best if it is installed both on the server and the client. This is because it needs to syncronize the map projection used by the server to the client, but it is still usable if installed only on the client, as long as you take the time to manually set the projection used (For the Build the Earth Project, this is bteairocean, with upright as the orientation).

Terramap is still in beta, it's likely to have a few bugs and still lacks intended features. It also only has English, French, Simplified Chinese, and Spanish translations at the moment.
If you have any questions, suggestions or bugs to report, feel free to stop by the [Terramap Discord](https://discord.gg/zSMq3GN "Terramap Discord").

Logo by [@DropeArt](https://twitter.com/DropeArt)

Make sure you have the proper dependencies installed: Terra1:1, CubicChunks, CubicWorldGen, and LetsEncryptCraft (The lets encrypt craft version from the BTE modpack may not be enough, make sure you install it from CurseForge if you have any issues).
If the map crashes when opened on a BTE server, your version of Terra1:1 is probably to old and does not know about the new projection, you need to update to the new version included in the new installer.

Terramap on a solo world, with the vanilla osm style:
![terramap screenshot solo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap.png)


Terramap on BTE-France, with player synchronization enabled:
![terramap screenshot server](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap_server.png)

Custom map styles on BTE-France:
![terramap screenshot styles](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/custom_map_styles.png)

Terramap on BTE Italia:
![terramap screenshot italia](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tilermap_server_italia.png)

The Terramap minimap on the left, with the Journeymap minimap on the right:
![terramap screenshot minimap](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/minimap.png)

## How to use:
Add the mod to your mods folder along with Terra1:1 and the other mods of your modpack.
In game, press m to open the map (by default, but it can be changed in the config)
Drag it, zoom in and out, and explore the world just like with any other digital map.
Right-click somwhere to get a menu with various useful tools (see the screenshots).
Additionally, you can: press p to toggle debug mod, press control to enable quick tp mode, change the map style in the map style menu, change entity display preferences, follow an entity or player by double clicking it, configure the minimap in the mod's config, add your own map styles, and more...
Checkout [the wiki](https://github.com/SmylerMC/terramap/wiki) for more technical information.

## Contributing:
In case you want to contribute, please make sure you join the Discord. You will get a contributor role there.

### Code
Future updates are already planned ahead, so please contact me before contributing code so we can discuss the changes you want to make first.
In any case, please fork this repository and create a pull request from your fork. You can clone your fork to your computer and work from your IDE there. Make sure you follow the [Minecraft Forge instructions to setup the development environment](https://github.com/MinecraftForge/Documentation/blob/1.12.x/docs/gettingstarted/index.md). Additionnaly, if when running the mod in that dev environment forge complains about a mod being absent, copy the corresponding file from the lib folder to the run/mods folder. 

### Translation
Contribution for translations are welcomed at any time. Please note however that the mod will still change quite a lot, and your translation will probably need to be updated to reflect theese changes. You don't have to stay available to update it personally, but your work may end up being modified by someone else if needs to be. If you are not comfortable using Github you can send me your translation file on the Discord or ask me anything there.
