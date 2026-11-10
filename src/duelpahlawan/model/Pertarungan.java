package duelpahlawan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mengatur jalannya satu duel antara pahlawan dan lawan.
 * Konsep: aggregation (Pertarungan memiliki Karakter, tetapi Karakter
 * tetap ada walaupun Pertarungan dibuang), polymorphism (reference bertipe
 * Karakter / BisaMenyembuhkan menunjuk object subclass).
 */
public class Pertarungan {

    private final Karakter pahlawan;
    private final Karakter lawan;
    private final List<String> riwayat = new ArrayList<>();
    private int giliran = 1;
    private Karakter pemenang;

    public Pertarungan(Karakter pahlawan, Karakter lawan) {
        if (pahlawan == null || lawan == null) {
            throw new IllegalArgumentException("Peserta pertarungan tidak boleh null");
        }
        this.pahlawan = pahlawan;
        this.lawan = lawan;
        catat("Pertarungan dimulai: " + pahlawan.getNama() + " vs " + lawan.getNama());
    }

    public void pahlawanMenyerang() throws KarakterMatiException {
        pastikanBerlangsung();
        int damage = pahlawan.serang(lawan);                 // dynamic method dispatch
        catat(pahlawan.getNama() + " menyerang " + lawan.getNama() + " (-" + damage + " HP)");
        akhiriGiliran();
    }

    public void pahlawanMenyembuhkan() throws KarakterMatiException {
        pastikanBerlangsung();
        if (!pahlawanBisaMenyembuhkan()) {
            throw new IllegalStateException(pahlawan.getNama() + " tidak bisa menyembuhkan");
        }
        BisaMenyembuhkan penyembuh = (BisaMenyembuhkan) pahlawan;   // reference interface
        int pulih = penyembuh.sembuhkan(pahlawan);
        catat(pahlawan.getNama() + " menyembuhkan diri (+" + pulih + " HP)");
        akhiriGiliran();
    }

    public boolean pahlawanBisaMenyembuhkan() {
        return pahlawan instanceof BisaMenyembuhkan;
    }

    // ---- langkah internal ----
    private void akhiriGiliran() throws KarakterMatiException {
        if (lawan.masihHidup()) {
            int damage = lawan.serang(pahlawan);
            catat(lawan.getNama() + " membalas " + pahlawan.getNama() + " (-" + damage + " HP)");
        }
        periksaPemenang();
        if (pemenang == null) {
            giliran++;
        }
    }

    private void periksaPemenang() {
        if (!lawan.masihHidup()) {
            pemenang = pahlawan;
        } else if (!pahlawan.masihHidup()) {
            pemenang = lawan;
        }
        if (pemenang != null) {
            catat(pemenang.getNama() + " memenangkan pertarungan");
        }
    }

    private void pastikanBerlangsung() {
        if (isSelesai()) {
            throw new IllegalStateException("Pertarungan sudah selesai");
        }
    }

    private void catat(String pesan) {
        riwayat.add(pesan);
    }

    // ---- getter ----
    public boolean isSelesai()          { return pemenang != null; }
    public Karakter getPemenang()       { return pemenang; }
    public Karakter getPahlawan()       { return pahlawan; }
    public Karakter getLawan()          { return lawan; }
    public int getGiliran()             { return giliran; }
    public List<String> getRiwayat()    { return Collections.unmodifiableList(riwayat); }
}
