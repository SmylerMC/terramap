package fr.thesmyler.smylibgui.widgets.buttons;

import java.util.function.Consumer;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class ToggleButtonWidget extends AbstractButtonWidget {

	protected boolean value;
	private int onEnableU, onEnableV, offEnableU, offEnableV, onDisableU, onDisableV, offDisableU, offDisableV,
	onEnableUFocus, onEnableVFocus, offEnableUFocus, offEnableVFocus;
	protected Consumer<Boolean> onChange;
	
	public ToggleButtonWidget(
			int x, int y, int z, int width, int height,
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
	
	public ToggleButtonWidget(int x, int y, int z, boolean startValue, Consumer<Boolean> onChange) {
		this(x, y, z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, onChange);
	}
	
	public ToggleButtonWidget(int x, int y, int z, boolean startValue) {
		this(x, y, z, 26, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, null);
	}
	
	public ToggleButtonWidget(
			int z, int width, int height,
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
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(SmyLibGui.WIDGET_TEXTURES);
		GlStateManager.color(255, 255, 255, 255);
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
		parent.drawTexturedModalRect(x, y, u, v, this.getWidth(), this.getHeight());
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
