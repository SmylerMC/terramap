![Terramap logo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/terramap_logo-x256.png)

# Terramap

An addon mod to Terra1:1 providing an map in-game of the world with various perks, including easy teleportation without having to copy-paste the gps coordinates from a web browser, or open various map websites at the desired location directly from the game.

Terramap will work best if it is installed both on the server and the client. This is because it needs to syncronize the map projection used by the server to the client, but it is still usable if installed only on the client, as long as you take the time to manually set the projection used (For the Build the Earth Project, this is bteairocean, with upright as the orientation).

Terramap is still in beta, si it's likely to have a few bugs and still lacks intended features. It also has no translation yet.

If you have any questions, suggestions or bugs to report, feel free to stop by the [Terramap Discord](https://discord.gg/zSMq3GN"Terramap Discord").

Logo by [@DropeArt](https://twitter.com/DropeArt)

Make sure you have the proper dependencies installed: Terra1:1, CubicChunks, CubicWorldGen and LetsEncryptCraft (The lets encrypt craft version from the BTE modpack may not be enough, make sure you install it from CurseForge if you have any issue)

Terramap on a solo world, with the humanitarian osm style:
![terramap screenshot solo](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap.png)


Terramap on a server where it's not installed:
![terramap screenshot server](https://raw.githubusercontent.com/SmylerMC/terramap/master/images/tiledmap_server.png)

### How to use:
Add the mod to your mods folder along with Terra1:1 and the other mods of you modpack.
In game, press m to open the map (by default, but it can be changed in the config)
Drag it, zoom, and explore the world just like with any other digital map.
Right-click somwhere to get a menu with various usefull tools (see the screenshots).
Additionally, you can: press p to toggle debug mod, enable entity rendering in the mod config.
If the map looks too pixelated, you can lower the tile scaling option in the config gui. Powers of two work best., but do not go lower than  0.125.

### Planned Features:
* Show all logged-in players, even if they are far away from you
* Show Forge Essential Warps and JourneyMap waipoints on Terramap
* Choose wich type of entities you want to display
* I'm open to suggestions!
