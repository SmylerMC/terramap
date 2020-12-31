package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;

public class OptionSliderWidget<T> extends AbstractSliderWidget {

	protected T[] options;
	protected int option;
	protected Consumer<T> onCycle;
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options, int startOptionIndex, Consumer<T> onCycle) {
		super(x, y, z, width);
		this.options = options;
		this.option = startOptionIndex;
		this.onCycle = onCycle;
	}
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options, Consumer<T> onCycle) {
		this(x, y, z, width, options, 0, onCycle);
	}
	
	public OptionSliderWidget(int x, int y, int z, int width, T[] options) {
		this(x, y, z, width, options, null);
	}
	
	public OptionSliderWidget(int z, T[] options, int startOption, Consumer<T> onCycle) {
		this(0, 0, z, 0, options, startOption, onCycle);
		int maxWidth = 0;
		for(T o: options) {
			int w = Minecraft.getMinecraft().fontRenderer.getStringWidth(o.toString());
			maxWidth = Math.max(maxWidth,  w);
		}
		this.setWidth(maxWidth + 20);
	}
	
	public OptionSliderWidget(int z, T[] options, Consumer<T> onCycle) {
		this(z, options, 0, onCycle);
	}
	
	public OptionSliderWidget(int z, T[] options) {
		this(z, options, null);
	}

	@Override
	protected void setValueFromPos(float sliderPosition) {
		this.option = Math.round((this.options.length - 1)* sliderPosition);
		this.onCycle();
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
	
	/**
	 * Sets the current option if it exists, it is added if not available
	 * 
	 * @param option - The option
	 */
	@SuppressWarnings("unchecked")
	public void setCurrentOption(T option) {
		for(int i=0; i<this.options.length; i++) {
			if(option.equals(this.options[i])) {
				this.option = i;
			}
		}
		List<T> l = new ArrayList<>();
		Collections.addAll(l, this.options);
		this.options = (T[]) l.toArray();
		this.onCycle();
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
	
	public OptionSliderWidget<T> setOnChange(Consumer<T> onChange) {
		this.onCycle = onChange;
		return this;
	}

}
