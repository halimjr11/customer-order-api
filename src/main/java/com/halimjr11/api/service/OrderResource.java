package com.halimjr11.api.service;

import com.halimjr11.api.model.ApiResponse;
import com.halimjr11.api.model.Order;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;

/**
 * REST resource class that handles order processing.
 * Provides endpoints to receive order requests,
 * save them to the database, and send messages to ActiveMQ.
 */
@Path("/orders")
public class OrderResource {

    /**
     * List of possible JNDI names to try for DataSource lookup.
     */
    private static final String[] JNDI_PATTERNS = {
            "java:comp/env/jdbc/CustomerOrderDS",
            "CustomerOrderDS",
            "jdbc/CustomerOrderDS"
    };

    /**
     * Handles incoming order creation requests.
     *
     * @param order The order payload submitted in JSON format.
     * @return JSON response indicating success or failure.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(Order order) {
        if (order == null) {
            ApiResponse<Object> response = ApiResponse.error(400, "Order payload is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        try {
            // Save the order into the database
            saveToDatabase(order);

            // Send order message to ActiveMQ
            String msg = String.format("Customer: %s, Product: %s, Qty: %d",
                    order.customerId, order.productCode, order.quantity);
            new JmsService().sendOrderMessage(msg);

            ApiResponse<Order> response = ApiResponse.success("Order received and processed", order);
            return Response.ok(response).build();

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
            ApiResponse<Object> response = ApiResponse.error(500,
                    "Failed to process order: " + e.getMessage());
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * Saves the order into the database using a DataSource obtained from JNDI.
     *
     * @param order The order object containing customer, product, and quantity.
     * @throws SQLException    If an SQL error occurs during insert.
     * @throws NamingException If the DataSource lookup fails.
     */
    private void saveToDatabase(Order order) throws SQLException, NamingException {
        String sql = "INSERT INTO orders (customer_id, product_code, quantity) VALUES (?, ?, ?)";

        try (Connection conn = getConnectionFromJndi();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.customerId);
            stmt.setString(2, order.productCode);
            stmt.setInt(3, order.quantity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert order, no rows affected");
            }
        }
    }

    /**
     * Attempts to retrieve a database connection from one of several JNDI names.
     *
     * @return A live SQL connection from a JNDI DataSource.
     * @throws NamingException If no JNDI pattern resolves to a valid DataSource.
     * @throws SQLException    If connection retrieval fails.
     */
    private Connection getConnectionFromJndi() throws NamingException, SQLException {
        Context ctx = null;
        try {
            ctx = new InitialContext();

            // Try each JNDI name pattern
            for (String pattern : JNDI_PATTERNS) {
                try {
                    System.out.println("Trying JNDI pattern: " + pattern);
                    DataSource ds = (DataSource) ctx.lookup(pattern);
                    Connection conn = ds.getConnection();
                    System.out.println("SUCCESS: Connected using pattern: " + pattern);
                    return conn;
                } catch (Exception e) {
                    System.out.println("FAILED: Pattern " + pattern + " - " + e.getMessage());
                }
            }

            throw new NamingException("DataSource not found with any JNDI pattern");

        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    System.err.println("Failed to close JNDI context: " + e.getMessage());
                }
            }
        }
    }
}
