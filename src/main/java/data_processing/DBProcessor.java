package data_processing;

import models.products.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
public class DBProcessor implements DataProcessor {

    // region Queries

    private static final String DB_URL = "jdbc:h2:~/test";
    //jdbc:h2:tcp://localhost/~/test

//    private static final String seed = "CREATE SCHEMA IF NOT EXISTS store;\n" +
//            "set schema store;\n" +
//            "\n" +
//            "CREATE TABLE IF NOT EXISTS `products` (\n" +
//            "  `id` int NOT NULL,\n" +
//            "  `price` real DEFAULT NULL,\n" +
//            "  PRIMARY KEY (`id`)\n" +
//            ");\n" +
//            "\n" +
//            "CREATE TABLE IF NOT EXISTS `books` (\n" +
//            "  `prod_id` int NOT NULL,\n" +
//            "  `title` varchar(255) NOT NULL,\n" +
//            "  `pages` int DEFAULT NULL,\n" +
//            "  PRIMARY KEY (`prod_id`)\n" +
//            ");\n" +
//            "\n" +
//            "CREATE TABLE IF NOT EXISTS `laptops` (\n" +
//            "  `prod_id` int NOT NULL,\n" +
//            "  `model` varchar(255) NOT NULL,\n" +
//            "  `cores` int DEFAULT NULL,\n" +
//            "  PRIMARY KEY (`prod_id`)\n" +
//            ");\n" +
//            "\n" +
//            "INSERT INTO products VALUES(1, 100);\n" +
//            "INSERT INTO products VALUES(2, 200);\n" +
//            "INSERT INTO products VALUES(3, 1000);\n" +
//            "INSERT INTO products VALUES(4, 2000);\n" +
//            "\n" +
//            "INSERT INTO books VALUES(1, 'War&Peace', 2000);\n" +
//            "INSERT INTO books VALUES(2, 'Shantaram', 1000);\n" +
//            "INSERT INTO laptops VALUES(3, 'YOGA11', 1);\n" +
//            "INSERT INTO laptops VALUES(4, 'MACBOOK', 2);\n";

    private static final String Products = "STORE.PRODUCTS";
    private static final String Books = "STORE.BOOKS";
    private static final String Laptops = "STORE.LAPTOPS";

    private static final String insertProduct   =
            "INSERT INTO " + Products + "(id, price) VALUES(?,?)";
    private static final String insertBook =
            "INSERT INTO " + Books + "(prod_id, title, pages) VALUES(?,?,?)";
    private static final String insertLaptop =
            "INSERT INTO " + Laptops + "(prod_id, model, cores) VALUES(?,?,?)";

    private static String clearQuery(String table) {
        return "DELETE FROM " + table;
    }

    private static String fetchAllQuery(String table) {
        return "SELECT * " +
                "FROM " + Products + " JOIN " + table +
                " ON " + Products + ".id = " + table + ".prod_id";
    }
    // endregion

    // region Private methods

    // Connect to database
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

    // Execute simple query without result set
    private static void executeQuery(Connection connection, String query) throws SQLException, ClassNotFoundException {
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    private static void clearAll(Connection connection) throws SQLException, ClassNotFoundException {
        executeQuery(connection, clearQuery(Books));
        executeQuery(connection, clearQuery(Laptops));
        executeQuery(connection, clearQuery(Products));
    }

    // Insert product to database
    private static void insert(Connection connection, Product product) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement(insertProduct);

        statement.setLong(1, product.getId());
        statement.setDouble(2, product.getPrice());
        statement.executeUpdate();

        if (product instanceof Book) {
            statement = connection.prepareStatement(insertBook);
            Book b = (Book)product;
            statement.setLong(1, b.getId());
            statement.setString(2, b.getTitle());
            statement.setInt(3, b.getPages());
        } else if (product instanceof Laptop) {
            statement = connection.prepareStatement(insertLaptop);
            Laptop b = (Laptop) product;
            statement.setLong(1, b.getId());
            statement.setString(2, b.getModel());
            statement.setInt(3, b.getCores());
        }


        statement.executeUpdate();
        statement.close();
    }

    // Get all products
    private static List<Product> fetchAll(Connection connection, String table) throws SQLException {
        List<Product> result = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement(fetchAllQuery(table));
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            Product p;
            Long id = rs.getLong("id");
            double price = rs.getDouble("price");

            if (table.equals(Books)) {
                String title = rs.getString("title");
                int pages = rs.getInt("pages");
                p = new Book(id, price, title, pages);
            } else {
                String model = rs.getString("model");
                int cores = rs.getInt("cores");
                p = new Laptop(id, price, model, cores);
            }

            result.add(p);
        }

        statement.close();
        return result;
    }

    // endregion

//    public DBProcessor() {
//        try {
//            Connection connection = getConnection();
//            executeQuery(connection, seed);
//            connection.commit();
//            connection.close();
//        } catch (Exception ignored) {
//
//        }
//
//    }

    // region Public API

    public void save(Collection<Product> list) throws DataProcessingException {
        try (Connection connection = getConnection()) {
            clearAll(connection);
            for (Product t: list) {
                insert(connection, t);
            }
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new DataProcessingException(e.getLocalizedMessage());
        }
    }

    public List<Product> load() throws DataProcessingException {
        try (Connection connection = getConnection()) {
            List<Product> products = new ArrayList<>();

            products.addAll(fetchAll(connection, Books));
            products.addAll(fetchAll(connection, Laptops));

            connection.close();
            return products;
        } catch (ClassNotFoundException | SQLException e) {
            throw new DataProcessingException(e.getLocalizedMessage());
        }
    }

    // endregion
}
