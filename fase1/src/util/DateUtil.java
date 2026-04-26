package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        if (date == null) return "null";
        return date.format(FORMATTER);
    }

    public static LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.equals("null")) return null;
        return LocalDate.parse(dateStr, FORMATTER);
    }

    public static long hitungSelisihHari(LocalDate dari, LocalDate ke) {
        return ChronoUnit.DAYS.between(dari, ke);
    }

    public static LocalDate hari ini() {
        return LocalDate.now();
    }
}