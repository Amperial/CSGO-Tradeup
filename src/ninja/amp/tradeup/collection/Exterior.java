package ninja.amp.tradeup.collection;

public enum Exterior {
    BATTLE_SCARRED("Battle-Scarred", new Range(0.44, 1.00, false)),
    WELL_WORN("Well-Worn", new Range(0.38, 0.44, false)),
    FIELD_TESTED("Field-Tested", new Range(0.15, 0.38, false)),
    MINIMAL_WEAR("Minimal Wear", new Range(0.07, 0.15, false)),
    FACTORY_NEW("Factory New", new Range(0.0, 0.07));

    private String name;
    private Range range;
    private Exterior next;

    Exterior(String name, Range range) {
        this.name = name;
        this.range = range;
    }

    public Range getRange() {
        return this.range;
    }

    public Exterior getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Exterior getExterior(double wear) {
        if (wear > BATTLE_SCARRED.getRange().getMin()) {
            return BATTLE_SCARRED;
        } else if (wear > WELL_WORN.getRange().getMin()) {
            return WELL_WORN;
        } else if (wear > FIELD_TESTED.getRange().getMin()) {
            return FIELD_TESTED;
        } else if (wear > MINIMAL_WEAR.getRange().getMin()) {
            return MINIMAL_WEAR;
        } else {
            return FACTORY_NEW;
        }
    }

    static {
        BATTLE_SCARRED.next = WELL_WORN;
        WELL_WORN.next = FIELD_TESTED;
        FIELD_TESTED.next = MINIMAL_WEAR;
        MINIMAL_WEAR.next = FACTORY_NEW;
        FACTORY_NEW.next = null;
    }

}
