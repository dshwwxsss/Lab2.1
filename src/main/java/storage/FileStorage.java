package storage;

import domain.*;
import validation.ValidationException;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.Locale;  // ← ДОБАВЛЕН ИМПОРТ

/**
 * Класс для сохранения и загрузки данных в CSV-файл.
 * Работает как "переводчик": Java-объекты ↔ Текст файла.
 */
public class FileStorage {

    // ===== СОХРАНЕНИЕ (ЗАПИСЬ В ФАЙЛ) =====

    /**
     * Сохраняет все данные (образцы, отчёты, строки) в файл по пути path.
     */
    public void saveAll(String path,
                        Set<Sample> samples,
                        Set<Report> reports,
                        Set<ReportLine> lines) throws IOException {
        // PrintWriter удобно записывает текст в файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            // 1. Секция образцов
            writer.println("#SAMPLES");
            writer.println("id,name"); // заголовок
            for (Sample s : samples) {
                // Экранируем имя на случай, если в нём есть запятая
                writer.printf("%d,%s%n", s.getId(), escapeCsv(s.getName()));
            }
            writer.println(); // пустая строка для разделения секций

            // 2. Секция отчётов
            writer.println("#REPORTS");
            writer.println("id,name,sampleId,experimentId,status,ownerUsername,signedBy,createdAt,updatedAt");
            for (Report r : reports) {
                writer.printf("%d,%s,%d,%d,%s,%s,%s,%s,%s%n",
                        r.getId(),
                        escapeCsv(r.getName()),
                        r.getSampleId(),
                        r.getExperimentId(),
                        r.getStatus().name(), // enum превращаем в строку "DRAFT"
                        escapeCsv(r.getOwnerUsername()),
                        escapeCsv(r.getSignedBy()),
                        CsvFormat.formatInstant(r.getCreatedAt()), // дату в строку
                        CsvFormat.formatInstant(r.getUpdatedAt())
                );
            }
            writer.println();

            // 3. Секция строк отчёта
            writer.println("#REPORT_LINES");
            writer.println("id,reportId,param,value,unit,createdAt,updatedAt");
            for (ReportLine l : lines) {
                // ← ГЛАВНОЕ ИСПРАВЛЕНИЕ: Locale.US гарантирует точку в десятичных дробях
                writer.printf(Locale.US, "%d,%d,%s,%.2f,%s,%s,%s%n",
                        l.getId(),
                        l.getReportId(),
                        l.getParam().name(),
                        l.getValue(),  // теперь 7.12, а не 7,12
                        escapeCsv(l.getUnit()),
                        CsvFormat.formatInstant(l.getCreatedAt()),
                        CsvFormat.formatInstant(l.getUpdatedAt())
                );
            }
        }
    }

    // ===== ЗАГРУЗКА (ЧТЕНИЕ ИЗ ФАЙЛА) =====

    /**
     * Контейнер для загруженных данных (возвращаем всё сразу).
     */
    public static class LoadedData {
        public final Set<Sample> samples;
        public final Set<Report> reports;
        public final Set<ReportLine> lines;

        public LoadedData(Set<Sample> samples, Set<Report> reports, Set<ReportLine> lines) {
            this.samples = samples;
            this.reports = reports;
            this.lines = lines;
        }
    }

    /**
     * Читает файл и возвращает данные во временных коллекциях.
     * Валидацию делает отдельный класс FileValidator.
     */
    public LoadedData loadAll(String path) throws IOException, ValidationException {
        Set<Sample> samples = new HashSet<>();
        Set<Report> reports = new HashSet<>();
        Set<ReportLine> lines = new HashSet<>();

        String currentSection = null;
        boolean headerSkipped = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Пропускаем пустые строки
                if (line.isEmpty()) continue;

                // Проверка на секцию (начинается с #)
                if (line.startsWith("#")) {
                    currentSection = line.substring(1).trim(); // убираем #
                    headerSkipped = false; // следующая строка — заголовок
                    continue;
                }

                // Пропускаем строку заголовка внутри секции
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                // Разбираем строку данных в зависимости от секции
                String[] parts = parseCsvLine(line);
                switch (currentSection) {
                    case "SAMPLES" -> samples.add(parseSample(parts));
                    case "REPORTS" -> reports.add(parseReport(parts));
                    case "REPORT_LINES" -> lines.add(parseReportLine(parts));
                    default -> {
                        // Неизвестная секция — игнорируем
                    }
                }
            }
        }

        return new LoadedData(samples, reports, lines);
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    // Если в строке есть запятая, оборачиваем в кавычки
    private String escapeCsv(String s) {
        if (s == null) return "";
        return s.contains(",") ? "\"" + s + "\"" : s;
    }

    // Разбирает строку CSV, учитывая кавычки (чтобы запятая внутри текста не ломала структуру)
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    // Превращает строку из CSV в объект Sample
    private Sample parseSample(String[] parts) {
        // Ожидаем: id,name
        long id = CsvFormat.parseLong(parts[0]);
        String name = unescapeCsv(parts[1]);
        return new Sample(id, name);
    }

    // Превращает строку из CSV в объект Report
    private Report parseReport(String[] parts) {
        // Ожидаем: id,name,sampleId,experimentId,status,ownerUsername,signedBy,createdAt,updatedAt

        // 1. Создаём объект через конструктор (обязательные поля)
        Report r = new Report(
                CsvFormat.parseLong(parts[0]),
                unescapeCsv(parts[1]),
                CsvFormat.parseLong(parts[2]),
                CsvFormat.parseLong(parts[3]),
                unescapeCsv(parts[5])
        );

        // 2. Восстанавливаем поля, которые конструктор ставит сам (статус, даты, подписант)
        r.setStatus(CsvFormat.parseEnum(parts[4], ReportStatus.class));
        r.setSignedBy(unescapeCsv(parts[6]));
        r.setCreatedAt(CsvFormat.parseInstant(parts[7]));
        r.setUpdatedAt(CsvFormat.parseInstant(parts[8]));

        return r;
    }

    // Превращает строку из CSV в объект ReportLine
    private ReportLine parseReportLine(String[] parts) {
        // Ожидаем: id,reportId,param,value,unit,createdAt,updatedAt

        // 1. Создаём объект
        ReportLine l = new ReportLine(
                CsvFormat.parseLong(parts[0]),
                CsvFormat.parseLong(parts[1]),
                CsvFormat.parseEnum(parts[2], MeasurementParam.class),
                CsvFormat.parseDouble(parts[3]),
                unescapeCsv(parts[4])
        );

        // 2. Восстанавливаем даты
        l.setCreatedAt(CsvFormat.parseInstant(parts[5]));
        l.setUpdatedAt(CsvFormat.parseInstant(parts[6]));

        return l;
    }

    // Убирает кавычки, если они были добавлены при сохранении
    private String unescapeCsv(String s) {
        if (s == null || s.isEmpty()) return null;
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() > 1) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}