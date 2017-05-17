package com.codecool.shop.dao.implementation;

import com.codecool.shop.controller.DBController;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kata on 2017.05.08..
 */
public class ProductDaoWithJdbc implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductDaoWithJdbc.class);
    private static ProductDaoWithJdbc instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    protected ProductDaoWithJdbc() {
    }

    public static ProductDaoWithJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoWithJdbc();
        }
        return instance;
    }


    @Override
    public void add(Product product) {
        logger.info("Add new product to products table...");
        int id;
        List<Product> existingProducts = getAll();
        if (find(product.getName()) == null) {
            if (existingProducts.size() != 0) {
                id = existingProducts.size() + 1;
            } else {
                id = 1;
            }
            String query = "INSERT INTO products (id, name, default_price, currency, description," +
                    " supplier, product_category)" +
                    "VALUES ('" + id + "','"
                    + product.getName() + "', '"
                    + product.getDefaultPrice() + "', '"
                    + product.getDefaultCurrency() + "', '"
                    + product.getDescription() + "', '"
                    + product.getSupplier().getId() + "', '"
                    + product.getProductCategory().getId() + "');";
            try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
                product.setId(id);
                logger.info("Product is successfully added!");
            } catch (SQLException e) {
                logger.error("SQL exception: {}", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public Product find(int id) {
        logger.info("Find product with id:{} ...", id);
        String query = "SELECT product_categories.name AS pc_name, products.name AS p_name, suppliers.name AS s_name, * " +
                "FROM products LEFT JOIN product_categories ON products.product_category=product_categories.id " +
                "LEFT JOIN suppliers ON products.supplier=suppliers.id WHERE products.id ='" + id + "';";
        Product product = null;

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {
                ProductCategory category = new ProductCategory(result.getString("pc_name"),
                        result.getString("department"),
                        result.getString("description"));
                category.setId(result.getInt("product_category"));
                Supplier supplier = new Supplier(result.getString("s_name"),
                        result.getString("description"));
                supplier.setId(result.getInt("supplier"));
                product = new Product(result.getString("p_name"),
                        result.getInt("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        category, supplier);
                product.setId(id);
            }

        } catch (SQLException e) {
            logger.error("SQL exception: {}", e);
            e.printStackTrace();
        }
        logger.info("Product found!");
        return product;
    }

    public Product find(String name) {
        logger.info("Find product with name: {} ...", name);

        String query = "SELECT product_categories.name AS pc_name, products.name AS p_name, suppliers.name AS s_name, * " +
                "FROM products LEFT JOIN product_categories ON products.product_category=product_categories.id " +
                "LEFT JOIN suppliers ON products.supplier=suppliers.id WHERE products.name ='" + name + "';";

        Product product = null;
        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {
                ProductCategory category = new ProductCategory(result.getString("pc_name"),
                        result.getString("department"),
                        result.getString("description"));
                category.setId(result.getInt("product_category"));
                Supplier supplier = new Supplier(result.getString("s_name"),
                        result.getString("description"));
                supplier.setId(result.getInt("supplier"));
                product = new Product(name, result.getInt("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        category, supplier);
                product.setId(result.getInt("id"));
            }
        } catch (SQLException e) {
            logger.error("SQL exception: {}", e);
            e.printStackTrace();
        }
        logger.info("Product(s) found!");
        return product;
    }

    @Override
    public void remove(int id) {
        logger.info("Remove product with id:{}...!", id);

        String query = "DELETE FROM products WHERE id = '" + id + "';";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            logger.info("Product was successfully deleted!");
        } catch (SQLException e) {
            logger.error("SQL exception: {}", e);
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products;";

        return queryExecuteHandler(query);
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        String query = "SELECT id FROM products WHERE supplier=" + supplier.getId() + ";";

        return queryExecuteHandler(query);
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        String query = "SELECT id FROM products WHERE product_category=" + productCategory.getId() + ";";

        return queryExecuteHandler(query);
    }

    @Override
    public void clearAll() {
        String query = "DELETE FROM products;";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.error("SQL exception: {}", e);
            e.printStackTrace();
        }
    }


    private ArrayList<Product> queryExecuteHandler(String query) {
        ArrayList<Product> allProducts = new ArrayList<>();

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Product product = find(result.getInt("id"));
                product.setId(result.getInt("id"));
                allProducts.add(product);
            }
            return allProducts;
        } catch (SQLException e) {
            logger.error("SQL exception: {}", e);
            e.printStackTrace();
        }

        return null;
    }

}
