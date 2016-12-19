package ep.rest;

import java.util.Locale;

public class Book {
    public int id, year;
    public String author, title, uri;
    public double price;

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s: %s, %d (%.2f EUR)",
                author, title, year, price);
    }
}
