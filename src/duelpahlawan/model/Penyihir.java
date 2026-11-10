package duelpahlawan.model;

/**
 * Pahlawan sihir: HP rendah, damage besar, dan dapat menyembuhkan.
 * Konsep: inheritance + implements interface, method overriding.
 */
public class Penyihir extends Karakter implements BisaMenyembuhkan {

    private static final int JUMLAH_SEMBUH = 25;

    public Penyihir(String nama) {
        super(nama, 70, 12);
    }

    @Override
    public int serang(Karakter lawan) throws KarakterMatiException {
        pastikanHidup();
        int damage = getKekuatan() * 2;  // serangan sihir: damage besar
        lawan.terimaDamage(damage);
        return damage;
    }

    @Override
    public int sembuhkan(Karakter target) throws KarakterMatiException {
        pastikanHidup();
        return target.pulihkanHp(JUMLAH_SEMBUH);
    }
}
