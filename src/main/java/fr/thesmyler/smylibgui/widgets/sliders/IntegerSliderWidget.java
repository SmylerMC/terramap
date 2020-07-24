package fr.thesmyler.smylibgui.widgets.sliders;

import fr.thesmyler.smylibgui.Utils;

public class IntegerSliderWidget extends AbstractSliderWidget {

	protected long min, max, value;
	
	public IntegerSliderWidget(int x, int y, int z, int width, int min, int max, int startValue) {
		super(x, y, z, width);
		this.min = min;
		this.max = max;
		this.value = startValue;
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.value = Math.round((this.max - this.min) * sliderPosition + this.min);
	}

	@Override
	protected float getPosition() {
		return (float)this.value / (this.max - this.min);
	}

	@Override
	protected String getDisplayString() {
		return "" + this.value;
	}
	
	public long getMin() {
		return this.min;
	}
	
	public long getMax() {
		return this.max;
	}
	
	public long getValue() {
		return this.value;
	}

	@Override
	public void goToNext() {
		this.setValueFromPos(Utils.saturate(this.getPosition() + 0.01f));
	}

	@Override
	public void goToPrevious() {
		this.setValueFromPos(Utils.saturate(this.getPosition() - 0.01f));
	}

}
