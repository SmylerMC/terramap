package fr.thesmyler.smylibgui.widgets.sliders;

import fr.thesmyler.smylibgui.Utils;

public class FloatSliderWidget extends AbstractSliderWidget {

	protected double min, max, value;
	private int resolution;
	
	public FloatSliderWidget(int x, int y, int z, int width, double min, double max, double startValue) {
		super(x, y, z, width);
		this.min = min;
		this.max = max;
		this.value = startValue;
		this.resolution = (int) Math.pow(10, Math.max(1, Math.ceil(Math.log10((float)width / (max - min)))));
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.value = (this.max - this.min) * sliderPosition + this.min;
	}

	@Override
	protected float getPosition() {
		return (float) (this.value / (this.max - this.min));
	}

	@Override
	protected String getDisplayString() {
		return "" + (float)Math.round(this.value*resolution) / resolution;
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
