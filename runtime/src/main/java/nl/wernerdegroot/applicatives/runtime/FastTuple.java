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
public class FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> implements
        Tuple0,
        Tuple1<First>,
        Tuple2<First, Second>,
        Tuple3<First, Second, Third>,
        Tuple4<First, Second, Third, Fourth>,
        Tuple5<First, Second, Third, Fourth, Fifth>,
        Tuple6<First, Second, Third, Fourth, Fifth, Sixth>,
        Tuple7<First, Second, Third, Fourth, Fifth, Sixth, Seventh>,
        Tuple8<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth>,
        Tuple9<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth>,
        Tuple10<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth>,
        Tuple11<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh>,
        Tuple12<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth>,
        Tuple13<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth>,
        Tuple14<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth>,
        Tuple15<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth>,
        Tuple16<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth>,
        Tuple17<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth>,
        Tuple18<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth>,
        Tuple19<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth>,
        Tuple20<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth>,
        Tuple21<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst>,
        Tuple22<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond>,
        Tuple23<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird>,
        Tuple24<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth>,
        Tuple25<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth>,
        Tuple26<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> {

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

    FastTuple(Object[] elements) {
        this.elementsInitializedBitmap = (1 << (elements.length + 1)) - 1;
        this.maxSize = elements.length;
        this.elements = elements;
    }

    private FastTuple(int elementsInitializedBitmap, int maxSize) {
        this.elementsInitializedBitmap = elementsInitializedBitmap;
        this.maxSize = maxSize;
        this.elements = new Object[maxSize];
    }

    public FastTuple(int maxSize) {
        this(0, maxSize);
    }

    public FastTuple(First first, Second second, int maxSize) {
        this(BITMAP_INDEX[0] | BITMAP_INDEX[1], maxSize);

        // Initialize the first two elements.
        this.elements[0] = first;
        this.elements[1] = second;
    }

    public static Tuple0 emptyWithMaxSize(int maxSize) {
        return new FastTuple<>(maxSize);
    }

    public static <First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> BiFunction<First, Second, FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>> withMaxSize(int maxSize) {
        return (first, second) -> new FastTuple<>(first, second, maxSize);
    }

    public First getFirst() {
        return (First) elements[0];
    }

    @Override
    public <T> FastTuple<T, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFirst(T value) {
        return (FastTuple<T, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(0, value);
    }

    public Second getSecond() {
        return (Second) elements[1];
    }

    @Override
    public <T> FastTuple<First, T, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSecond(T value) {
        return (FastTuple<First, T, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(1, value);
    }

    public Third getThird() {
        return (Third) elements[2];
    }

    @Override
    public <T> FastTuple<First, Second, T, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withThird(T value) {
        return (FastTuple<First, Second, T, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(2, value);
    }

    public Fourth getFourth() {
        return (Fourth) elements[3];
    }

    @Override
    public <T> FastTuple<First, Second, Third, T, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFourth(T value) {
        return (FastTuple<First, Second, Third, T, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(3, value);
    }

    public Fifth getFifth() {
        return (Fifth) elements[4];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, T, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFifth(T value) {
        return (FastTuple<First, Second, Third, Fourth, T, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(4, value);
    }

    public Sixth getSixth() {
        return (Sixth) elements[5];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, T, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSixth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, T, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(5, value);
    }

    public Seventh getSeventh() {
        return (Seventh) elements[6];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, T, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSeventh(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, T, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(6, value);
    }

    public Eighth getEighth() {
        return (Eighth) elements[7];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, T, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEighth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, T, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(7, value);
    }

    public Ninth getNinth() {
        return (Ninth) elements[8];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, T, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withNinth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, T, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(8, value);
    }

    public Tenth getTenth() {
        return (Tenth) elements[9];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, T, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, T, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(9, value);
    }

    public Eleventh getEleventh() {
        return (Eleventh) elements[10];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, T, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEleventh(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, T, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(10, value);
    }

    public Twelfth getTwelfth() {
        return (Twelfth) elements[11];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, T, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwelfth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, T, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(11, value);
    }

    public Thirteenth getThirteenth() {
        return (Thirteenth) elements[12];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, T, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withThirteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, T, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(12, value);
    }

    public Fourteenth getFourteenth() {
        return (Fourteenth) elements[13];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, T, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFourteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, T, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(13, value);
    }

    public Fifteenth getFifteenth() {
        return (Fifteenth) elements[14];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, T, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withFifteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, T, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(14, value);
    }

    public Sixteenth getSixteenth() {
        return (Sixteenth) elements[15];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, T, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSixteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, T, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(15, value);
    }

    public Seventeenth getSeventeenth() {
        return (Seventeenth) elements[16];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, T, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withSeventeenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, T, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(16, value);
    }

    public Eighteenth getEighteenth() {
        return (Eighteenth) elements[17];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, T, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withEighteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, T, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(17, value);
    }

    public Nineteenth getNineteenth() {
        return (Nineteenth) elements[18];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, T, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withNineteenth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, T, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(18, value);
    }

    public Twentieth getTwentieth() {
        return (Twentieth) elements[19];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, T, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentieth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, T, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(19, value);
    }

    public TwentyFirst getTwentyFirst() {
        return (TwentyFirst) elements[20];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, T, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentyFirst(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, T, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(20, value);
    }

    public TwentySecond getTwentySecond() {
        return (TwentySecond) elements[21];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, T, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth> withTwentySecond(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, T, TwentyThird, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(21, value);
    }

    public TwentyThird getTwentyThird() {
        return (TwentyThird) elements[22];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, T, TwentyFourth, TwentyFifth, TwentySixth> withTwentyThird(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, T, TwentyFourth, TwentyFifth, TwentySixth>) mutateOrCopy(22, value);
    }

    public TwentyFourth getTwentyFourth() {
        return (TwentyFourth) elements[23];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, T, TwentyFifth, TwentySixth> withTwentyFourth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, T, TwentyFifth, TwentySixth>) mutateOrCopy(23, value);
    }

    public TwentyFifth getTwentyFifth() {
        return (TwentyFifth) elements[24];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, T, TwentySixth> withTwentyFifth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, T, TwentySixth>) mutateOrCopy(24, value);
    }

    public TwentySixth getTwentySixth() {
        return (TwentySixth) elements[25];
    }

    @Override
    public <T> FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, T> withTwentySixth(T value) {
        return (FastTuple<First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth, Thirteenth, Fourteenth, Fifteenth, Sixteenth, Seventeenth, Eighteenth, Nineteenth, Twentieth, TwentyFirst, TwentySecond, TwentyThird, TwentyFourth, TwentyFifth, T>) mutateOrCopy(25, value);
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