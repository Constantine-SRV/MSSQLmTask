package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;

public final class DbConnector {

    private DbConnector() { }

    public static CompletableFuture<Connection> getConnectionAsync(
            String url, String user, String password) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // явная регистрация драйвера (актуально для fat-jar)
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                return DriverManager.getConnection(url, user, password);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get connection", ex);
            }
        });
    }
}
