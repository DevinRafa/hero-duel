package duelpahlawan.ui;

/**
 * Daftar aksi animasi yang dimiliki sprite karakter.
 * Setiap aksi sesuai dengan satu file gambar, misalnya ksatria_serang.png.
 * Konsep: enum (kumpulan konstanta bernilai tetap) dengan atribut private.
 */
public enum AksiSprite {
    IDLE("idle", true),
    SERANG("serang", false),
    TERLUKA("terluka", false),
    MATI("mati", false),
    SEMBUH("sembuh", false),
    MENANG("menang", true);

    private final String akhiranFile;
    private final boolean berulang;

    AksiSprite(String akhiranFile, boolean berulang) {
        this.akhiranFile = akhiranFile;
        this.berulang = berulang;
    }

    /** Bagian akhir nama file, misalnya "serang" pada ksatria_serang.png. */
    public String getAkhiranFile() { return akhiranFile; }

    /** True bila animasinya diputar terus-menerus (idle dan menang). */
    public boolean isBerulang()    { return berulang; }
}
