package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.function.Consumer;

public class OptionSliderWidget<T> extends AbstractSliderWidget {

	protected T[] options;
	protected int option;
	protected Consumer<T> onCycle;
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options, int startOption, Consumer<T> onCycle) {
		super(x, y, z, width);
		this.options = options;
		this.option = startOption;
		this.onCycle = onCycle;
	}
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options, Consumer<T> onCycle) {
		this(x, y, z, width, options, 0, onCycle);
	}
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options) {
		this(x, y, z, width, options, null);
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.option = Math.round((this.options.length - 1)* sliderPosition);
	}

	@Override
	protected float getPosition() {
		return (float)this.option / (this.options.length - 1);
	}

	@Override
	protected String getDisplayString() {
		return this.options[option].toString();
	}
	
	public T getCurrentOption() {
		return this.options[this.option];
	}

	@Override
	public void goToNext() {
		this.option = (this.option + 1) % this.options.length;
		this.onCycle();
	}

	@Override
	public void goToPrevious() {
		this.option = Math.floorMod((this.option - 1), this.options.length);
		this.onCycle();
	}
	
	protected void onCycle() {
		if(this.onCycle != null) this.onCycle.accept(this.getCurrentOption());
	}

}
