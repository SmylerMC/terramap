package fr.thesmyler.terramap.gui.screens.config;

import java.awt.Desktop;
import java.io.IOException;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.BackgroundOption;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TerramapConfigScreen extends Screen {

    private GuiScreen parent;
    private FlexibleWidgetContainer[] pages;
    private String[] titles;
    private TextWidget title;
    private int currentSubScreen = 0;
    private TexturedButtonWidget next = new TexturedButtonWidget(10, IncludedTexturedButtons.RIGHT, this::nextPage);
    private TexturedButtonWidget previous = new TexturedButtonWidget(10, IncludedTexturedButtons.LEFT, this::previousPage);
    private ToggleButtonWidget unlockZoomToggle = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget saveUIStateToggle = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget showChatOnMapToggle = new ToggleButtonWidget(10, false);
    private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<>(10, TileScalingOption.values());
    private IntegerSliderWidget doubleClickDelaySlider = new IntegerSliderWidget(10, TerramapConfig.CLIENT.DOUBLE_CLICK_DELAY_MIN, TerramapConfig.CLIENT.DOUBLE_CLICK_DELAY_MAX, TerramapConfig.CLIENT.DOUBLE_CLICK_DELAY_DEFAULT);
    private IntegerSliderWidget maxLoadedTilesSlider = new IntegerSliderWidget(10, TerramapConfig.CLIENT.TILE_LOAD_MIN, TerramapConfig.CLIENT.TILE_LOAD_MAX, TerramapConfig.CLIENT.TILE_LOAD_DEFAULT);
    private IntegerSliderWidget lowZoomLevelSlider = new IntegerSliderWidget(10, TerramapConfig.CLIENT.LOW_ZOOM_LEVEL_MIN, TerramapConfig.CLIENT.LOW_ZOOM_LEVEL_MAX, TerramapConfig.CLIENT.LOW_ZOOM_LEVEL_DEFAULT);
    private ToggleButtonWidget debugMapStylesToggle = new ToggleButtonWidget(10, false);
    private TextButtonWidget reloadMapStylesButton;
    private TextFieldWidget tpCommandField;
    private TextWidget pageText;

    public TerramapConfigScreen(GuiScreen parent) {
        super(BackgroundOption.DEFAULT);
        this.parent = parent;
        this.pageText = new TextWidget(10, SmyLibGui.DEFAULT_FONT);
        this.tpCommandField = new TextFieldWidget(10, SmyLibGui.DEFAULT_FONT).setWidth(200);
        this.reset();
    }

    @Override
    public void initGui() {
        int inter = 9;
        WidgetContainer content = this.getContent();
        content.removeAllWidgets(); //Remove the widgets that were already there
        content.cancellAllScheduled(); //Cancel all callbacks that were already there
        this.title = new TextWidget(this.width/2, 10, 5, new TextComponentTranslation("terramap.configmenu.title"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        content.addWidget(this.title);
        TextButtonWidget save = new TextButtonWidget(this.width/2 + 30, this.height - 30, 10, 100, I18n.format("terramap.configmenu.save"), this::saveAndClose);
        TextButtonWidget cancel = new TextButtonWidget(this.width/2 - 130, save.getY(), save.getZ(), save.getWidth(), I18n.format("terramap.configmenu.cancel"), this::close);
        TextButtonWidget reset = new TextButtonWidget(this.width/2 - 25, save.getY(), save.getZ(), 50, I18n.format("terramap.configmenu.reset"), this::reset);
        content.addWidget(save.setTooltip(I18n.format("terramap.configmenu.save.tooltip")));
        content.addWidget(cancel.setTooltip(I18n.format("terramap.configmenu.save.cancel")));
        content.addWidget(reset.setTooltip(I18n.format("terramap.configmenu.save.reset")));
        content.addWidget(this.next.setX(save.getX() + save.getWidth() + 5).setY(save.getY() + 2));
        content.addWidget(this.previous.setX(cancel.getX() - 20).setY(this.next.getY()));
        FlexibleWidgetContainer mapConfigScreen = new FlexibleWidgetContainer(20, 20, 1, this.width - 40, this.height - 75);
        FlexibleWidgetContainer mapStylesConfigScreen = new FlexibleWidgetContainer(20, 20, 1, this.width - 40, this.height - 75);
        FlexibleWidgetContainer otherConfigScreen = new FlexibleWidgetContainer(20, 20, 1, this.width - 40, this.height - 75);
        this.pages = new FlexibleWidgetContainer[] {
                mapConfigScreen,
                mapStylesConfigScreen,
                otherConfigScreen
        };
        this.titles = new String[] {
                I18n.format("terramap.configmenu.title.mapsettings"),
                I18n.format("terramap.configmenu.title.mapstyles"),
                I18n.format("terramap.configmenu.title.other")
        };

        // Map settings
        TextWidget unlockZoomText = new TextWidget(10, new TextComponentTranslation("terramap.configmenu.unlockzoom"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        unlockZoomText.setAnchorX((mapConfigScreen.getWidth() - unlockZoomText.getWidth() - this.unlockZoomToggle.getWidth())/2 - 71).setAnchorY(this.height / 4 - 30);
        mapConfigScreen.addWidget(unlockZoomText);
        this.unlockZoomToggle.setTooltip(I18n.format("terramap.configmenu.unlockzoom.tooltip"));
        mapConfigScreen.addWidget(this.unlockZoomToggle.setX(unlockZoomText.getX() + unlockZoomText.getWidth() + 5).setY(unlockZoomText.getAnchorY() - 4));
        TextWidget saveUIStateText = new TextWidget(10, new TextComponentTranslation("terramap.configmenu.saveui"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        this.saveUIStateToggle.setTooltip(I18n.format("terramap.configmenu.saveui.tooltip"));
        saveUIStateText.setAnchorX((mapConfigScreen.getWidth() - saveUIStateText.getWidth() - this.saveUIStateToggle.getWidth())/2 + 64).setAnchorY(unlockZoomText.getAnchorY());
        mapConfigScreen.addWidget(saveUIStateText);
        mapConfigScreen.addWidget(this.saveUIStateToggle.setX(saveUIStateText.getX() + saveUIStateText.getWidth() + 5).setY(saveUIStateText.getAnchorY() - 4));
        TextWidget chatOnMapText = new TextWidget(10, new TextComponentTranslation("terramap.configmenu.chatonmap"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        chatOnMapText.setAnchorX(unlockZoomText.getAnchorX()).setAnchorY(unlockZoomText.getAnchorY() + unlockZoomText.getFont().height() + 10);
        mapConfigScreen.addWidget(chatOnMapText);
        this.showChatOnMapToggle.setTooltip(I18n.format("terramap.configmenu.chatonmap.tooltip"));
        mapConfigScreen.addWidget(this.showChatOnMapToggle.setX(chatOnMapText.getX() + chatOnMapText.getWidth() + 9).setY(chatOnMapText.getAnchorY() - 4));
        mapConfigScreen.addWidget(this.tileScalingSlider.setX(mapConfigScreen.getWidth()/2 - 130).setY(this.showChatOnMapToggle.getY() + this.unlockZoomToggle.getHeight() + inter).setWidth(125).setDisplayPrefix(I18n.format("terramap.configmenu.tilescaling")));
        mapConfigScreen.addWidget(this.doubleClickDelaySlider.setX(mapConfigScreen.getWidth()/2 + 5).setY(this.tileScalingSlider.getY()).setWidth(this.tileScalingSlider.getWidth()).setDisplayPrefix(I18n.format("terramap.configmenu.doubleclick")));
        this.maxLoadedTilesSlider.setTooltip(I18n.format("terramap.configmenu.tilecache.tooltip"));
        mapConfigScreen.addWidget(this.maxLoadedTilesSlider.setX(mapConfigScreen.getWidth()/2 - 130).setY(this.doubleClickDelaySlider.getY() + this.doubleClickDelaySlider.getHeight() + inter).setWidth(125).setDisplayPrefix(I18n.format("terramap.configmenu.tilecache")));
        this.lowZoomLevelSlider.setTooltip(I18n.format("terramap.configmenu.lowzoom.tooltip"));
        mapConfigScreen.addWidget(this.lowZoomLevelSlider.setX(mapConfigScreen.getWidth()/2 + 5).setY(this.maxLoadedTilesSlider.getY()).setWidth(this.maxLoadedTilesSlider.getWidth()).setDisplayPrefix(I18n.format("terramap.configmenu.lowzoom")));
        TextButtonWidget hudButton = new TextButtonWidget(mapConfigScreen.getWidth() / 2 - 100, this.lowZoomLevelSlider.getY() + this.lowZoomLevelSlider.getHeight() + inter, 10, 200, I18n.format("terramap.configmenu.configureminimap"), () -> Minecraft.getMinecraft().displayGuiScreen(new HudConfigScreen()));
        hudButton.setTooltip(I18n.format("terramap.configmenu.configureminimap.tooltip"));
        mapConfigScreen.addWidget(hudButton);

        // Map styles
        TextWidget debugMapStylesText = new TextWidget(10, new TextComponentTranslation("terramap.configmenu.debugmapstyles"), SmyLibGui.DEFAULT_FONT);
        mapStylesConfigScreen.addWidget(debugMapStylesText.setAnchorX((mapStylesConfigScreen.getWidth() - debugMapStylesToggle.getWidth() - debugMapStylesText.getWidth() - 3) / 2).setAnchorY(mapStylesConfigScreen.getHeight() / 4 - 30));
        debugMapStylesToggle.setTooltip(I18n.format("terramap.configmenu.debugmapstyles.tooltip"));
        mapStylesConfigScreen.addWidget(debugMapStylesToggle.setX(debugMapStylesText.getX() + debugMapStylesText.getWidth() + 3).setY(debugMapStylesText.getY() - 4));
        Set<String> baseIDs = MapStylesLibrary.getBaseMaps().keySet();
        Set<String> userIDs = MapStylesLibrary.getUserMaps().keySet();
        Set<String> serverIDs = TerramapClientContext.getContext().getServerMapStyles().keySet();
        Set<String> proxyIDs = TerramapClientContext.getContext().getProxyMapStyles().keySet();
        Set<String> resolved = TerramapClientContext.getContext().getMapStyles().keySet();
        TextWidget baseText = new TextWidget(mapStylesConfigScreen.getWidth() / 2, 40, 10, new TextComponentTranslation("terramap.configmenu.mapstyles.base", baseIDs.size(), String.join(", ", baseIDs)), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        TextWidget proxyText = new TextWidget(mapStylesConfigScreen.getWidth() / 2, 57, 10, new TextComponentTranslation("terramap.configmenu.mapstyles.proxy", proxyIDs.size(), String.join(", ", proxyIDs)), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        TextWidget serverText = new TextWidget(mapStylesConfigScreen.getWidth() / 2, 74, 10, new TextComponentTranslation("terramap.configmenu.mapstyles.server", serverIDs.size(), String.join(", ", serverIDs)), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        TextWidget userText = new TextWidget( mapStylesConfigScreen.getWidth() / 2, 91, 10, new TextComponentTranslation("terramap.configmenu.mapstyles.custom", userIDs.size(), String.join(", ", userIDs)),TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        TextWidget effectiveText = new TextWidget(mapStylesConfigScreen.getWidth() / 2, 108, 10, new TextComponentTranslation("terramap.configmenu.mapstyles.effective", resolved.size(), String.join(", ", resolved)), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        mapStylesConfigScreen.addWidget(baseText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(debugMapStylesToggle.getY() + debugMapStylesToggle.getHeight() + 10));
        mapStylesConfigScreen.addWidget(proxyText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(baseText.getY() + baseText.getHeight() + inter));
        mapStylesConfigScreen.addWidget(serverText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(proxyText.getY() + proxyText.getHeight() + inter));
        mapStylesConfigScreen.addWidget(userText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(serverText.getY() + serverText.getHeight() + inter));
        mapStylesConfigScreen.addWidget(effectiveText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(userText.getY() + userText.getHeight() + inter));
        this.reloadMapStylesButton = new TextButtonWidget(mapStylesConfigScreen.getWidth() / 2 - 153, (effectiveText.getY() + effectiveText.getHeight() + mapStylesConfigScreen.getHeight()) / 2 - 10, 10, 150, I18n.format("terramap.configmenu.mapstyles.reload"), () -> {MapStylesLibrary.reload(); TerramapConfigScreen.this.initGui();});
        mapStylesConfigScreen.addWidget(this.reloadMapStylesButton);
        mapStylesConfigScreen.addWidget(new TextButtonWidget(this.reloadMapStylesButton.getX() + this.reloadMapStylesButton.getWidth() + 3, this.reloadMapStylesButton.getY(), 10, 150, I18n.format("terramap.configmenu.mapstyles.open"), () ->  {
            try {
                Desktop.getDesktop().open(MapStylesLibrary.getFile());
            } catch (IOException e) {
                TerramapMod.logger.error("Failed to open map style config file: ");
                TerramapMod.logger.catching(e);
            }
        }));

        // Other config screen
        TextWidget tpCommandText = new TextWidget(10, new TextComponentTranslation("terramap.configmenu.teleportcmd"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        otherConfigScreen.addWidget(tpCommandText.setAnchorX((otherConfigScreen.getWidth() - this.tpCommandField.getWidth() - tpCommandText.getWidth()) / 2).setAnchorY(60));
        otherConfigScreen.addWidget(this.tpCommandField.setX(tpCommandText.getX() + tpCommandText.getWidth() + inter).setY(tpCommandText.getY() - 7));

        // Footer
        this.getContent().addWidget(this.pages[this.currentSubScreen]);
        this.title.setText(new TextComponentString(this.titles[this.currentSubScreen]));
        this.getContent().addWidget(this.pageText.setAnchorX(this.width/2).setAnchorY(this.height - 45).setAlignment(TextAlignment.CENTER));
        this.updateButtons();
    }

    private void nextPage() {
        this.getContent().removeWidget(this.pages[this.currentSubScreen]);
        this.currentSubScreen++;
        this.getContent().addWidget(this.pages[this.currentSubScreen]);
        this.title.setText(new TextComponentString(this.titles[currentSubScreen]));
        this.updateButtons();
    }

    private void previousPage() {
        this.getContent().removeWidget(this.pages[this.currentSubScreen]);
        this.currentSubScreen--;
        this.getContent().addWidget(this.pages[this.currentSubScreen]);
        this.title.setText(new TextComponentString(this.titles[currentSubScreen]));
        this.updateButtons();
    }

    private void updateButtons() {
        if(this.currentSubScreen <= 0) this.previous.disable();
        else this.previous.enable();
        if(this.currentSubScreen >= this.pages.length - 1) this.next.disable();
        else this.next.enable();
        this.pageText.setText(new TextComponentTranslation("terramap.configmenu.pagenumber", this.currentSubScreen + 1, this.pages.length));
    }

    private void saveAndClose() {
        TerramapConfig.CLIENT.tileScaling = this.tileScalingSlider.getCurrentOption().value;
        TerramapConfig.CLIENT.unlockZoom = this.unlockZoomToggle.getState();
        TerramapConfig.CLIENT.saveUiState = this.saveUIStateToggle.getState();
        TerramapConfig.CLIENT.chatOnMap = this.showChatOnMapToggle.getState();
        TerramapConfig.CLIENT.doubleClickDelay = (int) this.doubleClickDelaySlider.getValue();
        TerramapConfig.CLIENT.maxTileLoad = (int) this.maxLoadedTilesSlider.getValue();
        TerramapConfig.CLIENT.lowZoomLevel = (int) this.lowZoomLevelSlider.getValue();
        TerramapConfig.tpllcmd = this.tpCommandField.getText();
        TerramapConfig.enableDebugMaps = this.debugMapStylesToggle.getState();
        TerramapConfig.sync();
        this.close();
    }

    private void close() {
        Minecraft.getMinecraft().displayGuiScreen(this.parent);
    }

    private void reset() {
        this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.CLIENT.tileScaling));
        this.unlockZoomToggle.setState(TerramapConfig.CLIENT.unlockZoom);
        this.saveUIStateToggle.setState(TerramapConfig.CLIENT.saveUiState);
        this.showChatOnMapToggle.setState(TerramapConfig.CLIENT.chatOnMap);
        this.doubleClickDelaySlider.setValue(TerramapConfig.CLIENT.doubleClickDelay);
        this.maxLoadedTilesSlider.setValue(TerramapConfig.CLIENT.maxTileLoad);
        this.lowZoomLevelSlider.setValue(TerramapConfig.CLIENT.lowZoomLevel);
        this.tpCommandField.setText(TerramapConfig.tpllcmd);
        this.debugMapStylesToggle.setState(TerramapConfig.enableDebugMaps);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        switch(keyCode) {
            case Keyboard.KEY_ESCAPE:
                Minecraft.getMinecraft().displayGuiScreen(new ConfirmScreen());
                break;
            default: super.keyTyped(typedChar, keyCode);
        }
    }

    private class ConfirmScreen extends Screen {

        public ConfirmScreen() {
            super(BackgroundOption.DEFAULT);
        }

        @Override
        public void initGui() {
            WidgetContainer content = this.getContent();
            content.removeAllWidgets();
            content.cancellAllScheduled();
            TextWidget text = new TextWidget(content.getWidth() / 2, content.getHeight() / 2 - 20, 10, new TextComponentTranslation("terramap.configmenu.asksave.prompt"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
            content.addWidget(text);
            content.addWidget(new TextButtonWidget(content.getWidth() / 2 - 125, text.getY() + text.getHeight() + 15, 10, 80, I18n.format("terramap.configmenu.asksave.deny"), TerramapConfigScreen.this::close));
            content.addWidget(new TextButtonWidget(content.getWidth() / 2 - 40, text.getY() + text.getHeight() + 15, 10, 80, I18n.format("terramap.configmenu.asksave.cancel"), () -> Minecraft.getMinecraft().displayGuiScreen(TerramapConfigScreen.this)));
            content.addWidget(new TextButtonWidget(content.getWidth() / 2 + 45, text.getY() + text.getHeight() + 15, 10, 80, I18n.format("terramap.configmenu.asksave.confirm"),TerramapConfigScreen.this::saveAndClose));
        }
    }

    protected enum TileScalingOption {

        AUTO(0), POINT5(0.5), ONE(1), TWO(2), FOUR(4), HEIGHT(8);

        double value;

        TileScalingOption(double v) {
            this.value = v;
        }

        protected static TileScalingOption getFromValue(double val) {
            for(TileScalingOption o: TileScalingOption.values()) {
                if(o.value == val) return o;
            }
            return AUTO;
        }

        @Override
        public String toString() {
            if(this == AUTO) {
                return I18n.format("terramap.configmenu.tilescaling.auto");
            }
            return "" + this.value;
        }
    }

}
