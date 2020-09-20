package fr.thesmyler.smylibgui.widgets;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.screen.Screen;
import net.minecraft.client.gui.GuiScreen;

public class SlidingPannelWidget extends Screen {
	
	protected int showX, hiddenX, showY, hiddenY;
	protected int bgColor = 0xA0000000;
	protected Animation mainAnimation;
	protected boolean closeOnClickOther = false;
	
	public SlidingPannelWidget(int showX, int hiddenX, int showY, int hiddenY, int z, int width, int height, long delay) {
		super(hiddenX, hiddenY, z, width, height, BackgroundType.NONE);
		this.showX = showX;
		this.showY = showY;
		this.hiddenX = hiddenX;
		this.hiddenY = hiddenY;
		this.mainAnimation = new Animation(delay);
	}
	
	public SlidingPannelWidget(int z, long delay) {
		this(0, 0, 0, 0, z, 50, 50, delay);
	}
	
	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, @Nullable Screen parent){
		GuiScreen.drawRect(x, y, x + this.width, y + this.height, this.bgColor);
		super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
		this.mainAnimation.update();
	}
	
	public PannelTarget getTarget() {
		switch(this.mainAnimation.getState()) {
		case LEAVE:
			return PannelTarget.CLOSED;
		case ENTER:
			return PannelTarget.OPENNED;
		default:
			return this.mainAnimation.getProgress() < 0.5 ? PannelTarget.CLOSED: PannelTarget.OPENNED;
		}
	}
	
	@Override
	public boolean onParentClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		if(this.closeOnClickOther && !this.getTarget().equals(PannelTarget.CLOSED)) {
			this.hide();
			return false;
		}
		return true;
	}
	
	public void show() {
		this.mainAnimation.start(AnimationState.ENTER);
	}
	
	public void hide() {
		this.mainAnimation.start(AnimationState.LEAVE);
	}
	
	public int getShowX() {
		return this.showX;
	}
	
	public SlidingPannelWidget setShowX(int x) {
		this.showX = x;
		return this;
	}
	
	public int getHiddenX() {
		return this.hiddenX;
	}
	
	public SlidingPannelWidget setHiddenX(int x) {
		this.hiddenX = x;
		return this;
	}
	
	public int getShowY() {
		return this.showY;
	}
	
	public SlidingPannelWidget setShowY(int y) {
		this.showY = y;
		return this;
	}
	
	public int getHiddenY() {
		return this.hiddenY;
	}
	
	public SlidingPannelWidget setHiddenY(int y) {
		this.hiddenY = y;
		return this;
	}
	
	public SlidingPannelWidget setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public SlidingPannelWidget setHeight(int height) {
		this.height = height;
		return this;
	}
	
	public boolean closesOnClickOther() {
		return this.closeOnClickOther;
	}
	
	public SlidingPannelWidget setCloseOnClickOther(boolean yesNo) {
		this.closeOnClickOther = yesNo;
		return this;
	}
	
	@Override
	public int getX() {
		return this.mainAnimation.between(this.showX, this.hiddenX);
	}
	
	@Override
	public int getY() {
		return this.mainAnimation.between(this.showY, this.hiddenY);
	}
	
	public int getBackroundColor() {
		return this.bgColor;
	}
	
	public SlidingPannelWidget setBackgroundColor(int color) {
		this.bgColor = color;
		return this;
	}
	
	public enum PannelTarget {
		OPENNED, CLOSED;
	}
	
}
