package duelpahlawan.model;

/**
 * Pahlawan petarung jarak dekat: HP tinggi, damage stabil.
 * Konsep: inheritance (Ksatria IS-A Karakter), method overriding.
 */
public class Ksatria extends Karakter {

    public Ksatria(String nama) {
        super(nama, 120, 15);
    }

    @Override
    public int serang(Karakter lawan) throws KarakterMatiException {
        pastikanHidup();
        int damage = getKekuatan();      // damage selalu sama
        lawan.terimaDamage(damage);
        return damage;
    }
}
