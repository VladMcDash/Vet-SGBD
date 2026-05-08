package org.vet;

import org.vet.gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                System.out.println("Aplicatia Clinica Veterinara (ORM) a pornit cu succes.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}