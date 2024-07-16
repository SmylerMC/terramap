package net.smyler.terramap.gui.widgets.map;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Translator;
import net.smyler.smylib.gui.widgets.MenuWidget;
import net.smyler.smylib.gui.Font;
import net.smyler.terramap.util.geo.GeoPointReadOnly;
import net.smyler.terramap.util.geo.GeoServices;

import org.jetbrains.annotations.Nullable;

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

    public MapMenuWidget(MapWidget map) {
        super(1500, getGameClient().defaultFont());

        this.map = map;
        this.controller = map.getController();
        this.mouseLocation = this.map.getMouseLocation();

        GameClient game = getGameClient();
        Font font = game.defaultFont();
        Translator translator = game.translator();

        this.addEntry(translator.format("terramap.mapwidget.rclickmenu.teleport"));
        this.centerHere = this.addEntry(translator.format("terramap.mapwidget.rclickmenu.center"), this::moveLocationToCenter);

        MenuWidget copySubMenu = new MenuWidget(this.getZ(), font);
        copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.geo"), this::copyGeoLocation);
        this.copyBlockMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.block"));
        this.copyChunkMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.chunk"));
        this.copyRegionMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.region"));
        this.copy3drMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.3dr"));
        this.copy2drMenuEntry = copySubMenu.addEntry(translator.format("terramap.mapwidget.rclickmenu.copy.2dr"));
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

        this.setProjectionMenuEntry = this.addEntry(translator.format("terramap.mapwidget.rclickmenu.set_proj"));
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
        super.onUpdate(mouseX, mouseY, parent);
        boolean hasProjection = false;
        this.centerHere.enabled = this.map.isInteractive();
        this.copyBlockMenuEntry.enabled = hasProjection;
        this.copyChunkMenuEntry.enabled = hasProjection;
        this.copyRegionMenuEntry.enabled = hasProjection;
        this.copy3drMenuEntry.enabled = hasProjection;
        this.copy2drMenuEntry.enabled = hasProjection;
        this.setProjectionMenuEntry.enabled = false;
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
        GeoServices.openInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void getOpenInBTEMap() {
        GeoServices.openInBTEMap(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void getOpenInOSMWeb() {
        GeoServices.openInOSMWeb(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude());
    }

    private void moveLocationToCenter() {
        this.controller.moveLocationToCenter(this.map.getMouseLocation(), true);
    }

    private void copyGeoLocation() {
        getGameClient().clipboard().setContent(this.mouseLocation.latitude() + " " + this.mouseLocation.longitude());
    }
    
    private void reportError(String errorTranslationKey) {
        String s = String.valueOf(System.nanoTime()); // Just a random string
        this.map.reportError(s, getGameClient().translator().format(errorTranslationKey));
        this.map.scheduleBeforeUpdate(() -> this.map.discardPreviousErrors(s), 5000);
    }

}
