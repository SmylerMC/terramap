package fr.thesmyler.terramap.gui.screens.config;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.ScrollableWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.devices.GameContext;
import fr.thesmyler.smylibgui.screen.PopupScreen;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import net.minecraft.util.text.TextComponentString;

import static fr.thesmyler.smylibgui.SmyLibGui.getDefaultFont;
import static fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons.CROSS;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

public class LayerConfigurationPopup extends PopupScreen {

    private final TextWidget titleText;
    private final TexturedButtonWidget closeButton;
    private final ScrollableWidgetContainer scroll;
    private final FlexibleWidgetContainer container;

    private static final float HEADER_HEIGHT = 24;
    private static final float CONTENT_MARGIN = 5f;

    public LayerConfigurationPopup(MapLayer layer) {
        super(500f, 500f);
        WidgetContainer content = this.getContent();
        this.container = requireNonNull(layer.createConfigurationContainer());
        this.titleText = new TextWidget(0,
                new TextComponentString(layer.name()),
                TextAlignment.CENTER, getDefaultFont());
        this.closeButton = new TexturedButtonWidget(0, CROSS, this::close);
        this.scroll = new ScrollableWidgetContainer(1f, 1f, 0,
                container.getWidth() - 2f, container.getHeight() - 2f,
                container);
        content.addWidget(this.titleText);
        content.addWidget(this.closeButton);
        content.addWidget(this.scroll);
        this.container.setDoScissor(false);
        this.scroll.setDoScissor(true);
        content.setDoScissor(false);
    }

    @Override
    public void initGui() {

        // Recalculate sizes
        GameContext gameContext = SmyLibGui.getGameContext();
        float mcWidth = gameContext.getWindowWidth();
        float mcHeight = gameContext.getWindowHeight();
        float contourSize = this.getContourSize();
        float contentWidth = this.container.getWidth();
        float contentHeight = this.container.getHeight();
        float scrollWidth = min(mcWidth - contourSize - CONTENT_MARGIN * 2f, contentWidth + 15f);
        float windowWidth = scrollWidth + contourSize + CONTENT_MARGIN * 2f;
        float scrollHeight = min(mcHeight - contourSize - HEADER_HEIGHT - CONTENT_MARGIN, contentHeight + 15f);
        float windowHeight = scrollHeight + contourSize + HEADER_HEIGHT + CONTENT_MARGIN;
        this.setContentSize(windowWidth, windowHeight);
        this.scroll.setPosition(CONTENT_MARGIN + contourSize, HEADER_HEIGHT);
        this.scroll.setSize(scrollWidth, scrollHeight);

        // Move stuff around
        this.titleText.setAnchorX((windowWidth - 15 - CONTENT_MARGIN * 2) / 2);
        this.titleText.setAnchorY(CONTENT_MARGIN + 2f);
        this.closeButton.setX(windowWidth - CONTENT_MARGIN - this.closeButton.getWidth());
        this.closeButton.setY(CONTENT_MARGIN);
        super.initGui();
    }

}
