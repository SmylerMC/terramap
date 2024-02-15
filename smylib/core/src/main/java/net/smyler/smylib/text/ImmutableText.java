package net.smyler.smylib.text;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * An immutable implementation of {@link Text}.
 * Any cascading modification is applied to the siblings by creating copies.
 *
 * @author Smyler
 */
public final class ImmutableText implements Text {

    @NotNull private final ImmutableText[] siblings;
    @NotNull private final List<Text> siblingList;
    @NotNull private final TextContent content;
    @NotNull private final TextStyle style;

    public ImmutableText(@NotNull TextContent content, @NotNull TextStyle style, @NotNull ImmutableText... siblings) {
        this.siblings = Arrays.stream(siblings)
                .map(Objects::requireNonNull)
                .map(t -> t.withParentStyle(style))
                .toArray(ImmutableText[]::new);
        this.siblingList = unmodifiableList(asList(this.siblings));
        this.content = requireNonNull(content);
        this.style = requireNonNull(style);
    }

    @Override
    public TextContent content() {
        return this.content;
    }

    @Override
    public TextStyle style() {
        return this.style;
    }

    @Override
    public List<Text> siblings() {
        return this.siblingList;
    }

    @Override
    public String getFormattedText() {
        StringBuilder builder = new StringBuilder();
        this.stream().forEachOrdered(s -> builder.append(s.style()).append(s.content()));
        return builder.toString();
    }

    @Override
    public String getUnformattedText() {
        StringBuilder builder = new StringBuilder();
        this.stream().map(Text::content).forEachOrdered(builder::append);
        return builder.toString();
    }

    @Override
    public Stream<Text> stream() {
        Spliterator<Text> spliterator = spliteratorUnknownSize(this.iterator(), IMMUTABLE);
        return StreamSupport.stream(spliterator, false);
    }

    @NotNull
    @Override
    public Iterator<Text> iterator() {
        return new ImmutableTextIterator();
    }

    @NotNull
    public ImmutableText withStyle(@NotNull TextStyle style) {
        if (this.style.equals(style)) {
            return this;
        }
        ImmutableText[] siblings = Arrays.stream(this.siblings)
                .map(t -> t.withParentStyle(style))
                .toArray(ImmutableText[]::new);
        return new ImmutableText(this.content, style, siblings);
    }

    private ImmutableText withParentStyle(@NotNull TextStyle style) {
        TextStyle newStyle = this.style.withParentStyle(style);
        return this.withStyle(newStyle);
    }

    public ImmutableText withNewSiblings(ImmutableText... siblings) {
        ImmutableText[] newSiblings = Stream.concat(
                Arrays.stream(this.siblings),
                Arrays.stream(siblings).map(t -> t.withParentStyle(this.style))
        ).toArray(ImmutableText[]::new);
        return new ImmutableText(this.content, this.style, newSiblings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableText texts = (ImmutableText) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(siblings, texts.siblings)) return false;
        if (!content.equals(texts.content)) return false;
        return style.equals(texts.style);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(siblings);
        result = 31 * result + content.hashCode();
        result = 31 * result + style.hashCode();
        return result;
    }

    private class ImmutableTextIterator implements Iterator<Text> {
        private Iterator<Text> rootNode;
        private Iterator<Text> currentNode;

        @Override
        public boolean hasNext() {
            return this.rootNode == null || this.rootNode.hasNext() || (this.currentNode != null && this.currentNode.hasNext());
        }

        @Override
        public ImmutableText next() {
            if (this.rootNode == null) {
                this.rootNode = ImmutableText.this.siblingList.iterator();
                return ImmutableText.this;

            }
            if (this.currentNode == null || !currentNode.hasNext()) {
                this.currentNode = this.rootNode.next().iterator();
            }
            return (ImmutableText) this.currentNode.next();
        }

    }

}
