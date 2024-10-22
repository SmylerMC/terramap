package net.smyler.smylib.resources;

import java.util.*;

/**
 * Texture animation metadata.
 * Animated textures contain the various frames of the animation stitched together vertically.
 *
 * @see <a href="https://minecraft.wiki/w/Resource_pack#Animation">the Minecraft wiki</a> for more details.
 * @author Smyler
 */
public class AnimationMetadata {

    private final int time;
    private final int width, height;
    private final boolean interpolate;
    private final List<Frame> frames;

    public AnimationMetadata(int time, int width, int height, boolean interpolate, Collection<Frame> frames) {
        this.time = time;
        this.width = width;
        this.height = height;
        this.interpolate = interpolate;
        this.frames = Collections.unmodifiableList(new ArrayList<>(frames));
    }

    public int time() {
        return this.time;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public boolean interpolate() {
        return this.interpolate;
    }

    public List<Frame> frames() {
        return this.frames;
    }

    public static class Frame {

        private final int index;
        private final Integer time;

        public Frame(int index) {
            this.index = index;
            this.time = null;
        }

        public Frame(int index, int time) {
            this.time = time;
            this.index = index;
        }

        public OptionalInt time() {
            if (this.time == null) {
                return OptionalInt.empty();
            } else {
                return OptionalInt.of(this.time);
            }
        }

        public int index() {
            return this.index;
        }

    }

}
