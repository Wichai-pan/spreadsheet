package spreadsheet.model;

import java.math.BigDecimal;

public class NumberCell extends Cell {
    private final double value;

    public NumberCell(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    // 7.0 -> "7", 0.5 -> "0.5", 1e7 -> "10000000" (never "1.0E7")
    @Override
    public String getContent() {
        if (value == 0) {
            return "0";
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}