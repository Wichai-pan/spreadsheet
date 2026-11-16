package spreadsheet.model;

import java.util.HashMap;
import java.util.Map;

public class Spreadsheet {
    private final Map<Coordinate, Cell> cells = new HashMap<>();
    private String fileName;
    private boolean unsavedChanges;

    public void setContent(Coordinate coordinate, String input) {
        Cell cell = CellFactory.create(input);
        if (cell == null) {
            cells.remove(coordinate);
        } else {
            cells.put(coordinate, cell);
        }
        unsavedChanges = true;
    }

    // Empty string
    public String getContent(Coordinate coordinate) {
        Cell cell = cells.get(coordinate);
        if (cell == null) {
            return "";
        }
        return cell.getContent();
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    public int getLastRow() {
        int last = 0;
        for (Coordinate c : cells.keySet()) {
            last = Math.max(last, c.getRow());
        }
        return last;
    }

    public int getLastColumn() {
        int last = 0;
        for (Coordinate c : cells.keySet()) {
            last = Math.max(last, c.getColumn());
        }
        return last;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    public void markSaved() {
        unsavedChanges = false;
    }
}