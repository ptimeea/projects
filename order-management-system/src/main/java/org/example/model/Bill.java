package org.example.model;

import java.util.List;

/**
 * The class represents the bill after an order is finalized.
 */

public record Bill(int id, int order_id, String client_name, String product_name, int quantity, Double price) {

}
