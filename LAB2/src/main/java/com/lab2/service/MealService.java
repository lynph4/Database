package com.lab2.service;

import com.lab2.dto.MealDTO;
import com.lab2.entity.Meal;
import com.lab2.entity.Order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MealService implements Service<Meal, MealDTO, Long> {
    private static final Logger LOGGER = Logger.getLogger(MealService.class.getName());
    private EntityManagerFactory emf;

    public MealService(EntityManagerFactory emf) {
        this.emf = emf;
        LOGGER.info("MealService initialized.");
        LOGGER.setLevel(Level.OFF);
    }

    @Override
    public Optional<Meal> findRecord(Long mealID) throws RuntimeException {
        LOGGER.info("Finding meal with ID: " + mealID);
        EntityManager em = emf.createEntityManager();
        try {
            Meal meal = em.find(Meal.class, mealID);
            if (meal == null) {
                LOGGER.warning("Meal with ID " + mealID + " not found.");
            }
            return Optional.ofNullable(meal);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while finding meal with ID: " + mealID, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Meal> getAllRecords() throws RuntimeException {
        LOGGER.info("Fetching all meals.");
        EntityManager em = emf.createEntityManager();
        try {
            List<Meal> meals = em.createQuery("SELECT m FROM Meal m", Meal.class).getResultList();
            LOGGER.info("Retrieved " + meals.size() + " meals.");
            return meals;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching all meals.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void addRecord(MealDTO mealDTO) throws RuntimeException {
        LOGGER.info("Adding meal: " + mealDTO.getName());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // It is assumed that the order exists and has been validated prior to this call
            Order existingOrder = em.find(Order.class, mealDTO.getOrderID());
            Meal newMeal = Meal.builder()
                .mealID(mealDTO.getMealID()) // Manually set identifier
                .order(existingOrder)
                .name(mealDTO.getName())
                .price(mealDTO.getPrice())
                .weight(mealDTO.getWeight())
                .servingSize(mealDTO.getServingSize())
                .build();
    
            em.persist(newMeal);
            transaction.commit();
            LOGGER.info("Meal added successfully: " + mealDTO.getName());
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while adding meal: " + mealDTO.getName(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateRecord(MealDTO meal) throws RuntimeException {
        LOGGER.info("Updating meal: " + meal.getName());
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Meal existingMeal = em.find(Meal.class, meal.getMealID());
            if (existingMeal == null) {
                transaction.rollback();
                LOGGER.warning("Meal with ID " + meal.getMealID() + " not found for update.");
                return false;
            }

            existingMeal.setName(meal.getName());
            existingMeal.setPrice(meal.getPrice());
            existingMeal.setWeight(meal.getWeight());
            existingMeal.setServingSize(meal.getServingSize());
            em.merge(existingMeal);
            transaction.commit();
            LOGGER.info("Meal updated successfully: " + meal.getName());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while updating meal: " + meal.getName(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteRecord(Long mealID) throws RuntimeException {
        LOGGER.info("Deleting meal with ID: " + mealID);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Meal meal = em.find(Meal.class, mealID);
            if (meal == null) {
                transaction.rollback();
                LOGGER.warning("Meal with ID " + mealID + " not found for deletion.");
                return false;
            }

            em.remove(meal);
            transaction.commit();
            LOGGER.info("Meal deleted successfully: " + meal.getName());
            return true;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error while deleting meal with ID: " + mealID, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Meal convertToEntity(MealDTO dto) throws NoSuchElementException {
        return findRecord(dto.getMealID()).get();
    }

    public void close() {
        LOGGER.info("Closing EntityManagerFactory.");
        if (emf != null) {
            emf.close();
        }
    }
}
