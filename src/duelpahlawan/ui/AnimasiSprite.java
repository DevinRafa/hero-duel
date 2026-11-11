package duelpahlawan.ui;

import duelpahlawan.model.Karakter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Menyimpan semua frame animasi (sprite sheet) milik satu jenis karakter.
 * Setiap aksi disimpan sebagai strip horizontal; class ini memotongnya
 * menjadi frame-frame terpisah.
 *
 * Konsep OOP: encapsulation (semua atribut private, diakses lewat getter)
 * dan factory method (untuk(Karakter) memilih sprite yang sesuai).
 */
public class AnimasiSprite {

    private static final int TINGGI_FRAME = 96;

    private final Map<AksiSprite, BufferedImage[]> frame = new EnumMap<>(AksiSprite.class);
    private final int lebarFrame;
    private final boolean menghadapKanan;   // arah hadap gambar aslinya
    private final int barisKaki;            // baris piksel tempat kaki menyentuh tanah
    private final int jarakMaju;            // seberapa jauh karakter maju saat menyerang (piksel layar)

    private AnimasiSprite(String awalan, int lebarFrame, boolean menghadapKanan,
                          int barisKaki, int jarakMaju) {
        this.lebarFrame = lebarFrame;
        this.menghadapKanan = menghadapKanan;
        this.barisKaki = barisKaki;
        this.jarakMaju = jarakMaju;
        for (AksiSprite aksi : AksiSprite.values()) {
            BufferedImage[] hasil = muatStrip(awalan, aksi);
            if (hasil != null) {
                frame.put(aksi, hasil);
            }
        }
        if (!punya(AksiSprite.IDLE)) {
            throw new IllegalStateException("Sprite idle untuk '" + awalan + "' tidak ditemukan");
        }
    }

    /**
     * Factory method: memilih sprite berdasarkan jenis object Karakter.
     * Nama class (Ksatria, Penyihir, Monster) dipakai sebagai awalan nama file.
     */
    public static AnimasiSprite untuk(Karakter karakter) {
        String jenis = karakter.getClass().getSimpleName().toLowerCase();
        switch (jenis) {
            case "ksatria":
                return new AnimasiSprite("ksatria", 112, true, 84, 48);
            case "penyihir":
                return new AnimasiSprite("penyihir", 96, false, 79, 0);   // menyerang dari jauh
            case "monster":
                return new AnimasiSprite("monster", 96, true, 79, 40);
            default:
                throw new IllegalArgumentException("Belum ada sprite untuk " + jenis);
        }
    }

    /** Membaca satu file strip, lalu memotongnya per frame. Null bila file tidak ada. */
    private BufferedImage[] muatStrip(String awalan, AksiSprite aksi) {
        String nama = "sprite/" + awalan + "_" + aksi.getAkhiranFile() + ".png";
        try (InputStream in = AnimasiSprite.class.getResourceAsStream(nama)) {
            if (in == null) {
                return null;                       // aksi ini tidak dimiliki karakter tersebut
            }
            BufferedImage strip = ImageIO.read(in);
            BufferedImage[] hasil = new BufferedImage[strip.getWidth() / lebarFrame];
            for (int i = 0; i < hasil.length; i++) {
                hasil[i] = strip.getSubimage(i * lebarFrame, 0, lebarFrame, TINGGI_FRAME);
            }
            return hasil;
        } catch (IOException e) {
            throw new IllegalStateException("Gagal membaca " + nama, e);
        }
    }

    public boolean punya(AksiSprite aksi)               { return frame.containsKey(aksi); }
    public int jumlahFrame(AksiSprite aksi)             { return frame.get(aksi).length; }
    public BufferedImage getFrame(AksiSprite aksi, int indeks) { return frame.get(aksi)[indeks]; }

    public int getLebarFrame()          { return lebarFrame; }
    public int getTinggiFrame()         { return TINGGI_FRAME; }
    public boolean isMenghadapKanan()   { return menghadapKanan; }
    public int getBarisKaki()           { return barisKaki; }
    public int getJarakMaju()           { return jarakMaju; }
}
