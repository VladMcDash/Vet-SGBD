package org.vet.dao;

import jakarta.persistence.*;
import org.vet.model.Animal;
import org.vet.model.Proprietar;

import java.util.List;

public class DatabaseManager {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("VetPU");

    public List<Proprietar> getAllProprietari() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Proprietar p", Proprietar.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Animal> getAnimaleByProprietar(int proprietarId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Animal a WHERE a.proprietar.id = :pId", Animal.class)
                    .setParameter("pId", proprietarId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void addAnimal(String nume, String specie, int varsta, int proprietarId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Proprietar proprietar = em.find(Proprietar.class, proprietarId);
            if (proprietar != null) {
                Animal animalNou = new Animal(nume, specie, varsta);
                animalNou.setProprietar(proprietar);
                em.persist(animalNou);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void updateAnimal(int id, String nume, String specie, int varsta) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Animal animal = em.find(Animal.class, id);
            if (animal != null) {
                animal.setNume(nume);
                animal.setSpecie(specie);
                animal.setVarsta(varsta);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void deleteAnimal(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Animal animal = em.find(Animal.class, id);
            if (animal != null) {
                em.remove(animal);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}