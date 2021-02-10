package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.function.Consumer;

import fr.thesmyler.smylibgui.Utils;

public class IntegerSliderWidget extends AbstractSliderWidget {

	protected long min, max, value;
	protected Consumer<Long> onChange;
	
	public IntegerSliderWidget(int x, int y, int z, int width, int min, int max, int startValue) {
		super(x, y, z, width);
		this.min = min;
		this.max = max;
		this.value = startValue;
	}
	
	public IntegerSliderWidget(int z, int min, int max, int startValue) {
		this(0, 0, z, 50, min, max, startValue);
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.value = Math.round((this.max - this.min) * sliderPosition + this.min);
		this.onChange();
	}

	@Override
	protected float getPosition() {
		return (float)(this.value - this.min)/ (this.max - this.min);
	}

	@Override
	protected String getDisplayString() {
		return "" + this.value;
	}
	
	public long getMin() {
		return this.min;
	}
	
	public IntegerSliderWidget setMin(long min) {
		this.min = min;
		this.setValue(this.value);
		return this;
	}
	
	public long getMax() {
		return this.max;
	}
	
	public IntegerSliderWidget setMax(long max) {
		this.max = max;
		this.setValue(this.value);
		return this;
	}
	
	public long getValue() {
		return this.value;
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
	
	public void setValue(long value) {
		this.value = Utils.clamp(value, this.min, this.max);
		this.onChange();
	}
	
	protected void onChange() {
		if(this.onChange != null) this.onChange.accept(this.getValue());
	}
	
	public void setOnChange(Consumer<Long> onChange) {
		this.onChange = onChange;
	}

}
