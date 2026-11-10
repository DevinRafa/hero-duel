package duelpahlawan.model;

/**
 * Class dasar untuk semua karakter dalam game (Ksatria, Penyihir, Monster).
 * Konsep: abstract class, encapsulation (atribut private + getter + validasi),
 * constructor, abstract method.
 */
public abstract class Karakter {

    private final String nama;
    private int hp;
    private final int hpMaks;
    private final int kekuatan;

    public Karakter(String nama, int hpMaks, int kekuatan) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama karakter tidak boleh kosong");
        }
        if (hpMaks <= 0 || kekuatan <= 0) {
            throw new IllegalArgumentException("HP dan kekuatan harus lebih dari 0");
        }
        this.nama = nama.trim();
        this.hpMaks = hpMaks;
        this.hp = hpMaks;
        this.kekuatan = kekuatan;
    }

    // ---- getter (atribut tidak bisa diubah langsung dari luar) ----
    public String getNama()    { return nama; }
    public int getHp()         { return hp; }
    public int getHpMaks()     { return hpMaks; }
    public int getKekuatan()   { return kekuatan; }

    public boolean masihHidup() {
        return hp > 0;
    }

    /** Dipakai subclass untuk memastikan karakter belum kalah sebelum beraksi. */
    protected void pastikanHidup() throws KarakterMatiException {
        if (!masihHidup()) {
            throw new KarakterMatiException(nama + " sudah kalah");
        }
    }

    /** HP berkurang sebesar damage, tidak pernah di bawah 0. */
    public void terimaDamage(int damage) throws KarakterMatiException {
        pastikanHidup();
        hp = Math.max(0, hp - Math.max(0, damage));
    }

    /** HP bertambah sebesar jumlah, tidak pernah melebihi hpMaks. */
    public int pulihkanHp(int jumlah) throws KarakterMatiException {
        pastikanHidup();
        int sebelum = hp;
        hp = Math.min(hpMaks, hp + Math.max(0, jumlah));
        return hp - sebelum;
    }

    /**
     * Menyerang karakter lain. Setiap subclass punya cara berbeda
     * menghitung damage (method overriding -> polymorphism).
     *
     * @return damage yang diberikan
     */
    public abstract int serang(Karakter lawan) throws KarakterMatiException;
}
