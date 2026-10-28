package org.example.businessLogic;

import org.example.dataAccess.BillDAO;
import org.example.dataAccess.OrderItemsDAO;
import org.example.dataAccess.ProductDAO;
import org.example.dataAccess.OrdersDAO;
import org.example.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business Logic Layer class for managing Orders operations.
 */

public class OrdresBLL {
    private OrdersDAO ordresDAO = new OrdersDAO();
    private OrderItemsDAO orderItemsDAO = new OrderItemsDAO();
    private ProductDAO productDAO = new ProductDAO();
    private BillDAO billDAO = new BillDAO();
    private Map<Integer, List<CartItem>> clientCarts = new HashMap<>();
    public OrdresBLL() {}
    public List<CartItem> getCartForClient(int clientId) {
        if (!clientCarts.containsKey(clientId)) {
            clientCarts.put(clientId, new ArrayList<>());
        }
        return clientCarts.get(clientId);
    }
    public void addProductToCart(int clientId, Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero!");
        }
        List<CartItem> cart = getCartForClient(clientId);
        CartItem existingItem = null;
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                existingItem = item;
                break;
            }
        }
        int totalTargetQuantity = quantity;
        if (existingItem != null) {
            totalTargetQuantity += existingItem.getQuantity();
        }
        if (totalTargetQuantity > product.getStock()) {
            throw new IllegalArgumentException("Insufficient products!");
        }
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(product, quantity));
        }
    }

    public void clearCartForClient(int clientId) {
        getCartForClient(clientId).clear();
    }
    public List<Orders> findAllOrders() {
        return ordresDAO.findAll();
    }
    public void finalizeOrder(Client client) {
        if (getCartForClient(client.getId()).isEmpty()) {
            throw new IllegalStateException("Empty shopping cart!");
        }
        double totalSum = getCartForClient(client.getId()).stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Orders order = new Orders();
        order.setClient_id(client.getId());
        order.setStatus("PENDING");
        order.setTotal_sum(totalSum);
        Orders savedOrder = (Orders) ordresDAO.insert(order);

        try {
            for (CartItem item : getCartForClient(client.getId())) {
                Product dbProduct = (Product) productDAO.findById(item.getProduct().getId());
                if (dbProduct.getStock() < item.getQuantity()) {
                    savedOrder.setStatus("FAILED");
                    ordresDAO.update(savedOrder);
                    throw new UnderStockException("Not enough products!");
                }
                dbProduct.setStock(dbProduct.getStock() - item.getQuantity());
                productDAO.update(dbProduct);

                OrderItems orderItem = new OrderItems();
                orderItem.setOrder_id(savedOrder.getId());
                orderItem.setProduct_id(dbProduct.getId());
                orderItem.setQuantity(item.getQuantity());
                orderItemsDAO.insert(orderItem);

                Bill bill = new Bill(0, savedOrder.getId(), client.getName(), dbProduct.getName(), item.getQuantity(), dbProduct.getPrice());
                billDAO.insert(bill);

            }

            savedOrder.setStatus("SUCCESS");
            ordresDAO.update(savedOrder);
            clearCartForClient(client.getId());

        } catch (UnderStockException e) {
            throw e;
        }
    }
    public List<Bill> findAllBills() {
        return billDAO.findAll();
    }
}
