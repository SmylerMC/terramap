package fr.thesmyler.smylibgui.screen;

import net.smyler.smylib.game.Key;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * A screen that shows up on to of other screens.
 *
 * @author SmylerMC
 */
public class PopupScreen extends Screen {

    private GuiScreen other;
    private final WidgetContainer content;
    private float contentWidth;
    private float contentHeight;
    private boolean closeOnClickOutContent = true;
    private Color contentBackgroundColor = Color.DARKER_OVERLAY;
    private Color shadeColor = Color.TRANSPARENT;
    private Color contourColor = Color.DARK_GRAY;
    private float contourSize = 2f;

    public PopupScreen(float contentWidth, float contentHeight) {
        super(BackgroundOption.NONE);
        this.content = new ContentContainer();
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
        super.getContent().addWidget(this.content);
    }

    public void show() {
        this.other = Minecraft.getMinecraft().currentScreen;
        Object o = new Object() {
            
            @SubscribeEvent
            public void onPostGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
                Minecraft.getMinecraft().displayGuiScreen(PopupScreen.this);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            
        };
        MinecraftForge.EVENT_BUS.register(o);
    }

    @Override
    public void initGui() {
        if(this.other != null) this.other.setWorldAndResolution(this.mc, this.width, this.height);
        super.initGui();
    }

    @Override
    public void drawScreen(int x, int y, float partialTicks) {
        if(this.other != null) this.other.drawScreen(x, y, partialTicks);
        RenderUtil.drawRect(0, 0, this.width, this.height, this.shadeColor);
        super.drawScreen(x, y, partialTicks);
    }


    @Override
    public void updateScreen() {
        this.other.updateScreen();
        super.updateScreen();
    }

    @Override
    public WidgetContainer getContent() {
        return this.content;
    }

    public void close() {
        if(Minecraft.getMinecraft().currentScreen != this) return;
        Minecraft.getMinecraft().displayGuiScreen(this.other);
    }

    public boolean isCloseOnClickOutContent() {
        return closeOnClickOutContent;
    }

    public void setCloseOnClickOutContent(boolean closeOnClickOutContent) {
        this.closeOnClickOutContent = closeOnClickOutContent;
    }

    public void setContentSize(float width, float height) {
        this.contentWidth = width;
        this.contentHeight = height;
        this.content.init();
    }

    /**
     * Shows a pup-up screen with a text component and an OK button.
     *
     * @param message   the text to display
     */
    public static void showMessage(ITextComponent message) {
        TextWidget text = new TextWidget(0, 0, 0, message, TextAlignment.CENTER, getGameClient().defaultFont());
        float padding = 10;
        text.setMaxWidth(300);
        text.setAnchorX(text.getWidth() / 2 + padding).setAnchorY(padding);
        TextButtonWidget button = new TextButtonWidget(
                text.getWidth() / 2 + padding - 20,
                text.getY() + text.getHeight() + padding,
                1, 40, getGameClient().translator().format("smylibgui.popup.info.ok"));
        PopupScreen screen = new PopupScreen(text.getWidth() + padding*2, button.getY() + padding + button.getHeight());
        button.setOnClick(screen::close);
        button.enable();
        screen.getContent().addWidget(text);
        screen.getContent().addWidget(button);
        screen.show();
    }

    private class ContentContainer extends WidgetContainer {

        public ContentContainer() {
            super(0);
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            float right = x + this.getWidth();
            float bottom = y + this.getHeight();
            RenderUtil.drawRect(x, y, right, bottom, PopupScreen.this.contentBackgroundColor);
            super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
            RenderUtil.drawClosedStrokeLine(PopupScreen.this.contourColor, PopupScreen.this.contourSize,
                    x, y,
                    x, bottom,
                    right, bottom,
                    right, y);
        }

        @Override
        public float getX() {
            return (PopupScreen.this.width - this.getWidth()) / 2;
        }

        @Override
        public float getY() {
            return (PopupScreen.this.height - this.getHeight()) / 2;
        }

        @Override
        public float getWidth() {
            return PopupScreen.this.contentWidth;
        }

        @Override
        public float getHeight() {
            return PopupScreen.this.contentHeight;
        }

        @Override
        public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            if(PopupScreen.this.closeOnClickOutContent) {
                PopupScreen.this.close();
                return false;
            }
            return super.onParentClick(mouseX, mouseY, mouseButton, parent);
        }

        @Override
        public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            if(PopupScreen.this.closeOnClickOutContent) return this.onParentClick(mouseX, mouseY, mouseButton, parent);
            return super.onParentDoubleClick(mouseX, mouseY, mouseButton, parent);
        }

        @Override
        public void onKeyTyped(char typedChar, @Nullable Key key, WidgetContainer parent) {
            if(key == Key.KEY_ESCAPE) PopupScreen.this.close();
            else super.onKeyTyped(typedChar, key, parent);
        }

    }

    public Color getContentBackgroundColor() {
        return contentBackgroundColor;
    }

    public void setContentBackgroundColor(Color contentBackgroundColor) {
        this.contentBackgroundColor = contentBackgroundColor;
    }

    public Color getShadeColor() {
        return shadeColor;
    }

    public void setShadeColor(Color shadeColor) {
        this.shadeColor = shadeColor;
    }

    public Color getContourColor() {
        return contourColor;
    }

    public void setContourColor(Color contourColor) {
        this.contourColor = contourColor;
    }

    public float getContourSize() {
        return contourSize;
    }

    public void setContourSize(float contourSize) {
        this.contourSize = contourSize;
    }

}
