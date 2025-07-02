# MSSQLmTask

A Java 17-based demo project for executing multiple concurrent T-SQL queries against Microsoft SQL Server, showcasing practical multi-threading with JDBC.

## Description

This project demonstrates how to:

- Launch a configurable number of concurrent tasks (e.g., 80) for executing SQL queries on Microsoft SQL Server, even on a machine with only 4 CPU cores.
- Use a fixed-size thread pool to manage parallel execution efficiently.
- Write clean, modular Java code using `CompletableFuture` for asynchronous workflow, without any external libraries.

## Project Structure

- **Main.java**  
  - Entry point.  
  - Creates a thread pool (80 threads for this demo).  
  - Builds a list of 40 `ServerRequest` tasks.  
  - Executes all tasks in parallel using `CompletableFuture.allOf(...)`.  
  - Prints results and total elapsed time.

- **db/DbConnector.java**  
  - Utility to **asynchronously** open a JDBC connection to MSSQL.  
  - Wraps connection logic inside a `CompletableFuture`.

- **db/ServerRequest.java**  
  - A record class holding:  
    - `recordId` (unique task ID),  
    - connection info (`url`, `user`, `password`),  
    - list of SQL queries.  
  - `execute(executor)` method: opens a connection and sequentially executes all queries for this task, using the provided executor.

- **db/QueryExecutor.java**  
  - A helper utility that runs a single SQL query asynchronously on a given connection, and prints all result rows prefixed with `"exec {recordId}:"`.

## Sample Output

When running with 80 threads and 80 tasks, results may look like:

```
exec 80: master
Done. Elapsed: 4895 ms (4.90 s)
```

This shows task 80 completed querying the `master` database, and the whole process took ~4.9 seconds.

## How It Works

- Creates 80-thread pool on a 4-core machine.
- Submits 40 tasks (`ServerRequest`), each running two queries with built‑in delays.
- Uses `CompletableFuture` to manage asynchronous execution and waiting.
- Prints elapsed time at the end.

## Running Instructions

1. Clone the repository.
2. Open it in IntelliJ IDEA or any Java 17 IDE.
3. Add the Microsoft JDBC driver (e.g., `mssql-jdbc-12.10.1.jre11.jar`) to project dependencies.
4. Adjust MSSQL connection details (`url`, `user`, `password`).
5. Run `Main.java`.

## Notes

- All 40 `ServerRequest` objects use the same connection and queries—purely for demo.
- Use `WAITFOR DELAY '00:00:02'` in SQL to simulate query latency.
- No external dependencies: pure Java 17, modular and easy to adapt.

Feel free to fork or modify for your own testing and educational needs!
