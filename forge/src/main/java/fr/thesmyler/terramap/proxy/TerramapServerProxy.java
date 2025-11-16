package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.command.TerrashowCommand;
import fr.thesmyler.terramap.command.TilesetReloadCommand;
import fr.thesmyler.terramap.eventhandlers.ServerTerramapEventHandler;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smyler.terramap.TerramapForgeClient;
import net.smyler.terramap.world.ForgeWorldCache;

import static net.smyler.terramap.Terramap.getTerramap;

@SuppressWarnings("unused")  // An instance of this is injected into the main mod by FML
public class TerramapServerProxy extends TerramapProxy {

    private final ForgeWorldCache worldCache = new ForgeWorldCache();

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        getTerramap().logger().debug("Terramap server pre-init");
        TerramapNetworkManager.registerHandlers(Side.SERVER);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        getTerramap().logger().debug("Terramap server init");
        MinecraftForge.EVENT_BUS.register(new ServerTerramapEventHandler());
    }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new TerrashowCommand());
        event.registerServerCommand(new TilesetReloadCommand());
    }

    @Override
    public GameType getGameMode(EntityPlayer e) {
        EntityPlayerMP player = (EntityPlayerMP)e;
        return player.interactionManager.getGameType();
    }

    @Override
    public TerramapForgeClient getClient() {
        throw new IllegalStateException("Trying to call client code from a dedicated server!!!");
    }

    @Override
    public ForgeWorldCache worldCache() {
        return this.worldCache;
    }

    @Override
    public void onConfigChanged(OnConfigChangedEvent event) {
    }

}
