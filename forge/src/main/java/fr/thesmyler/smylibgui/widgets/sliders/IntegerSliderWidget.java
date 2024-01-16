package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.function.Consumer;

import static net.smyler.smylib.math.Math.clamp;
import static net.smyler.smylib.math.Math.saturate;

public class IntegerSliderWidget extends AbstractSliderWidget {

    protected long min, max, value;
    protected Consumer<Long> onChange;

    public IntegerSliderWidget(float x, float y, int z, float width, float height, int min, int max, int startValue) {
        super(x, y, z, width, height);
        this.min = min;
        this.max = max;
        this.value = startValue;
    }
    
    public IntegerSliderWidget(float x, float y, int z, float width, int min, int max, int startValue) {
        this(x, y, z, width, 20, min, max, startValue);
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
        return String.valueOf(this.value);
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
        this.setValueFromPos(saturate(this.getPosition() + 0.01f));
        this.onChange();
    }

    @Override
    public void goToPrevious() {
        this.setValueFromPos(saturate(this.getPosition() - 0.01f));
        this.onChange();
    }

    public void setValue(long value) {
        this.value = clamp(value, this.min, this.max);
        this.onChange();
    }

    protected void onChange() {
        if(this.onChange != null) this.onChange.accept(this.getValue());
    }

    public void setOnChange(Consumer<Long> onChange) {
        this.onChange = onChange;
    }

}
