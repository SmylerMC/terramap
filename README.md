![Terramap logo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/terramap_logo-x256.png)

# Terramap

An addon mod to Terra1:1 providing an map in-game of the world with various perks, including easy teleportation without having to copy-paste the gps coordinates from a web browser, or open various map websites at the desired location directly from the game.

Terramap will work best if it is installed both on the server and the client. This is because it needs to syncronize the map projection used by the server to the client, but it is still usable if installed only on the client, as long as you take the time to manually set the projection used (For the Build the Earth Project, this is bteairocean, with upright as the orientation).

Terramap is still in beta, si it's likely to have a few bugs and still lacks intended features. It also only has English, French simplified Chinese and spanish translation at the moment.
If you have any questions, suggestions or bugs to report, feel free to stop by the [Terramap Discord](https://discord.gg/zSMq3GN "Terramap Discord").

Logo by [@DropeArt](https://twitter.com/DropeArt)

Make sure you have the proper dependencies installed: Terra1:1, CubicChunks, CubicWorldGen and LetsEncryptCraft (The lets encrypt craft version from the BTE modpack may not be enough, make sure you install it from CurseForge if you have any issue).
If the map crashes when opened on a BTE server, your version of Terra1:1 is probably to old and does not know about the new projection, you need to update to the new version included in the new installer.

Terramap on a solo world, with the humanitarian osm style:
![terramap screenshot solo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap.png)


Terramap on BTE-France, with player synchronization enabled:
![terramap screenshot server](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap_server.png)


Terramap on BTE Italia:
![terramap screenshot italia](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tilermap_server_italia.png)

## How to use:
Add the mod to your mods folder along with Terra1:1 and the other mods of you modpack.
In game, press m to open the map (by default, but it can be changed in the config)
Drag it, zoom, and explore the world just like with any other digital map.
Right-click somwhere to get a menu with various usefull tools (see the screenshots).
Additionally, you can: press p to toggle debug mod, enable entity rendering in the mod config.
If the map looks too pixelated, you can lower the tile scaling option in the config gui. Powers of two work best, but do not go lower than  0.125.

## How to setup on servers:
Add Terramap to your server's mod folder and restart the server to generate the config file.
Players can connect whether or not they have Terramap installed on their client. They will still need it if they want to look at the map.
You can edit your config file, here are some important options:

### `synchronize_players`
you need to set this to true if you want all players to be visible on the map, if it is set to false, only players in the range of the server's render distance will be displayed

### `sync_spectators`
Set it to false if you only want non spectator players to appear on the map no matter the distance

### `sync_interval`
This is the time interval between each time the map updates will be sent to clients. Increase it if you suspect Terramap is lagging your server (not likely, it's pretty light as long as you don't have ~50 players all looking at the map at the same time). It's in ticks.

### `sync_hearthbeet_timeout`
You probably don't need to touch that, read the comment and make sure you understand what it does before changing it.

### `force_client_tp_cmd`
You can set this to true if you want clients to use a specific command when teleporting from the map. You then need to change the tpll_command config value to the command you want. You can use {longitude}, {latitude}, {x} and {z} in the said command, they will be replaced by the corresponding value when executed.

### `players_opt_in_to_display_default`
Set this to false if you want players to need to do /terrashow show to be visible on the map

There are also to permission nodes related to /terrashow:
### `terramap.commands.terrashow.others` allows to change anyone's visibility
### `terramap.commands.terrashow.self` allows to change the visibility of the current players
## Contributing:
In case you want to contribute, please make sure you join the Discord. You will get a contributor role there.

### Code
Future updates are already planned ahead, so please contact me before contributing code so we can discuss the changes you want to make first.
In any case, please fork this repository and create a pull request from your fork. You can clone your fork to your computer and work from your IDE there. Make sure you follow the [Minecraft Forge instructions to setup the development environment](https://github.com/MinecraftForge/Documentation/blob/1.12.x/docs/gettingstarted/index.md). Additionnaly, if when running the mod in that dev environment forge complains about a mod being absent, copy the corresponding file from the lib folder to the run/mods folder. 

### Translation
Contribution for translations are welcomed at any time. Please note however that the mod will still change quite a lot an your translation will probably need to be updated to reflect this changes. You don't have to stay available to update it personnaly, but your work may end up being modified by someone else if need be. If you are not comfortable using Github you can send me your translation file on the Discord or ask me anything there.
