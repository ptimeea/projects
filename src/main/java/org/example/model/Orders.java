package org.example.model;

/**
 * The class represents an order that can be made.
 * It contains the information about the order.
 */


public class Orders {
    private int id;
    private String status;
    private int client_id;
    private double total_sum;
    public Orders(){}
    public Orders(int id, String status, int client_id, double total_sum) {
        this.id = id;
        this.status = status;
        this.client_id = client_id;
        this.total_sum = total_sum;
    }
    public Orders(String status, int client_id, double total_sum) {
        this.status = status;
        this.client_id = client_id;
        this.total_sum = total_sum;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public int getClient_id() {return client_id;}
    public void setClient_id(int client_id) {this.client_id = client_id;}
    public double getTotal_sum() {return total_sum;}
    public void setTotal_sum(double total_sum) {this.total_sum = total_sum;}


}
