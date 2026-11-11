package duelpahlawan.ui;

import duelpahlawan.model.Karakter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Kartu pilihan pahlawan: nama, sprite idle beranimasi, statistik, deskripsi,
 * dan tombol Pilih. Statistik diambil dari object Karakter contoh, jadi angkanya
 * selalu sama dengan yang ada di model.
 *
 * Konsep OOP: inheritance (extends JPanel), encapsulation (atribut private),
 * composition (memiliki AnimasiSprite). Rancangan kartu berasal dari ide Fardizza.
 */
public class KartuKarakter extends JPanel {

    private static final int SKALA = 5;
    private static final int JEDA_FRAME_MS = 90;

    private final AnimasiSprite sprite;
    private int indeksFrame = 0;

    public KartuKarakter(Karakter contoh, String deskripsi, Color warnaLatar,
                         Color warnaTombol, Runnable saatDipilih) {
        this.sprite = AnimasiSprite.untuk(contoh);
        String jenis = contoh.getClass().getSimpleName();

        setLayout(new BorderLayout(0, 8));
        setBackground(warnaLatar);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warnaLatar.brighter(), 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel labelJenis = new JLabel(jenis, SwingConstants.CENTER);
        labelJenis.setFont(labelJenis.getFont().deriveFont(Font.BOLD, 22f));
        labelJenis.setForeground(Color.WHITE);

        JPanel pratinjau = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                gambarPratinjau((Graphics2D) g, getWidth(), getHeight());
            }
        };
        pratinjau.setOpaque(false);

        JLabel labelStatistik = new JLabel("HP: " + contoh.getHpMaks() + "   |   Kekuatan: " + contoh.getKekuatan(),
                SwingConstants.CENTER);
        labelStatistik.setFont(labelStatistik.getFont().deriveFont(Font.BOLD, 15f));
        labelStatistik.setForeground(Color.WHITE);

        JLabel labelDeskripsi = new JLabel("<html><div style='text-align:center'>" + deskripsi + "</div></html>",
                SwingConstants.CENTER);
        labelDeskripsi.setFont(labelDeskripsi.getFont().deriveFont(Font.PLAIN, 13f));
        labelDeskripsi.setForeground(new Color(0xD8DAF0));

        JButton tombolPilih = new JButton("Pilih " + jenis);
        tombolPilih.setBackground(warnaTombol);
        tombolPilih.setForeground(Color.WHITE);
        tombolPilih.setFont(tombolPilih.getFont().deriveFont(Font.BOLD, 15f));
        tombolPilih.setFocusPainted(false);
        tombolPilih.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tombolPilih.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombolPilih.addActionListener(e -> saatDipilih.run());

        JPanel info = new JPanel(new GridLayout(0, 1, 0, 6));
        info.setOpaque(false);
        info.add(labelStatistik);
        info.add(labelDeskripsi);

        JPanel bawah = new JPanel(new BorderLayout(0, 10));
        bawah.setOpaque(false);
        bawah.add(info, BorderLayout.CENTER);
        bawah.add(tombolPilih, BorderLayout.SOUTH);

        add(labelJenis, BorderLayout.NORTH);
        add(pratinjau, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(420, 470));

        // animasi idle berulang
        new Timer(JEDA_FRAME_MS, e -> {
            indeksFrame = (indeksFrame + 1) % sprite.jumlahFrame(AksiSprite.IDLE);
            pratinjau.repaint();
        }).start();
    }

    /** Menggambar frame idle saat ini, menghadap ke kanan, dengan kaki di dasar area. */
    private void gambarPratinjau(Graphics2D g, int lebar, int tinggi) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = sprite.getLebarFrame();
        int h = sprite.getTinggiFrame();
        int x0 = (lebar - w * SKALA) / 2;
        int y0 = tinggi - 10 - (sprite.getBarisKaki() + 1) * SKALA;

        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(lebar / 2 - 80, tinggi - 22, 160, 24);

        BufferedImage gambar = sprite.getFrame(AksiSprite.IDLE, indeksFrame);
        if (sprite.isMenghadapKanan()) {
            g.drawImage(gambar, x0, y0, x0 + w * SKALA, y0 + h * SKALA, 0, 0, w, h, null);
        } else {                                                     // balik horizontal
            g.drawImage(gambar, x0 + w * SKALA, y0, x0, y0 + h * SKALA, 0, 0, w, h, null);
        }
    }
}
