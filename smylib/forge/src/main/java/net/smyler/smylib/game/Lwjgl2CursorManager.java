package net.smyler.smylib.game;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.resources.CursorResourceMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import java.util.IdentityHashMap;

public class Lwjgl2CursorManager extends CursorManager<Lwjgl2Cursor> {

    private final IdentityHashMap<Cursor, Lwjgl2Cursor> nativeCursors = new IdentityHashMap<>();

    @Override
    public @Nullable Lwjgl2Cursor getCurrent() {
        Cursor nativeCursor = Mouse.getNativeCursor();
        return this.nativeCursors.get(nativeCursor);
    }

    @Override
    protected void set(@Nullable Lwjgl2Cursor cursor) throws Exception {
        if (cursor == null) {
            Mouse.setNativeCursor(null);
        } else {
            Mouse.setNativeCursor(cursor.getNativeCursor());
        }
    }

    @Override
    protected @Nullable Lwjgl2Cursor load(Identifier texture, CursorResourceMetadata metadata) throws Exception {
        Lwjgl2Cursor cursor = new Lwjgl2Cursor(texture, metadata);
        cursor.load();
        this.nativeCursors.put(cursor.getNativeCursor(), cursor);
        return cursor;
    }

    @Override
    protected void unload(@NotNull Lwjgl2Cursor cursor) {
        this.nativeCursors.remove(cursor.getNativeCursor());
        cursor.getNativeCursor().destroy();
    }

}
