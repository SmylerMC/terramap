package fr.thesmyler.smylibgui.util;

import net.minecraft.client.multiplayer.ServerData;

/**
 * Information about a Minecraft server.
 *
 * @author Smyler
 */
public class MinecraftServerInfo {

    /**
     * The name of the server in the multiplayer menu.
     */
    public final String name;

    /**
     * The hostname of the server (Often refered to by the somewhat inaccurate "IP").
     */
    public final String host;

    /**
     * The server's MOTD.
     */
    public final String motd;

    /**
     * Whether the server is on lan or not.
     */
    public final boolean lanServer;

    public MinecraftServerInfo(String name, String host, String motd, boolean lanServer) {
        this.name = name;
        this.host = host;
        this.motd = motd;
        this.lanServer = lanServer;
    }

    public MinecraftServerInfo(ServerData data) {
        this.name = data.serverName;
        this.host = data.serverIP;
        this.motd = data.serverMOTD;
        this.lanServer = data.isOnLAN();
    }

}
