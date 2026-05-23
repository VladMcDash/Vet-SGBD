package org.vet.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proprietari")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Proprietar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nume;
    private String telefon;

    @OneToMany(mappedBy = "proprietar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Animal> animale = new ArrayList<>();

    public Proprietar() {}

    public Proprietar(String nume, String telefon) {
        this.nume = nume;
        this.telefon = telefon;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getTelefon() { return telefon; }
    public List<Animal> getAnimale() { return animale; }

    @Override
    public String toString() { return nume; }
}