package models.products;

public class Laptop extends Product {
    private String model;
    private int cores;

    public Laptop(long id, double price, String model, int cores) {
        super(id, price);
        this.model = model;
        this.cores = cores;
    }

    public String getModel() {
        return model;
    }
    public int getCores() {
        return cores;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public void setCores(int cores) {
        this.cores = cores;
    }

    @Override
    public String toString() {
        return super.toString() + model + "\t| " + cores + "\t| LAPTOP";
    }

    @Override
    public ProductType getType() {
        return ProductType.LAPTOP;
    }
}
