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
        } finally { em.close(); }
    }

    public Proprietar getProprietarById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Proprietar.class, id); // Va folosi Cache-ul L2
        } finally { em.close(); }
    }

    public void addAnimal(String nume, String specie, int varsta, int proprietarId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Proprietar p = em.find(Proprietar.class, proprietarId);
            if (p != null) {
                Animal a = new Animal(nume, specie, varsta);
                a.setProprietar(p);
                em.persist(a);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally { em.close(); }
    }

    //PAGINARE (OFFSET)
    public List<Animal> getAnimalePageOffset(int proprietarId, int pageNumber, int pageSize) {
        EntityManager em = emf.createEntityManager();
        try {
            int offset = pageNumber * pageSize;
            return em.createQuery("SELECT a FROM Animal a WHERE a.proprietar.id = :pId ORDER BY a.id", Animal.class)
                    .setParameter("pId", proprietarId)
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally { em.close(); }
    }

    public long countAnimale(int proprietarId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Animal a WHERE a.proprietar.id = :pId", Long.class)
                    .setParameter("pId", proprietarId)
                    .getSingleResult();
        } finally { em.close(); }
    }

    //OPERATII BULK
    public int incrementVarstaToateAnimalele(int proprietarId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            int rowsUpdated = em.createQuery("UPDATE Animal a SET a.varsta = a.varsta + 1 WHERE a.proprietar.id = :pId")
                    .setParameter("pId", proprietarId)
                    .executeUpdate();
            em.getTransaction().commit();
            return rowsUpdated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally { em.close(); }
    }

    //REZOLVARE N+1
    public List<Proprietar> getProprietariCuAnimaleNPlus1() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Proprietar> proprietari = em.createQuery("SELECT p FROM Proprietar p", Proprietar.class).getResultList();

            for(Proprietar p : proprietari) {
                p.getAnimale().size();
            }
            return proprietari;
        } finally {
            em.close();
        }
    }

    public List<Proprietar> getProprietariCuAnimaleEager() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT p FROM Proprietar p LEFT JOIN FETCH p.animale", Proprietar.class).getResultList();
        } finally { em.close(); }
    }

    public EntityManagerFactory getEmf() { return emf; }
}