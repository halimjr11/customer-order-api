package com.halimjr11.api.service;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * JmsService is responsible for sending order messages
 * to an Apache ActiveMQ queue using JMS API.
 */
public class JmsService {

    // URL to the ActiveMQ broker
    private static final String BROKER_URL = "tcp://localhost:61616";

    // Target queue name in ActiveMQ
    private static final String QUEUE_NAME = "order-queue";

    /**
     * Sends an order message to the configured ActiveMQ queue.
     *
     * @param orderMessage The message content representing the order
     */
    public void sendOrderMessage(String orderMessage) {
        Connection connection = null;
        Session session = null;

        try {
            // Create a connection to the ActiveMQ broker
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = factory.createConnection();
            connection.start();

            // Create a session with no transaction and auto acknowledgment
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create or get the destination queue
            Destination destination = session.createQueue(QUEUE_NAME);

            // Create a message producer for the queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create and send the text message
            TextMessage message = session.createTextMessage(orderMessage);
            producer.send(message);

            System.out.println("Order message sent to ActiveMQ: " + orderMessage);
        } catch (JMSException e) {
            System.err.println("Failed to send message to ActiveMQ: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Ensure resources are closed properly
            try {
                if (session != null)
                    session.close();
                if (connection != null)
                    connection.close();
            } catch (JMSException ex) {
                System.err.println("Failed to close JMS resources: " + ex.getMessage());
            }
        }
    }
}
