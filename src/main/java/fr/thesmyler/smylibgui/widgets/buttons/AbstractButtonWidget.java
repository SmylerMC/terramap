package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

//TODO Add the possibility to bind a keybinding to a button
public abstract class AbstractButtonWidget implements IWidget {
	
	protected int x, y, z, width, height;
	protected Runnable onClick;
	protected Runnable onDoubleClick;
	protected boolean enabled = true;
	
	public AbstractButtonWidget(int x, int y, int width, int height, int z, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.onClick = onClick;
		this.onDoubleClick = onDoubleClick;
	}
	
	public AbstractButtonWidget(int x, int y, int width, int height, int z, @Nullable Runnable onClick) {
		this(x, y, width, height, z, onClick, null);
	}
	
	public AbstractButtonWidget(int x, int y, int width, int height, int z) {
		this(x, y, width, height, z, null, null);
		this.disable();
	}

	@Override
	public abstract void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent);
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if(parent != null && parent.getFocusedWidget() != null && parent.getFocusedWidget().equals(this)) {
			parent.setFocus(null); //We don't want to keep the focus
		}
		if(this.onClick != null) {
			this.onClick.run();
			parent.setFocus(null);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if(parent != null && parent.getFocusedWidget() != null && parent.getFocusedWidget().equals(this)) {
			parent.setFocus(null);
		}
		if(this.onDoubleClick != null) {
			this.onDoubleClick.run();
			parent.setFocus(null);
			return false;
		}
		return true;
	}
	
	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean yesNo) {
		this.enabled = yesNo;
	}
	
	public void enable() {
		this.setEnabled(true);
	}
	
	public void disable() {
		this.setEnabled(false);
	}

}
