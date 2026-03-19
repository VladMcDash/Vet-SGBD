package org.vet.gui;

import org.vet.dao.DatabaseManager;
import org.vet.model.Animal;
import org.vet.model.Proprietar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private DatabaseManager dbManager;
    private JTable tableProprietari;
    private JTable tableAnimale;
    private DefaultTableModel modelProprietari;
    private DefaultTableModel modelAnimale;

    private JTextField txtNumeAnimal, txtSpecieAnimal, txtVarstaAnimal;
    private int selectedProprietarId = -1;
    private int selectedAnimalId = -1;

    public MainFrame() {
        dbManager = new DatabaseManager();
        setTitle("Clinica Veterinara");
        setSize(800, 600);
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
                Object val = modelProprietari.getValueAt(tableProprietari.getSelectedRow(), 0);
                selectedProprietarId = Integer.parseInt(val.toString());

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

                Object valId = modelAnimale.getValueAt(row, 0);
                selectedAnimalId = Integer.parseInt(valId.toString());

                txtNumeAnimal.setText((String) modelAnimale.getValueAt(row, 1));
                txtSpecieAnimal.setText((String) modelAnimale.getValueAt(row, 2));
                txtVarstaAnimal.setText(modelAnimale.getValueAt(row, 3).toString());
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tableProprietari), new JScrollPane(tableAnimale));
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        txtNumeAnimal = new JTextField(10);
        txtSpecieAnimal = new JTextField(10);
        txtVarstaAnimal = new JTextField(5);

        formPanel.add(new JLabel("Nume:")); formPanel.add(txtNumeAnimal);
        formPanel.add(new JLabel("Specie:")); formPanel.add(txtSpecieAnimal);
        formPanel.add(new JLabel("Varsta:")); formPanel.add(txtVarstaAnimal);

        JButton btnAdd = new JButton("Adauga");
        JButton btnUpdate = new JButton("Actualizeaza");
        JButton btnDelete = new JButton("Sterge");

        btnAdd.addActionListener(e -> addAnimal());
        btnUpdate.addActionListener(e -> updateAnimal());
        btnDelete.addActionListener(e -> deleteAnimal());

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadProprietari() {
        modelProprietari.setRowCount(0);
        try {
            List<Proprietar> list = dbManager.getAllProprietari();
            for (Proprietar p : list) {
                modelProprietari.addRow(new Object[]{p.getId(), p.getNume(), p.getTelefon()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la inczrcarea proprietarilor: " + ex.getMessage(), "Eroare BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAnimale(int proprietarId) {
        modelAnimale.setRowCount(0);
        try {
            List<Animal> list = dbManager.getAnimaleByProprietar(proprietarId);
            for (Animal a : list) {
                modelAnimale.addRow(new Object[]{a.getId(), a.getNume(), a.getSpecie(), a.getVarsta()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la incarcarea animalelor: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAnimal() {
        if (selectedProprietarId == -1) {
            JOptionPane.showMessageDialog(this, "Selectează un proprietar");
            return;
        }
        if (!validateForm()) return;

        try {
            dbManager.addAnimal(txtNumeAnimal.getText(), txtSpecieAnimal.getText(), Integer.parseInt(txtVarstaAnimal.getText()), selectedProprietarId);
            loadAnimale(selectedProprietarId);
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la adaugare: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAnimal() {
        if (selectedAnimalId == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un animal pentru edit");
            return;
        }
        if (!validateForm()) return;

        try {
            dbManager.updateAnimal(selectedAnimalId, txtNumeAnimal.getText(), txtSpecieAnimal.getText(), Integer.parseInt(txtVarstaAnimal.getText()));
            loadAnimale(selectedProprietarId);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la actualizare: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAnimal() {
        if (selectedAnimalId == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un animal pentru stergere");
            return;
        }
        // Dialog de confirmare
        int confirm = JOptionPane.showConfirmDialog(this, "Sigur dorești sa stergi acest animal?", "Confirmare", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteAnimal(selectedAnimalId);
                loadAnimale(selectedProprietarId);
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la ștergere: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtNumeAnimal.getText().trim().isEmpty() || txtSpecieAnimal.getText().trim().isEmpty() || txtVarstaAnimal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Toate campurile sunt obligatorii!");
            return false;
        }
        try {
            Integer.parseInt(txtVarstaAnimal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Varsta trebuie sa fie un numar valid!");
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
}