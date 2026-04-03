package storage;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Вспомогательный класс для преобразования типов Java в строки CSV и обратно.
 * Нужен, чтобы не писать одно и то же преобразование в разных местах.
 */
public class CsvFormat {
    // Формат даты ISO (например: 2026-04-01T10:00:00Z)
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_INSTANT;

    // === Instant (Дата/Время) ===

    // Превращает дату в строку для записи в файл
    public static String formatInstant(Instant instant) {
        if (instant == null) return "";
        return FMT.format(instant);
    }

    // Превращает строку из файла обратно в дату
    public static Instant parseInstant(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return Instant.parse(s.trim());
    }

    // === Enum (Перечисления: статусы, параметры) ===

    // Превращает enum (например ReportStatus.DRAFT) в строку "DRAFT"
    public static String formatEnum(Enum<?> e) {
        if (e == null) return "";
        return e.name();
    }

    // Превращает строку "DRAFT" обратно в enum ReportStatus
    public static <T extends Enum<T>> T parseEnum(String s, Class<T> enumClass) {
        if (s == null || s.trim().isEmpty()) return null;
        return Enum.valueOf(enumClass, s.trim().toUpperCase());
    }

    // === Числа ===

    // Строка -> long (для ID)
    public static long parseLong(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Long.parseLong(s.trim());
    }

    // Строка -> double (для значений измерений)
    public static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        return Double.parseDouble(s.trim());
    }
}