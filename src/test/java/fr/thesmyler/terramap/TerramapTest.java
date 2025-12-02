package fr.thesmyler.terramap;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class TerramapTest extends SmyLibGuiTest {

    @BeforeEach
    public void initTerramap() throws NoSuchFieldException, IllegalAccessException {
        TerramapMod.logger = LogManager.getLogger("Terramap unit test");

        // Terra++'s HTTP client uses FMLCommonHandler.instance().getSide(), so we need to fool that into not NPEing
        Field sidedDelegateField = FMLCommonHandler.class.getDeclaredField("sidedDelegate");
        sidedDelegateField.setAccessible(true);
        sidedDelegateField.set(FMLCommonHandler.instance(), new MockedFmlSideHandler());

        MapStylesLibrary.reload();
        TerramapClientContext.resetContext();
    }

    private static class MockedFmlSideHandler implements IFMLSidedHandler {

        @Override
        public List<String> getAdditionalBrandingInformation() {
            return singletonList("Terramap tests");
        }

        @Override
        public Side getSide() {
            return CLIENT;
        }

        @Override
        public void haltGame(String message, Throwable exception) {
            TerramapMod.logger.fatal("Something tried to halt the game from the mocked FML");
            throw new IllegalStateException(message, exception);
        }

        @Override
        public void showGuiScreen(Object clientGuiElement) {
            TerramapMod.logger.fatal("Something tried to show a GUI screen from the mocked FML");
            throw new IllegalStateException();
        }

        @Override
        public void queryUser(StartupQuery query) throws InterruptedException {
            TerramapMod.logger.warn("Something tried to query user from the mocked FML");
        }

        @Override
        public void beginServerLoading(MinecraftServer server) {
            TerramapMod.logger.info("Mocked FML begin server Loading called (no-op)");
        }

        @Override
        public void finishServerLoading() {
            TerramapMod.logger.info("Mocked FML finish server Loading called (no-op)");
        }

        @Override
        public File getSavesDirectory() {
            TerramapMod.logger.warn("Something tried to get the saves directory from the mocked FML");
            throw new NullPointerException();
        }

        @Override
        public MinecraftServer getServer() {
            TerramapMod.logger.warn("Something tried to access the server from the mocked FML");
            throw new IllegalStateException();
        }

        @Override
        public boolean isDisplayCloseRequested() {
            return false;
        }

        @Override
        public boolean shouldServerShouldBeKilledQuietly() {
            return false;
        }

        @Override
        public void addModAsResource(ModContainer container) {
            TerramapMod.logger.warn("Something tried to add a mod as resource to mocked FML");
        }

        @Override
        public String getCurrentLanguage() {
            return "en-us";
        }

        @Override
        public void serverStopped() {

        }

        @Override
        public NetworkManager getClientToServerNetworkManager() {
            TerramapMod.logger.warn("Something tried to access the c2s network manager from the mocked FML");
            throw new IllegalStateException();
        }

        @Override
        public INetHandler getClientPlayHandler() {
            TerramapMod.logger.warn("Something tried to access the client play handler from the mocked FML");
            throw new IllegalStateException();
        }

        @Override
        public void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel, Side side) {
            TerramapMod.logger.warn("Something tried to fire a net registration event from the mocked FML");
        }

        @Override
        public boolean shouldAllowPlayerLogins() {
            return false;
        }

        @Override
        public void allowLogins() {
            TerramapMod.logger.warn("Something tried to allow logins from the mocked FML");
        }

        @Override
        public IThreadListener getWorldThread(INetHandler net) {
            return null;
        }

        @Override
        public void processWindowMessages() {

        }

        @Override
        public String stripSpecialChars(String message) {
            return message;
        }

        @Override
        public void reloadRenderers() {
            TerramapMod.logger.warn("Something tried to reload rendered from the mocked FML");
        }

        @Override
        public void fireSidedRegistryEvents() {
            TerramapMod.logger.warn("Something tried to fire sided registry events from the mocked FML");
        }

        @Override
        public CompoundDataFixer getDataFixer() {
            TerramapMod.logger.warn("Something tried to access data fixers from the mocked FML");
            throw new NullPointerException();
        }

        @Override
        public boolean isDisplayVSyncForced() {
            return false;
        }

    }

}
