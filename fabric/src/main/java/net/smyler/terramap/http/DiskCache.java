package net.smyler.terramap.http;


import net.smyler.smylib.io.CountingInputStream;
import net.smyler.smylib.threading.DefaultThreadLocal;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.smyler.smylib.Strings.hexString;

public class DiskCache implements HttpCache {

    private final Logger logger;
    private final Path directory;
    private final DefaultThreadLocal<Random> random = new DefaultThreadLocal<>(Random::new);

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
        try (FileInputStream fis = new FileInputStream(file)) {
            CountingInputStream stream = new CountingInputStream(new GZIPInputStream(fis));
            DataInputStream data = new DataInputStream(stream);
            if (!data.readUTF().equals(uri.toString())) {
                // Hash collision, we don't have the response in cache
                return null;
            }

            long lastModified = data.readLong();
            long maxAge = data.readLong();
            boolean etagPresent = data.readBoolean();
            String etag;
            if (etagPresent) {
                etag = data.readUTF();
            } else {
                etag = null;
            }
            boolean immutable = data.readBoolean();
            boolean mustRevalidate = data.readBoolean();
            final long toSkip = stream.count();
            return new CacheEntry(
                    uri, lastModified, maxAge, etag, immutable, mustRevalidate, () -> {
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
            );
        } catch (IOException e) {
            this.logger.error("Error when reading cache entry");
            this.logger.catching(e);
            return null;
        }
    }

    private Path getSaveFile(URI uri) {
        return this.directory.resolve(this.computeKey(uri));
    }

    private Path getTempFile() {
        Random random = this.random.get();
        byte[] randomBuffer = new byte[16];
        random.nextBytes(randomBuffer);
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

}
