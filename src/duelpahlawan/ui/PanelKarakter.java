package duelpahlawan.ui;

import duelpahlawan.model.Karakter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Panel yang menampilkan satu karakter: nama, sprite beranimasi, dan bar HP.
 * Satu class dipakai untuk pahlawan maupun monster (reuse), karena yang
 * berbeda hanya data Karakter dan AnimasiSprite-nya.
 *
 * Konsep OOP: inheritance (extends JPanel), encapsulation (atribut private),
 * composition (memiliki AnimasiSprite).
 */
public class PanelKarakter extends JPanel {

    private static final int SKALA = 4;            // sprite 96 px ditampilkan 4x lebih besar
    private static final int JEDA_FRAME_MS = 90;   // kecepatan animasi
    private static final int LEBAR = 550;
    private static final int TINGGI_KANVAS = 340;

    private final AnimasiSprite sprite;
    private final boolean hadapKanan;              // true bila karakter berada di kiri layar
    private final JProgressBar barHp = new JProgressBar();
    private final JPanel kanvas;
    private final Timer pewaktu;

    private AksiSprite aksi = AksiSprite.IDLE;
    private int indeksFrame = 0;
    private int geserX = 0;
    private boolean tertahan = false;              // true saat berhenti di frame terakhir (mati)
    private Runnable setelahSelesai;

    // teks angka melayang (damage / heal)
    private String teksAngka;
    private Color warnaAngka = Color.WHITE;
    private int umurAngka;

    public PanelKarakter(Karakter karakter, boolean hadapKanan) {
        this.sprite = AnimasiSprite.untuk(karakter);
        this.hadapKanan = hadapKanan;

        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setPreferredSize(new Dimension(LEBAR, TINGGI_KANVAS + 70));

        JLabel labelNama = new JLabel(karakter.getNama() + "  (" + karakter.getClass().getSimpleName() + ")",
                SwingConstants.CENTER);
        labelNama.setFont(labelNama.getFont().deriveFont(Font.BOLD, 16f));
        labelNama.setForeground(Color.WHITE);

        kanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                gambarSprite((Graphics2D) g, getWidth(), getHeight());
            }
        };
        kanvas.setOpaque(false);

        barHp.setStringPainted(true);
        barHp.setPreferredSize(new Dimension(LEBAR - 80, 26));
        setHp(karakter.getHp(), karakter.getHpMaks());

        // bar HP dibungkus panel transparan supaya ada jarak di kiri dan kanan
        JPanel pembungkusBar = new JPanel(new BorderLayout());
        pembungkusBar.setOpaque(false);
        pembungkusBar.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        pembungkusBar.add(barHp, BorderLayout.CENTER);

        add(labelNama, BorderLayout.NORTH);
        add(kanvas, BorderLayout.CENTER);
        add(pembungkusBar, BorderLayout.SOUTH);

        pewaktu = new Timer(JEDA_FRAME_MS, e -> langkah());
        pewaktu.start();
    }

    // ------------------------------------------------------------ API untuk JendelaUtama

    /** Memperbarui bar HP. */
    public void setHp(int hp, int hpMaks) {
        barHp.setMaximum(hpMaks);
        barHp.setValue(Math.max(hp, 0));
        barHp.setString(Math.max(hp, 0) + " / " + hpMaks);
        double rasio = (double) hp / hpMaks;
        barHp.setForeground(rasio > 0.5 ? new Color(0x3FA34D) : rasio > 0.25 ? new Color(0xE0A100) : new Color(0xC1121F));
    }

    /**
     * Memutar satu aksi. Setelah selesai, karakter kembali ke idle (kecuali MATI
     * yang berhenti di frame terakhir) lalu setelahSelesai dijalankan.
     */
    public void mainkan(AksiSprite aksiBaru, Runnable setelahSelesai) {
        if (!sprite.punya(aksiBaru)) {                // aksi tidak dimiliki karakter ini
            if (setelahSelesai != null) {
                setelahSelesai.run();
            }
            return;
        }
        this.aksi = aksiBaru;
        this.indeksFrame = 0;
        this.tertahan = false;
        this.setelahSelesai = setelahSelesai;
        perbaruiGeser();
        repaint();
    }

    /** Menampilkan angka melayang di atas karakter, misalnya "-15" atau "+25". */
    public void tampilkanAngka(String teks, Color warna) {
        this.teksAngka = teks;
        this.warnaAngka = warna;
        this.umurAngka = 0;
    }

    public void hentikan() {
        pewaktu.stop();
    }

    // ------------------------------------------------------------ animasi

    private void langkah() {
        if (teksAngka != null && ++umurAngka > 14) {
            teksAngka = null;
        }
        if (!tertahan) {
            indeksFrame++;
            int total = sprite.jumlahFrame(aksi);
            if (indeksFrame >= total) {
                selesaiSatuPutaran(total);
            }
            perbaruiGeser();
        }
        kanvas.repaint();
    }

    private void selesaiSatuPutaran(int total) {
        if (aksi.isBerulang()) {
            indeksFrame = 0;                          // idle dan menang diputar terus
            return;
        }
        Runnable lanjut = setelahSelesai;
        setelahSelesai = null;
        if (aksi == AksiSprite.MATI) {
            indeksFrame = total - 1;                  // tetap terbaring di frame terakhir
            tertahan = true;
        } else {
            aksi = AksiSprite.IDLE;
            indeksFrame = 0;
        }
        if (lanjut != null) {
            lanjut.run();
        }
    }

    /** Saat menyerang, karakter maju ke arah lawan lalu kembali (gerakan tambahan). */
    private void perbaruiGeser() {
        if (aksi == AksiSprite.SERANG) {
            double kemajuan = (double) indeksFrame / sprite.jumlahFrame(aksi);
            int arah = hadapKanan ? 1 : -1;
            geserX = (int) (arah * sprite.getJarakMaju() * Math.sin(Math.PI * kemajuan));
        } else {
            geserX = 0;
        }
    }

    // ------------------------------------------------------------ menggambar

    private void gambarSprite(Graphics2D g, int lebarKanvas, int tinggiKanvas) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = sprite.getLebarFrame();
        int h = sprite.getTinggiFrame();
        int x0 = (lebarKanvas - w * SKALA) / 2 + geserX;
        int tanah = tinggiKanvas - 14;                                   // garis tanah
        int y0 = tanah - (sprite.getBarisKaki() + 1) * SKALA;

        // latar: langit bergradasi dan tanah
        g.setPaint(new GradientPaint(0, 0, new Color(0x1B1F3B), 0, tanah, new Color(0x3B3F7C)));
        g.fillRect(0, 0, lebarKanvas, tanah);
        g.setColor(new Color(0x2A2438));
        g.fillRect(0, tanah - 6, lebarKanvas, tinggiKanvas - tanah + 6);

        // bayangan di bawah kaki
        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(lebarKanvas / 2 - 70 + geserX, tanah - 12, 140, 22);

        BufferedImage gambar = sprite.getFrame(aksi, Math.min(indeksFrame, sprite.jumlahFrame(aksi) - 1));
        if (sprite.isMenghadapKanan() == hadapKanan) {
            g.drawImage(gambar, x0, y0, x0 + w * SKALA, y0 + h * SKALA, 0, 0, w, h, null);
        } else {                                                         // balik horizontal
            g.drawImage(gambar, x0 + w * SKALA, y0, x0, y0 + h * SKALA, 0, 0, w, h, null);
        }

        if (teksAngka != null) {
            Font font = getFont().deriveFont(Font.BOLD, 26f);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int x = lebarKanvas / 2 - fm.stringWidth(teksAngka) / 2;
            int y = y0 + 34 * SKALA - umurAngka * 3;
            float alpha = Math.max(0f, 1f - umurAngka / 15f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(Color.BLACK);
            g.drawString(teksAngka, x + 2, y + 2);
            g.setColor(warnaAngka);
            g.drawString(teksAngka, x, y);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }
}
