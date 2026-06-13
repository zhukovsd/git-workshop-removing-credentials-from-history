package ru.prplhd.weather.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class CoordinateNormalizer {
    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal normalize(BigDecimal coordinate) {
        return coordinate.setScale(SCALE, ROUNDING_MODE);
    }
}
