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
    private JTable tableProprietari;
    private DefaultTableModel modelProprietari;
    private JTable tableAnimale;
    private DefaultTableModel modelAnimale;
    private JTextField txtNumeAnimal, txtSpecieAnimal, txtVarstaAnimal;
    private int selectedProprietarId = -1;
    private int selectedAnimalId = -1;

    public MainFrame() {
        dbManager = new DatabaseManager();
        setTitle("Clinica Veterinara");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        loadProprietari();
    }

    private void initComponents() {
        modelProprietari = new DefaultTableModel(new String[]{"ID", "Nume Proprietar", "Telefon"}, 0);
        tableProprietari = new JTable(modelProprietari);
        tableProprietari.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableProprietari.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableProprietari.getSelectedRow() != -1) {
                int row = tableProprietari.getSelectedRow();
                selectedProprietarId = (int) modelProprietari.getValueAt(row, 0);
                loadAnimale(selectedProprietarId);
                clearForm();
            }
        });

        modelAnimale = new DefaultTableModel(new String[]{"ID", "Nume", "Specie", "Varsta"}, 0);
        tableAnimale = new JTable(modelAnimale);
        tableAnimale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableAnimale.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableAnimale.getSelectedRow() != -1) {
                int row = tableAnimale.getSelectedRow();
                selectedAnimalId = (int) modelAnimale.getValueAt(row, 0);
                txtNumeAnimal.setText((String) modelAnimale.getValueAt(row, 1));
                txtSpecieAnimal.setText((String) modelAnimale.getValueAt(row, 2));
                txtVarstaAnimal.setText(modelAnimale.getValueAt(row, 3).toString());
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tableProprietari), new JScrollPane(tableAnimale));
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new FlowLayout());
        txtNumeAnimal = new JTextField(10);
        txtSpecieAnimal = new JTextField(10);
        txtVarstaAnimal = new JTextField(5);

        formPanel.add(new JLabel("Nume:")); formPanel.add(txtNumeAnimal);
        formPanel.add(new JLabel("Specie:")); formPanel.add(txtSpecieAnimal);
        formPanel.add(new JLabel("Varsta:")); formPanel.add(txtVarstaAnimal);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Adauga Animal");
        JButton btnUpdate = new JButton("Actualizeaza");
        JButton btnDelete = new JButton("Sterge Animal");
        JButton btnRefresh = new JButton("Refresh");
        btnAdd.addActionListener(e -> addAnimal());
        btnUpdate.addActionListener(e -> updateAnimal());
        btnDelete.addActionListener(e -> deleteAnimal());
        btnRefresh.addActionListener(e -> {
            loadProprietari();
            modelAnimale.setRowCount(0);
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadProprietari() {
        modelProprietari.setRowCount(0);
        try {
            List<Proprietar> list = dbManager.getAllProprietari();
            for (Proprietar p : list) {
                modelProprietari.addRow(new Object[]{p.getId(), p.getNume(), p.getTelefon()});
            }
        } catch (Exception ex) {
            showError("Eroare la incarcarea proprietarilor: " + ex.getMessage());
        }
    }

    private void loadAnimale(int proprietarId) {
        modelAnimale.setRowCount(0);
        try {
            List<Animal> list = dbManager.getAnimaleByProprietar(proprietarId);
            for (Animal a : list) {
                modelAnimale.addRow(new Object[]{a.getId(), a.getNume(), a.getSpecie(), a.getVarsta()});
            }
        } catch (Exception ex) {
            showError("Eroare la incarcarea animalelor: " + ex.getMessage());
        }
    }
    private void addAnimal() {
        if (selectedProprietarId == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un proprietar din lista din stanga!");
            return;
        }
        if (!validateForm()) return;

        try {
            dbManager.addAnimal(txtNumeAnimal.getText(),
                    txtSpecieAnimal.getText(),
                    Integer.parseInt(txtVarstaAnimal.getText()),
                    selectedProprietarId);
            loadAnimale(selectedProprietarId);
            clearForm();
        } catch (Exception ex) {
            showError("Eroare la adaugare: " + ex.getMessage());
        }
    }

    private void updateAnimal() {
        if (selectedAnimalId == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un animal din tabel pentru a-l edita!");
            return;
        }
        if (!validateForm()) return;

        try {
            dbManager.updateAnimal(selectedAnimalId,
                    txtNumeAnimal.getText(),
                    txtSpecieAnimal.getText(),
                    Integer.parseInt(txtVarstaAnimal.getText()));
            loadAnimale(selectedProprietarId);
            JOptionPane.showMessageDialog(this, "Datele animalului au fost actualizate.");
        } catch (Exception ex) {
            showError("Eroare la actualizare: " + ex.getMessage());
        }
    }

    private void deleteAnimal() {
        if (selectedAnimalId == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un animal pentru stergere!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Sigur doriti sa stergeti acest animal?", "Confirmare Stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteAnimal(selectedAnimalId);
                loadAnimale(selectedProprietarId);
                clearForm();
            } catch (Exception ex) {
                showError("Eroare la stergere: " + ex.getMessage());
            }
        }
    }

    private boolean validateForm() {
        if (txtNumeAnimal.getText().trim().isEmpty() ||
                txtSpecieAnimal.getText().trim().isEmpty() ||
                txtVarstaAnimal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Toate campurile sunt obligatorii!");
            return false;
        }
        try {
            int varsta = Integer.parseInt(txtVarstaAnimal.getText());
            if (varsta < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Varsta trebuie sa fie un numar intreg pozitiv!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtNumeAnimal.setText("");
        txtSpecieAnimal.setText("");
        txtVarstaAnimal.setText("");
        selectedAnimalId = -1;
        tableAnimale.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Eroare Sistem", JOptionPane.ERROR_MESSAGE);
    }
}