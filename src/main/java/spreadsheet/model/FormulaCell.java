package spreadsheet.model;

//  formula is kept
public class FormulaCell extends Cell {
    private final String formula;

    public FormulaCell(String formula) {
        this.formula = formula;
    }

    @Override
    public String getContent() {
        return formula;
    }
}