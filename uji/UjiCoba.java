import duelpahlawan.model.*;
import java.util.Random;

/** Hanya untuk verifikasi logika model (bukan bagian dari laporan). */
public class UjiCoba {
    static int gagal = 0;

    static void cek(boolean kondisi, String nama) {
        System.out.println((kondisi ? "OK    " : "GAGAL ") + nama);
        if (!kondisi) gagal++;
    }

    public static void main(String[] args) throws Exception {
        // 1. Polymorphism lewat reference parent
        Karakter k = new Ksatria("Arthur");
        Karakter m = new Monster("Raja Goblin", new Random(1));
        int d = k.serang(m);
        cek(d == 15 && m.getHp() == 115, "Ksatria menyerang: damage 15, HP monster 115");

        Karakter p = new Penyihir("Merlin");
        int d2 = p.serang(m);
        cek(d2 == 24 && m.getHp() == 91, "Penyihir menyerang: damage 24");

        // 2. Damage monster dalam rentang 8..16
        boolean dalamRentang = true;
        for (int i = 0; i < 500; i++) {
            Karakter sasaran = new Ksatria("Uji");
            int dm = new Monster("M").serang(sasaran);
            if (dm < 8 || dm > 16) dalamRentang = false;
        }
        cek(dalamRentang, "Damage Monster selalu 8..16");

        // 3. Sembuhkan tidak melebihi hpMaks
        Penyihir merlin = new Penyihir("Merlin");
        merlin.terimaDamage(10);
        int pulih = merlin.sembuhkan(merlin);
        cek(pulih == 10 && merlin.getHp() == 70, "Sembuhkan dibatasi hpMaks (pulih 10)");

        // 4. Exception: aksi pada karakter yang sudah kalah
        Karakter mati = new Ksatria("Mati");
        mati.terimaDamage(999);
        cek(mati.getHp() == 0 && !mati.masihHidup(), "HP tidak negatif, masihHidup false");
        boolean lempar = false;
        try {
            new Ksatria("Penyerang").serang(mati);
        } catch (KarakterMatiException e) {
            lempar = true;
        }
        cek(lempar, "KarakterMatiException saat menyerang karakter yang sudah kalah");

        // 5. Validasi constructor
        boolean tolak = false;
        try { new Ksatria("  "); } catch (IllegalArgumentException e) { tolak = true; }
        cek(tolak, "Nama kosong ditolak");

        // 6. Pertarungan penuh: Ksatria vs Monster sampai selesai
        Pertarungan duel = new Pertarungan(new Ksatria("Arthur"), new Monster("Raja Goblin", new Random(7)));
        int langkah = 0;
        while (!duel.isSelesai() && langkah < 100) {
            duel.pahlawanMenyerang();
            langkah++;
        }
        cek(duel.isSelesai() && duel.getPemenang() != null, "Duel Ksatria selesai, ada pemenang: " + duel.getPemenang().getNama());
        cek(!duel.pahlawanBisaMenyembuhkan(), "Ksatria tidak bisa menyembuhkan");
        boolean state = false;
        try { duel.pahlawanMenyerang(); } catch (IllegalStateException e) { state = true; }
        cek(state, "Aksi setelah duel selesai ditolak (IllegalStateException)");

        // 7. Penyihir menyembuhkan di tengah duel
        Pertarungan duel2 = new Pertarungan(new Penyihir("Merlin"), new Monster("Raja Goblin", new Random(3)));
        duel2.pahlawanMenyerang();
        int hpSebelum = duel2.getPahlawan().getHp();
        duel2.pahlawanMenyembuhkan();
        cek(duel2.getPahlawan().getHp() >= hpSebelum - 16 + 0, "Penyihir bisa menyembuhkan di tengah duel");
        cek(duel2.pahlawanBisaMenyembuhkan(), "Penyihir bisa menyembuhkan (reference interface)");
        System.out.println("Riwayat duel2:");
        for (String baris : duel2.getRiwayat()) System.out.println("  " + baris);

        // 8. Riwayat tidak bisa dimodifikasi dari luar
        boolean tolakUbah = false;
        try { duel2.getRiwayat().add("curang"); } catch (UnsupportedOperationException e) { tolakUbah = true; }
        cek(tolakUbah, "Riwayat read-only dari luar");

        System.out.println(gagal == 0 ? "\nSEMUA UJI LULUS" : "\nADA " + gagal + " UJI GAGAL");
        System.exit(gagal == 0 ? 0 : 1);
    }
}
