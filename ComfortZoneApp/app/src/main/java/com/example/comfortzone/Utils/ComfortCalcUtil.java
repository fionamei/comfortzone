package com.example.comfortzone.Utils;

public class ComfortCalcUtil {
    public static final String KEY_PERFECT_COMFORT = "perfectComfort";
    public static final double LEVEL_ZERO_WEIGHT = 0.1;
    public static final double LEVEL_ONE_WEIGHT = 0.2;
    public static final double LEVEL_TWO_WEIGHT = 0.35;
    public static final double LEVEL_THREE_WEIGHT = 0.5;
    public static final double LEVEL_FOUR_WEIGHT = 1.0;
    public static final double LEVEL_FIVE_WEIGHT = 2.0;
    public static final double LEVEL_SIX_WEIGHT = 1.0;
    public static final double LEVEL_SEVEN_WEIGHT = 0.5;
    public static final double LEVEL_EIGHT_WEIGHT = 0.35;
    public static final double LEVEL_NINE_WEIGHT = 0.2;
    public static final double LEVEL_TEN_WEIGHT = 0.1;
    public static final int LEVEL_ZERO = 0;
    public static final int LEVEL_ONE = 1;
    public static final int LEVEL_TWO = 2;
    public static final int LEVEL_THREE = 3;
    public static final int LEVEL_FOUR = 4;
    public static final int LEVEL_FIVE = 5;
    public static final int LEVEL_SIX = 6;
    public static final int LEVEL_SEVEN = 7;
    public static final int LEVEL_EIGHT = 8;
    public static final int LEVEL_NINE = 9;
    public static final int LEVEL_TEN = 10;

    public static int initialComfortCalculator(int tempZero, int tempFive, int tempTen) {
        double zeroWeight = tempZero * LEVEL_ZERO_WEIGHT;
        double fiveWeight = tempFive * LEVEL_FIVE_WEIGHT;
        double tenWeight = tempTen * LEVEL_TEN_WEIGHT;
        double sum = zeroWeight + fiveWeight + tenWeight;
        double denom_sum = LEVEL_ZERO_WEIGHT + LEVEL_FIVE_WEIGHT + LEVEL_TEN_WEIGHT;
        return (int) (sum / denom_sum);
    }
}
