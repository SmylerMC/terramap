package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Strings.isNullOrEmpty;

public class Identifier {
    @NotNull public final String namespace;
    @NotNull public final String path;

    public Identifier(@NotNull String namespace, @NotNull String path) {
        checkArgument(isValidNamespace(namespace), "Identifier namespace must match [a-z0-9/_.-]+");
        checkArgument(isValidPath(path), "Identifier path must match [a-z0-9_.-]+");
        this.namespace = namespace;
        this.path = path;
    }

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

}
