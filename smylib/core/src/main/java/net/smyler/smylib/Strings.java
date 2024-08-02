package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


/**
 * A string manipulation utility clas.
 *
 * @author Smyler
 */
public final class Strings {

    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

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

    public static Optional<Integer> parseOptionalInt(@NotNull String string) {
        try {
            return Optional.of(parseInt(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseOptionalLong(@NotNull String string) {
        try {
            return Optional.of(parseLong(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Float> parseOptionalFloat(@NotNull String string) {
        try {
            return Optional.of(parseFloat(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseOptionalDouble(@NotNull String string) {
        try {
            return Optional.of(parseDouble(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static String hexString(byte @NotNull [] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(HEX_DIGITS[(b >> 8) & 0X0f]);
            builder.append(HEX_DIGITS[ b       & 0x0f]);
        }
        return builder.toString();
    }

}
