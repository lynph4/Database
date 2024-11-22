package com.lab2.service;

import com.lab2.dto.CourierDTO;
import com.lab2.entity.Courier;
import com.lab2.util.SQLQueryRuntime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CourierService implements Service<Courier, CourierDTO, String> {
    private static final Logger LOGGER = Logger.getLogger(CourierService.class.getName());
    private EntityManagerFactory emf;

    public CourierService(EntityManagerFactory emf) {
        this.emf = emf;
        LOGGER.info("CourierService initialized.");
        LOGGER.setLevel(Level.OFF);
    }

    @Override
    public Optional<Courier> findRecord(String phone) throws RuntimeException {
        LOGGER.info("Finding courier with phone: " + phone);
        EntityManager em = emf.createEntityManager();
        try {
            Courier courier = em.find(Courier.class, phone);
            if (courier == null) {
                LOGGER.warning("Courier with phone " + phone + " not found.");
            }
            return Optional.ofNullable(courier);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while finding courier with phone: " + phone, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Courier> getAllRecords() throws RuntimeException {
        LOGGER.info("Fetching all couriers.");
        EntityManager em = emf.createEntityManager();
        try {
            List<Courier> couriers = em.createQuery("SELECT c FROM Courier c", Courier.class).getResultList();
            LOGGER.info("Retrieved " + couriers.size() + " couriers.");
            return couriers;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching all couriers.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void addRecord(CourierDTO courier) throws RuntimeException {
        LOGGER.info("Adding courier: " + courier.getName());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(Courier.builder()
                .phone(courier.getPhone())
                .name(courier.getName())
                .transport(courier.getTransport())
                .build());
            transaction.commit();
            LOGGER.info("Courier added successfully: " + courier.getName());
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while adding courier: " + courier.getName(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateRecord(CourierDTO courier) throws RuntimeException {
        LOGGER.info("Updating courier: " + courier.getName());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Courier existingCourier = em.find(Courier.class, courier.getPhone());
            if (existingCourier == null) {
                transaction.rollback();
                LOGGER.warning("Courier with phone " + courier.getPhone() + " not found for update.");
                return false;
            }

            existingCourier.setName(courier.getName());
            existingCourier.setTransport(courier.getTransport());
            em.merge(existingCourier);
            transaction.commit();
            LOGGER.info("Courier updated successfully: " + courier.getName());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while updating courier: " + courier.getName(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteRecord(String phone) throws RuntimeException {
        LOGGER.info("Deleting courier with phone: " + phone);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Courier courier = em.find(Courier.class, phone);
            if (courier == null) {
                transaction.rollback();
                LOGGER.warning("Courier with phone " + phone + " not found for deletion.");
                return false;
            }

            em.remove(courier);
            transaction.commit();
            LOGGER.info("Courier deleted successfully: " + phone);
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while deleting courier with phone: " + phone, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Pair<Courier, Integer>> getCouriersWithMostOrders(int numberOfRecords) throws RuntimeException {
        EntityManager em = emf.createEntityManager();
        List<Pair<Courier, Integer>> couriers = new ArrayList<>();
        
        try {
            em.getTransaction().begin();

            final String jpql = """
                SELECT c, COUNT(o) AS orderCount
                FROM Courier c
                LEFT JOIN Order o ON c.phone = o.courier.phone
                GROUP BY c.name, c.phone
                ORDER BY orderCount DESC
            """;

            // Begin SQL query measurement scope
            SQLQueryRuntime.beginScope();
            
            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            query.setMaxResults(numberOfRecords);
            
            List<Object[]> resultList = query.getResultList();

            // End SQL query measurement scope
            SQLQueryRuntime.endScope();
            
            for (Object[] result : resultList) {
                Courier courier = (Courier) result[0];
                Long orderCount = (Long) result[1];
                couriers.add(Pair.of(courier, orderCount.intValue()));
            }
            
            em.getTransaction().commit();
            
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        
        return couriers;
    }

    public void generateRandomCouriers(int numberOfRecords) throws RuntimeException {
        final String sql = """
            INSERT INTO "Courier" ("Phone", "Name", "Transport")
            SELECT
                LPAD(FLOOR(RANDOM() * 10000000000)::text, 10, '0') AS "Phone",
                (ARRAY['Alice', 'Bob', 'Charlie', 'David', 'Eve', 'Frank', 'Grace'])[FLOOR(RANDOM() * 7) + 1] || ' ' || 
                (ARRAY['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller'])[FLOOR(RANDOM() * 7) + 1] AS "Name",
                (ARRAY['Bicycle', 'Motorbike', 'Van', 'Truck', 'Scooter'])[FLOOR(RANDOM() * 5) + 1] AS "Transport"
            FROM generate_series(1, :numberOfRecords) AS s;
        """;
    
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery(sql)
              .setParameter("numberOfRecords", numberOfRecords)
              .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalStateException("Error generating random couriers", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Courier convertToEntity(CourierDTO dto) throws NoSuchElementException {
        return findRecord(dto.getPhone()).get();
    }

    public void close() {
        LOGGER.info("Closing EntityManagerFactory.");
        if (emf != null) {
            emf.close();
        }
    }
}
