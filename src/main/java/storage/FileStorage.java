package storage;

import domain.*;
import validation.ValidationException;
import java.io.*;
import java.util.*;
import java.util.Locale;

//Класс для сохранения и загрузки данных в CSV-файл.
public class FileStorage {
    public void saveAll(String path,
                        Set<Sample> samples,
                        Set<Report> reports,
                        Set<ReportLine> lines) throws IOException { //Input/Output Exception
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) { // рабочий, который берет path (путь), идет к операционной системе и открывает файл для записи
            writer.println("#SAMPLES");
            writer.println("id,name");
            for (Sample s : samples) {
                writer.printf("%d,%s%n", s.getId(), escapeCsv(s.getName()));
            }
            writer.println();

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

            writer.println("#REPORT_LINES");
            writer.println("id,reportId,param,value,unit,createdAt,updatedAt");
            for (ReportLine l : lines) {
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

    //ЧТЕНИЕ ИЗ ФАЙЛА
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

    public LoadedData loadAll(String path) throws IOException, ValidationException {
        Set<Sample> samples = new HashSet<>();
        Set<Report> reports = new HashSet<>();
        Set<ReportLine> lines = new HashSet<>();

        String currentSection = null; //название секции
        boolean headerSkipped = false; //для пропуска строк с названиями

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) {
                    currentSection = line.substring(1).trim();
                    headerSkipped = false;
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = parseCsvLine(line);
                switch (currentSection) {
                    case "SAMPLES" -> samples.add(parseSample(parts));
                    case "REPORTS" -> reports.add(parseReport(parts));
                    case "REPORT_LINES" -> lines.add(parseReportLine(parts));
                    default -> {
                        // неизвестная секция — игнорируем
                    }
                }
            }
        }

        return new LoadedData(samples, reports, lines);
    }
    //запятая
    private String escapeCsv(String s) {
        if (s == null) return "";
        return s.contains(",") ? "\"" + s + "\"" : s;
    }

    // учитываем кавычки
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();//черновик
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

    // превращает строку из CSV в объект Sample
    private Sample parseSample(String[] parts) {
        long id = CsvFormat.parseLong(parts[0]);
        String name = unescapeCsv(parts[1]);
        return new Sample(id, name);
    }

    // превращает строку из CSV в объект Report
    private Report parseReport(String[] parts) {
        Report r = new Report(
                CsvFormat.parseLong(parts[0]),//id
                unescapeCsv(parts[1]),//name
                CsvFormat.parseLong(parts[2]),//id sample
                CsvFormat.parseLong(parts[3]),//id ex
                unescapeCsv(parts[5])//login
        );
        r.setStatus(CsvFormat.parseEnum(parts[4], ReportStatus.class));
        r.setSignedBy(unescapeCsv(parts[6]));
        r.setCreatedAt(CsvFormat.parseInstant(parts[7]));
        r.setUpdatedAt(CsvFormat.parseInstant(parts[8]));
        return r;
    }

    private ReportLine parseReportLine(String[] parts) {
        ReportLine l = new ReportLine(
                CsvFormat.parseLong(parts[0]),//id
                CsvFormat.parseLong(parts[1]),//id report
                CsvFormat.parseEnum(parts[2], MeasurementParam.class),//param
                CsvFormat.parseDouble(parts[3]),//value
                unescapeCsv(parts[4])//unit
        );
        l.setCreatedAt(CsvFormat.parseInstant(parts[5]));
        l.setUpdatedAt(CsvFormat.parseInstant(parts[6]));

        return l;
    }
    //убирает кавычки
    private String unescapeCsv(String s) {
        if (s == null || s.isEmpty()) return null;
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() > 1) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}