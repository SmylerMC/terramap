package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;

/**
 * A string manipulation utility clas.
 *
 * @author Smyler
 */
public final class Strings {

    private Strings() {
        throw new IllegalStateException();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isBlank(@NotNull String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrBlank(String string) {
        return string == null || isBlank(string);
    }

    public static String strip(@NotNull String string) {
        int start = 0;
        int end = string.length();
        while (start < string.length()) {
            char c = string.charAt(start);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                break;
            }
            start++;
        }
        while (end > start) {
            char c = string.charAt(end - 1);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                break;
            }
            end--;
        }
        return string.substring(start, end);
    }

}
