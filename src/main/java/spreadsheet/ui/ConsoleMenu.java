package spreadsheet.ui;

import spreadsheet.model.Coordinate;
import spreadsheet.model.Spreadsheet;
import spreadsheet.storage.S2VFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private Spreadsheet sheet = new Spreadsheet();

    public void run() {
        while (true) {
            System.out.println();
            System.out.println("1. New spreadsheet");
            System.out.println("2. Set cell content");
            System.out.println("3. View cell content");
            System.out.println("4. Show spreadsheet");
            System.out.println("5. Load spreadsheet");
            System.out.println("6. Save spreadsheet");
            System.out.println("7. Save spreadsheet as");
            System.out.println("0. Exit");
            String option = ask("Option: ");
            try {
                switch (option) {
                    case "1" -> newSpreadsheet();
                    case "2" -> setCellContent();
                    case "3" -> viewCellContent();
                    case "4" -> showSpreadsheet();
                    case "5" -> load();
                    case "6" -> save();
                    case "7" -> saveAs();
                    case "0" -> {
                        if (canDiscardChanges()) {
                            return;
                        }
                    }
                    default -> System.out.println("Unknown option");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void newSpreadsheet() {
        if (canDiscardChanges()) {
            sheet = new Spreadsheet();
            System.out.println("New empty spreadsheet created");
        }
    }

    private void setCellContent() {
        Coordinate coordinate = Coordinate.parse(ask("Cell: "));
        String current = sheet.getContent(coordinate);
        System.out.println("Current content: " + (current.isEmpty() ? "(empty)" : current));
        sheet.setContent(coordinate, ask("New content: "));
        System.out.println(coordinate + " updated");
    }

    private void viewCellContent() {
        Coordinate coordinate = Coordinate.parse(ask("Cell: "));
        String content = sheet.getContent(coordinate);
        System.out.println(coordinate + ": " + (content.isEmpty() ? "(empty)" : content));
    }

    private void showSpreadsheet() {
        if (sheet.isEmpty()) {
            System.out.println("The spreadsheet is empty");
            return;
        }
        System.out.printf("%-5s", "");
        for (int column = 1; column <= sheet.getLastColumn(); column++) {
            System.out.printf("%-12s", Coordinate.columnName(column));
        }
        System.out.println();
        for (int row = 1; row <= sheet.getLastRow(); row++) {
            System.out.printf("%-5d", row);
            for (int column = 1; column <= sheet.getLastColumn(); column++) {
                System.out.printf("%-12s", sheet.getContent(new Coordinate(column, row)));
            }
            System.out.println();
        }
    }

    private void load() {
        if (!canDiscardChanges()) {
            return;
        }
        String fileName = ask("File to load: ");
        try {
            Spreadsheet loaded = S2VFile.load(Path.of(fileName));
            loaded.setFileName(fileName);
            sheet = loaded;
            System.out.println("Loaded " + fileName);
        } catch (IOException e) {
            System.out.println("Could not load " + fileName + ": " + e.getMessage());
        }
    }

    private boolean save() {
        if (sheet.getFileName() == null) {
            return saveAs();
        }
        return writeFile(sheet.getFileName());
    }

    private boolean saveAs() {
        String fileName = ask("File to save: ");
        if (fileName.isEmpty()) {
            System.out.println("Save cancelled");
            return false;
        }
        if (Files.exists(Path.of(fileName)) && !ask("File exists. Overwrite? (y/n): ").equals("y")) {
            System.out.println("Save cancelled");
            return false;
        }
        return writeFile(fileName);
    }

    private boolean writeFile(String fileName) {
        try {
            S2VFile.save(sheet, Path.of(fileName));
            sheet.setFileName(fileName);
            sheet.markSaved();
            System.out.println("Saved to " + fileName);
            return true;
        } catch (IOException e) {
            System.out.println("Could not save " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    // Ask
    private boolean canDiscardChanges() {
        if (!sheet.hasUnsavedChanges()) {
            return true;
        }
        String answer = ask("There are unsaved changes. Save them? (y/n/c): ");
        if (answer.equals("y")) {
            return save();
        }
        return answer.equals("n");
    }

    private String ask(String question) {
        System.out.print(question);
        if (!scanner.hasNextLine()) {
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }
}