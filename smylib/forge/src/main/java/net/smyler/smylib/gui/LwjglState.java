package net.smyler.smylib.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.smyler.smylib.Color;
import org.lwjgl.opengl.GL11;

public class LwjglState implements GlState {
    @Override
    public void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    @Override
    public void disableAlpha() {
        GlStateManager.disableAlpha();
    }

    @Override
    public void setColor(Color color) {
        GlStateManager.color(
            color.redf(),
            color.greenf(),
            color.bluef(),
            color.alphaf()
        );
    }

    @Override
    public Color getColor() {
        return new Color(GL11.glGetInteger(GL11.GL_CURRENT_COLOR));
    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {
        GlStateManager.LogicOp op = this.getGlColorLogic(colorLogic);
        GlStateManager.colorLogicOp(op);
        GlStateManager.enableColorLogic();
    }

    private GlStateManager.LogicOp getGlColorLogic(ColorLogic logic) {
        switch (logic) {
            case CLEAR:
                return GlStateManager.LogicOp.CLEAR;
            case SET:
                return GlStateManager.LogicOp.SET;
            case COPY:
                return GlStateManager.LogicOp.COPY;
            case COPY_INVERTED:
                return GlStateManager.LogicOp.COPY_INVERTED;
            case NOOP:
                return GlStateManager.LogicOp.NOOP;
            case INVERT:
                return GlStateManager.LogicOp.INVERT;
            case AND:
                return GlStateManager.LogicOp.AND;
            case NAND:
                return GlStateManager.LogicOp.NAND;
            case OR:
                return GlStateManager.LogicOp.OR;
            case NOR:
                return GlStateManager.LogicOp.NOR;
            case XOR:
                return GlStateManager.LogicOp.XOR;
            case EQUIV:
                return GlStateManager.LogicOp.EQUIV;
            case AND_REVERSE:
                return GlStateManager.LogicOp.AND_REVERSE;
            case OR_REVERSE:
                return GlStateManager.LogicOp.OR_REVERSE;
            case OR_INVERTED:
                return GlStateManager.LogicOp.OR_INVERTED;
        }
        throw new IllegalStateException("Illegal enum value");
    }

    @Override
    public void disableColorLogic() {
        GlStateManager.disableColorLogic();
    }

}
