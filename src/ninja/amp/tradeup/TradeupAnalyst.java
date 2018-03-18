package ninja.amp.tradeup;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

public final class TradeupAnalyst {

    private TradeupAnalyst() {
    }

    public static void main(String[] args) {

    }

    private static String queryWear(String link) throws IOException {
        URL url = new URL(Config.WEAR_API + link);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", Config.USER_AGENT);
        connection.setRequestProperty("Accept", Config.ACCEPT);
        connection.setRequestProperty("Cookie", Config.COOKIE);
        connection.setRequestProperty("Host", Config.HOST);
        connection.setRequestProperty("Referer", Config.REFERER);
        return IOUtils.toString(connection.getInputStream());
    }

}
