package spreadsheet.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinate {
    public static final int MAX_COLUMN = 18278; // ZZZ
    public static final int MAX_ROW = 999999;

    private static final Pattern FORMAT = Pattern.compile("([A-Z]{1,3})([1-9][0-9]*)");

    private final int column;
    private final int row;

    public Coordinate(int column, int row) {
        if (column < 1 || column > MAX_COLUMN || row < 1 || row > MAX_ROW) {
            throw new IllegalArgumentException("Coordinate out of range");
        }
        this.column = column;
        this.row = row;
    }

    public static Coordinate parse(String text) {
        Matcher matcher = FORMAT.matcher(text.trim().toUpperCase());
        if (!matcher.matches() || matcher.group(2).length() > 6) {
            throw new IllegalArgumentException("Invalid coordinate: " + text);
        }
        return new Coordinate(columnNumber(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    // A -> 1, Z -> 26, AA -> 27
    public static int columnNumber(String letters) {
        int number = 0;
        for (char c : letters.toCharArray()) {
            number = number * 26 + (c - 'A' + 1);
        }
        return number;
    }

    public static String columnName(int number) {
        String name = "";
        while (number > 0) {
            number--;
            name = (char) ('A' + number % 26) + name;
            number = number / 26;
        }
        return name;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Coordinate)) {
            return false;
        }
        Coordinate c = (Coordinate) other;
        return column == c.column && row == c.row;
    }

    @Override
    public int hashCode() {
        return column * 1000003 + row;
    }

    @Override
    public String toString() {
        return columnName(column) + row;
    }
}