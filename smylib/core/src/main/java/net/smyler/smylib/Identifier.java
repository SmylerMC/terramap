package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Strings.isNullOrEmpty;

/**
 * A namespaced identifier for resources within the game.
 * <ul>
 *     <li>The namespace part identifies the project that owns the resource (e.g. it is usually the mod id for mods)
 *     and must match <code>[a-z0-9_.-]+</code>.</li>
 *     <li>The namespace part identifies the resource within the project.
 *     For resource file it is usually the path within the namespace's resource folder.
 *     It must match <code>[a-z0-9/_.-]+</code>.</li>
 * </ul>
 */
public final class Identifier {

    @NotNull public final String namespace;
    @NotNull public final String path;

    public Identifier(@NotNull String namespace, @NotNull String path) {
        checkArgument(isValidNamespace(namespace), "Identifier namespace must match [a-z0-9_.-]+");
        checkArgument(isValidPath(path), "Identifier path must match [a-z0-9/_.-]+");
        this.namespace = namespace;
        this.path = path;
    }

    /**
     * Parse an identifier from a {@link String}.
     *
     * @param identifier    an identifier in the "<namespace>:<path>" format
     * @return              the parsed identifier
     */
    public static Identifier parse(String identifier) {
        checkArgument(identifier != null, "Cannot parse null identifier");
        String[] parts = identifier.split(":");
        checkArgument(parts.length == 2, "Identifiers must match the <namespace>:<path> syntax");
        return new Identifier(parts[0], parts[1]);
    }

    public static boolean isValidNamespace(String namespace) {
        if (isNullOrEmpty(namespace)) {
            return false;
        }
        for (int i = 0; i < namespace.length(); i++) {
            char c = namespace.charAt(i);
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '-' && c != '_' && c != '.') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPath(String path) {
        if (isNullOrEmpty(path)) {
            return false;
        }
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '-' && c != '_' && c != '.' && c != '/') {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Identifier that = (Identifier) o;
        return this.namespace.equals(that.namespace) && this.path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    /**
     * Creates an identifier that is the child of this one, by appending <code>/&lt;child></code> to the path.
     *
     * @param   child the child resource
     * @return  a new identifier that is a child of this one
     */
    public Identifier resolve(String child) {
        return new Identifier(this.namespace, this.path + "/" + child);
    }

}
