package com.halimjr11.api.model;

/**
 * Represents an order placed by a customer.
 * Contains basic order information such as customer ID,
 * product code, and quantity.
 */
public class Order {

    /**
     * Unique identifier of the customer placing the order.
     */
    public String customerId;

    /**
     * Code of the product being ordered.
     */
    public String productCode;

    /**
     * Quantity of the product ordered.
     */
    public int quantity;
}
