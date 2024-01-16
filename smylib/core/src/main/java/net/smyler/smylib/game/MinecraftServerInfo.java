package net.smyler.smylib.game;


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

}
