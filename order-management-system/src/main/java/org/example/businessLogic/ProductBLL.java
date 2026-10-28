package org.example.businessLogic;

import org.example.dataAccess.ProductDAO;
import org.example.model.Product;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business Logic Layer class for managing Product operations.
 */

public class ProductBLL {
    private ProductDAO productDAO;
    public ProductBLL() {
        this.productDAO = new ProductDAO();
    }
    public Product findProductById(int id) {
        Product product = (Product) productDAO.findById(id);
        if (product == null) {
            throw new NoSuchElementException("The product with id " + id + " was not found!");
        }
        return product;
    }
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }
    public void insertProduct(Product product) {
        if (product.getPrice() < 0 || product.getStock() < 0) {
            throw new IllegalArgumentException("The value is impossible!");
        }
        productDAO.insert(product);
    }
    public void updateProduct(Product product) {
        findProductById(product.getId());
        productDAO.update(product);
    }
    public void deleteProduct(int id) {
        findProductById(id);
        productDAO.delete(id);
    }
}
