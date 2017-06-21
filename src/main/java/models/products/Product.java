package models.products;

import java.io.Serializable;

public class Product implements Serializable {
    private long id;
    private double price;

    public Product(long id, double price) {
        this.id = id;
        this.price = price;
    }

    public long getId() {
        return id;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProductType getType() {
        return ProductType.NONE;
    }

    @Override
    public String toString() {
        return id + "\t| " + price + "\t\t| ";
    }
}
