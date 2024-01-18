package fr.thesmyler.smylibgui.widgets.buttons;

import java.util.function.Consumer;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.smyler.smylib.gui.DrawContext;

import static net.smyler.smylib.Color.WHITE;
import static fr.thesmyler.smylibgui.util.RenderUtil.applyColor;

public class ToggleButtonWidget extends AbstractButtonWidget {

    protected boolean value;
    private final int onEnableU;
    private final int onEnableV;
    private final int offEnableU;
    private final int offEnableV;
    private final int onDisableU;
    private final int onDisableV;
    private final int offDisableU;
    private final int offDisableV;
    private final int onEnableUFocus;
    private final int onEnableVFocus;
    private final int offEnableUFocus;
    private final int offEnableVFocus;
    protected Consumer<Boolean> onChange;

    public ToggleButtonWidget(
            float x, float y, int z, float width, float height,
            int onEnableU, int onEnableV, int offEnableU, int offEnableV,
            int onDisableU, int onDisableV, int offDisableU, int offDisableV,
            int onEnableUFocus, int onEnableVFocus, int offEnableUFocus, int offEnableVFocus,
            boolean startValue, Consumer<Boolean> onChange) {
        super(x, y, z, width, height, null);
        this.onClick = this::toggle;
        this.onDoubleClick = this::toggle;
        this.value = startValue;
        this.onEnableU = onEnableU;
        this.onEnableV = onEnableV;
        this.offEnableU = offEnableU;
        this.offEnableV = offEnableV;
        this.onDisableU = onDisableU;
        this.onDisableV = onDisableV;
        this.offDisableU = offDisableU;
        this.offDisableV = offDisableV;
        this.onEnableUFocus = onEnableUFocus;
        this.onEnableVFocus = onEnableVFocus;
        this.offEnableUFocus = offEnableUFocus;
        this.offEnableVFocus = offEnableVFocus;
        this.onChange = onChange;
    }

    public ToggleButtonWidget(float x, float y, int z, boolean startValue, Consumer<Boolean> onChange) {
        this(x, y, z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, onChange);
    }

    public ToggleButtonWidget(float x, float y, int z, boolean startValue) {
        this(x, y, z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, null);
    }

    public ToggleButtonWidget(
            int z, float width, float height,
            int onEnableU, int onEnableV, int offEnableU, int offEnableV,
            int onDisableU, int onDisableV, int offDisableU, int offDisableV,
            int onEnableUFocus, int onEnableVFocus, int offEnableUFocus, int offEnableVFocus,
            boolean startValue, Consumer<Boolean> onChange) {
        this(
                0, 0, z, width, height,
                onEnableU, onEnableV, offEnableU, offEnableV,
                onDisableU, onDisableV, offDisableU, offDisableV,
                onEnableUFocus, onEnableVFocus, offEnableUFocus, offEnableVFocus,
                startValue, onChange);
    }

    public ToggleButtonWidget(int z, boolean startValue, Consumer<Boolean> onChange) {
        this(z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, onChange);
    }

    public ToggleButtonWidget(int z, boolean startValue) {
        this(z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, null);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(SmyLibGuiTextures.WIDGET_TEXTURES);
        applyColor(WHITE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int u = 0;
        int v = 0;
        if(!this.isEnabled()) {
            if(this.getState()) {
                u = this.onDisableU;
                v = this.onDisableV;
            } else {
                u = this.offDisableU;
                v = this.offDisableV;
            }
        } else if(hovered || hasFocus) {
            if(this.getState()) {
                u = this.onEnableUFocus;
                v = this.onEnableVFocus;
            } else {
                u = this.offEnableUFocus;
                v = this.offEnableVFocus;
            }
        } else {
            if(this.getState()) {
                u = this.onEnableU;
                v = this.onEnableV;
            } else {
                u = this.offEnableU;
                v = this.offEnableV;
            }
        }
        RenderUtil.drawTexturedModalRect(x, y, u, v, this.getWidth(), this.getHeight());
    }

    public void toggle() {
        this.value = !this.value;
        if(this.onChange != null) this.onChange.accept(this.value);
    }

    public boolean getState() {
        return this.value;
    }

    public void setState(boolean state) {
        this.value = state;
    }

    public ToggleButtonWidget setOnChange(Consumer<Boolean> action) {
        this.onChange = action;
        return this;
    }

}
