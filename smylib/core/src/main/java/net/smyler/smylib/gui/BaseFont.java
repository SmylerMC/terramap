package net.smyler.smylib.gui;

import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.PlainTextContent;
import net.smyler.smylib.text.Text;
import net.smyler.smylib.text.TextStyle;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static net.smyler.smylib.text.ImmutableText.EMPTY;

public abstract class BaseFont implements Font {

    @Override
    public Text[] wrapToWidth(Text text, float maxWidth) {
        List<Text> lines = new ArrayList<>();
        List<ImmutableText> toProcess = text.stream()
                .map(ImmutableText::asResolvedSolo)
                .collect(toCollection(ArrayList::new));
        while (!toProcess.isEmpty()) {
            float currentWidth = 0;
            int indexOfLastTextWithBlank = -1;
            int indexOfLastBlankInText = -1;
            int textIndex = 0;
            int charIndex = 0;
            for (ImmutableText part: toProcess) {
                String content = part.content().toString();
                for (charIndex = 0; charIndex < content.length(); charIndex++) {
                    char chr = content.charAt(charIndex);
                    currentWidth += this.getCharWidth(chr);
                    if (chr == '\n' || chr == ' ' || chr == '\t') {
                        // Found a blank character, take note of where we encountered it
                        indexOfLastTextWithBlank = textIndex;
                        indexOfLastBlankInText = charIndex;
                    }
                    if (chr == '\n' || currentWidth > maxWidth) {
                        // Either with have an explicit new line character,
                        // or the line is getting to large, either way it's time to split it.
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
                lines.add(this.splitLine(toProcess, indexOfLastTextWithBlank, indexOfLastBlankInText, indexOfLastBlankInText + 1));
            } else {
                // Hard split the line where it overflowed
                lines.add(this.splitLine(toProcess, textIndex, charIndex, charIndex));
            }

        }

        return lines.toArray(new Text[0]);
    }

    private Text splitLine(List<ImmutableText> toProcess, int cutText, int cutChar, int resumeChar) {
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

}
