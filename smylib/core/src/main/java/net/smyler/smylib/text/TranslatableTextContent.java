package net.smyler.smylib.text;

import net.smyler.smylib.game.Translator;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * Translatable text content.
 *
 * @author Smyler
 */
public class TranslatableTextContent implements TextContent {

    private final String translationKey;
    @Nullable
    private final String fallback;
    private final List<Text> with;

    public TranslatableTextContent(String translationKey, @Nullable String fallback, Text... with) {
        this.translationKey = translationKey;
        this.fallback = fallback;
        this.with = unmodifiableList(asList(with));
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public List<Text> getWith() {
        return this.with;
    }

    @Nullable
    public String getFallback() {
        return this.fallback;
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public String toString() {
        Translator translator = getGameClient().translator();
        if (this.fallback != null && !translator.hasKey(this.translationKey)) {
            return this.fallback;
        }
        // Explicitly convert everything to strings, discarding formatting (we can't support it for now)
        Object[] with = this.with.stream()
                .map(Text::getUnformattedText)
                .toArray(String[]::new);
        return translator.format(this.translationKey, with).replace("" + Formatting.PREFIX, "");
    }
}
