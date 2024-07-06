package net.smyler.smylib.gui.advanced;

public enum VertexFormat {
    POSITION(true, false, false),
    POSITION_TEXTURE(true, true, false),
    POSITION_COLOR(true, false, true),
    POSITION_TEXTURE_COLOR(true, true, true);

    final boolean position;
    final boolean texture;
    final boolean color;
    VertexFormat(boolean position, boolean texture, boolean color) {
        this.position = position;
        this.color = color;
        this.texture = texture;
    }
}
