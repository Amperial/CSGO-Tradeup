package ninja.amp.tradeup;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import ninja.amp.tradeup.collection.Collection;
import ninja.amp.tradeup.collection.Collections;
import ninja.amp.tradeup.collection.Exterior;
import ninja.amp.tradeup.collection.Skin;
import ninja.amp.tradeup.collection.Weapon;

public final class ItemUpdater {

    private ItemUpdater() {
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        SQLiteConnection db;
        try {
            db = MarketAnalyst.connectDatabase();
        } catch (SQLiteException e) {
            System.out.println("Error opening connection to database");
            e.printStackTrace();
            return;
        }

        try {
            if (Config.UPDATE) {
                JSONObject items;
                System.out.println("Querying market api...");
                String str = queryMarket();

                JSONObject json = (JSONObject) JSONValue.parse(str);
                JSONObject response = (JSONObject) json.get("response");
                if ((long)response.get("success") == 0) {
                    System.out.println("Failed to query market api: " + response.get("message"));
                } else {
                    items = (JSONObject) response.get("items");
                    System.out.println("Fetched list of market items");

                    System.out.println("Updating main table...");
                    db.exec(dropMain());
                    db.exec(createMain());
                    for (Map.Entry<String, JSONObject> entry : ((Set<Map.Entry<String, JSONObject>>) items.entrySet())) {
                        String hash = entry.getKey();
                        if (hash.matches(Config.WEAPON)) {
                            int bar = hash.indexOf('|');
                            String pre = hash.substring(0, bar - 1);
                            String post = hash.substring(bar + 2, hash.length());

                            boolean stattrak = false;
                            if (pre.startsWith("StatTrak™")) {
                                stattrak = true;
                                pre = pre.substring(10);
                            } else if (pre.startsWith("Souvenir")) {
                                continue;
                            }

                            String weapon;
                            if (Weapon.isWeapon(pre)) {
                                weapon = pre;
                            } else {
                                continue;
                            }

                            String skin, exterior;
                            Pattern p = Pattern.compile("\\((.*?)\\)");
                            Matcher m = p.matcher(post);
                            m.find(Math.max(0, post.length() - 17));
                            exterior = m.group(1);
                            skin = post.substring(0, post.length() - exterior.length() - 3);

                            long quantity, price;
                            JSONObject item = entry.getValue();
                            quantity = (long)item.get("quantity");
                            price = (long)item.get("value");

                            db.exec(insertMain(stattrak, weapon, skin, exterior, quantity, price));
                        }
                    }
                    System.out.println("Updated main table");
                }
            }

            System.out.println("Updating collection tables...");
            for (Collection collection : Collections.values()) {
                db.exec(dropCollection(collection));
                db.exec(createCollection(collection));

                for (Skin skin : collection.getSkins()) {
                    for (Exterior exterior : Exterior.values()) {
                        for (boolean stattrak : new boolean[]{true, false}) {
                            SQLiteStatement st = db.prepare(queryMain(stattrak, skin.getWeapon().toString(), skin.getName(), exterior.toString()));
                            try {
                                if (st.step()) {
                                    int quantity = st.columnInt(0);
                                    int price = st.columnInt(1);
                                    Item item = new Item(stattrak, skin, exterior, collection, quantity, price);
                                    db.exec(insertCollection(item));
                                }
                            } finally {
                                st.dispose();
                            }
                        }
                    }
                }
                System.out.println("Updated collection table " + collection.getName());
            }
            System.out.println("Finished updating collection tables");
        } catch (SQLiteException e) {
            System.out.println("Error interacting with SQL database:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to query market api:");
            e.printStackTrace();
        }

        db.dispose();
        System.out.println("Closed connection to database");
    }

    private static String queryMarket() throws IOException {
        URL url = new URL(Config.MARKET_API);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", Config.USER_AGENT);
        connection.setRequestProperty("Accept", Config.ACCEPT);
        return IOUtils.toString(connection.getInputStream());
    }

    private static String createMain() {
        return "CREATE TABLE MAIN("
                + "Stattrak BOOLEAN NOT NULL CHECK (Stattrak IN (0,1)), "
                + "Weapon   TEXT    NOT NULL, "
                + "Skin     TEXT    NOT NULL, "
                + "Exterior TEXT    NOT NULL, "
                + "Quantity INTEGER NOT NULL, "
                + "Price    INTEGER NOT NULL);";
    }

    private static String dropMain() {
        return "DROP TABLE IF EXISTS MAIN;";
    }

    private static String insertMain(boolean stattrak, String weapon, String skin, String exterior, long quantity, long price) {
        return "INSERT INTO MAIN VALUES("
                + (stattrak ? 1 : 0) + ", "
                + "'" + weapon + "', "
                + "'" + skin.replace("'", "''") + "', "
                + "'" + exterior + "', "
                + quantity + ", "
                + price + ");";
    }

    private static String queryMain(boolean stattrak, String weapon, String skin, String exterior) {
        return "SELECT Quantity, Price FROM MAIN WHERE "
                + "Stattrak = '" + (stattrak ? 1 : 0) + "' AND "
                + "Weapon = '" + weapon + "' AND "
                + "Skin = '" + skin.replace("'", "''") + "' AND "
                + "Exterior = '" + exterior + "';";
    }

    private static String createCollection(Collection collection) {
        return "CREATE TABLE IF NOT EXISTS " + collection.getName() + "("
                + "Stattrak BOOLEAN NOT NULL CHECK (Stattrak IN (0,1)), "
                + "Weapon   TEXT    NOT NULL, "
                + "Skin     TEXT    NOT NULL, "
                + "Min      REAL    NOT NULL, "
                + "Max      REAL    NOT NULL, "
                + "Quality  TEXT    NOT NULL, "
                + "Exterior TEXT    NOT NULL, "
                + "Quantity INTEGER NOT NULL, "
                + "Price    INTEGER NOT NULL);";
    }

    private static String dropCollection(Collection collection) {
        return "DROP TABLE IF EXISTS " + collection.getName() + ";";
    }

    private static String insertCollection(Item item) {
        return "INSERT INTO " + item.getCollection().getName() + " VALUES("
                + (item.isStatTrak() ? 1 : 0) + ", "
                + "'" + item.getSkin().getWeapon().name() + "', "
                + "'" + item.getSkin().getName().replace("'", "''") + "', "
                + item.getSkin().getRange().getMin() + ", "
                + item.getSkin().getRange().getMax() + ", "
                + "'" + item.getSkin().getQuality().name() + "', "
                + "'" + item.getExterior().name() + "', "
                + item.getQuantity() + ", "
                + item.getPrice() + ");";
    }

}
