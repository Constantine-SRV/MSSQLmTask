import db.ServerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        // ── стартуем секундомер ──────────────────────────────────
        long t0 = System.nanoTime();

        // ── создаём пул на 20 потоков ────────────────────────────
        ExecutorService ioPool = Executors.newFixedThreadPool(80);

        // ── параметры одного «тестового» сервера ────────────────
        String url  = "jdbc:sqlserver://sqlupdate2019:1433;databaseName=master;encrypt=false";
        String user = "userJava";
        String pwd  = "qaz123";

        // ── SQL-набор (с задержкой 2 с) ─────────────────────────
        List<String> queries = List.of(
                "WAITFOR DELAY '00:00:02'; SELECT @@SERVERNAME",
                "WAITFOR DELAY '00:00:02'; SELECT TOP (1) name FROM sys.databases"
        );

        // ── генерируем 40 одинаковых ServerRequest ──────────────
        List<ServerRequest> servers = new ArrayList<>();
        for (int i = 1; i <= 80; i++) {
            servers.add(new ServerRequest(i, url, user, pwd, queries));
        }

        // ── выполняем всё параллельно (20 потоков) ──────────────
        CompletableFuture
                .allOf(servers.stream()
                        .map(sr -> sr.execute(ioPool))
                        .toArray(CompletableFuture[]::new))
                .join();

        ioPool.shutdown();

        // ── выводим время ───────────────────────────────────────
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Done. Elapsed: %d ms (%.2f s)%n",
                elapsedMs, elapsedMs / 1000.0);
    }
}
