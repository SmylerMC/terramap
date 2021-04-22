package fr.thesmyler.smylibgui.widgets;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.screen.Screen;

public class SlidingPanelWidget extends Screen {

	protected float showX, hiddenX, showY, hiddenY;
	protected int bgColor = 0xA0000000;
	protected Animation mainAnimation;
	protected boolean closeOnClickOther = false;
	protected boolean visible = true;

	public SlidingPanelWidget(float showX, float hiddenX, float showY, float hiddenY, int z, float width, float height, long delay) {
		super(hiddenX, hiddenY, z, width, height, BackgroundType.NONE);
		this.showX = showX;
		this.showY = showY;
		this.hiddenX = hiddenX;
		this.hiddenY = hiddenY;
		this.mainAnimation = new Animation(delay);
	}

	public SlidingPanelWidget(int z, long delay) {
		this(0, 0, 0, 0, z, 50, 50, delay);
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, @Nullable Screen parent){
		RenderUtil.drawRect(x, y, x + this.width, y + this.height, this.bgColor);
		super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
		this.mainAnimation.update();
	}
	
	@Override
	public void onUpdate(Screen parent) {
		this.mainAnimation.update();
		super.onUpdate(parent);
	}

	public PanelTarget getTarget() {
		switch(this.mainAnimation.getState()) {
		case LEAVE:
			return PanelTarget.CLOSED;
		case ENTER:
			return PanelTarget.OPENED;
		default:
			return this.mainAnimation.getProgress() < 0.5 ? PanelTarget.CLOSED: PanelTarget.OPENED;
		}
	}

	@Override
	public boolean onParentClick(float mouseX, float mouseY, int mouseButton, @Nullable Screen parent) {
		if(this.closeOnClickOther && !this.getTarget().equals(PanelTarget.CLOSED)) {
			this.close();
			return false;
		}
		return true;
	}

	public void open() {
		this.mainAnimation.start(AnimationState.ENTER);
	}

	public void close() {
		this.mainAnimation.start(AnimationState.LEAVE);
	}

	public SlidingPanelWidget setStateNoAnimation(boolean opened) {
		this.mainAnimation.start(opened? AnimationState.LEAVE: AnimationState.ENTER);
		this.mainAnimation.stop();
		return this;
	}

	public float getOpenX() {
		return this.showX;
	}

	public SlidingPanelWidget setOpenX(float x) {
		this.showX = x;
		return this;
	}

	public float getClosedX() {
		return this.hiddenX;
	}

	public SlidingPanelWidget setClosedX(float x) {
		this.hiddenX = x;
		return this;
	}

	public float getOpenY() {
		return this.showY;
	}

	public SlidingPanelWidget setOpenY(float y) {
		this.showY = y;
		return this;
	}

	public float getClosedY() {
		return this.hiddenY;
	}

	public SlidingPanelWidget setClosedY(float y) {
		this.hiddenY = y;
		return this;
	}

	public SlidingPanelWidget setWidth(float width) {
		//TODO handle float width
//		this.width = width;
		this.width = Math.round(width);
		return this;
	}

	public SlidingPanelWidget setHeight(float height) {
		//TODO handle float height
//		this.height = height;
		this.height = Math.round(height);
		return this;
	}

	public boolean closesOnClickOther() {
		return this.closeOnClickOther;
	}

	public SlidingPanelWidget setCloseOnClickOther(boolean yesNo) {
		this.closeOnClickOther = yesNo;
		return this;
	}

	@Override
	public float getX() {
		return this.mainAnimation.blend(this.showX, this.hiddenX);
	}

	@Override
	public float getY() {
		return this.mainAnimation.blend(this.showY, this.hiddenY);
	}

	public int getBackroundColor() {
		return this.bgColor;
	}

	public SlidingPanelWidget setBackgroundColor(int color) {
		this.bgColor = color;
		return this;
	}
	
	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}
	
	public SlidingPanelWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
	public SlidingPanelWidget show() {
		return this.setVisibility(true);
	}
	
	public SlidingPanelWidget hide() {
		return this.setVisibility(false);
	}

	public enum PanelTarget {
		OPENED, CLOSED;
	}

}
