package net.smyler.smylib.game;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Cursor;
import net.smyler.smylib.resources.CursorResourceMetadata;
import net.smyler.smylib.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.smyler.smylib.SmyLib.getGameClient;

abstract class CursorManager<CursorImplementation extends Cursor> {

    private final Map<Identifier, CursorImplementation> cursors = new HashMap<>();

    public @Nullable CursorImplementation get(Identifier id) throws Exception {
        CursorImplementation cursor = this.cursors.get(id);
        if (cursor == null && !this.cursors.containsKey(id)) {
            Optional<Resource> optional = getGameClient().getResource(id);
            if (optional.isPresent()) {
                try (Resource resource = optional.get()) {
                    cursor = this.load(id, resource.metadata().cursor().orElse(null));
                }
            }
            this.cursors.put(id, cursor);
        }
        return cursor;
    }

    public void set(@Nullable Identifier identifier) throws Exception {
        if (identifier == null) {
            this.set((CursorImplementation) null);
        } else {
            CursorImplementation cursor = this.get(identifier);
            this.set(cursor);
        }
    }

    public void reload() throws Exception {
        CursorImplementation currentCursor = this.getCurrent();
        Identifier currentIdentifier = currentCursor != null ? currentCursor.identifier(): null;
        this.set((CursorImplementation) null);
        this.cursors.values().forEach(this::unload);
        this.cursors.clear();
        this.set(currentIdentifier);
    }

    public abstract @Nullable CursorImplementation getCurrent();

    protected abstract void set(@Nullable CursorImplementation cursor) throws Exception;

    protected abstract @Nullable CursorImplementation load(Identifier texture, CursorResourceMetadata metadata) throws Exception;

    protected abstract void unload(@NotNull CursorImplementation cursor);

}
