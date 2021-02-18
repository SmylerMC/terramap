package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.AbstractWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

//TODO Add the possibility to bind a keybinding to a button
public abstract class AbstractButtonWidget extends AbstractWidget {

	protected Runnable onClick;
	protected Runnable onDoubleClick;
	protected boolean enabled = true;

	public AbstractButtonWidget(int x, int y, int z, int width, int height, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
		super(x, y, z, width, height);
		this.onClick = onClick;
		this.onDoubleClick = onDoubleClick;
	}

	public AbstractButtonWidget(int x, int y, int z, int width, int height, @Nullable Runnable onClick) {
		this(x, y, z, width, height, onClick, onClick);
	}

	public AbstractButtonWidget(int x, int y, int z, int width, int height) {
		this(x, y, z, width, height, null);
		this.disable();
	}

	@Override
	public abstract void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent);

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(!this.isEnabled()) return false;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		parent.setFocus(null); // We don't want to keep the focus
		if(this.onClick != null && mouseButton == 0) {
			this.onClick.run();
		}
		return false;
	}

	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(!this.isEnabled()) return false;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		parent.setFocus(null);
		if(mouseButton == 0) {
			if(this.onDoubleClick != null) {
				this.onDoubleClick.run();
			} else if(this.onClick != null){
				this.onClick.run();
			}
		}
		return false;
	}

	@Override
	public long getTooltipDelay() {
		return 750;
	}

	public Runnable getOnClick() {
		return onClick;
	}

	public AbstractButtonWidget setOnClick(Runnable onClick) {
		this.onClick = onClick;
		return this;
	}

	public Runnable getOnDoubleClick() {
		return onDoubleClick;
	}

	public AbstractButtonWidget setOnDoubleClick(Runnable onDoubleClick) {
		this.onDoubleClick = onDoubleClick;
		return this;
	}

	public AbstractButtonWidget setX(int x) {
		this.x = x;
		return this;
	}

	public AbstractButtonWidget setY(int y) {
		this.y = y;
		return this;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public AbstractButtonWidget setEnabled(boolean yesNo) {
		this.enabled = yesNo;
		return this;
	}

	public AbstractButtonWidget enable() {
		this.setEnabled(true);
		return this;
	}

	public AbstractButtonWidget disable() {
		this.setEnabled(false);
		return this;
	}

}
