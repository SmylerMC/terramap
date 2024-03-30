package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import net.smyler.smylib.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
import static net.smyler.smylib.Objects.requireNonNullElse;
import static net.smyler.smylib.text.BooleanTextStyle.TRUE;
import static net.smyler.smylib.text.Formatting.*;
import static net.smyler.smylib.text.ImmutableText.EMPTY;

abstract class BaseFont implements Font {

    protected final float interlineFactor;

    private final StyleState styleState = new StyleState();  // Used for internal calculations

    BaseFont(float interlineFactor) {
        this.interlineFactor = interlineFactor;
    }

    @Override
    public float interline() {
        return this.height() * this.interlineFactor;
    }

    @Override
    public float draw(float x, float y, @NotNull Text text, @NotNull Color color, boolean shadow) {
        return this.draw(x, y, text.getFormattedText(), color, shadow);
    }

    @Override
    public float drawCentered(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow) {
        float width = this.computeWidth(text);
        return this.draw(x - width / 2, y, text, color, shadow);
    }

    @Override
    public float drawCentered(float x, float y, @NotNull Text text, @NotNull Color color, boolean shadow) {
        String content = text.getFormattedText();
        return this.drawCentered(x, y, content, color, shadow);
    }

    @Override
    public float drawLines(float x, float y, @NotNull Color color, boolean shadow, String... lines) {
        float maxWidth = 0;
        for (String line: lines) {
            maxWidth = max(maxWidth, this.draw(x, y, line, color, shadow));
            y += this.height() + this.interline();
        }
        return maxWidth;
    }

    @Override
    public float drawLines(float x, float y, @NotNull Color color, boolean shadow, Text... lines) {
        float maxWidth = 0;
        for (Text line: lines) {
            String resolvedLine = line.getFormattedText();
            maxWidth = max(maxWidth, this.draw(x, y, resolvedLine, color, shadow));
            y += this.height() + this.interline();
        }
        return maxWidth;
    }

    @Override
    public float drawCenteredLines(float x, float y, @NotNull Color color, boolean shadow, String... lines) {
        float maxWidth = 0;
        for (String line: lines) {
            maxWidth = max(maxWidth, this.drawCentered(x, y, line, color, shadow));
            y += this.height() + this.interline();
        }
        return maxWidth;
    }

    @Override
    public float drawCenteredLines(float x, float y, @NotNull Color color, boolean shadow, Text... lines) {
        float maxWidth = 0;
        for (Text line: lines) {
            String resolvedLine = line.getFormattedText();
            maxWidth = max(maxWidth, this.drawCentered(x, y, resolvedLine, color, shadow));
            y += this.height() + this.interline();
        }
        return maxWidth;
    }

    @Override
    public String[] wrapToWidth(@NotNull String text, float maxWidth) {
        List<String> lines = new ArrayList<>();
        this.styleState.reset();
        int processed = 0;
        int lastBlank = -1;
        float widthAtLastBlank = -1;
        StyleState styleAtLastBlank = null;
        float width = 0;
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == PREFIX && i + 1 < text.length()) {
                this.styleState.applyChar(text.charAt(++i));
            } else {
                width += this.getEffectiveCharWidth(chr);
            }
            if (chr == ' ' || chr == '\t') {
                lastBlank = i;
                styleAtLastBlank = this.styleState.copy();
                widthAtLastBlank = width;
            } else if (chr == '\n') {
                lines.add(this.styleState + text.substring(processed, i));
                processed = i + 1;
                width = 0;
            } else if (width > maxWidth) {
                if (lastBlank != -1) {
                    lines.add(styleAtLastBlank + text.substring(processed, lastBlank));
                    processed = lastBlank + 1;
                    width -= widthAtLastBlank;
                } else if (processed != i){
                    lines.add(this.styleState + text.substring(processed, i));
                    processed = i;
                    width = 0;
                }
                lastBlank = -1;
            }
        }
        lines.add(text.substring(processed));
        return lines.toArray(new String[0]);
    }

    @Override
    public Text[] wrapToWidth(@NotNull Text text, float maxWidth) {
        List<Text> lines = new ArrayList<>();
        List<ImmutableText> toProcess = text.stream()
                .map(ImmutableText::asResolvedSolo)
                .collect(toCollection(ArrayList::new));
        this.styleState.reset();
        while (!toProcess.isEmpty()) {
            float currentWidth = 0;
            int indexOfLastTextWithBlank = -1;
            int indexOfLastBlankInText = -1;
            int textIndex = 0;
            int charIndex = 0;
            for (ImmutableText part: toProcess) {
                String content = part.content().toString();
                this.styleState.bold = part.style().isBold() == TRUE;
                for (charIndex = 0; charIndex < content.length(); charIndex++) {
                    char chr = content.charAt(charIndex);
                    currentWidth += this.getEffectiveCharWidth(chr);
                    if (chr == '\n' || chr == ' ' || chr == '\t') {
                        // Found a blank character, take note of where we encountered it
                        indexOfLastTextWithBlank = textIndex;
                        indexOfLastBlankInText = charIndex;
                    }
                    if (chr == '\n' || (currentWidth > maxWidth && charIndex > 0)) {
                        // Either there was an explicit new line character,
                        // or the line is getting to large, either way it's time to split it.
                        // We should never split at 0, so we always process at least one char per line
                        break;
                    }
                }
                if (charIndex < content.length()) {
                    // Last inner loop exited early, propagate
                    break;
                }
                textIndex++; // Going to the next text
            }

            if (textIndex >= toProcess.size()) {
                // We reached the end of the entire text!
                lines.add(EMPTY.withNewSiblings(toProcess.toArray(new ImmutableText[0])));
                toProcess.clear();
                break;
            }

            if (indexOfLastTextWithBlank >= 0) {
                // We are splitting the line on a blank
                // The blank is discarded
                lines.add(this.splitTextLine(toProcess, indexOfLastTextWithBlank, indexOfLastBlankInText, indexOfLastBlankInText + 1));
            } else {
                // Hard split the line where it overflowed
                lines.add(this.splitTextLine(toProcess, textIndex, charIndex, charIndex));
            }

        }

        return lines.toArray(new Text[0]);
    }

    @Override
    public float computeWidth(String... texts) {
        float maxWidth = 0;
        this.styleState.reset();
        for (String line: texts) {
            float width = 0;
            for (int i = 0; i < line.length(); i++) {
                char chr = line.charAt(i);
                if (chr == PREFIX) {
                    char modifierChar = line.charAt(++i);  // Skip next char
                    this.styleState.applyChar(modifierChar);
                } else {
                    width += this.getEffectiveCharWidth(chr);
                }
            }
            maxWidth = max(maxWidth, width);
        }
        return maxWidth;
    }

    @Override
    public float computeWidth(Text... texts) {
        return this.computeWidth(stream(texts).map(Text::getFormattedText).toArray(String[]::new));
    }

    @Override
    public float computeHeight(String... lines) {
        return lines.length * (this.height() + this.interline()) - this.interline();
    }

    @Override
    public float computeHeight(Text... lines) {
        return lines.length * (this.height() + this.interline()) - this.interline();
    }

    @Override
    public String trimRight(@NotNull String text, float width) {
        float computedWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == PREFIX) {
                this.styleState.applyChar(text.charAt(++i));  // Skip next char
            } else {
                computedWidth += this.getEffectiveCharWidth(chr);
            }
            if (computedWidth > width) {
                return text.substring(0, i);
            }
        }
        return text;
    }

    @Override
    public Text trimRight(@NotNull Text text, float maxWidth) {
        text = ImmutableText.ofResolved(text);
        List<ImmutableText> toProcess = text.stream()
                .map(ImmutableText::asResolvedSolo)
                .collect(toCollection(LinkedList::new));
        List<ImmutableText> processed = new ArrayList<>();
        float width = 0f;
        this.styleState.reset();
        while (!toProcess.isEmpty()) {
            ImmutableText fragment = toProcess.remove(0);
            String content = fragment.content().toString();
            TextStyle style = fragment.style();
            this.styleState.bold = style.isBold() == TRUE;
            for (int i = 0; i < content.length(); i++) {
                width += this.getEffectiveCharWidth(content.charAt(i));
                if (width > maxWidth) {
                    processed.add(new ImmutableText(
                            new PlainTextContent(content.substring(0, i)),
                            style
                    ));
                    return EMPTY.withNewSiblings(
                            processed.toArray(new ImmutableText[0])
                    );
                }
            }
            processed.add(fragment);
        }
        return text;  // Total length is under max length
    }

    @Override
    public String trimLeft(@NotNull String text, float width) {
        if (text.isEmpty()) {
            return text;
        }
        // Character width depend on style, so we start by precalculating widths and styles
        float[] widths = new float[text.length()];
        StyleState[] styles = new StyleState[text.length()];

        this.styleState.reset();
        styles[0] = this.styleState.copy();
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == PREFIX) {
                this.styleState.applyChar(text.charAt(++i));
                styles[i] = this.styleState.copy();
            } else {
                widths[i] = this.getEffectiveCharWidth(chr);
                if (i > 0) {
                    styles[i] = styles[i - 1];
                }
            }
        }

        // Now start computing total width from the end of the string
        float computedWidth = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            computedWidth += widths[i];
            if (computedWidth > width) {  // Found desired length
                while(i < text.length()  - 1 && widths[i] == 0) {
                    i++;  // Skip heading style characters
                }
                return styles[i] + text.substring(i + 1);
            }
        }
        return text;
    }

    @Override
    public Text trimLeft(@NotNull Text text, float maxWidth) {
        List<ImmutableText> toProcess = text.stream()
                .map(ImmutableText::asResolvedSolo)
                .collect(Collectors.toCollection(ArrayList::new));
        List<ImmutableText> processed = new LinkedList<>();
        this.styleState.reset();
        float width = 0f;
        while (!toProcess.isEmpty()) {
            ImmutableText fragment = toProcess.remove(toProcess.size() - 1);
            String content = fragment.content().toString();
            TextStyle style = fragment.style();
            this.styleState.bold = style.isBold() == TRUE;
            for (int i = content.length() - 1; i >= 0; i--) {
                width += this.getEffectiveCharWidth(content.charAt(i));
                if (width > maxWidth) {
                    processed.add(0, new ImmutableText(
                            new PlainTextContent(content.substring(i + 1)),
                            style
                    ));
                    return EMPTY.withNewSiblings(
                            processed.toArray(new ImmutableText[0])
                    );
                }
            }
            processed.add(0, fragment);
        }
        return text;
    }

    abstract float getCharWidth(char character);

    private float getEffectiveCharWidth(char character) {
        float width = this.getCharWidth(character);
        if (width > 0 && this.styleState.bold) {
            width += this.scale();
        }
        return width;
    }

    private Text splitTextLine(List<ImmutableText> toProcess, int cutText, int cutChar, int resumeChar) {
        ImmutableText[] siblings = new ImmutableText[cutText + 1];
        for (int i = 0; i < cutText; i++) {
            siblings[i] = toProcess.remove(0);
        }
        ImmutableText cut = toProcess.get(0);
        String content = cut.content().toString();
        TextStyle style = cut.style();
        siblings[cutText] = new ImmutableText(
                new PlainTextContent(content.substring(0, cutChar)),
                style
        );
        toProcess.set(0, new ImmutableText(
                new PlainTextContent(content.substring(resumeChar)),
                style
        ));
        return EMPTY.withNewSiblings(siblings);
    }

    private static class StyleState {
        private boolean bold = false;
        private boolean italic = false;
        private boolean underline = false;
        private boolean strikethrough = false;
        private boolean obfuscated = false;
        private Formatting colorFormat = null;
        private Color colorRgb = null;

        void reset() {
            this.bold = false;
            this.italic = false;
            this.underline = false;
            this.strikethrough = false;
            this.obfuscated = false;
            this.colorFormat = null;
            this.colorRgb = null;
        }

        void applyChar(char formatCode) {
            // This is stupid, but on vanilla 1.12, format code + invalid char = white
            Formatting formatting = requireNonNullElse(
                    Formatting.fromChar(formatCode),
                    Formatting.WHITE
            );
            if (formatting.isColor()) {
                // Colors reset the context
                this.reset();
                this.colorFormat = formatting;
            } else if (formatting == RESET) {
                this.reset();
            } else {
                switch (formatting) {
                    case BOLD:
                        this.bold = true;
                        break;
                    case ITALIC:
                        this.italic = true;
                        break;
                    case UNDERLINE:
                        this.underline = true;
                        break;
                    case STRIKETHROUGH:
                        this.strikethrough = true;
                        break;
                    case OBFUSCATED:
                        this.obfuscated = true;
                        break;
                    default:
                        throw new IllegalStateException("Invalid formatting");
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.colorRgb != null) {
                builder.append(this.colorRgb.asHtmlHexString());
            } else if (this.colorFormat != null){
                builder.append(this.colorFormat);
            }
            if (this.bold) {
                builder.append(BOLD);
            }
            if (this.italic) {
                builder.append(ITALIC);
            }
            if (this.underline) {
                builder.append(UNDERLINE);
            }
            if (this.strikethrough) {
                builder.append(STRIKETHROUGH);
            }
            if (this.obfuscated) {
                builder.append(OBFUSCATED);
            }
            return builder.toString();
        }

        StyleState copy() {
            StyleState state = new StyleState();
            state.bold = this.bold;
            state.italic = this.italic;
            state.underline = this.underline;
            state.strikethrough = this.strikethrough;
            state.obfuscated = this.obfuscated;
            state.colorFormat = this.colorFormat;
            state.colorRgb = this.colorRgb;
            return state;
        }

    }

}
