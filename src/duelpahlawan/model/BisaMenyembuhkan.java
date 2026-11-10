package duelpahlawan.model;

/**
 * Kontrak untuk karakter yang memiliki kemampuan menyembuhkan.
 * Konsep: interface (abstraction) - hanya mendefinisikan "apa", bukan "bagaimana".
 */
public interface BisaMenyembuhkan {

    /**
     * Memulihkan HP karakter target.
     *
     * @return jumlah HP yang benar-benar pulih
     * @throws KarakterMatiException jika penyembuh atau target sudah kalah
     */
    int sembuhkan(Karakter target) throws KarakterMatiException;
}
