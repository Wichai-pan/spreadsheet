package spreadsheet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordinateTest {

    @Test
    void parsesColumnAndRow() {
        Coordinate c = Coordinate.parse("B3");
        assertEquals(2, c.getColumn());
        assertEquals(3, c.getRow());
    }

    @Test
    void acceptsLowerCase() {
        assertEquals(Coordinate.parse("AA10"), Coordinate.parse("aa10"));
    }

    @Test
    void convertsColumnNames() {
        assertEquals(1, Coordinate.columnNumber("A"));
        assertEquals(26, Coordinate.columnNumber("Z"));
        assertEquals(27, Coordinate.columnNumber("AA"));
        assertEquals("AB", Coordinate.columnName(28));
        assertEquals("ZZZ", Coordinate.columnName(Coordinate.MAX_COLUMN));
    }

    @Test
    void rejectsInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parse("A0"));
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parse("1A"));
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parse("A01"));
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parse("AAAA1"));
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parse("A1000000"));
    }
}