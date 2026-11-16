package spreadsheet.storage;

import spreadsheet.model.Coordinate;
import spreadsheet.model.Spreadsheet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class S2VFile {

    public static void save(Spreadsheet sheet, Path path) throws IOException {
        StringBuilder text = new StringBuilder();
        for (int row = 1; row <= sheet.getLastRow(); row++) {
            text.append(rowToLine(sheet, row)).append("\n");
        }
        Files.writeString(path, text.toString(), StandardCharsets.UTF_8);
    }

    public static Spreadsheet load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        Spreadsheet sheet = new Spreadsheet();
        for (int i = 0; i < lines.size(); i++) {
            List<String> fields = splitLine(lines.get(i), i + 1);
            for (int j = 0; j < fields.size(); j++) {
                String field = fields.get(j);
                if (field.isEmpty()) {
                    continue;
                }
                if (field.trim().startsWith("=")) {
                    field = field.replace(',', ';');
                }
                sheet.setContent(new Coordinate(j + 1, i + 1), field);
            }
        }
        sheet.markSaved();
        return sheet;
    }

    private static String rowToLine(Spreadsheet sheet, int row) {
        List<String> fields = new ArrayList<>();
        for (int column = 1; column <= sheet.getLastColumn(); column++) {
            fields.add(encode(sheet.getContent(new Coordinate(column, row))));
        }
        // a line ends with the last non-empty cell
        while (!fields.isEmpty() && fields.get(fields.size() - 1).isEmpty()) {
            fields.remove(fields.size() - 1);
        }
        return String.join(";", fields);
    }

    private static String encode(String content) {
        if (content.startsWith("=")) {
            return content.replace(';', ',');
        }
        if (content.contains(";") || content.startsWith("\"")) {
            return "\"" + content.replace("\"", "\"\"") + "\"";
        }
        return content;
    }

    // Splits a line by ';'
    private static List<String> splitLine(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        int i = 0;
        while (true) {
            StringBuilder field = new StringBuilder();
            if (i < line.length() && line.charAt(i) == '"') {
                i++;
                while (true) {
                    if (i >= line.length()) {
                        throw new IOException("Line " + lineNumber + ": missing closing quote");
                    }
                    char c = line.charAt(i);
                    if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i += 2;
                    } else if (c == '"') {
                        i++;
                        break;
                    } else {
                        field.append(c);
                        i++;
                    }
                }
                if (i < line.length() && line.charAt(i) != ';') {
                    throw new IOException("Line " + lineNumber + ": text after closing quote");
                }
            } else {
                while (i < line.length() && line.charAt(i) != ';') {
                    field.append(line.charAt(i));
                    i++;
                }
            }
            fields.add(field.toString());
            if (i >= line.length()) {
                return fields;
            }
            i++; // skip ;
        }
    }
}