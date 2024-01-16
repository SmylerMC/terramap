package fr.thesmyler.smylibgui.widgets.sliders;

import java.util.function.Consumer;

import static net.smyler.smylib.math.Math.clamp;
import static net.smyler.smylib.math.Math.saturate;

public class FloatSliderWidget extends AbstractSliderWidget {

    private double min, max, value;
    private int resolution;
    protected Consumer<Double> onChange;

    public FloatSliderWidget(float x, float y, int z, float width, float height, double min, double max, double startValue) {
        super(x, y, z, width, height);
        this.min = min;
        this.max = max;
        this.value = startValue;
        this.updateResolution();
    }
    
    public FloatSliderWidget(float x, float y, int z, float width, double min, double max, double startValue) {
        this(x, y, z, width, 20, min, max, startValue);
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
        return String.valueOf((float) Math.round(this.value * this.resolution) / this.resolution);
    }

    public double getMin() {
        return this.min;
    }

    public FloatSliderWidget setMin(double min) {
        this.min = min;
        this.setValue(this.value);
        this.onChange();
        return this;
    }

    public double getMax() {
        return this.max;
    }

    public FloatSliderWidget setMax(double max) {
        this.max = max;
        this.setValue(this.value);
        this.onChange();
        return this;
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

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = clamp(value, this.min, this.max);
        this.onChange();
    }

    protected void onChange() {
        this.updateResolution();
        if(this.onChange != null) this.onChange.accept(this.getValue());
    }

    private void updateResolution() {
        this.resolution = (int) Math.pow(10, Math.max(1, Math.ceil(Math.log10(width / (max - min)))));
    }

    public void setOnChange(Consumer<Double> onChange) {
        this.onChange = onChange;
    }

}
