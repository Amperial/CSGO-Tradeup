package ninja.amp.tradeup;

import java.util.Scanner;

public class CSGOTradeup {

    public static void main(String[] args) {
        System.out.println("==== CSGOTradeup ====");
        System.out.println("Commands:");
        System.out.println("update");
        System.out.println("tradeups");
        System.out.println("tradeup <tradeup>");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("Enter a command:");

            String command = scanner.next();

            System.out.println("Command input: " + command);
        }
    }

}
