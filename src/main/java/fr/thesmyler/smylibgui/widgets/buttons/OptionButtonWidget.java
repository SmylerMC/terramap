package fr.thesmyler.smylibgui.widgets.buttons;

import java.util.function.Consumer;

public class OptionButtonWidget<T> extends TextButtonWidget {

	protected T[] options;
	protected int option;
	protected Consumer<T> onCycle;
	
	public OptionButtonWidget(int x, int y, int width, int z, T[] options, int startOption, Consumer<T> onCycle) {
		super(x, y, width, "", z, null, null);
		this.onClick = this::cycle;
		this.onDoubleClick = this::cycle;
		this.options = options;
		this.option = startOption;
		this.onCycle = onCycle;
	}
	
	public OptionButtonWidget(int x, int y, int width, int z, T[] options, Consumer<T> onCycle) {
		this(x, y, width, z, options, 0, onCycle);
	}
	
	public OptionButtonWidget(int x, int y, int width, int z, T[] options) {
		this(x, y, width, z, options, 0, null);
	}
	
	public void cycle() {
		this.option = Math.floorMod(this.option + 1, this.options.length);
		if(this.onCycle != null) this.onCycle.accept(this.getCurrentOption());
	}

	@Override
	public String getText() {
		return this.getCurrentOption().toString();
	}
	
	public T getCurrentOption() {
		return this.options[this.option];
	}

}
