package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Translator;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import net.smyler.smylib.gui.Font;
import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointReadOnly;
import fr.thesmyler.terramap.util.geo.GeoServices;
import net.buildtheearth.terraplusplus.control.PresetEarthGui;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;

import static java.lang.Math.floorDiv;
import static java.lang.Math.round;
import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * The context menu which is opened when the map is right-clicked.
 *
 * @author Smyler
 */
public class MapMenuWidget extends MenuWidget {

    private final MapWidget map;
    private final MapController controller;
    private final GeoPointReadOnly mouseLocation;

    private final MenuEntry centerHere;
    private final MenuEntry copyBlockMenuEntry;
    private final MenuEntry copyChunkMenuEntry;
    private final MenuEntry copyRegionMenuEntry;
    private final MenuEntry copy3drMenuEntry;
    private final MenuEntry copy2drMenuEntry;
    private final MenuEntry setProjectionMenuEntry;

    // This only exists is so we can use it to send chat messages
    private static final GuiScreen CHAT_SENDER_GUI = new GuiScreen() {};
    static { CHAT_SENDER_GUI.mc = Minecraft.getMinecraft(); }

    public MapMenuWidget(MapWidget map) {
        super(1500, getGameClient().getDefaultFont());

        this.map = map;
        this.controller = map.getController();
        this.mouseLocation = this.map.getMouseLocation();

        GameClient game = getGameClient();
        Font font = game.getDefaultFont();
        Translator translator = game.getTranslator();

        this.addEntry(translator.format("terramap.mapwidget.rclickmenu.teleport"), this::teleport);
        this.centerHere = this.addEntry(translator.format("terramap.mapwidget.rclickmenu.center"), this::moveLocationToCenter);

        MenuWidget copySubMenu = new MenuWidget(this.getZ(), font);
        copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.geo"), this::copyGeoLocation);
        this.copyBlockMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.block"), this::copyBlockPosition);
        this.copyChunkMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.chunk"), this::copyChunkPosition);
        this.copyRegionMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.region"), this::copyRegionPosition);
        this.copy3drMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.3dr"), this::copy3drPosition);
        this.copy2drMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.2dr"), this::copy2drPosition);
        this.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy"), copySubMenu);

        this.addSeparator();

        MenuWidget openSubMenu = new MenuWidget(this.getZ(), font);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_osm"), this::getOpenInOSMWeb);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_bte"), this::getOpenInBTEMap);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_gmaps"), this::openInGoogleMaps);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_gearth_web"), this::openInGoogleEarthWeb);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_gearth_pro"), this::openInGoogleEarthPro);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_bing"), this::getOpenInBingMaps);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_wikimapia"), this::getOpenInWikimapia);
        openSubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.open_yandex"), this::openInYandexMaps);
        this.addEntry(translator.format("terramap.mapwidget.rclickmenu.open"), openSubMenu);

        this.addSeparator();

        this.setProjectionMenuEntry = this.addEntry(translator.format("terramap.mapwidget.rclickmenu.set_proj"), this::setProjection);
    }

    void teleport() {
        String cmdFormat = TerramapClientContext.getContext().getTpCommand();
        String cmd = cmdFormat.replace("{longitude}", String.valueOf(this.mouseLocation.longitude()))
                              .replace("{latitude}", String.valueOf(this.mouseLocation.latitude()));

        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null && (cmd.contains("{x}") || cmd.contains("{z}"))) {
            this.reportError("terramap.mapwidget.error.tp");
            return;
        }
        if(projection != null) {
            try {
                double[] xz = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                cmd = cmd.replace("{x}", String.valueOf(xz[0]))
                         .replace("{z}", String.valueOf(xz[1]));
            } catch (OutOfProjectionBoundsException e) {
                this.reportError("terramap.mapwidget.error.tp");
                return;
            }
        }
        CHAT_SENDER_GUI.sendChatMessage(cmd, false);
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
        super.onUpdate(mouseX, mouseY, parent);
        boolean hasProjection = TerramapClientContext.getContext().getProjection() != null;
        this.centerHere.enabled = this.map.isInteractive();
        this.copyBlockMenuEntry.enabled = hasProjection;
        this.copyChunkMenuEntry.enabled = hasProjection;
        this.copyRegionMenuEntry.enabled = hasProjection;
        this.copy3drMenuEntry.enabled = hasProjection;
        this.copy2drMenuEntry.enabled = hasProjection;
        this.setProjectionMenuEntry.enabled = (!TerramapClientContext.getContext().isInstalledOnServer() && TerramapClientContext.getContext().isOnEarthWorld());
    }

    private void setProjection() {
        EarthGeneratorSettings stg = TerramapClientContext.getContext().getGeneratorSettings();
        Minecraft.getMinecraft().displayGuiScreen(new PresetEarthGui(null, stg != null ? stg.toString(): PresetEarthGui.DEFAULT_PRESETS.get("default"), s ->  {
            TerramapClientContext.getContext().setGeneratorSettings(EarthGeneratorSettings.parse(s));
            TerramapClientContext.getContext().saveState();
        }));
    }

    private void openInYandexMaps() {
        GeoServices.openInYandex((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void getOpenInWikimapia() {
        GeoServices.openInWikimapia((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void getOpenInBingMaps() {
        GeoServices.openInBingMaps((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void openInGoogleEarthPro() {
        GeoServices.openInGoogleEarthPro(this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void openInGoogleEarthWeb() {
        GeoServices.opentInGoogleEarthWeb(this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void openInGoogleMaps() {
        MainPlayerMarker playerMarker = this.map.getMainPlayerMarker();
        if(playerMarker != null) {
            if(playerMarker.isVisible(this.map)) {
                GeoPoint<?> playerLocation = playerMarker.getLocation();
                GeoServices.openPlaceInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), playerLocation.longitude(), playerLocation.latitude());
            } else {
                GeoServices.openInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude());
            }
        } else {
            GeoServices.openInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude());
        }
    }

    private void getOpenInBTEMap() {
        GeoServices.openInBTEMap(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void getOpenInOSMWeb() {
        GeoServices.openInOSMWeb(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void copy2drPosition() {
        try {
            double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
            String dispX = String.valueOf(floorDiv(round(coords[0]), 512));
            String dispY = String.valueOf(floorDiv(round(coords[1]), 512));
            getGameClient().getClipboard().setContent(dispX + "." + dispY + ".2dr");
        } catch (OutOfProjectionBoundsException e) {
            this.reportError("terramap.mapwidget.error.copy2dregion");
        }
    }

    private void copy3drPosition() {
        try {
            double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
            String dispX = String.valueOf(floorDiv(round(coords[0]), 256));
            String dispY = String.valueOf(floorDiv(round(coords[1]), 256));
            getGameClient().getClipboard().setContent(dispX + ".0." + dispY + ".3dr");
        } catch (OutOfProjectionBoundsException e) {
            this.reportError("terramap.mapwidget.error.copy2dregion");
        }
    }

    private void copyRegionPosition() {
        try {
            double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
            String dispX = String.valueOf(floorDiv(round(coords[0]), 512));
            String dispY = String.valueOf(floorDiv(round(coords[1]), 512));
            getGameClient().getClipboard().setContent("r." + dispX + "." + dispY + ".mca");
        } catch (OutOfProjectionBoundsException e) {
            this.reportError("terramap.mapwidget.error.copyregion");
        }
    }

    private void copyChunkPosition() {
        try {
            double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
            String dispX = String.valueOf(floorDiv(round(coords[0]), 16));
            String dispY = String.valueOf(floorDiv(round(coords[1]), 16));
            getGameClient().getClipboard().setContent(dispX + " " + dispY);
        } catch (OutOfProjectionBoundsException e) {
            this.reportError("terramap.mapwidget.error.copychunk");
        }
    }

    private void copyBlockPosition() {
        try {
            double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
            String dispX = String.valueOf(round(coords[0]));
            String dispY = String.valueOf(round(coords[1]));
            getGameClient().getClipboard().setContent(dispX + " " + dispY);
        } catch (OutOfProjectionBoundsException e) {
            this.reportError("terramap.mapwidget.error.copyblock");
        }
    }


    private void moveLocationToCenter() {
        this.controller.moveLocationToCenter(this.map.getMouseLocation(), true);
    }

    private void copyGeoLocation() {
        getGameClient().getClipboard().setContent(this.mouseLocation.latitude() + " " + this.mouseLocation.longitude());
    }
    
    private void reportError(String errorTranslationKey) {
        String s = String.valueOf(System.nanoTime()); // Just a random string
        this.map.reportError(s, getGameClient().getTranslator().format(errorTranslationKey));
        this.map.scheduleBeforeUpdate(() -> this.map.discardPreviousErrors(s), 5000);
    }

}
