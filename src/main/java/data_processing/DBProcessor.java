package data_processing;

import models.products.Book;
import models.products.Laptop;
import models.products.Product;
import models.products.ProductType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
public class DBProcessor implements DataProcessor {

    // region Private constants and related methods

    // region Queries

    private static final String DB_URL = "jdbc:h2:~/test";

    private static final String SCHEMA_NAME = "STORE";

    private static final String PRODUCTS_TABLE = SCHEMA_NAME + ".PRODUCTS";
    private static final String BOOKS_TABLE = SCHEMA_NAME + ".BOOKS";
    private static final String LAPTOPS_TABLE = SCHEMA_NAME + ".LAPTOPS";

    private static final String CREATE_SCHEMA =
            "CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME + ";\n" +
                    "SET SCHEMA " + SCHEMA_NAME + ";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `" + PRODUCTS_TABLE + "` (\n" +
                    "  `id` int NOT NULL,\n" +
                    "  `price` real DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `" + BOOKS_TABLE + "` (\n" +
                    "  `prod_id` int NOT NULL,\n" +
                    "  `title` varchar(255) NOT NULL,\n" +
                    "  `pages` int DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`prod_id`)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `" + LAPTOPS_TABLE + "` (\n" +
                    "  `prod_id` int NOT NULL,\n" +
                    "  `model` varchar(255) NOT NULL,\n" +
                    "  `cores` int DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`prod_id`)\n" +
                    ");\n";

    private static final String SEED =
            "INSERT INTO " + PRODUCTS_TABLE + " VALUES(1, 100);\n" +
                    "INSERT INTO " + PRODUCTS_TABLE + " VALUES(2, 200);\n" +
                    "INSERT INTO " + PRODUCTS_TABLE + " VALUES(3, 1000);\n" +
                    "INSERT INTO " + PRODUCTS_TABLE + " VALUES(4, 2000);\n" +
                    "\n" +
                    "INSERT INTO " + BOOKS_TABLE + " VALUES(1, 'War&Peace', 2000);\n" +
                    "INSERT INTO " + BOOKS_TABLE + " VALUES(2, 'Shantaram', 1000);\n" +
                    "INSERT INTO " + LAPTOPS_TABLE + " VALUES(3, 'YOGA11', 1);\n" +
                    "INSERT INTO " + LAPTOPS_TABLE + " VALUES(4, 'MACBOOK', 2);\n";


    private static final String INSERT_PRODUCT =
            "INSERT INTO " + PRODUCTS_TABLE + "(id, price) VALUES(?,?)";
    private static final String INSERT_BOOK =
            "INSERT INTO " + BOOKS_TABLE + "(prod_id, title, pages) VALUES(?,?,?)";
    private static final String INSERT_LAPTOP =
            "INSERT INTO " + LAPTOPS_TABLE + "(prod_id, model, cores) VALUES(?,?,?)";


    private static String clearTable(String table) {
        return "DELETE FROM " + table;
    }

    private static String fetchWholeTable(String table) {
        return "SELECT * " +
                "FROM " + PRODUCTS_TABLE + " JOIN " + table +
                " ON " + PRODUCTS_TABLE + ".id = " + table + ".prod_id";
    }

    // endregion

    private interface PreparedStatementGetter {
        PreparedStatement get(Connection connection, Product product) throws SQLException;
    }

    private static final HashMap<ProductType, PreparedStatementGetter> statementGetters
            = new HashMap<ProductType, PreparedStatementGetter>() {
        {
            put(ProductType.BOOK, DBProcessor::getPreparedStatementForBook);
            put(ProductType.LAPTOP, DBProcessor::getPreparedStatementForLaptop);
        }
    };

    private interface ProductGetter {
        Product get(ResultSet rs) throws SQLException;
    }

    private static PreparedStatement getPreparedStatementForLaptop(Connection connection, Product product) throws SQLException {
        Laptop pc = (Laptop)product;
        PreparedStatement statement = connection.prepareStatement(INSERT_LAPTOP);
        statement.setLong(1, pc.getId());
        statement.setString(2, pc.getModel());
        statement.setInt(3, pc.getCores());
        return statement;
    }

    private static PreparedStatement getPreparedStatementForBook(Connection connection, Product product) throws SQLException {
        Book b = (Book)product;
        PreparedStatement statement = connection.prepareStatement(INSERT_BOOK);
        statement.setLong(1, b.getId());
        statement.setString(2, b.getTitle());
        statement.setInt(3, b.getPages());
        return statement;
    }

    private static final HashMap<String, ProductGetter> productGetters = new HashMap<String, ProductGetter>() {
        {
            put(BOOKS_TABLE, DBProcessor::getBookFromResultSet);
            put(LAPTOPS_TABLE, DBProcessor::getLaptopFromResultState);
        }
    };

    private static Product getLaptopFromResultState(ResultSet rs) throws SQLException {
        Product p = getProductFromResultSet(rs);
        String model = rs.getString("model");
        int cores = rs.getInt("cores");
        return new Laptop(p.getId(), p.getPrice(), model, cores);
    }

    private static Product getBookFromResultSet(ResultSet rs) throws SQLException {
        Product p = getProductFromResultSet(rs);
        String title = rs.getString("title");
        int pages = rs.getInt("pages");
        return new Book(p.getId(), p.getPrice(), title, pages);
    }

    private static Product getProductFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        double price = rs.getDouble("price");
        return new Product(id, price);
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
        executeQuery(connection, clearTable(BOOKS_TABLE));
        executeQuery(connection, clearTable(LAPTOPS_TABLE));
        executeQuery(connection, clearTable(PRODUCTS_TABLE));
    }

    // Insert product to database
    private static void insert(Connection connection, Product product) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT);

        statement.setLong(1, product.getId());
        statement.setDouble(2, product.getPrice());
        statement.executeUpdate();

        PreparedStatementGetter statementGetter = statementGetters.get(product.getType());
        statement = statementGetter.get(connection, product);
        statement.executeUpdate();

        statement.close();
    }

    // Get all products
    private static List<Product> fetchAll(Connection connection, String tableName) throws SQLException {
        List<Product> result = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement(fetchWholeTable(tableName));
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            ProductGetter productGetter = productGetters.get(tableName);
            result.add(productGetter.get(rs));
        }

        statement.close();
        return result;
    }

    // endregion

    static {
        try {
            fetchAll(getConnection(), BOOKS_TABLE); // Check schema
        } catch (SQLException | ClassNotFoundException e) {
            try (Connection connection = getConnection()) {
                executeQuery(connection, CREATE_SCHEMA);
                executeQuery(connection, SEED);
                connection.commit();
                connection.close();
            } catch (SQLException | ClassNotFoundException ignored) {}
        }
    }

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

            products.addAll(fetchAll(connection, BOOKS_TABLE));
            products.addAll(fetchAll(connection, LAPTOPS_TABLE));

            connection.close();
            return products;
        } catch (ClassNotFoundException | SQLException e) {
            throw new DataProcessingException(e.getLocalizedMessage());
        }
    }

    // endregion
}
