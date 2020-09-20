package fr.thesmyler.smylibgui.widgets.buttons;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;

public class OptionButtonWidget<T> extends TextButtonWidget {

	protected T[] options;
	protected int option;
	protected Consumer<T> onCycle;
	
	public OptionButtonWidget(int x, int y, int z, int width, T[] options, int startOption, Consumer<T> onCycle) {
		super(x, y, z, width, "", null, null);
		this.onClick = this::cycle;
		this.onDoubleClick = this::cycle;
		this.options = options;
		this.option = startOption;
		this.onCycle = onCycle;
	}
	
	public OptionButtonWidget(int x, int y, int z, int width, T[] options, Consumer<T> onCycle) {
		this(x, y, z, width, options, 0, onCycle);
	}
	
	public OptionButtonWidget(int x, int y, int z, int width, T[] options) {
		this(x, y, z, width, options, 0, null);
	}
	
	public OptionButtonWidget(int z, T[] options, int startOption, Consumer<T> onCycle) {
		super(z, "", null, null);
		int maxWidth = 0;
		for(T o: options) {
			int w = Minecraft.getMinecraft().fontRenderer.getStringWidth(o.toString());
			maxWidth = Math.max(maxWidth,  w);
		}
		this.width = maxWidth + 20;
		this.onClick = this::cycle;
		this.onDoubleClick = this::cycle;
		this.options = options;
		this.option = startOption;
		this.onCycle = onCycle;
	}
	
	public OptionButtonWidget(int z, T[] options, Consumer<T> onCycle) {
		this(z, options, 0, onCycle);
	}
	
	public OptionButtonWidget(int z, T[] options) {
		this(z, options, 0, null);
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
