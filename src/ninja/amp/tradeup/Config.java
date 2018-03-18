package ninja.amp.tradeup;

public class Config {

    public static String MARKET_API = "http://backpack.tf/api/IGetMarketPrices/v1/?key=569f2c2bba8d887142e4f7ef&appid=730";

    public static String WEAR_API = "http://www.csgozone.net/_service/user?type=marketInspect&link=";
    public static String AUTH_KEY = "";
    public static String TIMESTAMP = "";
    public static String STEAM_ID = "76561198035468569";
    public static String COOKIE = "auth=" + AUTH_KEY + "; timestamp=" + TIMESTAMP + "; steamid=" + STEAM_ID;
    public static String HOST = "www.csgozone.net";
    public static String REFERER = "http://www.csgozone.net/";

    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:45.0) Gecko/20100101 Firefox/45.0";
    public static String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

    public static String WEAPON = "(StatTrak™ )?([A-Za-z0-9- ]+)(\\|)([A-Za-z- ()'龍王壱弐]+)(\\()([A-Za-z- ]+)(\\))";
    public static String DATABASE = "./database.db";
    public static boolean UPDATE = true;

    public static float STEAM_TAX = 1 - 0.15f;
    public static int MAX_PRICE = 1000;
    public static int REQUIRED_QUANTITY = 50;
    public static float REQUIRED_PERCENT_PROFIT = 0.10f;
    public static int REQUIRED_FLAT_PROFIT = 100;
    public static float REQUIRED_LUCKY_PROFIT = 1.0f;

    //
    // GET http://www.csgozone.net/_service/user?type=marketInspect&link=(encoded inspect link)
    // Cookie: auth=(auth key); timestamp=(timestamp); steamid=(steamid)
    // Host: www.csgozone.net
    // Referer: http://www.csgozone.net/

}
