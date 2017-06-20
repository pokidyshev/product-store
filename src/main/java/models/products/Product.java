package models.products;

import java.io.Serializable;

public abstract class Product implements Serializable {
    private long id;
    private double price;

    Product(long id, double price) {
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

    public abstract ProductType getType();

    @Override
    public String toString() {
        return id + "\t| " + price + "\t\t| ";
    }
}
