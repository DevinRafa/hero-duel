package duelpahlawan.ui;

import duelpahlawan.model.Karakter;
import duelpahlawan.model.KarakterMatiException;
import duelpahlawan.model.Ksatria;
import duelpahlawan.model.Monster;
import duelpahlawan.model.Pertarungan;
import duelpahlawan.model.Penyihir;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Jendela utama aplikasi Duel Pahlawan.
 * Konsep: JFrame Swing, association ke Pertarungan (model), pemisahan ui dan model.
 * Alur: layar pilih pahlawan -> layar pertarungan -> layar hasil.
 */
public class JendelaUtama extends JFrame {

    private Pertarungan pertarungan;

    private final CardLayout kartu = new CardLayout();
    private final JPanel panelKartu = new JPanel(kartu);
    private final JPanel panelPilih = new JPanel(new BorderLayout(10, 10));
    private final JPanel panelLaga  = new JPanel(new BorderLayout(0, 8));
    private final JPanel panelHasil = new JPanel(new BorderLayout(10, 10));

    private JLabel lblNamaPahlawan, lblNamaLawan, lblGiliran, lblPemenang;
    private HpBar barHpPahlawan, barHpLawan;
    private JTextArea areaLog;
    private JButton btnSerang, btnSembuhkan, btnUlangi;
    private SpritePanel spritePahlawan, spriteGoblin, spriteHasil;

    private static final String KARTU_PILIH = "PILIH";
    private static final String KARTU_LAGA  = "LAGA";
    private static final String KARTU_HASIL = "HASIL";
    private static final String PATH_KNIGHT = "/duelpahlawan/assets/KNIGHT/";
    private static final String PATH_WIZARD = "/duelpahlawan/assets/WIZARD/";
    private static final String PATH_GOBLIN = "/duelpahlawan/assets/GOBLIN/GOBLIN/";

    private int pIdleF, pAttackF, pHurtF, pDeathF;
    private String pPath;

    public JendelaUtama() {
        super("Duel Pahlawan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(620, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        bangunPanelPilih();
        bangunPanelLaga();
        bangunPanelHasil();

        panelKartu.add(panelPilih, KARTU_PILIH);
        panelKartu.add(panelLaga,  KARTU_LAGA);
        panelKartu.add(panelHasil, KARTU_HASIL);
        add(panelKartu);
        kartu.show(panelKartu, KARTU_PILIH);
    }

    // ================================================================
    // Panel 1: Pilih Pahlawan (Gambar 8)
    // ================================================================
    private void bangunPanelPilih() {
        panelPilih.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel judul = new JLabel("Pilih Pahlawanmu", SwingConstants.CENTER);
        judul.setFont(new Font("SansSerif", Font.BOLD, 22));
        panelPilih.add(judul, BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridLayout(1, 2, 20, 0));
        SpritePanel prevK = buatPreview(PATH_KNIGHT + "idle_knight.png", 14);
        SpritePanel prevP = buatPreview(PATH_WIZARD + "idle_wizard.png",  6);

        JPanel kartuK = buatKartuKarakter("Ksatria",  "HP: 120 | Kekuatan: 15",
                "Damage stabil.<br>Tidak bisa menyembuhkan.", Color.decode("#1a3a5c"), prevK);
        JPanel kartuP = buatKartuKarakter("Penyihir", "HP: 70 | Kekuatan: 12",
                "Damage besar (x2).<br>Bisa sembuhkan diri (+25 HP).", Color.decode("#3a1a5c"), prevP);

        JButton btnK = new JButton("Pilih Ksatria");
        JButton btnP = new JButton("Pilih Penyihir");
        styleButton(btnK, new Color(30, 100, 180));
        styleButton(btnP, new Color(100, 30, 180));

        tengah.add(wrapKartu(kartuK, btnK));
        tengah.add(wrapKartu(kartuP, btnP));
        panelPilih.add(tengah, BorderLayout.CENTER);

        JLabel info = new JLabel("Lawanmu: Goblin (HP 130)", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.ITALIC, 13));
        info.setForeground(Color.GRAY);
        panelPilih.add(info, BorderLayout.SOUTH);

        btnK.addActionListener(e -> mulaiPermainan(new Ksatria("Arthur"),  PATH_KNIGHT, 14, 8, 8, 15));
        btnP.addActionListener(e -> mulaiPermainan(new Penyihir("Merlin"), PATH_WIZARD,  6, 9, 6,  6));
    }

    private JPanel buatKartuKarakter(String nama, String stats, String desk, Color bg, SpritePanel sprite) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel lNama = new JLabel(nama, SwingConstants.CENTER);
        lNama.setFont(new Font("SansSerif", Font.BOLD, 16));
        lNama.setForeground(Color.WHITE);
        JPanel info = new JPanel(new GridLayout(2, 1, 2, 2));
        info.setOpaque(false);
        JLabel lStats = new JLabel(stats, SwingConstants.CENTER);
        lStats.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lStats.setForeground(Color.LIGHT_GRAY);
        JLabel lDesk = new JLabel("<html><center>" + desk + "</center></html>", SwingConstants.CENTER);
        lDesk.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lDesk.setForeground(Color.LIGHT_GRAY);
        info.add(lStats);
        info.add(lDesk);
        p.add(lNama,  BorderLayout.NORTH);
        p.add(sprite, BorderLayout.CENTER);
        p.add(info,   BorderLayout.SOUTH);
        return p;
    }

    private JPanel wrapKartu(JPanel k, JButton btn) {
        JPanel w = new JPanel(new BorderLayout(5, 5));
        w.setOpaque(false);
        w.add(k,   BorderLayout.CENTER);
        w.add(btn, BorderLayout.SOUTH);
        return w;
    }

    // ================================================================
    // Panel 2: Pertarungan (Gambar 9)
    // ================================================================
    private void bangunPanelLaga() {
        panelLaga.setBackground(Color.decode("#12162b"));
        panelLaga.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // --- Atas: Bar Nama & Giliran ---
        JPanel atas = new JPanel(new BorderLayout(10, 0));
        atas.setOpaque(false);

        lblNamaPahlawan = new JLabel("", SwingConstants.LEFT);
        lblNamaPahlawan.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNamaPahlawan.setForeground(Color.WHITE);

        lblGiliran = new JLabel("", SwingConstants.CENTER);
        lblGiliran.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblGiliran.setForeground(Color.decode("#f1c40f"));

        lblNamaLawan = new JLabel("", SwingConstants.RIGHT);
        lblNamaLawan.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNamaLawan.setForeground(Color.WHITE);

        atas.add(lblNamaPahlawan, BorderLayout.WEST);
        atas.add(lblGiliran, BorderLayout.CENTER);
        atas.add(lblNamaLawan, BorderLayout.EAST);
        panelLaga.add(atas, BorderLayout.NORTH);

        // --- Tengah: Arena + HP Bars ---
        JPanel tengah = new JPanel(new BorderLayout(0, 8));
        tengah.setOpaque(false);

        ArenaPanel arena = new ArenaPanel();
        arena.setPreferredSize(new Dimension(580, 185));
        spritePahlawan = new SpritePanel(null, 0, false, 175);
        spriteGoblin   = new SpritePanel(null, 0, true,  175);
        spritePahlawan.setDrawShadow(true);
        spriteGoblin.setDrawShadow(true);
        arena.add(spritePahlawan);
        arena.add(spriteGoblin);
        tengah.add(arena, BorderLayout.CENTER);

        JPanel panelHp = new JPanel(new GridLayout(1, 2, 24, 0));
        panelHp.setOpaque(false);
        barHpPahlawan = new HpBar(120);
        barHpLawan    = new HpBar(130);
        panelHp.add(barHpPahlawan);
        panelHp.add(barHpLawan);
        tengah.add(panelHp, BorderLayout.SOUTH);

        panelLaga.add(tengah, BorderLayout.CENTER);

        // --- Bawah: Tombol Aksi + Combat Log ---
        JPanel bawah = new JPanel(new BorderLayout(0, 8));
        bawah.setOpaque(false);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 2));
        panelTombol.setOpaque(false);

        btnSerang    = new JButton("Serang");
        btnSembuhkan = new JButton("Sembuhkan");
        btnUlangi    = new JButton("Ulangi");

        styleButtonWhite(btnSerang);
        styleButtonWhite(btnSembuhkan);
        styleButtonWhite(btnUlangi);

        panelTombol.add(btnSerang);
        panelTombol.add(btnSembuhkan);
        panelTombol.add(btnUlangi);
        bawah.add(panelTombol, BorderLayout.NORTH);

        areaLog = new JTextArea(6, 40);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("SansSerif", Font.PLAIN, 12));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setBackground(Color.WHITE);
        areaLog.setForeground(Color.BLACK);
        areaLog.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(Color.decode("#c0c0c0"), 1));
        scrollLog.setPreferredSize(new Dimension(580, 130));
        bawah.add(scrollLog, BorderLayout.CENTER);

        panelLaga.add(bawah, BorderLayout.SOUTH);

        btnSerang.addActionListener(e    -> klikSerang());
        btnSembuhkan.addActionListener(e -> klikSembuhkan());
        btnUlangi.addActionListener(e    -> ulangPertarungan());
    }

    // ================================================================
    // Panel 3: Hasil Akhir (Gambar 10)
    // ================================================================
    private void bangunPanelHasil() {
        panelHasil.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        lblPemenang = new JLabel("", SwingConstants.CENTER);
        lblPemenang.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel sub = new JLabel("Pertarungan selesai!", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(Color.GRAY);
        spriteHasil = new SpritePanel(null, 0, false, 200);
        JButton btnUlang = new JButton("Main Lagi");
        styleButton(btnUlang, new Color(60, 130, 60));
        btnUlang.addActionListener(e -> kartu.show(panelKartu, KARTU_PILIH));
        JPanel tengah = new JPanel(new BorderLayout(10, 15));
        tengah.add(lblPemenang, BorderLayout.NORTH);
        tengah.add(spriteHasil, BorderLayout.CENTER);
        JPanel bawah = new JPanel(new GridLayout(2, 1, 5, 10));
        bawah.add(sub);
        JPanel wBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wBtn.add(btnUlang);
        bawah.add(wBtn);
        tengah.add(bawah, BorderLayout.SOUTH);
        panelHasil.add(tengah, BorderLayout.CENTER);
    }

    // ================================================================
    // Logika Aksi
    // ================================================================
    private void mulaiPermainan(Karakter pahlawan, String path,
                                int idleF, int attackF, int hurtF, int deathF) {
        pPath = path; pIdleF = idleF; pAttackF = attackF; pHurtF = hurtF; pDeathF = deathF;
        spritePahlawan.loadSheet(loadImg(idleFile(path)), idleF, false);
        spriteGoblin.loadSheet(loadImg(PATH_GOBLIN + "idle_goblin1.png"), 8, true);
        spritePahlawan.setDrawShadow(true);
        spriteGoblin.setDrawShadow(true);
        pertarungan = new Pertarungan(pahlawan, new Monster("Goblin"));
        perbaruiTampilan();
        kartu.show(panelKartu, KARTU_LAGA);
    }

    private void ulangPertarungan() {
        Karakter pahlawanBaru;
        if (pPath != null && pPath.contains("KNIGHT")) {
            pahlawanBaru = new Ksatria("Arthur");
        } else {
            pahlawanBaru = new Penyihir("Merlin");
        }
        mulaiPermainan(pahlawanBaru, pPath, pIdleF, pAttackF, pHurtF, pDeathF);
    }

    private void klikSerang() {
        btnSerang.setEnabled(false);
        btnSembuhkan.setEnabled(false);
        anim(spritePahlawan, pPath + attackFile(pPath), pAttackF, false, () -> {
            try { pertarungan.pahlawanMenyerang(); }
            catch (KarakterMatiException | IllegalStateException ex) { showErr(ex.getMessage()); }

            Karakter l = pertarungan.getLawan();
            if (!l.masihHidup()) {
                anim(spriteGoblin, PATH_GOBLIN + "death_goblin.png", 9, true, this::perbaruiTampilan);
            } else {
                // Goblin kena serang (hurt)
                anim(spriteGoblin, PATH_GOBLIN + "hurt_goblin.png", 5, true, () -> {
                    // Goblin membalas menyerang (attack)
                    anim(spriteGoblin, PATH_GOBLIN + "attack_goblin2.png", 8, true, () -> {
                        spriteGoblin.loadSheet(loadImg(PATH_GOBLIN + "idle_goblin1.png"), 8, true);
                        Karakter p = pertarungan.getPahlawan();
                        if (!p.masihHidup()) {
                            anim(spritePahlawan, pPath + deathFile(pPath), pDeathF, false, this::perbaruiTampilan);
                        } else {
                            anim(spritePahlawan, pPath + hurtFile(pPath), pHurtF, false, () -> {
                                spritePahlawan.loadSheet(loadImg(idleFile(pPath)), pIdleF, false);
                                perbaruiTampilan();
                            });
                        }
                    });
                });
            }
        });
    }

    private void klikSembuhkan() {
        btnSerang.setEnabled(false);
        btnSembuhkan.setEnabled(false);
        boolean isWizard = pPath.contains("WIZARD");
        String healPath  = isWizard ? PATH_WIZARD + "heal_wizard.png" : idleFile(pPath);
        int    healF     = isWizard ? 8 : pIdleF;
        anim(spritePahlawan, healPath, healF, false, () -> {
            try { pertarungan.pahlawanMenyembuhkan(); }
            catch (KarakterMatiException | IllegalStateException ex) { showErr(ex.getMessage()); }
            spritePahlawan.loadSheet(loadImg(idleFile(pPath)), pIdleF, false);

            // Goblin membalas menyerang (attack)
            anim(spriteGoblin, PATH_GOBLIN + "attack_goblin2.png", 8, true, () -> {
                spriteGoblin.loadSheet(loadImg(PATH_GOBLIN + "idle_goblin1.png"), 8, true);
                Karakter p = pertarungan.getPahlawan();
                if (!p.masihHidup()) {
                    anim(spritePahlawan, pPath + deathFile(pPath), pDeathF, false, this::perbaruiTampilan);
                } else {
                    anim(spritePahlawan, pPath + hurtFile(pPath), pHurtF, false, () -> {
                        spritePahlawan.loadSheet(loadImg(idleFile(pPath)), pIdleF, false);
                        perbaruiTampilan();
                    });
                }
            });
        });
    }

    private void perbaruiTampilan() {
        if (pertarungan == null) return;
        Karakter p = pertarungan.getPahlawan();
        Karakter l = pertarungan.getLawan();

        lblNamaPahlawan.setText(p.getNama() + " (" + p.getClass().getSimpleName() + ")");
        lblNamaLawan.setText(l.getNama() + " (Monster)");
        lblGiliran.setText("Giliran " + pertarungan.getGiliran());

        barHpPahlawan.perbarui(p.getHp(), p.getHpMaks());
        barHpLawan.perbarui(l.getHp(), l.getHpMaks());

        List<String> log = pertarungan.getRiwayat();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < log.size(); i++) {
            sb.append(i + 1).append(". ").append(log.get(i)).append("\n");
        }
        areaLog.setText(sb.toString());
        areaLog.setCaretPosition(areaLog.getDocument().getLength());

        boolean selesai = pertarungan.isSelesai();
        btnSerang.setEnabled(!selesai);
        btnSembuhkan.setEnabled(!selesai && pertarungan.pahlawanBisaMenyembuhkan());
        btnSembuhkan.setVisible(pertarungan.pahlawanBisaMenyembuhkan());
        btnUlangi.setEnabled(true);

        if (selesai) {
            Karakter menang = pertarungan.getPemenang();
            lblPemenang.setText(menang.getNama() + " menang!");
            lblPemenang.setForeground(menang == p ? new Color(60, 200, 60) : new Color(220, 60, 60));
            boolean pahlawanMenang = menang == p;
            String winPath   = pahlawanMenang ? idleFile(pPath) : PATH_GOBLIN + "idle_goblin1.png";
            int    winFrames = pahlawanMenang ? pIdleF : 8;
            spriteHasil.loadSheet(loadImg(winPath), winFrames, !pahlawanMenang);

            Timer timerHasil = new Timer(1500, e -> kartu.show(panelKartu, KARTU_HASIL));
            timerHasil.setRepeats(false);
            timerHasil.start();
        }
    }

    // ================================================================
    // Helper Path & Animasi
    // ================================================================
    private String idleFile(String path) {
        if (path.contains("KNIGHT")) return path + "idle_knight.png";
        if (path.contains("WIZARD")) return path + "idle_wizard.png";
        return path + "idle_goblin1.png";
    }
    private String attackFile(String path) {
        return path.contains("KNIGHT") ? "attack_knight.png" : "attack_wizard.png";
    }
    private String hurtFile(String path) {
        return path.contains("KNIGHT") ? "hurt_knight.png" : "hurt_wizard1.png";
    }
    private String deathFile(String path) {
        return path.contains("KNIGHT") ? "death_knight.png" : "death_wizard1.png";
    }

    private void anim(SpritePanel panel, String res, int frames, boolean flip, Runnable done) {
        BufferedImage sheet = loadImg(res);
        if (sheet == null) { if (done != null) done.run(); return; }
        panel.playOnce(sheet, frames, flip, done);
    }

    private BufferedImage loadImg(String res) {
        URL url = getClass().getResource(res);
        if (url == null) return null;
        try { return ImageIO.read(url); } catch (IOException e) { return null; }
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleButtonWhite(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.decode("#111111"));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#b0b0b0"), 1),
                BorderFactory.createEmptyBorder(6, 22, 6, 22)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private SpritePanel buatPreview(String path, int frames) {
        return new SpritePanel(loadImg(path), frames, false, 180);
    }

    // ================================================================
    // Komponen Khusus
    // ================================================================
    private static class ArenaPanel extends JPanel {
        public ArenaPanel() {
            super(new GridLayout(1, 2, 20, 0));
            setOpaque(true);
            setBackground(Color.decode("#12162b"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.decode("#4a3b63"));
            int yGround = getHeight() - 14;
            g2.fillRect(0, yGround, getWidth(), 3);
            g2.dispose();
        }
    }

    private static class HpBar extends JProgressBar {
        public HpBar(int max) {
            super(0, max);
            setValue(max);
            setStringPainted(true);
            setPreferredSize(new Dimension(220, 22));
            setOpaque(false);
            setBorderPainted(false);
        }

        public void perbarui(int hp, int hpMaks) {
            setMaximum(hpMaks);
            setValue(Math.max(0, hp));
            setString(hp + " / " + hpMaks);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Background track (dark grey)
            g2.setColor(Color.decode("#2b2b2b"));
            g2.fillRect(0, 0, w, h);

            // Green fill
            double pct = getMaximum() > 0 ? (double) getValue() / getMaximum() : 0.0;
            pct = Math.max(0.0, Math.min(1.0, pct));
            int fillW = (int) Math.round(w * pct);
            g2.setColor(Color.decode("#27ae60"));
            g2.fillRect(0, 0, fillW, h);

            // Border
            g2.setColor(Color.decode("#444444"));
            g2.drawRect(0, 0, w - 1, h - 1);

            // Centered text
            String txt = getString();
            if (txt != null && !txt.isEmpty()) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(txt)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, tx, ty);
            }
            g2.dispose();
        }
    }
}
