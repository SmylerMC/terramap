package fr.thesmyler.smylibgui.widgets.sliders;

public class OptionSliderWidget extends AbstractSliderWidget {

	protected String[] options;
	protected int option;
	
	public OptionSliderWidget(int x, int y, int z, int width, String[] options, int startOption) {
		super(x, y, z, width);
		this.options = options;
		this.option = startOption;
	}
	
	public OptionSliderWidget(int x, int y, int z, int width, String[] options) {
		this(x, y, z, width, options, 0);
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
		return this.options[option];
	}

	@Override
	public void goToNext() {
		this.option = (this.option + 1) % this.options.length;
	}

	@Override
	public void goToPrevious() {
		this.option = Math.floorMod((this.option - 1), this.options.length);
	}

}
