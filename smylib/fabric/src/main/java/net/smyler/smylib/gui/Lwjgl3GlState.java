package net.smyler.smylib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.smyler.smylib.Color;


public class Lwjgl3GlState implements GlState {
    @Override
    public void enableAlpha() {
        //TODO enable alpha
    }

    @Override
    public void disableAlpha() {
        //TODO disable alpha
    }

    @Override
    public void setColor(Color color) {
        RenderSystem.setShaderColor(color.redf(), color.greenf(), color.bluef(), color.alphaf());
    }

    @Override
    public Color getColor() {
        return null;  //TODO get color
    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {
        RenderSystem.enableColorLogicOp();
    }

    @Override
    public void disableColorLogic() {
        RenderSystem.disableColorLogicOp();
    }

    @Override
    public void pushViewMatrix() {
        RenderSystem.getModelViewStack().pushPose();
    }

    @Override
    public void rotate(double angle) {
        RenderSystem.getModelViewMatrix().rotate((float)(angle), 0f, 0f, 1f);
    }

    @Override
    public void translate(double x, double y) {
        RenderSystem.getModelViewMatrix().translate((float)x, (float)y, 0);
    }

    @Override
    public void scale(double x, double y) {
        RenderSystem.getModelViewMatrix().scale((float)x, (float)y, 1);
    }

    @Override
    public void popViewMatrix() {
        RenderSystem.getModelViewStack().popPose();
    }

}
