package org.example.model;

/**
 * The class represents the connection between orders and items.
 */


public class OrderItems {
    private int id;
    private int order_id;
    private int product_id;
    private int quantity;
    public OrderItems() {}
    public OrderItems(int id, int order_id, int product_id, int quantity) {
        this.id = id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }
    public OrderItems(int order_id, int product_id, int quantity) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getOrder_id() {return order_id;}
    public void setOrder_id(int order_id) {this.order_id = order_id;}
    public int getProduct_id() {return product_id;}
    public void setProduct_id(int product_id) {this.product_id = product_id;}
    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

}
