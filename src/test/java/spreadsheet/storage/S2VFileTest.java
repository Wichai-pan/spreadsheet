package spreadsheet.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spreadsheet.model.Coordinate;
import spreadsheet.model.Spreadsheet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class S2VFileTest {

    @TempDir
    Path folder;

    @Test
    void savesTheExampleOfTheStatement() throws IOException {
        Spreadsheet sheet = new Spreadsheet();
        sheet.setContent(Coordinate.parse("A1"), "=C1+C2");
        sheet.setContent(Coordinate.parse("B1"), "4");
        sheet.setContent(Coordinate.parse("C1"), "1");
        sheet.setContent(Coordinate.parse("C2"), "2");
        sheet.setContent(Coordinate.parse("B3"), "TOTAL");
        sheet.setContent(Coordinate.parse("C3"), "=A1+B1");

        Path file = folder.resolve("example.s2v");
        S2VFile.save(sheet, file);

        assertEquals("=C1+C2;4;1\n;;2\n;TOTAL;=A1+B1\n", Files.readString(file));
    }

    @Test
    void loadGivesBackTheSameContents() throws IOException {
        Spreadsheet sheet = new Spreadsheet();
        sheet.setContent(Coordinate.parse("A1"), "=SUM(A2;B2;3)");
        sheet.setContent(Coordinate.parse("B1"), "a;b");
        sheet.setContent(Coordinate.parse("C1"), "\"quoted\"");
        sheet.setContent(Coordinate.parse("A3"), "10000000");
        sheet.setContent(Coordinate.parse("D4"), "0.25");

        Path file = folder.resolve("round.s2v");
        S2VFile.save(sheet, file);
        Spreadsheet loaded = S2VFile.load(file);

        for (String cell : new String[]{"A1", "B1", "C1", "A3", "D4", "B2", "E9"}) {
            Coordinate c = Coordinate.parse(cell);
            assertEquals(sheet.getContent(c), loaded.getContent(c), cell);
        }
    }

    @Test
    void functionArgumentsAreStoredWithCommas() throws IOException {
        Path file = folder.resolve("f.s2v");
        Files.writeString(file, "=MAX(A2,B2,100);x\r\n");
        Spreadsheet loaded = S2VFile.load(file);
        assertEquals("=MAX(A2;B2;100)", loaded.getContent(Coordinate.parse("A1")));
        assertEquals("x", loaded.getContent(Coordinate.parse("B1")));
    }

    @Test
    void reportsMissingClosingQuote() throws IOException {
        Path file = folder.resolve("bad.s2v");
        Files.writeString(file, "\"abc;1\n");
        assertThrows(IOException.class, () -> S2VFile.load(file));
    }
}