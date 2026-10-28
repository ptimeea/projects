package org.example.dataAccess;

import org.example.ConnectionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;

/**
 * Generic Data Access Object class that implements fundamental CRUD operations.
 */

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(type.getSimpleName().toLowerCase());
        if (field != null) {
            sb.append(" WHERE ").append(field).append(" = ?");
        }
        return sb.append(";").toString();
    }
    private String createInsertQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(type.getSimpleName().toLowerCase()).append(" (");

        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("id")) {
                sb.append(fields[i].getName());
                if (i < fields.length - 1) sb.append(", ");
            }
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("id")) {
                sb.append("?");
                if (i < fields.length - 1) sb.append(", ");
            }
        }
        if (sb.toString().endsWith(", ")) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append(");").toString();
    }
    private String createUpdateQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(type.getSimpleName().toLowerCase()).append(" SET ");

        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("id")) {
                sb.append(field.getName()).append(" = ?, ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(" WHERE id = ?;");
        return sb.toString();
    }
    private String createDeleteQuery() {
        return "DELETE FROM " + type.getSimpleName().toLowerCase() + " WHERE id = ?;";
    }
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery(null);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, type.getName() + "DAO:findAll " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return new ArrayList<>();
    }
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            List<T> list = createObjects(resultSet);
            if (!list.isEmpty()) {
                return list.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, type.getName() + "DAO:findById " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }
    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        String query = createInsertQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int pIndex = 1;
            for (Field field : t.getClass().getDeclaredFields()) {
                if (!field.getName().equals("id")) {
                    field.setAccessible(true);
                    Object value = field.get(t);
                    statement.setObject(pIndex++, value);
                }
            }
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Field idField = t.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(t, generatedKeys.getInt(1));
            }
            return t;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, type.getName() + "DAO:insert " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }
    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            int pIndex = 1;
            Object idValue = null;

            for (Field field : t.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.getName().equals("id")) {
                    statement.setObject(pIndex++, field.get(t));
                } else {
                    idValue = field.get(t);
                }
            }
            statement.setObject(pIndex, idValue);
            statement.executeUpdate();
            return t;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, type.getName() + "DAO:update " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }
    public void delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, type.getName() + "DAO:delete " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
    protected List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                T instance = type.getDeclaredConstructor().newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String name = field.getName();
                    Object value = resultSet.getObject(name);
                    field.setAccessible(true);
                    field.set(instance, value);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error at Reflection: " + e.getMessage(), e);
        }
        return list;
    }
}

