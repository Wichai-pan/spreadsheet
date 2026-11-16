package spreadsheet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class CellFactoryTest {

    @Test
    void detectsNumbers() {
        assertInstanceOf(NumberCell.class, CellFactory.create("4"));
        assertInstanceOf(NumberCell.class, CellFactory.create(" -1.5 "));
        assertEquals("7", CellFactory.create("007").getContent());
        assertEquals("0.5", CellFactory.create(".5").getContent());
    }

    @Test
    void detectsFormulas() {
        Cell cell = CellFactory.create("=A1+B1");
        assertInstanceOf(FormulaCell.class, cell);
        assertEquals("=A1+B1", cell.getContent());
    }

    @Test
    void everythingElseIsText() {
        assertInstanceOf(TextCell.class, CellFactory.create("TOTAL"));
        assertInstanceOf(TextCell.class, CellFactory.create("1e3"));
        assertInstanceOf(TextCell.class, CellFactory.create("NaN"));
        assertInstanceOf(TextCell.class, CellFactory.create("1,5"));
    }

    @Test
    void emptyInputClearsTheCell() {
        assertNull(CellFactory.create("   "));
    }
}