package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import org.jetbrains.annotations.NotNull;

import static java.lang.Double.isFinite;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Preconditions.checkState;

public class Sprite {

    public final Identifier texture;
    public final double textureWidth, textureHeight;
    public final double xLeft, yTop, xRight, yBottom;

    private Sprite(Identifier texture, double textureWidth, double textureHeight, double xLeft, double yTop, double xRight, double yBottom) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xLeft = xLeft;
        this.yTop = yTop;
        this.xRight = xRight;
        this.yBottom = yBottom;
    }

    public double width() {
        return this.xRight - this.xLeft;
    }

    public double height() {
        return this.yBottom - this.yTop;
    }

    public static final class Builder {
        private Identifier texture = null;

        private double textureWidth = Double.NaN;
        private double textureHeight = Double.NaN;

        private double xLeft = Double.NaN;
        private double yTop = Double.NaN;
        private double xRight = Double.NaN;
        private double yBottom = Double.NaN;

        public Builder texture(Identifier texture) {
            this.texture = texture;
            return this;
        }

        public Builder textureWidth(double textureWidth) {
            checkArgument(isFinite(textureWidth) && textureWidth > 0, "Invalid texture width: " + textureWidth);
            this.textureWidth = textureWidth;
            return this;
        }

        public Builder textureHeight(double textureHeight) {
            checkArgument(isFinite(textureHeight) && textureHeight > 0, "Invalid texture height: " + textureHeight);
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder textureDimensions(double textureWidth, double textureHeight) {
            return this.textureWidth(textureWidth).textureHeight(textureHeight);
        }

        public Builder texture(@NotNull Identifier texture, double width, double height) {
            this.texture = texture;
            return this.textureWidth(width).textureHeight(height);
        }

        public Builder xLeft(double xLeft) {
            checkArgument(isFinite(xLeft), "Invalid left texture offset: " + xLeft);
            this.xLeft = xLeft;
            return this;
        }

        public Builder yTop(double yTop) {
            checkArgument(isFinite(yTop), "Invalid top texture offset: " + yTop);
            this.yTop = yTop;
            return this;
        }

        public Builder xRight(double xRight) {
            checkArgument(isFinite(xRight), "Invalid right texture offset: " + xRight);
            this.xRight = xRight;
            return this;
        }

        public Builder yBottom(double yBottom) {
            checkArgument(isFinite(yBottom), "Invalid bottom texture offset: " + yBottom);
            this.yBottom = yBottom;
            return this;
        }

        public Builder offsets(double xLeft, double yTop, double xRight, double yBottom) {
            return this.xLeft(xLeft).yTop(yTop).xRight(xRight).yBottom(yBottom);
        }

        public Builder width(double width) {
            if (isFinite(this.xLeft)) {
                return this.xRight(this.xLeft + width);
            } else if (isFinite(this.xRight)) {
                return this.xLeft(this.xRight - width);
            } else {
                throw new IllegalStateException("Neither xLeft nor xRight is set!");
            }
        }

        public Builder height(double height) {
            if (isFinite(this.yTop)) {
                return this.yBottom(this.yTop + height);
            } else if (isFinite(this.yBottom)) {
                return this.yTop(this.yBottom - height);
            } else {
                throw new IllegalStateException("Neither yTop nor yBottom is set!");
            }
        }

        public Builder fullTexture() {
            checkState(isFinite(this.textureWidth), "No texture width provided");
            checkState(isFinite(this.textureHeight), "No texture height provided");
            return this.offsets(0d, 0d, this.textureWidth, this.textureHeight);
        }

        public Sprite build() {
            checkState(this.texture != null, "No texture specified");
            checkState(isFinite(this.textureWidth), "No texture width specified");
            checkState(isFinite(this.textureHeight), "No texture height specified");
            checkState(isFinite(this.xLeft), "No left texture offset specified");
            checkState(isFinite(this.yTop), "No top texture offset specified");
            checkState(isFinite(this.xRight), "No right texture offset specified");
            checkState(isFinite(this.yBottom), "No bottom texture offset specified");
            return new Sprite(
                    this.texture,
                    this.textureWidth, this.textureHeight,
                    this.xLeft, this.yTop, this.xRight, this.yBottom
            );
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
