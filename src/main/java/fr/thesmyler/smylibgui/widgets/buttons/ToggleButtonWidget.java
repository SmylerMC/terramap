package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

//FIXME Default texture is cut
public class ToggleButtonWidget extends AbstractButtonWidget {

	protected boolean value;
	private int onEnableU, onEnableV, offEnableU, offEnableV, onDisableU, onDisableV, offDisableU, offDisableV,
	onEnableUFocus, onEnableVFocus, offEnableUFocus, offEnableVFocus;
	protected Runnable onEnable, onDisable;
	
	public ToggleButtonWidget(
			int x, int y, int z, int width, int height,
			int onEnableU, int onEnableV, int offEnableU, int offEnableV,
			int onDisableU, int onDisableV, int offDisableU, int offDisableV,
			int onEnableUFocus, int onEnableVFocus, int offEnableUFocus, int offEnableVFocus,
			boolean startValue, Runnable onEnable, Runnable onDisable) {
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
		this.onEnable = onEnable;
		this.onDisable = onDisable;
	}
	
	public ToggleButtonWidget(int x, int y, int z, boolean startValue, Runnable onEnable, Runnable onDisable) {
		this(x, y, z, 25, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, onEnable,  onDisable);
	}
	
	public ToggleButtonWidget(int x, int y, int z, boolean startValue) {
		this(x, y, z, 25, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, null,  null);
	}
	
	public ToggleButtonWidget(
			int z, int width, int height,
			int onEnableU, int onEnableV, int offEnableU, int offEnableV,
			int onDisableU, int onDisableV, int offDisableU, int offDisableV,
			int onEnableUFocus, int onEnableVFocus, int offEnableUFocus, int offEnableVFocus,
			boolean startValue, Runnable onEnable, Runnable onDisable) {
		this(
			0, 0, z, width, height,
			onEnableU, onEnableV, offEnableU, offEnableV,
			onDisableU, onDisableV, offDisableU, offDisableV,
			onEnableUFocus, onEnableVFocus, offEnableUFocus, offEnableVFocus,
			startValue, onEnable, onDisable);
	}
	
	public ToggleButtonWidget(int z, boolean startValue, Runnable onEnable, Runnable onDisable) {
		this(z, 25, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, onEnable,  onDisable);
	}
	
	public ToggleButtonWidget(int z, boolean startValue) {
		this(z, 25, 15, 30, 2, 2, 2, 30, 38, 2, 38, 30, 20, 2, 20, startValue, null,  null);
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
		if(this.value && this.onEnable != null) this.onEnable.run();
		else if(!this.value && this.onDisable != null) this.onDisable.run();
	}
	
	public boolean getState() {
		return this.value;
	}
	
	public void setState(boolean state) {
		this.value = state;
	}
	
	public ToggleButtonWidget setOnActivate(Runnable action) {
		this.onEnable = action;
		return this;
	}
	
	public ToggleButtonWidget setOnDeactivate(Runnable action) {
		this.onDisable = action;
		return this;
	}

}
