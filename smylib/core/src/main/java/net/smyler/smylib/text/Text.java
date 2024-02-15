package net.smyler.smylib.text;

import java.util.List;
import java.util.stream.Stream;

/**
 * A Minecraft text, often referred to as a text component.
 * Texts are tree-like structures with an inheritance system for styles and interaction events.
 * They can be expressed as JSON.
 * More information on <a href="https://minecraft.wiki/w/Raw_JSON_text_format">the minecraft wiki.</a>
 *
 * @author Smyler
 */
public interface Text extends Iterable<Text> {

    /**
     * Textual content of this text.
     *
     * @return a text content
     */
    TextContent content();

    /**
     * @return the style to apply to this text when drawn
     */
    TextStyle style();

    /**
     * @return this text's siblings.
     *         siblings inherit style and events from their parents.
     */
    List<Text> siblings();

    /**
     * @return a string representation of this text and its siblings with format codes
     */
    String getFormattedText();

    /**
     * @return a string representation of this text and its siblings without format codes
     */
    String getUnformattedText();

    /**
     * @return a stream of this text and its siblings
     */
    Stream<Text> stream();

}
