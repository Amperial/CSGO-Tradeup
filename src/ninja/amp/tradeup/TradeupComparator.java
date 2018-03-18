package ninja.amp.tradeup;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TradeupComparator implements Comparator<Tradeup> {

    private List<Comparator<Tradeup>> comparators;

    @SafeVarargs
    public TradeupComparator(Comparator<Tradeup>... comparators) {
        this.comparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(Tradeup o1, Tradeup o2) {
        for (Comparator<Tradeup> comparator : this.comparators) {
            int compare = comparator.compare(o1, o2);
            if (compare != 0) {
                return compare;
            }

        }
        return 0;
    }

}
