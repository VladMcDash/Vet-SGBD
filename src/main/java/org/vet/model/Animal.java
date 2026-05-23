package org.vet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "animale", indexes = {
        @Index(name = "idx_animale_proprietar", columnList = "proprietar_id"),
        @Index(name = "idx_animale_specie_varsta", columnList = "specie, varsta")
})
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nume;
    private String specie;
    private int varsta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietar_id")
    private Proprietar proprietar;

    public Animal() {}

    public Animal(String nume, String specie, int varsta) {
        this.nume = nume;
        this.specie = specie;
        this.varsta = varsta;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getSpecie() { return specie; }
    public int getVarsta() { return varsta; }
    public Proprietar getProprietar() { return proprietar; }

    public void setProprietar(Proprietar proprietar) { this.proprietar = proprietar; }
    public void setNume(String nume) { this.nume = nume; }
    public void setSpecie(String specie) { this.specie = specie; }
    public void setVarsta(int varsta) { this.varsta = varsta; }
}