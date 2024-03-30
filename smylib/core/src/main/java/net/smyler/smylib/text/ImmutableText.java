package net.smyler.smylib.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterators.spliteratorUnknownSize;
import static net.smyler.smylib.text.TextStyle.INHERIT_COLOR;

/**
 * An immutable implementation of {@link Text}.
 * Any cascading modification is applied to the siblings by creating copies.
 *
 * @author Smyler
 */
public final class ImmutableText implements Text {

    public static final ImmutableText EMPTY = new ImmutableText(
            new PlainTextContent(""),
             new TextStyle(INHERIT_COLOR)
    );

    @NotNull private final ImmutableText[] siblings;
    @NotNull private final List<Text> siblingList;
    @NotNull private final TextContent content;
    @NotNull private final TextStyle style;
    private final boolean resolved;

    public ImmutableText(@NotNull TextContent content, @NotNull TextStyle style, @NotNull ImmutableText... siblings) {
        this.siblings = Arrays.stream(siblings)
                .map(Objects::requireNonNull)
                .map(t -> t.withParentStyle(style))
                .toArray(ImmutableText[]::new);
        this.siblingList = unmodifiableList(asList(this.siblings));
        this.content = requireNonNull(content);
        this.style = requireNonNull(style);
        this.resolved = this.stream().map(Text::content).allMatch(TextContent::isResolved);
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

    @Override
    public boolean isContentResolved() {
        return this.resolved;
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

    public static ImmutableText of(Text text) {
        if (text instanceof ImmutableText) {
            return (ImmutableText) text; // I like when it's that simple
        }
        ImmutableText[] immutableSiblings = text.siblings().stream()
                .map(ImmutableText::of)
                .toArray(ImmutableText[]::new);
        return new ImmutableText(text.content(), text.style(), immutableSiblings);
    }

    public static ImmutableText ofResolved(Text text) {
        if (text.isContentResolved() && text instanceof ImmutableText) {
            return (ImmutableText) text;
        }
        ImmutableText[] siblings = text.siblings().stream()
                .map(ImmutableText::ofResolved)
                .toArray(ImmutableText[]::new);
        return new ImmutableText(
                new PlainTextContent(text.content().toString()),
                text.style(),
                siblings
        );
    }

    public static ImmutableText asSolo(Text text) {
        return new ImmutableText(
                text.content(),
                text.style()
        );
    }

    public static ImmutableText asResolvedSolo(Text text) {
        return new ImmutableText(
                new PlainTextContent(text.content().toString()),
                text.style()
        );
    }

    public static ImmutableText ofFlattened(Text text) {
        return new ImmutableText(
                new PlainTextContent(""),
                new TextStyle(INHERIT_COLOR),
                text.stream().map(t -> new ImmutableText(t.content(), t.style())).toArray(ImmutableText[]::new)
        );
    }

    public static ImmutableText ofPlainText(String text) {
        return new ImmutableText(
                new PlainTextContent(text),
                new TextStyle(INHERIT_COLOR)
        );
    }

    public static ImmutableText ofTranslationWithFallback(String translationKey, @Nullable String fallback, Object... with) {
        return new ImmutableText(
                new TranslatableTextContent(
                        translationKey,
                        fallback,
                        Arrays.stream(with)
                                .map(Object::toString)
                                .map(ImmutableText::ofPlainText)
                                .toArray(Text[]::new)
                ),
                new TextStyle(INHERIT_COLOR)
        );
    }

    public static ImmutableText ofTranslation(String translationKey, Object... with) {
        return ofTranslationWithFallback(translationKey, null, with);
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

    @Override
    public String toString() {
        return "ImmutableText{" + this.getFormattedText() + "}";
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
