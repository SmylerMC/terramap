package net.smyler.smylib.resources;

/**
 * GUI texture metadata.
 * Lets the game know how a texture should behave when used as part of a GUI
 * (e.g. how it should scale).
 * <br>
 * Only used by the vanilla game starting 1.20.5. Backported by SmyLib in earlier versions.
 *
 * @see <a href="https://minecraft.wiki/w/Resource_pack#GUI">the Minecraft wiki for more details</a>.
 *
 * @author Smyler
 *
 */
public class GuiMetadata {

    private final Scaling scaling;

    public GuiMetadata(Scaling scaling) {
        this.scaling = scaling;
    }

    public Scaling scaling() {
        return this.scaling;
    }

    public interface Scaling {}

    public static class Stretch implements Scaling {}

    public static class Tile implements Scaling {

        private final int width, height;

        public Tile(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

    }

    public static class NineSlice implements Scaling {

        private final int width, height;
        private final int borderLeft, borderRight, borderTop, borderBottom;

        public NineSlice(int width, int height, int borderLeft, int borderRight, int borderTop, int borderBottom) {
            this.width = width;
            this.height = height;
            this.borderLeft = borderLeft;
            this.borderRight = borderRight;
            this.borderTop = borderTop;
            this.borderBottom = borderBottom;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public int borderLeft() {
            return this.borderLeft;
        }

        public int borderRight() {
            return this.borderRight;
        }

        public int borderTop() {
            return this.borderTop;
        }

        public int borderBottom() {
            return this.borderBottom;
        }

    }

}
