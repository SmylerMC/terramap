package net.smyler.smylib;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static net.smyler.smylib.Preconditions.checkState;

/**
 * A utility class to help collect simple time-based performance data within a thread.
 * <br>
 * Data is collected in the form of a tree of named <i>sections</i>,
 * with the total time spent executing each one,
 * aggregated across multiple <i>ticks</i>.
 *
 * @author Smyler
 */
public class Profiler {

    private int tickRetentionCount = 1000;
    private long tickRetentionMaxAgeNanos = 5_000_000_000L;

    private @Nullable Section currentSection = null;
    private final List<@NotNull Section> previousTicks = new LinkedList<>();

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
     * Leaves the current subsection and enters another one.
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
     * Executes a consumer for all recorded sections.
     * Graph traversal happens in a similar fashion than DFS,
     * except parents are visited before their children.
     *
     * @param consumer the consumer to call
     */
    public void walkData(@NotNull Consumer<Section> consumer) {
        Section aggregated = this.getData();
        if (aggregated == null) {
            return;
        }
        this.getData().walk(consumer);
    }

    /**
     * Gets the aggregated profiling data from all completed ticks.
     *
     * @return the merged root section containing all aggregated data, or null if no ticks have been completed
     */
    public @Nullable Section getData() {
        if (this.previousTicks.isEmpty()) {
            return null;
        }
        return Section.merge(null, this.previousTicks);
    }

    /**
     * Completes the current profiling tick and starts a new one.
     * The current section tree is saved and a new root section is created.
     */
    public void tick() {
        if (this.currentSection == null) {
            return; // Nothing to do when disabled
        }
        this.previousTicks.add(this.currentSection);
        this.currentSection.leave();
        this.currentSection = null;
        this.enable();  // Will re-create the root section and enter it
        this.pruneOldTicks();
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

    /**
     * Sets the maximum number of ticks to retain in memory.
     * Older ticks will be discarded when this limit is exceeded.
     *
     * @param count the maximum number of ticks to retain
     */
    public void setTickRetentionCount(int count) {
        this.tickRetentionCount = count;
        this.pruneOldTicks();
    }

    /**
     * Sets the maximum age of ticks to retain in memory.
     * Ticks older than this age will be discarded.
     *
     * @param age the maximum age of ticks to retain
     * @param unit the time unit of the age parameter
     */
    public void setTickRetentionAge(long age, @NotNull TimeUnit unit) {
        this.tickRetentionMaxAgeNanos = unit.toNanos(age);
        this.pruneOldTicks();
    }

    private void pruneOldTicks() {
        // Count based
        int toDiscard = max(0, this.previousTicks.size() - this.tickRetentionCount);
        if (toDiscard > 0) {
            this.previousTicks.subList(0, toDiscard).clear();
        }

        // Time based
        Iterator<Section> iterator = this.previousTicks.iterator();
        long pruneTime = nanoTime() - this.tickRetentionMaxAgeNanos;
        while (iterator.hasNext()) {
            Section section = iterator.next();
            if (section.lastLeaveTimeNanos > pruneTime) {
                break; // We found the first section within the temporal retention time window, stop there
            }
            iterator.remove();
        }
    }

    /**
     * Represents a single time section in the profiling tree.
     */
    public static final class Section {
        private final String name;
        private final int depth;
        private final @Nullable Section parent;
        private final Map<@NotNull String, @NotNull Section> children = new HashMap<>();
        private long enterTimeNanos = Long.MIN_VALUE;
        private long lastLeaveTimeNanos = Long.MIN_VALUE;  // This is only valid for the root section

        private long averageTimeNanos = 0L;
        private int tickCount = 1;
        private long totalTimeNanos = 0L;
        private long minTimeNanos = 0L;
        private long maxTimeNanos = 0L;

        private Section(@NotNull String name, @Nullable Section parent, int depth) {
            checkSectionName(name);
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

        /**
         * Gets the number of ticks that contributed to this section's data.
         *
         * @return the tick count
         */
        public int getTickCount() {
            return this.tickCount;
        }

        /**
         * Executes a consumer for this section and all its children recursively.
         * Children are visited in order of decreasing elapsed time.
         *
         * @param consumer the consumer to call for each section
         */
        public void walk(@NotNull Consumer<Section> consumer) {
            consumer.accept(this);
            this.children.values().stream()
                    .sorted(comparing(Section::elapsedTimeNanos).reversed())
                    .forEachOrdered(c -> c.walk(consumer));
        }

        private void enter() {
            this.enterTimeNanos = nanoTime();
        }

        private void leave() {
            long now = nanoTime();
            this.totalTimeNanos += (now - this.enterTimeNanos);
            this.lastLeaveTimeNanos = now;
            this.minTimeNanos = totalTimeNanos;
            this.maxTimeNanos = totalTimeNanos;
            this.averageTimeNanos = totalTimeNanos;
            this.enterTimeNanos = Long.MIN_VALUE;
        }

        private Set<String> getChildrenNames() {
            return this.children.keySet();
        }

        private Section getChild(@NotNull String name) {
            return this.children.computeIfAbsent(name, n -> new Section(n, this, this.depth + 1));
        }

        /**
         * Gets a subsection by its path from this section.
         * The path is specified as a dot-separated string of section names.
         *
         * @param path the dot-separated path to the subsection
         * @return the subsection, or null if not found
         */
        public @Nullable Section getSubsectionByPath(@NotNull String path) {
            String[] names = path.split("\\.");
            Section section = this;
            for (String s : names) {
                section = section.children.get(s);
                if (section == null) {
                    return null;
                }
            }
            return section;
        }

        /**
         * Gets the parent section of this section.
         *
         * @return the parent section, or null if this is the root section.
         */
        public @Nullable Section parent() {
            return parent;
        }

        private static @NotNull Section merge(@Nullable Section parent, final List<@NotNull Section> sections) {
            checkState(!sections.isEmpty(), "cannot merge 0 sections");

            Section firstSection = sections.get(0);
            String name = firstSection.name;
            int depth = firstSection.depth;
            checkState(sections.stream().allMatch(s -> s.name.equals(name)), "merging sections with different names");
            checkState(sections.stream().allMatch(s -> s.depth == depth), "merging sections with different depths");

            Section merged = new Section(name, parent, depth);
            LongSummaryStatistics timeStats = sections.stream().mapToLong(Section::elapsedTimeNanos).summaryStatistics();
            merged.tickCount = sections.stream().mapToInt(Section::getTickCount).sum();
            merged.totalTimeNanos = timeStats.getSum();
            merged.minTimeNanos = timeStats.getMin();
            merged.maxTimeNanos = timeStats.getMax();
            merged.averageTimeNanos = round(timeStats.getAverage());

            Set<String> childrenNames = new HashSet<>();
            sections.stream().map(Section::getChildrenNames).forEach(childrenNames::addAll);

            childrenNames.stream()
                    .map(childName -> sections.stream().map(s -> s.getChild(childName)).collect(toList()))
                    .map(childSectionsAcrossTime -> merge(merged, childSectionsAcrossTime))
                    .forEach(s -> merged.children.put(s.name, s));
            return merged;
        }

        private static void checkSectionName(@NotNull String section) {
            requireNonNull(section, "section names shall not be null");
            if (section.contains(".")) {
                throw new IllegalArgumentException("section names shall not contain '.'");
            }
        }

        /**
         * Gets the minimum time spent in this section across all ticks.
         *
         * @return the minimum time in nanoseconds
         */
        public long minTimeNanos() {
            return this.minTimeNanos;
        }

        /**
         * Gets the maximum time spent in this section across all ticks.
         *
         * @return the maximum time in nanoseconds
         */
        public long maxTimeNanos() {
            return this.maxTimeNanos;
        }

        /**
         * Gets the average time spent in this section across all ticks.
         *
         * @return the average time in nanoseconds
         */
        public long averageTimeNanos() {
            return this.averageTimeNanos;
        }

    }

}
