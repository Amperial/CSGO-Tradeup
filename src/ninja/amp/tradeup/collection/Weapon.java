package ninja.amp.tradeup.collection;

import java.util.HashSet;
import java.util.Set;

public enum Weapon {
    CZ75_AUTO("CZ75-Auto"),
    DESERT_EAGLE("Desert Eagle"),
    DUAL_BERETTAS("Dual Berettas"),
    FIVE_SEVEN("Five-SeveN"),
    GLOCK_18("Glock-18"),
    P250("P250"),
    P2000("P2000"),
    R8_REVOLVER("R8 Revolver"),
    TEC_9("Tec-9"),
    USP_S("USP-S"),
    MAG_7("MAG-7"),
    NOVA("Nova"),
    SAWED_OFF("Sawed-Off"),
    XM1014("XM1014"),
    MAC_10("MAC-10"),
    MP7("MP7"),
    MP9("MP9"),
    P90("P90"),
    PP_BIZON("PP-Bizon"),
    UMP_45("UMP-45"),
    AK_47("AK-47"),
    AUG("AUG"),
    FAMAS("FAMAS"),
    GALIL_AR("Galil AR"),
    M4A1_S("M4A1-S"),
    M4A4("M4A4"),
    SG_553("SG 553"),
    AWP("AWP"),
    G3SG1("G3SG1"),
    SSG_08("SSG 08"),
    SCAR_20("SCAR-20"),
    M249("M249"),
    NEGEV("Negev");

    private String name;
    private static Set<String> names = new HashSet<>();

    Weapon(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static boolean isWeapon(String name) {
        return names.contains(name);
    }

    static {
        for (Weapon weapon : values()) {
            names.add(weapon.toString());
        }
    }

}
