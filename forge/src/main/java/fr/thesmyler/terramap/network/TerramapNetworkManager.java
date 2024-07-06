package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.network.P2CSledgehammerHelloPacket.P2CSledgehammerHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTpCommandPacket.S2CTpCommandPacketHandler;
import fr.thesmyler.terramap.network.SP2CRasterTileSetPacket.SP2CRasterTileSetPacketSledgehammerHandler;
import fr.thesmyler.terramap.network.SP2CRasterTileSetPacket.SP2CRasterTileSetPacketTerramapHandler;
import fr.thesmyler.terramap.network.playersync.C2SPRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.playersync.C2SPRegisterForUpdatesPacket.C2SRegisterForUpdatesPacketHandler;
import fr.thesmyler.terramap.network.playersync.SP2CPlayerSyncPacket;
import fr.thesmyler.terramap.network.playersync.SP2CPlayerSyncPacket.S2CPlayerSyncPacketHandler;
import fr.thesmyler.terramap.network.playersync.SP2CRegistrationExpiresPacket;
import fr.thesmyler.terramap.network.playersync.SP2CRegistrationExpiresPacket.S2CRegistrationExpiresPacketHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smyler.terramap.Terramap;

public abstract class TerramapNetworkManager {

    // The channel instances
    public static final SimpleNetworkWrapper CHANNEL_TERRAMAP = NetworkRegistry.INSTANCE.newSimpleChannel(Terramap.MOD_ID + ":terramap");
    public static final SimpleNetworkWrapper CHANNEL_MAPSYNC = NetworkRegistry.INSTANCE.newSimpleChannel(Terramap.MOD_ID + ":mapsync");
    public static final SimpleNetworkWrapper CHANNEL_SLEDGEHAMMER = NetworkRegistry.INSTANCE.newSimpleChannel(Terramap.MOD_ID + ":sh"); // Forge does not support channel names longer than 20

    /**
     * Registers the handlers
     * 
     * @param side
     */
    public static void registerHandlers(Side side){
        registerTerramapS2C(S2C_TERRAMAP_HELLO_DISCRIMINATOR, S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class);
        registerTerramapS2C(S2C_TERRAMAP_TPCMD_DISCRIMINATOR, S2CTpCommandPacketHandler.class, S2CTpCommandPacket.class);
        registerTerramapS2C(S2C_TERRAMAP_RASTER_TILE_SET_DISCRIMINATOR, SP2CRasterTileSetPacketTerramapHandler.class, SP2CRasterTileSetPacket.class);

        registerMapsyncCP2S(C2SP_MAPSYNC_REGISTER_DISCRIMINATOR, C2SRegisterForUpdatesPacketHandler.class, C2SPRegisterForUpdatesPacket.class);
        registerMapsyncSP2C(SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR, S2CPlayerSyncPacketHandler.class, SP2CPlayerSyncPacket.class);
        registerMapsyncSP2C(SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR, S2CRegistrationExpiresPacketHandler.class, SP2CRegistrationExpiresPacket.class);

        registerSledgehammerP2C(P2C_SH_HELLO_DISCRIMINATOR, P2CSledgehammerHelloPacketHandler.class, P2CSledgehammerHelloPacket.class);
        registerSledgehammerP2C(P2C_SH_RASTER_TILESET_DISCRIMINATOR, SP2CRasterTileSetPacketSledgehammerHandler.class, SP2CRasterTileSetPacket.class);

    }

    // terramap:terramap
    private static final int S2C_TERRAMAP_HELLO_DISCRIMINATOR = 0;
    private static final int S2C_TERRAMAP_TPCMD_DISCRIMINATOR = 1;
    private static final int S2C_TERRAMAP_RASTER_TILE_SET_DISCRIMINATOR = 2;

    // terramap:mapsync
    private static final int C2SP_MAPSYNC_REGISTER_DISCRIMINATOR = 0;
    private static final int SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR = 1;
    private static final int SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR = 2;

    //terramap:sh
    private static final int P2C_SH_HELLO_DISCRIMINATOR = 0;
    private static final int P2C_SH_RASTER_TILESET_DISCRIMINATOR = 2;

    private static <REQ extends IMessage, REPLY extends IMessage> void registerTerramapS2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_TERRAMAP.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerTerramapC2S(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_TERRAMAP.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMapsyncSP2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_MAPSYNC.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMapsyncCP2S(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_MAPSYNC.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerSledgehammerP2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_SLEDGEHAMMER.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerSledgehammerC2P(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
        CHANNEL_SLEDGEHAMMER.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
    }

}
