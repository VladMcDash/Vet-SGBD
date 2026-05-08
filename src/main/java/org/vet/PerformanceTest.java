package org.vet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {

    private static final String URL = "jdbc:postgresql://localhost:5432/sgbd_lab3";
    private static final String USER = "postgres";
    private static final String PASS = "Vlady123";

    public static void main(String[] args) throws SQLException {
        System.out.println("TESTARE PERFORMANTA\n");

        runTaskA_ConnectionOverhead();
        runTaskB_ConnectionLeaks();
    }

    private static void runTaskA_ConnectionOverhead() throws SQLException {
        System.out.println("--- Sarcina A: Overhead-ul Crearii Conexiunilor ---");

        long startNoPool = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            conn.close();
        }
        long endNoPool = System.currentTimeMillis();
        long totalNoPool = endNoPool - startNoPool;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(3000);

        HikariDataSource dataSource = new HikariDataSource(config);

        long startPool = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        long endPool = System.currentTimeMillis();
        long totalPool = endPool - startPool;

        System.out.println("Timp total (100 conexiuni FARA pooling): " + totalNoPool + " ms");
        System.out.println("Timp total (100 conexiuni CU pooling):   " + totalPool + " ms");

        dataSource.close();
    }

    private static void runTaskB_ConnectionLeaks() {
        System.out.println("--- Sarcina B: Detectarea Scurgerilor de Conexiuni ---");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(2000);

        HikariDataSource dataSource = new HikariDataSource(config);
        List<Connection> leakedConnections = new ArrayList<>();

        try {
            for (int i = 1; i <= 12; i++) {
                System.out.print("Cerere conexiune " + i + "... ");
                Connection conn = dataSource.getConnection();
                leakedConnections.add(conn);
                System.out.println("OK.");
            }
        } catch (SQLException e) {
            System.out.println("\nEROARE DETECTATA: " + e.getMessage());
            System.out.println("Pool-ul a devenit epuizat deoarece conexiunile nu au fost inchise.");
        } finally {
            System.out.println("Remediere: Inchidem conexiunile uitate pentru a elibera resursele.");
            for (Connection c : leakedConnections) {
                try { c.close(); } catch (SQLException e) { /* ignored */ }
            }
            System.out.println("Resurse eliberate cu succes.");
            dataSource.close();
        }
    }
}