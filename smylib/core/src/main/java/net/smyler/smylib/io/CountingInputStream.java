package net.smyler.smylib.io;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream {

    private final InputStream wrapped;
    private long count = 0;

    public CountingInputStream(InputStream wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int read() throws IOException {
        this.count++;
        return this.wrapped.read();
    }

    public long count() {
        return this.count;
    }

}
