package net.smyler.smylib.game;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Cursor;
import net.smyler.smylib.resources.CursorResourceMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DummyCursorManager extends CursorManager<Cursor> {

    @Override
    public @Nullable Cursor getCurrent() {
        // No-op
        return null;
    }

    @Override
    protected void set(@Nullable Cursor cursor) throws Exception {
        // No-op
    }

    @Override
    protected @Nullable Cursor load(Identifier texture, CursorResourceMetadata metadata) throws Exception {
        // No-op
        return null;
    }

    @Override
    protected void unload(@NotNull Cursor cursor) {
        // No-op
    }
}
