package ninja.amp.tradeup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import ninja.amp.tradeup.collection.Range;
import ninja.amp.tradeup.collection.Skin;

public class Tradeup {

    private Item item;
    private List<Item> outcomes;

    private int price;
    private float profit;
    private float probability;
    private boolean lucky;
    private Range range;

    private Tradeup(Item item, List<Item> outcomes) {
        this.item = item;
        this.outcomes = outcomes;

        this.price = item.getPrice() * 10;
        float expected = 0;
        int positive = 0;
        this.range = new Range(0, 1);
        for (Item i : this.outcomes) {
            float difference = (i.getPrice() * Config.STEAM_TAX) - this.price;
            expected += difference;
            if (difference > 0) {
                positive++;
            }
            if (difference / this.price > Config.REQUIRED_LUCKY_PROFIT) {
                this.lucky = true;
            }
            Range required = i.getRequiredRange();
            if (required.getMin() > this.range.getMin()) {
                this.range = new Range(required.getMin(), this.range.getMax());
            }
            if (required.getMax() < this.range.getMax()) {
                this.range = new Range(this.range.getMin(), required.getMax());
            }
        }
        if (this.outcomes.size() > 2) {
            this.lucky = false;
        }
        this.profit = expected / this.outcomes.size();
        this.probability = (float)positive / (float)this.outcomes.size();

    }

    public Item getItem() {
        return this.item;
    }

    public int getPrice() {
        return this.price;
    }

    public float getFlatProfit() {
        return this.profit;
    }

    public float getPercentProfit() {
        return 100 * this.profit / this.price;
    }

    public float getProbability() {
        return this.probability;
    }

    public Range getRequiredRange() {
        return this.range;
    }

    public boolean isReasonable() {
        int profitableQuantity = (Math.round(1f / this.probability) * Config.REQUIRED_QUANTITY);
        return this.price <= Config.MAX_PRICE &&
                this.profit > Config.REQUIRED_FLAT_PROFIT &&
                this.profit / this.price > Config.REQUIRED_PERCENT_PROFIT &&
                this.item.getQuantity() >= profitableQuantity;
    }

    public boolean isLucky() {
        return this.lucky;
    }

    @Override
    public String toString() {
        String str = "==================================================\n"
                + this.item.toString() + "\nTotal Price: $" + ((float)this.price)/100
                + "\nExpected Profit: $" + (this.getFlatProfit())/100 + " (" + this.getPercentProfit() + "%)"
                + "\nAverage Float Required: " + this.range.getMin() + " - " + this.range.getMax()
                + "\nPossible Outcomes:";

        for (Item i : this.outcomes) {
            float salePrice = i.getPrice() * Config.STEAM_TAX;
            float percent = 100 * (salePrice - this.price) / this.price;
            str += "\n" + i + ": $" + salePrice/100 + " (" + percent + "%)";
        }

        return str;
    }

    public static Set<Tradeup> getTradeups(Item item) {
        Set<Tradeup> tradeups = new HashSet<>();

        Map<Skin, Set<Item>> skins = new HashMap<>();
        for (Item i : item.getCollection().getItems()) {
            if (item.canProduce(i)) {
                Skin skin = i.getSkin();
                if (!skins.containsKey(skin)) {
                    skins.put(skin, new HashSet<>());
                }
                skins.get(skin).add(i);
            }
        }
        List<Set<Item>> sets = new ArrayList<>();
        for (Set<Item> skin : skins.values()) {
            sets.add(skin);
        }

        for (List<Item> outcomes : Sets.cartesianProduct(sets)) {
            if (isPossible(item, outcomes)) {
                tradeups.add(new Tradeup(item, outcomes));
            }
        }

        return tradeups;
    }

    private static boolean isPossible(Item item, List<Item> outcomes) {
        Range range = item.getExterior().getRange();
        for (Item i : outcomes) {
            if (i.getRequiredRange().overlaps(range)) {
                range = i.getRequiredRange().getOverlap(range);
            } else {
                return false;
            }
        }
        return true;
    }

}
