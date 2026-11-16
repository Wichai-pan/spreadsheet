package spreadsheet.model;

import java.util.regex.Pattern;

public class CellFactory {
    private static final Pattern NUMBER = Pattern.compile("[+-]?(\\d+(\\.\\d*)?|\\.\\d+)");

    // Returns null when the input is empty, which means "clear the cell".
    public static Cell create(String input) {
        String content = input.trim();
        if (content.isEmpty()) {
            return null;
        }
        if (content.startsWith("=")) {
            return new FormulaCell(content);
        }
        if (NUMBER.matcher(content).matches()) {
            double value = Double.parseDouble(content);
            if (Double.isFinite(value)) {
                return new NumberCell(value);
            }
        }
        return new TextCell(content);
    }
}