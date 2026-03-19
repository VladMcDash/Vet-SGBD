package org.vet.model;

public class Animal {
    private int id;
    private String nume;
    private String specie;
    private int varsta;
    private int proprietarId;

    public Animal(int id, String nume, String specie, int varsta, int proprietarId) {
        this.id = id;
        this.nume = nume;
        this.specie = specie;
        this.varsta = varsta;
        this.proprietarId = proprietarId;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getSpecie() { return specie; }
    public int getVarsta() { return varsta; }
    public int getProprietarId() { return proprietarId; }
}