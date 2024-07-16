package net.smyler.terramap;

import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * @author SmylerMC
 * 
 * Terramap's config
 * 
 */
public class TerramapConfig {

    public static final class Minimap {

        public boolean enable = true;

        public String style = "osm";

        public float posX = 1;

        public float posY = 1;

        public float width = 20;

        public float height = 15;

        public float zoomLevel = 18;

        public boolean showEntities = false;

        public boolean showOtherPlayers = true;

        public double tileScaling = 0.0;

        public boolean playerDirections = true;

        public boolean playerRotation = false;
        
        public boolean chunksRender = false;

        public double getEffectiveTileScaling() {
            if(this.tileScaling == 0) {
                return getGameClient().scaleFactor();
            } else {
                return CLIENT.minimap.tileScaling;
            }
        }

    }

    public static final class Compass {
        public boolean enable = true;

        public float posX = 1;

        public float posY = 16;

        public float width = 20;
    }

    public static final class Client {

        public final Minimap minimap = new Minimap();

        public final Compass compass = new Compass();

        public double tileScaling = 0;

        public final int LOW_ZOOM_LEVEL_DEFAULT = 2;
        public int lowZoomLevel = LOW_ZOOM_LEVEL_DEFAULT;

        public boolean unlockZoom = false;

        public final int DOUBLE_CLICK_DELAY_DEFAULT = 500;
        public int doubleClickDelay = DOUBLE_CLICK_DELAY_DEFAULT;

        public boolean saveUiState = false;

        public boolean chatOnMap = true;

        public boolean forceTerraWorld = false;

        public double getEffectiveTileScaling() {
            if(this.tileScaling == 0) {
                return getGameClient().scaleFactor();
            } else {
                return CLIENT.tileScaling;
            }
        }

    }

    public static final class Server {
        public boolean forceClientTpCmd = false;

        public boolean synchronizePlayers = true;

        public boolean synchronizeSpectators = true;

        public int syncInterval = 10;

        public int syncHeartbeatTimeout = 120000;

        public boolean playersDisplayDefault = true;

        public boolean sendCusomMapsToClient = true;

        public String joinWithoutModMessage = "";

        public String joinWithOutdatedModMessage = "[\"\",{\"text\":\"You are using an old and outdated version of Terramap, it will not work on this server. You can update \",\"color\":\"red\"},{\"text\":\"here\",\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.curseforge.com/minecraft/mc-mods/terramap\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open link.\"}},{\"text\":\".\",\"color\":\"red\"}]";

    }

    public static final Client CLIENT = new Client();

    public static final Server SERVER = new Server();

    public static String tpllcmd = "/tpll {latitude} {longitude}";

    public static boolean enableDebugMaps = false;


}
