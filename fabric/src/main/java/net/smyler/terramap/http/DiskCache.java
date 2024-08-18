package net.smyler.terramap.http;


import net.smyler.smylib.io.CountingInputStream;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.smyler.smylib.Strings.hexString;
import static net.smyler.terramap.http.CacheStatistics.CacheType.DISK;

public class DiskCache implements HttpCache {

    private static final int FILE_FORMAT_VERSION = 1;

    private final Logger logger;
    private final Path directory;

    public DiskCache(Path directory, Logger logger) {
        this.logger = logger;
        this.directory = directory;
    }

    @Override
    public void put(@NotNull URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, byte @NotNull [] body) {
        Path file = this.getSaveFile(uri);
        Path tempFile = this.getTempFile();
        try(OutputStream stream = new GZIPOutputStream(new FileOutputStream(tempFile.toFile()))) {
            DataOutputStream data = new DataOutputStream(stream);
            data.writeInt(FILE_FORMAT_VERSION);
            data.writeUTF(uri.toString());
            data.writeLong(lastModified);
            data.writeLong(maxAge);
            data.writeBoolean(etag != null);
            if (etag != null) {
                data.writeUTF(etag);
            }
            data.writeBoolean(immutable);
            data.writeBoolean(mustRevalidate);
            stream.write(body);
        } catch (IOException e) {
            this.logger.error("Error when writing cache entry");
            this.logger.catching(e);
        }
        try {
            Files.move(tempFile, file, REPLACE_EXISTING, ATOMIC_MOVE);
        } catch (IOException e) {
            this.logger.error("Error when renaming cache entry");
            this.logger.catching(e);
        }
    }

    @Override
    public @Nullable CacheEntry lookup(URI uri) {
        File file = this.getSaveFile(uri).toFile();
        if (!file.isFile()) {
            return null;
        }
        CacheEntry entry = this.readEntry(file);
        if (entry != null && !entry.uri().equals(uri)) {
            // Hash collision in file name
            return null;
        }
        return entry;
    }

    @Override
    public CacheStatistics statistics() throws IOException {
        try (Stream<Path> paths = Files.list(this.directory)) {
            AtomicLong counter = new AtomicLong(0L);
            long size = paths.parallel()
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .peek(f -> counter.incrementAndGet())
                    .map(File::length)
                    .reduce(Long::sum)
                    .orElse(0L);
            return new CacheStatistics(counter.get(), size, DISK);
        }
    }

    @Override
    public CacheStatistics cleanup(Predicate<CacheEntry> predicate) throws IOException {
        try (Stream<Path> paths = Files.list(this.directory)) {
            AtomicLong entries = new AtomicLong(0L);
            AtomicLong size = new AtomicLong(0L);
            paths.parallel()
                    .map(Path::toFile)
                    .map(f -> new EntryCleanup(f, this.readEntry(f), f.length()))
                    .filter(p -> p.entry() == null || predicate.test(p.entry()))
                    .filter(EntryCleanup::delete)
                    .forEach(e -> {
                        entries.incrementAndGet();
                        size.addAndGet(e.size());
                    });
            return new CacheStatistics(entries.get(), size.get(), DISK);
        }
    }

    private @Nullable CacheEntry readEntry(@NotNull File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            CountingInputStream stream = new CountingInputStream(new GZIPInputStream(fis));
            DataInputStream data = new DataInputStream(stream);
            int version = data.readInt();
            if (version != FILE_FORMAT_VERSION) {
                this.logger.warn("Unsupported disk cache format version {} in file {}", version, file);
            }
            URI uri = new URI(data.readUTF());
            long lastModified = data.readLong();
            long maxAge = data.readLong();
            boolean etagPresent = data.readBoolean();
            String etag = etagPresent ? data.readUTF() : null;
            boolean immutable = data.readBoolean();
            boolean mustRevalidate = data.readBoolean();
            final long toSkip = stream.count();
            return new CacheEntry(uri, lastModified, maxAge, etag, immutable, mustRevalidate, () -> this.getEntryContent(file, toSkip));
        } catch (IOException | URISyntaxException e) {
            this.logger.error("Error when reading cache entry");
            this.logger.catching(e);
            return null;
        }
    }

    private InputStream getEntryContent(final File file, final long toSkip) {
        try {
            InputStream finalStream = new GZIPInputStream(new FileInputStream(file));
            long skipped = finalStream.skip(toSkip);
            if (skipped != toSkip) {
                throw new IOException("Could not skip to index " + toSkip + " only to " + skipped);
            }
            return finalStream;
        } catch (IOException e) {
            this.logger.error("Error reading cache entry");
            this.logger.catching(e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    private Path getSaveFile(URI uri) {
        return this.directory.resolve(this.computeKey(uri));
    }

    private Path getTempFile() {
        byte[] randomBuffer = new byte[16];
        ThreadLocalRandom.current().nextBytes(randomBuffer);
        return this.directory.resolve(hexString(randomBuffer) + ".part");
    }

    private String computeKey(URI uri) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(uri.toString().getBytes());
            return hexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private record EntryCleanup(File file, CacheEntry entry, long size) {

        private boolean delete() {
            return this.file.delete();
        }

    }

}
