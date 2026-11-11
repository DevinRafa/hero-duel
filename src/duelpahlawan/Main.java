package duelpahlawan;

import duelpahlawan.ui.JendelaUtama;
import javax.swing.SwingUtilities;

/**
 * Titik masuk aplikasi Duel Pahlawan.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JendelaUtama jendela = new JendelaUtama();
            jendela.setVisible(true);
        });
    }
}
