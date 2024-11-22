package com.lab2.service;

import com.lab2.entity.Client;
import com.lab2.util.SQLQueryRuntime;
import com.lab2.dto.ClientDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ClientService implements Service<Client, ClientDTO, String> {
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private EntityManagerFactory emf;

    public ClientService(EntityManagerFactory emf) {
        this.emf = emf;
        LOGGER.info("ClientService initialized.");
        LOGGER.setLevel(Level.OFF);
    }

    @Override
    public Optional<Client> findRecord(String email) throws RuntimeException {
        LOGGER.info("Finding client with email: " + email);
        EntityManager em = emf.createEntityManager();
        try {
            Client client = em.find(Client.class, email);
            if (client == null) {
                LOGGER.warning("Client with email " + email + " not found.");
            }
            return Optional.ofNullable(client);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while finding client with email: " + email, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Client> getAllRecords() throws RuntimeException {
        LOGGER.info("Fetching all clients.");
        EntityManager em = emf.createEntityManager();
        try {
            List<Client> clients = em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
            LOGGER.info("Retrieved " + clients.size() + " clients.");
            return clients;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching all clients.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void addRecord(ClientDTO client) throws RuntimeException {
        LOGGER.info("Adding client: " + client.getEmail());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(Client.builder()
                .email(client.getEmail())
                .name(client.getName())
                .phone(client.getPhone())
                .build());
            transaction.commit();
            LOGGER.info("Client added successfully: " + client.getEmail());
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while adding client: " + client.getEmail(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateRecord(ClientDTO client) throws RuntimeException {
        LOGGER.info("Updating client: " + client.getEmail());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Client existingClient = em.find(Client.class, client.getEmail());
            if (existingClient == null) {
                transaction.rollback();
                LOGGER.warning("Client with email " + client.getEmail() + " not found for update.");
                return false;
            }

            existingClient.setName(client.getName());
            existingClient.setPhone(client.getPhone());
            em.merge(existingClient);
            transaction.commit();
            LOGGER.info("Client updated successfully: " + client.getEmail());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while updating client: " + client.getEmail(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteRecord(String email) throws RuntimeException {
        LOGGER.info("Deleting client with email: " + email);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Client client = em.find(Client.class, email);
            if (client == null) {
                transaction.rollback();
                LOGGER.warning("Client with email " + email + " not found for deletion.");
                return false;
            }

            em.remove(client);
            transaction.commit();
            LOGGER.info("Client deleted successfully: " + email);
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while deleting client with email: " + email, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Pair<Client, Integer>> getClientWithMostOrders() throws RuntimeException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            final String jpql = """
                SELECT c, COUNT(o) AS orderCount
                FROM Client c
                JOIN Order o ON c.email = o.client.email
                GROUP BY c.email
                ORDER BY orderCount DESC
            """;
            
            // Begin SQL query measurement scope
            SQLQueryRuntime.beginScope();

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            query.setMaxResults(1);
            
            Object[] result = query.getSingleResult();
            if (result == null) {
                return Optional.empty();
            }

            // End SQL query measurement scope
            SQLQueryRuntime.endScope();

            Client client = (Client) result[0];
            Long orderCount = (Long) result[1];
            
            em.getTransaction().commit();

            return Optional.of(Pair.of(client, Integer.valueOf(orderCount.intValue())));
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void generateRandomClients(int numberOfRecords) throws RuntimeException {
        final String sql = """
            INSERT INTO "Client" ("Email", "Name", "Phone")
            SELECT
                LOWER(first_name || '.' || last_name || FLOOR(RANDOM() * 10000000)::text || '@' || domain) AS "Email",
                first_name || ' ' || last_name AS "Name",
                LPAD(FLOOR(RANDOM() * 10000000000)::text, 10, '0') AS "Phone"
            FROM (
                SELECT 
                    (ARRAY['Ava', 'Ben', 'Cal', 'Dan', 'Eli', 'Fin', 'Gus', 'Hal', 'Ivy', 'Jax', 'Kai', 'Leo', 'Mia', 'Nia', 'Oli', 'Pax', 'Ray', 'Sky', 'Tia', 'Zoe'])[FLOOR(RANDOM() * 20) + 1] AS first_name,
                    (ARRAY['Doe', 'Lee', 'Kim', 'Zhu', 'Wang', 'Liu', 'Gar', 'Ali', 'Bai', 'Hsu', 'Roy', 'Joy', 'Lin', 'Tan', 'Yin'])[FLOOR(RANDOM() * 15) + 1] AS last_name,
                    (ARRAY['ex.com', 'tm.com', 'sm.com', 'dm.com', 'rnd.com', 'ml.com', 'd.com', 'svc.com', 'w.com', 'u.com'])[FLOOR(RANDOM() * 10) + 1] AS domain
                FROM generate_series(1, :numberOfRecords) AS s
            ) AS names
        """;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery(sql)
              .setParameter("numberOfRecords", numberOfRecords)
              .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw new IllegalStateException("Error generating random clients", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Client convertToEntity(ClientDTO dto) throws NoSuchElementException {
        return findRecord(dto.getEmail()).get();
    }

    public void close() {
        LOGGER.info("Closing EntityManagerFactory.");
        if (emf != null) {
            emf.close();
        }
    }
}
