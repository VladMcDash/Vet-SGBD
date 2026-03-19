package org.vet.dao;

import org.vet.model.Animal;
import org.vet.model.Proprietar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:vet.bd";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public List<Proprietar> getAllProprietari() throws SQLException {
        List<Proprietar> lista = new ArrayList<>();
        String sql = "SELECT id, nume, telefon FROM Proprietari";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Proprietar(rs.getInt("id"), rs.getString("nume"), rs.getString("telefon")));
            }
        }
        return lista;
    }

    public List<Animal> getAnimaleByProprietar(int proprietarId) throws SQLException {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT id, nume, specie, varsta FROM Animale WHERE proprietar_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proprietarId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Animal(rs.getInt("id"), rs.getString("nume"), rs.getString("specie"), rs.getInt("varsta"), proprietarId));
            }
        }
        return lista;
    }

    public void addAnimal(String nume, String specie, int varsta, int proprietarId) throws SQLException {
        String sql = "INSERT INTO Animale (nume, specie, varsta, proprietar_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nume);
            pstmt.setString(2, specie);
            pstmt.setInt(3, varsta);
            pstmt.setInt(4, proprietarId);
            pstmt.executeUpdate();
        }
    }

    public void updateAnimal(int id, String nume, String specie, int varsta) throws SQLException {
        String sql = "UPDATE Animale SET nume = ?, specie = ?, varsta = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nume);
            pstmt.setString(2, specie);
            pstmt.setInt(3, varsta);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteAnimal(int id) throws SQLException {
        String sql = "DELETE FROM Animale WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}