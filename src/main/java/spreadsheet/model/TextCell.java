package spreadsheet.model;

public class TextCell extends Cell {
    private final String text;

    public TextCell(String text) {
        this.text = text;
    }

    @Override
    public String getContent() {
        return text;
    }
}