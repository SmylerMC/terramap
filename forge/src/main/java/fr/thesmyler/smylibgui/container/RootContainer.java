package fr.thesmyler.smylibgui.container;

import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

import static java.lang.Math.floor;

/**
 * A container with no parent.
 * The key difference is that this container is in charge of drawing tooltips.
 *
 * @author Smyler
 */
public abstract class RootContainer extends WidgetContainer {

    private long startHoverTime;
    private IWidget lastHoveredWidget;
    private float lastRenderMouseX, lastRenderMouseY;
    private final GuiScreen screen;

    public RootContainer(GuiScreen screen) {
        super(0);
        this.screen = screen;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
        super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        IWidget hoveredWidget = this.getHoveredWidget();
        boolean mouseMoved = mouseX != this.lastRenderMouseX && mouseY != this.lastRenderMouseY;
        if(mouseMoved || (hoveredWidget != null && !hoveredWidget.equals(this.lastHoveredWidget))) {
            this.startHoverTime = System.currentTimeMillis();
        }
        if(
                hoveredWidget != null
                        && hoveredWidget.getTooltipText() != null
                        && !hoveredWidget.getTooltipText().isEmpty()
                        && this.startHoverTime + hoveredWidget.getTooltipDelay() <= System.currentTimeMillis()
        ) {
            this.drawHoveringText(hoveredWidget.getTooltipText(), mouseX, mouseY);
        }
        this.lastHoveredWidget = hoveredWidget;
        this.lastRenderMouseX = mouseX;
        this.lastRenderMouseY = mouseY;
    }

    public void drawHoveringText(String text, double x, double y) {
        // This is a workaround for vanilla not allowing double coordinates and re-enabling lighting without any check
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        int px = (int) floor(x);
        int py = (int) floor(y);
        double rx = x - px;
        double ry = y - py;
        GlStateManager.translate(rx, ry, 0);
        this.screen.drawHoveringText(text, px, py);
        GlStateManager.popMatrix();
        if(!lighting) GlStateManager.disableLighting();
    }

}
