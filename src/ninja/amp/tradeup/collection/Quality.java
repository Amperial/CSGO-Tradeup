package ninja.amp.tradeup.collection;

public enum Quality {
    CONSUMER,
    INDUSTRIAL,
    MIL_SPEC,
    RESTRICTED,
    CLASSIFIED,
    COVERT;

    private Quality next;

    public Quality getNext() {
        return this.next;
    }

    static {
        CONSUMER.next = INDUSTRIAL;
        INDUSTRIAL.next = MIL_SPEC;
        MIL_SPEC.next = RESTRICTED;
        RESTRICTED.next = CLASSIFIED;
        CLASSIFIED.next = COVERT;
        COVERT.next = null;
    }

}
