package com.erzbir.mirai.numeron.entity;

import com.erzbir.mirai.numeron.sql.SqlUtil;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

/**
 * @author Erzbir
 * @Date: 2022/11/26 13:32
 */
@Getter
public class IllegalList {
    public static IllegalList INSTANCE = new IllegalList();

    static {
        String sql = """
                CREATE TABLE IF NOT EXISTS ILLEGALS(
                    KEY BIGINT PRIMARY KEY NOT NULL
                    )
                """;
        String findAll = "SELECT * FROM ILLEGALS";
        ResultSet resultSet = null;
        try (Statement statement = SqlUtil.connection.createStatement()) {
            statement.executeUpdate(sql);
            resultSet = statement.executeQuery(findAll);
            while (resultSet.next()) {
                String key = resultSet.getString("KEY");
                INSTANCE.illegal.add(key);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private final HashSet<String> illegal = new HashSet<>();

    private boolean exist(String value) {
        ResultSet resultSet;
        try (Statement statement = SqlUtil.connection.createStatement()) {
            String sql = "SELECT * FROM ILLEGALS WHERE KEY = '" + value + "'";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return true;
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return false;
    }

    private void addS(String value) {
        if (exist(value)) {
            return;
        }
        try (Statement statement = SqlUtil.connection.createStatement()) {
            String sql = "INSERT INTO ILLEGALS(KEY) VALUES(" + value + ")";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void removeS(String value) {
        if (!exist(value)) {
            return;
        }
        try (Statement statement = SqlUtil.connection.createStatement()) {
            String sql = "DELETE FROM ILLEGALS WHERE KEY = " + value;
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    private void addD(String value) {
        illegal.remove(value);
    }

    private void removeD(String value) {
        illegal.remove(value);
    }

    public void add(String value) {
        addD(value);
        new Thread(() -> addS(value)).start();
    }

    public void remove(String value) {
        removeD(value);
        new Thread(() -> removeS(value)).start();
    }
}
