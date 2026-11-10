package duelpahlawan.model;

/**
 * Exception yang dilempar ketika sebuah aksi dilakukan oleh atau terhadap
 * karakter yang HP-nya sudah habis (sudah kalah).
 * Konsep: exception handling (checked exception).
 */
public class KarakterMatiException extends Exception {

    public KarakterMatiException(String pesan) {
        super(pesan);
    }
}
