package models.products;

public class Book extends Product {
    private String title;
    private int pages;

    public Book(long id, double price, String title, int pages) {
        super(id, price);
        this.title = title;
        this.pages = pages;
    }

    public String getTitle() {
        return title;
    }
    public int getPages() {
        return pages;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return super.toString() + title + "\t| " + pages + "\t| BOOK";
    }

    @Override
    public ProductType getType() {
        return ProductType.BOOK;
    }
}
