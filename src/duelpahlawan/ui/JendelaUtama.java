package duelpahlawan.ui;

import duelpahlawan.model.Karakter;
import duelpahlawan.model.KarakterMatiException;
import duelpahlawan.model.Ksatria;
import duelpahlawan.model.Monster;
import duelpahlawan.model.Penyihir;
import duelpahlawan.model.Pertarungan;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jendela utama aplikasi Duel Pahlawan.
 * Class ini hanya mengurus tampilan dan menerima klik; seluruh aturan game
 * ada di package model (class Pertarungan). GUI memanggil Pertarungan lalu
 * memutar animasi sesuai hasilnya.
 *
 * Konsep OOP: inheritance (extends JFrame), polymorphism (reference Karakter
 * menunjuk Ksatria atau Penyihir), exception handling (try-catch pada klik tombol),
 * dan interface (tombol Sembuhkan hanya untuk pahlawan BisaMenyembuhkan).
 */
public class JendelaUtama extends JFrame {

    private static final String KARTU_PILIH = "pilih";
    private static final String KARTU_DUEL = "duel";
    private static final Pattern POLA_ANGKA = Pattern.compile("\\(([+-]\\d+) HP\\)");
    private static final Color LATAR = new Color(0x1B1F3B);

    private final CardLayout kartu = new CardLayout();
    private final JPanel isi = new JPanel(kartu);

    // komponen halaman pemilihan pahlawan
    private final JTextField fieldNama = new JTextField("Arthur", 14);

    // komponen halaman pertarungan
    private final JPanel arena = new JPanel(new GridLayout(1, 2));
    private final JLabel labelStatus = new JLabel(" ", SwingConstants.CENTER);
    private final JButton tombolSerang = new JButton("Serang");
    private final JButton tombolSembuhkan = new JButton("Sembuhkan");
    private final JButton tombolUlangi = new JButton("Ulangi");
    private final JTextArea areaLog = new JTextArea(6, 40);

    private Pertarungan pertarungan;
    private PanelKarakter panelPahlawan;
    private PanelKarakter panelLawan;
    private boolean sibuk;          // true selama animasi berjalan, tombol dikunci

    public JendelaUtama() {
        super("Duel Pahlawan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        isi.add(buatHalamanPilih(), KARTU_PILIH);
        isi.add(buatHalamanDuel(), KARTU_DUEL);
        add(isi);

        tombolSerang.addActionListener(e -> klikSerang());
        tombolSembuhkan.addActionListener(e -> klikSembuhkan());
        tombolUlangi.addActionListener(e -> klikUlangi());

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------ susunan tampilan

    private JPanel buatHalamanPilih() {
        JPanel latar = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(0x2B2D5B), 0, getHeight(), LATAR));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        latar.setPreferredSize(new Dimension(1100, 670));
        latar.setBorder(BorderFactory.createEmptyBorder(22, 60, 18, 60));

        // bagian atas: judul, petunjuk, dan input nama
        JPanel atas = new JPanel(new GridLayout(0, 1, 0, 6));
        atas.setOpaque(false);

        JLabel judul = new JLabel("DUEL PAHLAWAN", SwingConstants.CENTER);
        judul.setFont(new Font(Font.SERIF, Font.BOLD, 40));
        judul.setForeground(Color.WHITE);
        atas.add(judul);

        JLabel petunjuk = new JLabel("Beri nama pahlawanmu, pilih salah satu kartu, lalu lawan Goblin sampai salah satu kalah.",
                SwingConstants.CENTER);
        petunjuk.setFont(petunjuk.getFont().deriveFont(15f));
        petunjuk.setForeground(new Color(0xC9CBE8));
        atas.add(petunjuk);

        fieldNama.setFont(fieldNama.getFont().deriveFont(16f));
        atas.add(barisForm("Nama pahlawan", fieldNama));
        latar.add(atas, BorderLayout.NORTH);

        // tengah: kartu pilihan pahlawan (object contoh hanya untuk membaca statistik dan sprite)
        JPanel daftarKartu = new JPanel(new GridLayout(1, 2, 40, 0));
        daftarKartu.setOpaque(false);
        daftarKartu.add(new KartuKarakter(new Ksatria("Contoh"),
                "Damage stabil sebesar kekuatannya.<br>Tidak bisa menyembuhkan.",
                new Color(0x1A3A5C), new Color(0x1E64B4), () -> klikMulai("Ksatria")));
        daftarKartu.add(new KartuKarakter(new Penyihir("Contoh"),
                "Damage besar (x2).<br>Bisa menyembuhkan diri (+25 HP).",
                new Color(0x3A1A5C), new Color(0x6A1FB4), () -> klikMulai("Penyihir")));
        latar.add(daftarKartu, BorderLayout.CENTER);

        // bawah: keterangan lawan
        Monster contohLawan = new Monster("Goblin");
        JLabel infoLawan = new JLabel("Lawanmu: Goblin (HP " + contohLawan.getHpMaks() + ")", SwingConstants.CENTER);
        infoLawan.setFont(infoLawan.getFont().deriveFont(Font.ITALIC, 14f));
        infoLawan.setForeground(new Color(0xC9CBE8));
        latar.add(infoLawan, BorderLayout.SOUTH);
        return latar;
    }

    private JPanel barisForm(String teks, java.awt.Component komponen) {
        JPanel baris = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        baris.setOpaque(false);
        JLabel label = new JLabel(teks);
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        label.setPreferredSize(new Dimension(140, 30));
        baris.add(label);
        baris.add(komponen);
        return baris;
    }

    private JPanel buatHalamanDuel() {
        JPanel halaman = new JPanel(new BorderLayout());
        halaman.setBackground(LATAR);
        halaman.setPreferredSize(new Dimension(1100, 670));

        labelStatus.setFont(labelStatus.getFont().deriveFont(Font.BOLD, 18f));
        labelStatus.setForeground(new Color(0xFFD166));
        labelStatus.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        halaman.add(labelStatus, BorderLayout.NORTH);

        arena.setOpaque(false);
        halaman.add(arena, BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout(0, 8));
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createEmptyBorder(0, 20, 14, 20));

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        tombol.setOpaque(false);
        tombol.add(tombolSerang);
        tombol.add(tombolSembuhkan);
        tombol.add(tombolUlangi);
        bawah.add(tombol, BorderLayout.NORTH);

        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        bawah.add(new JScrollPane(areaLog), BorderLayout.CENTER);

        halaman.add(bawah, BorderLayout.SOUTH);
        return halaman;
    }

    // ------------------------------------------------------------ aksi tombol

    private void klikMulai(String jenis) {
        String nama = fieldNama.getText().trim();
        try {
            // polymorphism: reference Karakter menunjuk object Ksatria atau Penyihir
            Karakter pahlawan = "Penyihir".equals(jenis)
                    ? new Penyihir(nama)
                    : new Ksatria(nama);
            pertarungan = new Pertarungan(pahlawan, new Monster("Goblin"));
        } catch (IllegalArgumentException e) {
            // validasi di class Karakter (nama tidak boleh kosong)
            tampilkanPesan(e.getMessage());
            return;
        }
        siapkanArena();
        kartu.show(isi, KARTU_DUEL);
    }

    private void klikSerang() {
        if (sibuk) {
            return;
        }
        int[] hpAwal = hpSekarang();
        int awalLog = pertarungan.getRiwayat().size();
        try {
            pertarungan.pahlawanMenyerang();
        } catch (KarakterMatiException e) {
            tampilkanPesan(e.getMessage());          // aksi pada karakter yang sudah kalah
            return;
        } catch (IllegalStateException e) {
            tampilkanPesan(e.getMessage());          // pertarungan sudah selesai
            return;
        }
        putarGiliran(AksiSprite.SERANG, hpAwal, awalLog);
    }

    private void klikSembuhkan() {
        if (sibuk) {
            return;
        }
        int[] hpAwal = hpSekarang();
        int awalLog = pertarungan.getRiwayat().size();
        try {
            pertarungan.pahlawanMenyembuhkan();
        } catch (KarakterMatiException | IllegalStateException e) {
            tampilkanPesan(e.getMessage());
            return;
        }
        putarGiliran(AksiSprite.SEMBUH, hpAwal, awalLog);
    }

    private void klikUlangi() {
        panelPahlawan.hentikan();
        panelLawan.hentikan();
        kartu.show(isi, KARTU_PILIH);
    }

    // ------------------------------------------------------------ pengaturan animasi

    private void siapkanArena() {
        arena.removeAll();
        panelPahlawan = new PanelKarakter(pertarungan.getPahlawan(), true);
        panelLawan = new PanelKarakter(pertarungan.getLawan(), false);
        arena.add(panelPahlawan);
        arena.add(panelLawan);
        arena.revalidate();

        sibuk = false;
        areaLog.setText(gabungkanLog(0));
        labelStatus.setText("Giliran " + pertarungan.getGiliran());
        aturTombol();
    }

    private int[] hpSekarang() {
        return new int[]{pertarungan.getPahlawan().getHp(), pertarungan.getLawan().getHp()};
    }

    /**
     * Memutar satu giliran penuh: aksi pahlawan, lalu (bila monster masih hidup)
     * balasan monster. Hasil hitungan sudah ada di model; di sini hanya
     * ditampilkan bertahap lewat animasi.
     */
    private void putarGiliran(AksiSprite aksiPahlawan, int[] hpAwal, int awalLog) {
        sibuk = true;
        aturTombol();

        Karakter pahlawan = pertarungan.getPahlawan();
        Karakter lawan = pertarungan.getLawan();
        List<String> baruDiLog = pertarungan.getRiwayat().subList(awalLog, pertarungan.getRiwayat().size());
        int angkaPahlawan = ambilAngka(baruDiLog, 0);                 // damage ke monster atau heal
        int angkaBalasan = lawan.masihHidup() ? ambilAngka(baruDiLog, 1) : 0;

        Runnable selesai = this::akhiriGiliran;

        // langkah 2: monster membalas, pahlawan terkena
        Runnable balasan = () -> {
            if (lawan.masihHidup()) {
                panelLawan.mainkan(AksiSprite.SERANG, () ->
                        terkena(panelPahlawan, pahlawan.getHp(), angkaBalasan, selesai));
            } else {
                selesai.run();
            }
        };

        // langkah 1: aksi pahlawan
        if (aksiPahlawan == AksiSprite.SERANG) {
            int hpLawanBaru = hpAwal[1] + angkaPahlawan;               // angkaPahlawan bernilai negatif
            panelPahlawan.mainkan(AksiSprite.SERANG, () ->
                    terkena(panelLawan, hpLawanBaru, -angkaPahlawan, balasan));
        } else {
            int hpPulih = Math.min(hpAwal[0] + angkaPahlawan, pahlawan.getHpMaks());
            panelPahlawan.mainkan(AksiSprite.SEMBUH, () -> {
                panelPahlawan.setHp(hpPulih, pahlawan.getHpMaks());
                panelPahlawan.tampilkanAngka("+" + angkaPahlawan, new Color(0x6BE675));
                balasan.run();
            });
        }
    }

    private void terkena(PanelKarakter panel, int hpBaru, int damage, Runnable lanjut) {
        Karakter k = (panel == panelPahlawan) ? pertarungan.getPahlawan() : pertarungan.getLawan();
        panel.setHp(hpBaru, k.getHpMaks());
        panel.tampilkanAngka("-" + damage, new Color(0xFF6B6B));
        panel.mainkan(hpBaru <= 0 ? AksiSprite.MATI : AksiSprite.TERLUKA, lanjut);
    }

    private void akhiriGiliran() {
        areaLog.setText(gabungkanLog(0));
        areaLog.setCaretPosition(areaLog.getDocument().getLength());

        if (pertarungan.isSelesai()) {
            Karakter menang = pertarungan.getPemenang();
            labelStatus.setText("Pemenang: " + menang.getNama());
            PanelKarakter panelMenang = (menang == pertarungan.getPahlawan()) ? panelPahlawan : panelLawan;
            panelMenang.mainkan(AksiSprite.MENANG, null);
        } else {
            labelStatus.setText("Giliran " + pertarungan.getGiliran());
        }
        sibuk = false;
        aturTombol();
    }

    private void aturTombol() {
        boolean berlangsung = pertarungan != null && !pertarungan.isSelesai() && !sibuk;
        tombolSerang.setEnabled(berlangsung);
        // interface: tombol Sembuhkan hanya muncul bila pahlawan bisa menyembuhkan
        boolean bisaSembuh = pertarungan != null && pertarungan.pahlawanBisaMenyembuhkan();
        tombolSembuhkan.setVisible(bisaSembuh);
        tombolSembuhkan.setEnabled(berlangsung && bisaSembuh);
        tombolUlangi.setEnabled(!sibuk);
    }

    // ------------------------------------------------------------ bantuan

    private String gabungkanLog(int dari) {
        StringBuilder sb = new StringBuilder();
        List<String> riwayat = pertarungan.getRiwayat();
        for (int i = dari; i < riwayat.size(); i++) {
            sb.append(i + 1).append(". ").append(riwayat.get(i)).append('\n');
        }
        return sb.toString();
    }

    /** Mengambil angka HP dari baris riwayat, misalnya "(-15 HP)" menjadi -15. */
    private int ambilAngka(List<String> baris, int indeks) {
        if (indeks >= baris.size()) {
            return 0;
        }
        Matcher m = POLA_ANGKA.matcher(baris.get(indeks));
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private void tampilkanPesan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
