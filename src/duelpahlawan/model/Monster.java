package duelpahlawan.model;

import java.util.Random;

/**
 * Lawan yang dikendalikan sistem: HP besar, damage acak.
 * Konsep: inheritance, method overriding, overloading constructor.
 */
public class Monster extends Karakter {

    private final Random acak;

    public Monster(String nama) {
        this(nama, new Random());
    }

    /** Constructor kedua (overloading) agar hasil acak bisa diuji. */
    public Monster(String nama, Random acak) {
        super(nama, 130, 16);
        this.acak = acak;
    }

    @Override
    public int serang(Karakter lawan) throws KarakterMatiException {
        pastikanHidup();
        int batasBawah = getKekuatan() / 2;
        int damage = batasBawah + acak.nextInt(getKekuatan() - batasBawah + 1); // 8..16
        lawan.terimaDamage(damage);
        return damage;
    }
}
