package ninja.amp.tradeup.collection;

public class Range {

    private double min;
    private double max;
    private boolean includeMin;

    public Range(double min, double max, boolean includeMin) {
        this.min = min;
        this.max = max;
        this.includeMin = includeMin;
    }

    public Range(double min, double max) {
        this(min, max, true);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public boolean overlaps(Range range) {
        if (this.includeMin) {
            return this.min <= range.max && this.max >= range.min;
        } else {
            return this.min < range.max && this.max > range.min;
        }
    }

    public Range getOverlap(Range range) {
        boolean includeMin = (this.min >= range.min && this.includeMin) || (range.min >= this.min && range.includeMin);
        return new Range(Math.max(this.min, range.min), Math.min(this.max, range.max), includeMin);
    }

}
