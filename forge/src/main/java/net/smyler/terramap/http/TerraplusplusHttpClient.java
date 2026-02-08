package net.smyler.terramap.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.buildtheearth.terraplusplus.util.http.Http;

public class TerraplusplusHttpClient implements HttpClient {


    @Override
    public CompletableFuture<byte[]> get(String url) {
        CompletableFuture<ByteBuf> future = Http.get(url);
        CompletableFuture<byte[]> result = future.thenApply(ByteBufUtil::getBytes);
        result.exceptionally(t -> {
            if (t instanceof CancellationException) {
                future.cancel(true);
            }
            return null;
        });
        return result;
    }

    @Override
    public void setMaxConcurrentRequests(String host, int maxConcurrentRequests) {
        Http.setMaximumConcurrentRequestsTo(host, maxConcurrentRequests);
    }

}
