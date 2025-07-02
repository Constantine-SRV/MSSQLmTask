package db;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record ServerRequest(
        int recordId,
        String url,
        String user,
        String password,
        List<String> queries) {

    /** Выполнить все запросы этого сервера последовательно, используя указанный пул потоков. */
    public CompletableFuture<Void> execute(Executor executor) {
        return DbConnector.getConnectionAsync(url, user, password)
                .thenCompose(conn -> {
                    CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
                    for (String q : queries) {
                        chain = chain.thenCompose(
                                v -> QueryExecutor.execAndPrintAsync(conn, q, recordId, executor));
                    }
                    return chain.whenComplete((v, ex) -> closeSilently(conn));
                });
    }

    private static void closeSilently(Connection c) {
        try { if (c != null && !c.isClosed()) c.close(); } catch (Exception ignored) {}
    }
}
