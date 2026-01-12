package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.System.nanoTime;
import static java.util.Comparator.comparing;

/**
 * A utility class to help collect simple time-based performance data within a thread.
 * <br>
 * Data is collected in the form of a tree of named <i>sections</i>,
 * with the total time spent executing each one.
 *
 * @author Smyler
 */
public class Profiler {

    private @Nullable Section currentSection = null;

    /**
     * Creates a new disabled profiler. Call {@link #enable()} to start profiling.
     */
    public Profiler() {
        // Nothing to do here, currentSection is null by default
    }

    /**
     * Enters a subsection with a given name.
     * If the current subsection does not have a subsection with the given name,
     * it gets created.
     *
     * @param name the name of the subsection to enter
     */
    public void enterSection(@NotNull String name) {
        if (this.currentSection == null) {
            return;
        }
        this.currentSection = this.currentSection.getChild(name);
        this.currentSection.enter();
    }

    /**
     * Leaves the current subsection and enter another one.
     * If the current parent section does not have a subsection with the given name,
     * it gets created.
     *
     * @param name the name of the section to enter
     * @throws IllegalStateException if the current section is the root section
     */
    public void nextSection(@NotNull String name) {
        this.leaveSection();
        this.enterSection(name);
    }

    /**
     * Leaves the current subsection.
     *
     * @throws IllegalStateException if the current section is the root section
     */
    public void leaveSection() {
        if (this.currentSection == null) {
            return;
        }
        if (this.currentSection.parent == null) {
            throw new IllegalStateException("Profiling error: cannot leave root section");
        }
        this.currentSection.leave();
        this.currentSection = this.currentSection.parent;
    }

    /**
     * Execute a consumer for all recorded sections.
     * Graph traversal happens in a similar fashion than DFS,
     * except parents are visited before their children.
     *
     * @param consumer the consumer to call
     */
    public void walkSections(@NotNull Consumer<Section> consumer) {
        if (this.currentSection == null) {
            return;
        }
        this.currentSection.update();
        this.rootSection().walk(consumer);
    }

    /**
     * Enables the profiler. If it's already enabled, this method does nothing.
     * If it was disabled, a new root section is created and entered.
     */
    public void enable() {
        if (this.currentSection == null) {
            this.currentSection = new Section("root", null, 0);
            this.currentSection.enter();
        }
    }

    /**
     * Disables the profiler. All collected data is discarded.
     */
    public void disable() {
        this.currentSection = null;
    }

    /**
     * Sets the enabled state of the profiler.
     *
     * @param enable true to enable, false to disable
     * @see #enable()
     * @see #disable()
     */
    public void setEnabled(boolean enable) {
        if (enable) {
            this.enable();
        } else {
            this.disable();
        }
    }

    /**
     * Checks if the profiler is currently enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return this.currentSection != null;
    }

    private Section rootSection() {
        Section section = this.currentSection;
        if (section == null) {
            throw new IllegalStateException("Cannot get root section of disabled profiler");
        }
        while (section.parent != null) {
            section = section.parent;
        }
        return section;
    }

    /**
     * Represents a single section in the profiling tree.
     */
    public static final class Section {
        private final String name;
        private final int depth;
        private final @Nullable Section parent;
        private final Map<@NotNull String, @NotNull Section> children = new HashMap<>();
        private long enterTimeNanos = Long.MIN_VALUE;
        private long totalTimeNanos = 0L;

        private Section(@NotNull String name, @Nullable Section parent, int depth) {
            this.name = name;
            this.parent = parent;
            this.depth = depth;
        }

        /**
         * Gets the name of this section.
         *
         * @return the name of the section.
         */
        public String name() {
            return this.name;
        }

        /**
         * Gets the total elapsed time in nanoseconds for this section.
         *
         * @return the total time spent in this section.
         */
        public long elapsedTimeNanos() {
            return this.totalTimeNanos;
        }

        /**
         * Calculates the share of time this section took compared to its parent.
         *
         * @return a float between 0.0 and 1.0 representing the share of parent's time.
         */
        public float shareOfParent() {
            if (this.parent == null) {
                return 1.0f;  // This is the root section, it has no siblings to share time with
            }
            return (float) this.totalTimeNanos / this.parent.totalTimeNanos;
        }

        /**
         * Gets the depth of this section in the profiling tree.
         *
         * @return the depth, where 0 is the root section.
         */
        public int depth() {
            return this.depth;
        }

        private void walk(@NotNull Consumer<Section> consumer) {
            consumer.accept(this);
            this.children.values().stream().sorted(comparing(Section::elapsedTimeNanos).reversed()).forEachOrdered(c -> c.walk(consumer));
        }

        private void enter() {
            this.enterTimeNanos = nanoTime();
        }

        private void leave() {
            this.totalTimeNanos += (nanoTime() - this.enterTimeNanos);
            this.enterTimeNanos = Long.MIN_VALUE;
        }

        private void update() {
            long now = nanoTime();
            Section section = this;
            while (section != null) {
                section.totalTimeNanos += (now - section.enterTimeNanos);
                section.enterTimeNanos = now;
                section = section.parent;
            }
        }

        private Section getChild(@NotNull String name) {
            return this.children.computeIfAbsent(name, n -> new Section(n, this, this.depth + 1));
        }

        /**
         * Gets the parent section of this section.
         *
         * @return the parent section, or null if this is the root section.
         */
        public @Nullable Section parent() {
            return parent;
        }
    }

}
