package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.function.Consumer;

import fr.thesmyler.smylibgui.Utils;

public class FloatSliderWidget extends AbstractSliderWidget {

	protected double min, max, value;
	private int resolution;
	protected Consumer<Double> onChange;
	
	public FloatSliderWidget(int x, int y, int z, int width, double min, double max, double startValue) {
		super(x, y, z, width);
		this.min = min;
		this.max = max;
		this.value = startValue;
		this.resolution = (int) Math.pow(10, Math.max(1, Math.ceil(Math.log10((float)width / (max - min)))));
	}
	
	public FloatSliderWidget(int z, double min, double max, double startValue) {
		this(0, 0, z, 50, min, max, startValue);
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.value = (this.max - this.min) * sliderPosition + this.min;
		this.onChange();
	}

	@Override
	protected float getPosition() {
		return (float) ((this.value - this.min) / (this.max - this.min));
	}

	@Override
	protected String getDisplayString() {
		return "" + (float)Math.round(this.value*resolution) / resolution;
	}
	
	public double getMin() {
		return this.min;
	}
	
	public FloatSliderWidget setMin(double min) {
		this.min = min;
		this.setValue(this.value);
		return this;
	}
	
	public double getMax() {
		return this.max;
	}
	
	public FloatSliderWidget setMax(double max) {
		this.max = max;
		this.setValue(this.value);
		return this;
	}

	@Override
	public void goToNext() {
		this.setValueFromPos(Utils.saturate(this.getPosition() + 0.01f));
		this.onChange();
	}

	@Override
	public void goToPrevious() {
		this.setValueFromPos(Utils.saturate(this.getPosition() - 0.01f));
		this.onChange();
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = Utils.clamp(value, this.min, this.max);
		this.onChange();
	}
	
	protected void onChange() {
		if(this.onChange != null) this.onChange.accept(this.getValue());
	}

	public void setOnChange(Consumer<Double> onChange) {
		this.onChange = onChange;
	}
	
}
