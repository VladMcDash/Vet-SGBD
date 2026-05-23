package org.vet.gui;

import org.vet.dao.DatabaseManager;
import org.vet.model.Animal;
import org.vet.model.Proprietar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private DatabaseManager dbManager;
    private JTable tableProprietari, tableAnimale;
    private DefaultTableModel modelProprietari, modelAnimale;
    private JTextField txtNumeAnimal, txtSpecieAnimal, txtVarstaAnimal;

    // Variabile Paginare
    private int selectedProprietarId = -1;
    private int currentPage = 0;
    private int pageSize = 10;
    private long totalRecords = 0;
    private JLabel lblPageInfo;
    private JComboBox<Integer> comboPageSize;

    public MainFrame() {
        dbManager = new DatabaseManager();
        setTitle("Clinica Veterinara (Paginare & Bulk Updates)");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        loadProprietari();
    }

    private void initComponents() {
        modelProprietari = new DefaultTableModel(new String[]{"ID", "Proprietar", "Telefon"}, 0);
        tableProprietari = new JTable(modelProprietari);
        tableProprietari.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProprietari.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableProprietari.getSelectedRow() != -1) {
                selectedProprietarId = (int) modelProprietari.getValueAt(tableProprietari.getSelectedRow(), 0);
                currentPage = 0; // Resetam pagina cand schimbam stapanul
                loadAnimalePaginat();
            }
        });

        modelAnimale = new DefaultTableModel(new String[]{"ID", "Nume", "Specie", "Varsta"}, 0);
        tableAnimale = new JTable(modelAnimale);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tableProprietari), createAnimalPanelWithPagination());
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        // FORMULAR ADAUGARE
        JPanel formPanel = new JPanel(new FlowLayout());
        txtNumeAnimal = new JTextField(10);
        txtSpecieAnimal = new JTextField(10);
        txtVarstaAnimal = new JTextField(5);
        formPanel.add(new JLabel("Nume:")); formPanel.add(txtNumeAnimal);
        formPanel.add(new JLabel("Specie:")); formPanel.add(txtSpecieAnimal);
        formPanel.add(new JLabel("Varsta:")); formPanel.add(txtVarstaAnimal);

        JButton btnAdd = new JButton("Adauga Animal");
        btnAdd.addActionListener(e -> {
            dbManager.addAnimal(txtNumeAnimal.getText(), txtSpecieAnimal.getText(), Integer.parseInt(txtVarstaAnimal.getText()), selectedProprietarId);
            loadAnimalePaginat();
        });

        // BUTON OPERATIE IN MASA (LAB 4)
        JButton btnBulk = new JButton("+1 An (Toate)");
        btnBulk.setBackground(Color.ORANGE);
        btnBulk.addActionListener(e -> {
            if(selectedProprietarId != -1) {
                int updated = dbManager.incrementVarstaToateAnimalele(selectedProprietarId);
                JOptionPane.showMessageDialog(this, "Actualizare in masa reusita!\nAnimale modificate: " + updated);
                loadAnimalePaginat();
            }
        });

        formPanel.add(btnAdd);
        formPanel.add(btnBulk);
        add(formPanel, BorderLayout.SOUTH);
    }

    private JPanel createAnimalPanelWithPagination() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tableAnimale), BorderLayout.CENTER);

        // CONTROALE PAGINARE (Lab 4)
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrev = new JButton("< Inapoi");
        JButton btnNext = new JButton("Inainte >");
        lblPageInfo = new JLabel("Pagina 1 (0 rezultate)");
        comboPageSize = new JComboBox<>(new Integer[]{10, 25, 50, 100});

        btnPrev.addActionListener(e -> { if(currentPage > 0) { currentPage--; loadAnimalePaginat(); } });
        btnNext.addActionListener(e -> { if((currentPage + 1) * pageSize < totalRecords) { currentPage++; loadAnimalePaginat(); } });
        comboPageSize.addActionListener(e -> {
            pageSize = (int) comboPageSize.getSelectedItem();
            currentPage = 0;
            loadAnimalePaginat();
        });

        paginationPanel.add(new JLabel("Per pagina:"));
        paginationPanel.add(comboPageSize);
        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);

        panel.add(paginationPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadProprietari() {
        List<Proprietar> list = dbManager.getAllProprietari();
        for (Proprietar p : list) modelProprietari.addRow(new Object[]{p.getId(), p.getNume(), p.getTelefon()});
    }

    private void loadAnimalePaginat() {
        if (selectedProprietarId == -1) return;
        modelAnimale.setRowCount(0);

        totalRecords = dbManager.countAnimale(selectedProprietarId);
        List<Animal> list = dbManager.getAnimalePageOffset(selectedProprietarId, currentPage, pageSize);

        for (Animal a : list) {
            modelAnimale.addRow(new Object[]{a.getId(), a.getNume(), a.getSpecie(), a.getVarsta()});
        }

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        lblPageInfo.setText("Pagina " + (currentPage + 1) + " din " + Math.max(1, totalPages));
    }
}