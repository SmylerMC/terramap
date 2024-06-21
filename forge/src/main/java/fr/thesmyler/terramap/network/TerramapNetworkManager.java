package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.network.P2CSledgehammerHelloPacket.P2CSledgehammerHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTpCommandPacket.S2CTpCommandPacketHandler;
import fr.thesmyler.terramap.network.SP2CMapStylePacket.SP2CMapStylePacketSledgehammerHandler;
import fr.thesmyler.terramap.network.SP2CMapStylePacket.SP2CMapStylePacketTerramapHandler;
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

//TODO Test the warp protocol
public abstract class TerramapNetworkManager {

    // The channel instances
    public static final SimpleNetworkWrapper CHANNEL_TERRAMAP = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":terramap");
    public static final SimpleNetworkWrapper CHANNEL_MAPSYNC = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":mapsync");
    public static final SimpleNetworkWrapper CHANNEL_SLEDGEHAMMER = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":sh"); // Forge does not support channel names longer than 20

    /**
     * Registers the handlers
     * 
     * @param side
     */
    public static void registerHandlers(Side side){
        registerTerramapS2C(S2C_TERRAMAP_HELLO_DISCRIMINATOR, S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class);
        registerTerramapS2C(S2C_TERRAMAP_TPCMD_DISCRIMINATOR, S2CTpCommandPacketHandler.class, S2CTpCommandPacket.class);
        registerTerramapS2C(S2C_TERRAMAP_MAPSTYLE_DISCRIMINATOR, SP2CMapStylePacketTerramapHandler.class, SP2CMapStylePacket.class);

        registerMapsyncCP2S(C2SP_MAPSYNC_REGISTER_DISCRIMINATOR, C2SRegisterForUpdatesPacketHandler.class, C2SPRegisterForUpdatesPacket.class);
        registerMapsyncSP2C(SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR, S2CPlayerSyncPacketHandler.class, SP2CPlayerSyncPacket.class);
        registerMapsyncSP2C(SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR, S2CRegistrationExpiresPacketHandler.class, SP2CRegistrationExpiresPacket.class);

        registerSledgehammerP2C(P2C_SH_HELLO_DISCRIMINATOR, P2CSledgehammerHelloPacketHandler.class, P2CSledgehammerHelloPacket.class);
        registerSledgehammerP2C(P2C_SH_MAPSTYLE_DISCRIMINATOR, SP2CMapStylePacketSledgehammerHandler.class, SP2CMapStylePacket.class);

    }

    // terramap:terramap
    private static final int S2C_TERRAMAP_HELLO_DISCRIMINATOR = 0;
    private static final int S2C_TERRAMAP_TPCMD_DISCRIMINATOR = 1;
    private static final int S2C_TERRAMAP_MAPSTYLE_DISCRIMINATOR = 2;
    private static final int C2S_TERRAMAP_REQUEST_WARP_DISCRIMINATOR = 3;
    private static final int S2C_TERRAMAP_WARP_DISCRIMINATOR = 4;
    private static final int C2S_TERRAMAP_REQUEST_MULTI_WARP_DISCRIMINATOR = 5;
    private static final int S2C_TERRAMAP_MULTI_WARP_DISCRIMINATOR = 6;
    private static final int C2S_TERRAMAP_CREATE_WARP_DISCRIMINATOR = 7;
    private static final int S2C_TERRAMAP_CREATE_WARP_CONFIRMATION_DISCRIMINATOR = 8;
    private static final int C2S_TERRAMAP_EDIT_WARP_DISCRIMINATOR = 9;
    private static final int S2C_TERRAMAP_EDIT_WARP_CONFIRMATION_DISCRIMINATOR = 10;
    private static final int S2C_TERRAMAP_WARP_COMMAND_DISCRIMINATOR = 11;

    // terramap:mapsync
    private static final int C2SP_MAPSYNC_REGISTER_DISCRIMINATOR = 0;
    private static final int SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR = 1;
    private static final int SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR = 2;

    //terramap:sh
    private static final int P2C_SH_HELLO_DISCRIMINATOR = 0;
    private static final int P2C_SH_MAPSTYLE_DISCRIMINATOR = 2;
    private static final int C2P_SH_REQUEST_WARP_DISCRIMINATOR = 3;
    private static final int P2C_SH_WARP_DISCRIMINATOR = 4;
    private static final int C2P_SH_REQUEST_MULTI_WARP_DISCRIMINATOR = 5;
    private static final int P2C_SH_MULTI_WARP_DISCRIMINATOR = 6;
    private static final int C2P_SH_CREATE_WARP_DISCRIMINATOR = 7;
    private static final int P2C_SH_CREATE_WARP_CONFIRMATION_DISCRIMINATOR = 8;
    private static final int C2P_SH_EDIT_WARP_DISCRIMINATOR = 9;
    private static final int P2C_SH_EDIT_WARP_CONFIRMATION_DISCRIMINATOR = 10;
    private static final int P2C_SH_WARP_COMMAND_DISCRIMINATOR = 11;

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
