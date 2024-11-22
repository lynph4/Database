package com.lab2.service;

import com.lab2.dto.OrderDTO;
import com.lab2.entity.Client;
import com.lab2.entity.Courier;
import com.lab2.entity.Order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OrderService implements Service<Order, OrderDTO, Long> {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private EntityManagerFactory emf;

    public OrderService(EntityManagerFactory emf) {
        this.emf = emf;
        LOGGER.info("OrderService initialized.");
        LOGGER.setLevel(Level.OFF);
    }

    @Override
    public Optional<Order> findRecord(Long orderID) throws RuntimeException {
        LOGGER.info("Finding order with ID: " + orderID);
        EntityManager em = emf.createEntityManager();
        try {
            Order order = em.find(Order.class, orderID);
            if (order == null) {
                LOGGER.warning("Order with ID " + orderID + " not found.");
            }
            return Optional.ofNullable(order);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while finding order with ID: " + orderID, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> getAllRecords() throws RuntimeException {
        LOGGER.info("Fetching all orders.");
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
            LOGGER.info("Retrieved " + orders.size() + " orders.");
            return orders;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching all orders.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void addRecord(OrderDTO order) throws RuntimeException {
        LOGGER.info("Adding order: " + order.getOrderID());

        LocalDateTime orderDate = null, deliveryDate = null;
        try {
            orderDate = LocalDateTime.parse(order.getOrderDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            deliveryDate = LocalDateTime.parse(order.getDeliveryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            LOGGER.warning("Invalid date format for Order ID " + order.getOrderID() + ". Skipping adding.");
        }
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Assumes that the courier and client are exist and have been validated prior to this call
            Courier existingCourier = em.find(Courier.class, order.getCourierPhone());
            Client existingClient = em.find(Client.class, order.getClientEmail());
            
            Order newOrder = Order.builder()
                .orderID(order.getOrderID())
                .courier(existingCourier)
                .client(existingClient)
                .orderDate(orderDate)
                .deliveryDate(deliveryDate)
                .rating(order.getRating())
                .deliveryAddress(order.getDeliveryAddress())
                .build();

            em.persist(newOrder);
            transaction.commit();
            LOGGER.info("Order added successfully: " + order.getOrderID());
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while adding order: " + order.getOrderID(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateRecord(OrderDTO order) throws RuntimeException {
        LOGGER.info("Updating order: " + order.getOrderID());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Order existingOrder = em.find(Order.class, order.getOrderID());
            if (existingOrder == null) {
                transaction.rollback();
                LOGGER.warning("Order with ID " + order.getOrderID() + " not found for update.");
                return false;
            }

            LocalDateTime orderDate = null, deliveryDate = null;
            try {
                orderDate = LocalDateTime.parse(order.getOrderDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                deliveryDate = LocalDateTime.parse(order.getDeliveryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid 'orderDate' format for Order ID " + order.getOrderID() + ". Skipping update.");
                return false;
            }
            
            existingOrder.setOrderDate(orderDate);
            existingOrder.setDeliveryDate(deliveryDate);
            existingOrder.setRating(order.getRating());
            existingOrder.setDeliveryAddress(order.getDeliveryAddress());
            em.merge(existingOrder);
            transaction.commit();
            LOGGER.info("Order updated successfully: " + order.getOrderID());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while updating order: " + order.getOrderID(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteRecord(Long orderID) throws RuntimeException {
        LOGGER.info("Deleting order with ID: " + orderID);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();    
            Order order = em.find(Order.class, orderID);
            if (order == null) {
                transaction.rollback();
                LOGGER.warning("Order with ID " + orderID + " not found for deletion.");
                return false;
            }

            em.remove(order);
            transaction.commit();
            LOGGER.info("Order deleted successfully: " + order.getOrderID());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while deleting order with ID: " + orderID, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Order convertToEntity(OrderDTO dto) throws NoSuchElementException {
        return findRecord(dto.getOrderID()).get();
    }

    public void close() {
        LOGGER.info("Closing EntityManagerFactory.");
        if (emf != null) {
            emf.close();
        }
    }
}
