package ninja.amp.tradeup;

import ninja.amp.tradeup.collection.Collection;
import ninja.amp.tradeup.collection.Exterior;
import ninja.amp.tradeup.collection.Range;
import ninja.amp.tradeup.collection.Skin;

public class Item {

    private boolean stattrak;
    private Skin skin;
    private Exterior exterior;
    private Collection collection;

    private int quantity;
    private int price;

    public Item(boolean stattrak, Skin skin, Exterior exterior, Collection collection, int quantity, int price) {
        this.stattrak = stattrak;
        this.skin = skin;
        this.exterior = exterior;
        this.collection = collection;
        this.quantity = quantity;
        this.price = price;
    }

    public boolean isStatTrak() {
        return this.stattrak;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public Exterior getExterior() {
        return this.exterior;
    }

    public Collection getCollection() {
        return this.collection;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public boolean canProduce(Item i) {
        boolean quality = i.skin.getQuality() == this.skin.getQuality().getNext();
        boolean stattrak = i.stattrak == this.stattrak;

        return quality && stattrak && i.getRequiredRange().overlaps(this.exterior.getRange());
    }

    public Range getRequiredRange() {
        Range itemRange = this.exterior.getRange();
        double skinMin = this.skin.getRange().getMin();
        double skinRange = this.skin.getRange().getMax() - skinMin;
        return new Range((itemRange.getMin() - skinMin) / skinRange, (itemRange.getMax() - skinMin) / skinRange, false);
    }

    @Override
    public String toString() {
        return (this.stattrak ? "StatTrak™ " : "") + this.skin + " (" + this.exterior + ")";
    }

}
