package ninja.amp.tradeup;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import ninja.amp.tradeup.collection.Collection;
import ninja.amp.tradeup.collection.Collections;
import ninja.amp.tradeup.collection.Exterior;
import ninja.amp.tradeup.collection.Quality;
import ninja.amp.tradeup.collection.Range;
import ninja.amp.tradeup.collection.Skin;
import ninja.amp.tradeup.collection.Weapon;

public final class MarketAnalyst {

    public static Comparator<Tradeup> FLAT = new Comparator<Tradeup>() {
        @Override
        public int compare(Tradeup t1, Tradeup t2) {
            if (t1.getFlatProfit() > t2.getFlatProfit()) {
                return 1;
            } else if (t1.getFlatProfit() < t2.getFlatProfit()) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    public static Comparator<Tradeup> PERCENT = new Comparator<Tradeup>() {
        @Override
        public int compare(Tradeup t1, Tradeup t2) {
            if (t1.getPercentProfit() > t2.getPercentProfit()) {
                return 1;
            } else if (t1.getPercentProfit() < t2.getPercentProfit()) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    public static Comparator<Tradeup> FLOAT = new Comparator<Tradeup>() {
        @Override
        public int compare(Tradeup t1, Tradeup t2) {
            Range i1 = t1.getItem().getExterior().getRange();
            Range o1 = t1.getRequiredRange().getOverlap(i1);
            double l1 = (o1.getMin() - i1.getMin()) / (i1.getMax() - i1.getMin());

            Range i2 = t2.getItem().getExterior().getRange();
            Range o2 = t2.getRequiredRange().getOverlap(i2);
            double l2 = (o2.getMin() - i2.getMin()) / (i2.getMax() - i2.getMin());

            if (l1 > l2) {
                return 1;
            } else if (l1 < l2) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    public static Comparator<Tradeup> PROBABILITY = new Comparator<Tradeup>() {
        @Override
        public int compare(Tradeup t1, Tradeup t2) {
            if (t1.getProbability() > t2.getProbability()) {
                return 1;
            } else if (t1.getProbability() < t2.getProbability()) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    public static Comparator<Tradeup> PRICE = new Comparator<Tradeup>() {
        @Override
        public int compare(Tradeup t1, Tradeup t2) {
            if (t1.getPrice() > t2.getPrice()) {
                return 1;
            } else if (t1.getPrice() < t2.getPrice()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    private MarketAnalyst() {
    }

    public static void main(String[] args) {
        SQLiteConnection db;
        try {
            db = connectDatabase();
        } catch (SQLiteException e) {
            System.out.println("Error opening connection to database");
            e.printStackTrace();
            return;
        }

        for (Collection collection : Collections.values()) {
            try {
                SQLiteStatement st = db.prepare(queryCollection(collection));
                try {
                    while (st.step()) {
                        boolean stattrak = st.columnInt(0) == 1;
                        Weapon weapon = Weapon.valueOf(st.columnString(1));
                        String name = st.columnString(2);
                        double min = st.columnDouble(3);
                        double max = st.columnDouble(4);
                        Quality quality = Quality.valueOf(st.columnString(5));
                        Skin skin = new Skin(weapon, name, quality, new Range(min, max));
                        Exterior exterior = Exterior.valueOf(st.columnString(6));
                        int quantity = st.columnInt(7);
                        int price = st.columnInt(8);
                        Item item = new Item(stattrak, skin, exterior, collection, quantity, price);
                        collection.addItem(item);
                    }
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                System.out.println("Error interacting with SQL database:");
                e.printStackTrace();
            }
        }

        List<Tradeup> tradeups = new ArrayList<>();
        for (Collection collection : Collections.values()) {
            for (Item item : collection.getItems()) {
                for (Tradeup tradeup : Tradeup.getTradeups(item)) {
                    if (tradeup.getPrice() <= Config.MAX_PRICE && (tradeup.isReasonable() || tradeup.isLucky())) {
                        tradeups.add(tradeup);
                    }
                }
            }
        }

        //tradeups.sort(new TradeupComparator(PROBABILITY, PERCENT, FLAT, FLOAT, PRICE)); // Most profitable
        tradeups.sort(new TradeupComparator(PROBABILITY, FLOAT, PERCENT, FLAT, PRICE)); // Most consistent
        //tradeups.sort(new TradeupComparator(FLAT, PROBABILITY, FLOAT, PERCENT, PRICE));

        for (Tradeup tradeup : tradeups) {
            System.out.println(tradeup);
        }

        db.dispose();
        System.out.println("Closed connection to database");
    }

    public static SQLiteConnection connectDatabase() throws SQLiteException {
        SQLiteConnection db = new SQLiteConnection(new File(Config.DATABASE));
        System.out.println("Opening database connection...");
        db.open(true);
        System.out.println("Connected to database");
        return db;
    }

    private static String queryCollection(Collection collection) {
        return "SELECT * FROM " + collection.getName() + ";";
    }

}
