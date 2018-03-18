package ninja.amp.tradeup.collection;

public class Skin {

    private Weapon weapon;
    private String name;
    private Quality quality;
    private Range range;

    public Skin(Weapon weapon, String name, Quality quality, Range range) {
        this.weapon = weapon;
        this.name = name;
        this.quality = quality;
        this.range = range;
    }

    public Skin(Weapon weapon, String name, Quality quality) {
        this.weapon = weapon;
        this.name = name;
        this.quality = quality;
        this.range = new Range(0.0, 1.0);
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public String getName() {
        return this.name;
    }

    public Quality getQuality() {
        return this.quality;
    }

    public Range getRange() {
        return this.range;
    }

    @Override
    public String toString() {
        return this.weapon + " | " + this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.weapon == null) ? 0 : this.weapon.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Skin other = (Skin) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.weapon != other.weapon) {
            return false;
        }
        return true;
    }


}
