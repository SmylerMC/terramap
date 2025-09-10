package net.smyler.smylib.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An executor that ONLY calls the first {@link Runnable} ever given to it.
 *
 * @author Smyler
 */
public class OnceExecutor implements Executor {
    private final AtomicBoolean hasRun = new AtomicBoolean(false);

    @Override
    public void execute(@NotNull Runnable command) {
        if (this.hasRun.compareAndSet(false, true)) {
            command.run();
        }
    }

}
