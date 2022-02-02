package nl.wernerdegroot.applicatives.runtime;

import java.util.function.BiFunction;

/**
 * This is a special little class, made for speed. It pretends to be generic tuple with an immutable
 * interface, but it uses mutation when it thinks nobody will notice. It is intended to use incrementally,
 * starting with two elements and adding extra elements, one at a time.
 * <p>
 * Check out the unit tests for examples on how to use this class, and what guarantees it offers when
 * you use it correctly.
 */
@SuppressWarnings("unchecked")
public class FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> {

    private static final int[] BITMAP_INDEX;

    static {
        int maxSize = 26;
        int acc = 1;
        BITMAP_INDEX = new int[maxSize];
        for (int i = 0; i < maxSize; ++i) {
            BITMAP_INDEX[i] = acc;
            acc = acc << 1;
        }
    }

    // The bits represent the elements of the tuple.
    // For example, when bits 1, 2, 3, 4 and 5 are set, this property will have the value
    // 0b00000000000000000000011111.
    private int elementsInitializedBitmap;

    // The maximum size that this tuple can grow to.
    private final int maxSize;

    // Instead of holding each element separately, we use an `Object` array for easy
    // and cheap copying.
    private final Object[] elements;

    private FastTuple(int elementsInitializedBitmap, int maxSize) {
        this.elementsInitializedBitmap = elementsInitializedBitmap;
        this.maxSize = maxSize;
        this.elements = new Object[maxSize];
    }

    public FastTuple(First first, Second second, int maxSize) {
        this(BITMAP_INDEX[0] | BITMAP_INDEX[1], maxSize);

        // Initialize the first two elements.
        this.elements[0] = first;
        this.elements[1] = second;
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> BiFunction<First, Second, FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>> withMaxSize(int maxSize) {
        return (first, second) -> new FastTuple<>(first, second, maxSize);
    }

    public First getFirst() {
        return (First) elements[0];
    }

    public Second getSecond() {
        return (Second) elements[1];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSecond(Second value) {
        return mutateOrCopy(1, value);
    }

    public Third getThird() {
        return (Third) elements[2];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withThird(Third value) {
        return mutateOrCopy(2, value);
    }

    public Fourth getFourth() {
        return (Fourth) elements[3];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFourth(Fourth value) {
        return mutateOrCopy(3, value);
    }

    public Fifth getFifth() {
        return (Fifth) elements[4];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFifth(Fifth value) {
        return mutateOrCopy(4, value);
    }

    public Sixth getSixth() {
        return (Sixth) elements[5];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSixth(Sixth value) {
        return mutateOrCopy(5, value);
    }

    public Seventh getSeventh() {
        return (Seventh) elements[6];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSeventh(Seventh value) {
        return mutateOrCopy(6, value);
    }

    public Eighth getEighth() {
        return (Eighth) elements[7];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEighth(Eighth value) {
        return mutateOrCopy(7, value);
    }

    public Ninth getNinth() {
        return (Ninth) elements[8];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withNinth(Ninth value) {
        return mutateOrCopy(8, value);
    }

    public Tenth getTenth() {
        return (Tenth) elements[9];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTenth(Tenth value) {
        return mutateOrCopy(9, value);
    }

    public Eleventh getEleventh() {
        return (Eleventh) elements[10];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEleventh(Eleventh value) {
        return mutateOrCopy(10, value);
    }

    public Twelfth getTwelfth() {
        return (Twelfth) elements[11];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwelfth(Twelfth value) {
        return mutateOrCopy(11, value);
    }

    public Thirteenth getThirteenth() {
        return (Thirteenth) elements[12];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withThirteenth(Thirteenth value) {
        return mutateOrCopy(12, value);
    }

    public Fourteenth getFourteenth() {
        return (Fourteenth) elements[13];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFourteenth(Fourteenth value) {
        return mutateOrCopy(13, value);
    }

    public Fifteenth getFifteenth() {
        return (Fifteenth) elements[14];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFifteenth(Fifteenth value) {
        return mutateOrCopy(14, value);
    }

    public Sixteenth getSixteenth() {
        return (Sixteenth) elements[15];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSixteenth(Sixteenth value) {
        return mutateOrCopy(15, value);
    }

    public Seventeenth getSeventeenth() {
        return (Seventeenth) elements[16];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSeventeenth(Seventeenth value) {
        return mutateOrCopy(16, value);
    }

    public Eighteenth getEighteenth() {
        return (Eighteenth) elements[17];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEighteenth(Eighteenth value) {
        return mutateOrCopy(17, value);
    }

    public Nineteenth getNineteenth() {
        return (Nineteenth) elements[18];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withNineteenth(Nineteenth value) {
        return mutateOrCopy(18, value);
    }

    public Twentieth getTwentieth() {
        return (Twentieth) elements[19];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentieth(Twentieth value) {
        return mutateOrCopy(19, value);
    }

    public TwentyFirst getTwentyFirst() {
        return (TwentyFirst) elements[20];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentyFirst(TwentyFirst value) {
        return mutateOrCopy(20, value);
    }

    public TwentySecond getTwentySecond() {
        return (TwentySecond) elements[21];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentySecond(TwentySecond value) {
        return mutateOrCopy(21, value);
    }

    public TwentyThird getTwentyThird() {
        return (TwentyThird) elements[22];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentyThird(TwentyThird value) {
        return mutateOrCopy(22, value);
    }

    public TwentyFourth getTwentyFourth() {
        return (TwentyFourth) elements[23];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentyFourth(TwentyFourth value) {
        return mutateOrCopy(23, value);
    }

    public TwentyFifth getTwentyFifth() {
        return (TwentyFifth) elements[24];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentyFifth(TwentyFifth value) {
        return mutateOrCopy(24, value);
    }

    public TwentySixth getTwentySixth() {
        return (TwentySixth) elements[25];
    }

    public FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentySixth(TwentySixth value) {
        return mutateOrCopy(25, value);
    }

    private FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> mutateOrCopy(int index, Object value) {
        FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> result;

        int elementBitmap = BITMAP_INDEX[index];

        // Check if the index-th element has already been set, by checking if
        // the corresponding bit in `elementsInitializedBitmap` is set to one.
        if ((elementsInitializedBitmap & elementBitmap) == 0) {

            // Make sure the bit corresponding to index-th element is set to one.
            // This applies to both `this` and the object returned (which also
            // happens to be `this`).
            elementsInitializedBitmap |= elementBitmap;

            // We are going to mutate and return this object.
            result = this;
        } else {

            // Since the index-th element was already set, we need to make a copy.
            result = new FastTuple<>(elementsInitializedBitmap, maxSize);
            System.arraycopy(elements, 0, result.elements, 0, index);
        }

        // Set the value and return.
        result.elements[index] = value;
        return result;
    }
}