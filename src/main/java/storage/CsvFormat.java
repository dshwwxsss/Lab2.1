package storage;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

//преобразование типов Java в строки CSV и обратно
public class CsvFormat {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_INSTANT;


    public static String formatInstant(Instant instant) {
        if (instant == null) return "";
        return FMT.format(instant);
    }

    public static Instant parseInstant(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return Instant.parse(s.trim());
    }


    public static String formatEnum(Enum<?> e) {
        if (e == null) return "";
        return e.name();
    }

    public static <T extends Enum<T>> T parseEnum(String s, Class<T> enumClass) {
        if (s == null || s.trim().isEmpty()) return null;
        return Enum.valueOf(enumClass, s.trim().toUpperCase());
    }


    public static long parseLong(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Long.parseLong(s.trim());
    }

    public static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        return Double.parseDouble(s.trim());
    }
}