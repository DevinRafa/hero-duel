package duelpahlawan.ui;

import javax.swing.SwingUtilities;

/** Titik masuk aplikasi: menampilkan JendelaUtama pada thread Swing. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JendelaUtama().setVisible(true));
    }
}
