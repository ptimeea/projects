package org.example.dataAccess;

import org.example.ConnectionFactory;
import org.example.model.Bill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Data Access Object for Bill class.
 */


public class BillDAO extends AbstractDAO<Bill> {
    @Override
    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM log;";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("id"),
                        resultSet.getInt("order_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price")
                );
                list.add(bill);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillDAO:findAll error: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return list;
    }
    @Override
    public Bill insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        String query = "INSERT INTO log (order_id, client_name, product_name, quantity, price) VALUES (?, ?, ?, ?, ?);";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, bill.order_id());
            statement.setString(2, bill.client_name());
            statement.setString(3, bill.product_name());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.price());

            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                return new Bill(
                        generatedId,
                        bill.order_id(),
                        bill.client_name(),
                        bill.product_name(),
                        bill.quantity(),
                        bill.price()
                );
            }
            return bill;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillDAO:insert error: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }
}