package com.ucsc.tutionplatform.database;

import com.ucsc.tutionplatform.utils.ConfigReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public final class DatabaseHandler {

    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final int DEFAULT_BATCH_SIZE = 500;

    private DatabaseHandler() {
    }

    public static Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(
                ConfigReader.getProperty(DB_URL),
                ConfigReader.getProperty(DB_USERNAME),
                ConfigReader.getProperty(DB_PASSWORD)
        );
    }

    public static int insert(String sql, Object... parameters) {
        return executeUpdate(sql, parameters);
    }

    public static List<Map<String, Object>> select(String sql, Object... parameters) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);

            try (ResultSet resultSet = statement.executeQuery()) {
                return toRows(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to execute select query: " + sql, exception);
        }
    }

    public static int update(String sql, Object... parameters) {
        return executeUpdate(sql, parameters);
    }

    public static int delete(String sql, Object... parameters) {
        return executeUpdate(sql, parameters);
    }

    public static int executeUpdate(String sql, Object... parameters) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to execute update query: " + sql, exception);
        }
    }

    public static void truncateTable(String tableName) {
        validateIdentifier(tableName);

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE " + tableName);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to truncate table: " + tableName, exception);
        }
    }

    public static int bulkInsertFromCsv(String tableName, String csvResourcePath) {
        return bulkInsertFromCsv(tableName, csvResourcePath, DEFAULT_BATCH_SIZE);
    }

    public static int bulkInsertFromCsvFile(String tableName, String csvFilePath) {
        return bulkInsertFromCsvFile(tableName, csvFilePath, DEFAULT_BATCH_SIZE);
    }

    public static int bulkInsertFromCsvFile(String tableName, String csvFilePath, int batchSize) {
        validateIdentifier(tableName);

        try (InputStream inputStream = Files.newInputStream(Path.of(csvFilePath))) {
            return bulkInsertFromCsv(tableName, inputStream, batchSize);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read CSV file: " + csvFilePath, exception);
        }
    }

    public static int bulkInsertFromCsv(String tableName, String csvResourcePath, int batchSize) {
        validateIdentifier(tableName);

        try (InputStream inputStream = DatabaseHandler.class.getClassLoader().getResourceAsStream(csvResourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("CSV file not found in resources: " + csvResourcePath);
            }

            return bulkInsertFromCsv(tableName, inputStream, batchSize);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to close CSV file: " + csvResourcePath, exception);
        }
    }

    public static int bulkInsertFromCsv(String tableName, InputStream inputStream, int batchSize) {
        validateIdentifier(tableName);

        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than zero");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             Connection connection = getConnection()) {
            List<String> columns = readColumns(reader);
            String sql = buildInsertSql(tableName, columns);
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int totalInsertedRows = insertCsvRows(reader, statement, columns.size(), batchSize);
                connection.commit();
                connection.setAutoCommit(originalAutoCommit);
                return totalInsertedRows;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                connection.setAutoCommit(originalAutoCommit);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to bulk insert CSV into table: " + tableName, exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read CSV data for table: " + tableName, exception);
        }
    }

    private static int insertCsvRows(
            BufferedReader reader,
            PreparedStatement statement,
            int columnCount,
            int batchSize
    ) throws IOException, SQLException {
        int pendingBatchRows = 0;
        int totalInsertedRows = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }

            List<String> values = parseCsvLine(line);

            if (values.size() != columnCount) {
                throw new IllegalArgumentException("CSV column count mismatch. Expected "
                        + columnCount + " values but found " + values.size() + " in row: " + line);
            }

            for (int index = 0; index < values.size(); index++) {
                statement.setString(index + 1, values.get(index));
            }

            statement.addBatch();
            pendingBatchRows++;

            if (pendingBatchRows == batchSize) {
                totalInsertedRows += countBatchRows(statement.executeBatch());
                pendingBatchRows = 0;
            }
        }

        if (pendingBatchRows > 0) {
            totalInsertedRows += countBatchRows(statement.executeBatch());
        }

        return totalInsertedRows;
    }

    private static List<String> readColumns(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();

        if (headerLine == null || headerLine.isBlank()) {
            throw new IllegalArgumentException("CSV file must contain a header row");
        }

        List<String> columns = parseCsvLine(headerLine);

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("CSV header row must contain at least one column");
        }

        for (String column : columns) {
            validateIdentifier(column);
        }

        return columns;
    }

    private static String buildInsertSql(String tableName, List<String> columns) {
        StringJoiner columnJoiner = new StringJoiner(", ");
        StringJoiner parameterJoiner = new StringJoiner(", ");

        for (String column : columns) {
            columnJoiner.add(column);
            parameterJoiner.add("?");
        }

        return "INSERT INTO " + tableName + " (" + columnJoiner + ") VALUES (" + parameterJoiner + ")";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);

            if (character == '"') {
                if (insideQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    value.append('"');
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (character == ',' && !insideQuotes) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }

        if (insideQuotes) {
            throw new IllegalArgumentException("Invalid CSV row. Missing closing quote: " + line);
        }

        values.add(value.toString().trim());
        return values;
    }

    private static void setParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }

    private static List<Map<String, Object>> toRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();

            for (int index = 1; index <= columnCount; index++) {
                row.put(metaData.getColumnLabel(index), resultSet.getObject(index));
            }

            rows.add(row);
        }

        return rows;
    }

    private static int countBatchRows(int[] batchResults) {
        int count = 0;

        for (int result : batchResults) {
            if (result >= 0 || result == Statement.SUCCESS_NO_INFO) {
                count++;
            }
        }

        return count;
    }

    private static void validateIdentifier(String identifier) {
        if (identifier == null || !identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid database identifier: " + identifier);
        }
    }
}
