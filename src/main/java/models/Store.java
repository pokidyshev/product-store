package models;

import models.products.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Store {

    // region Private Fields

    TreeMap<Long, Product> productMap = new TreeMap<>();

    // endregion



    // region Public API

    public int count() {
        return productMap.size();
    }

    public Product get(long i) {
        return productMap.get(i);
    }

    public void put(Product product) {
        productMap.put(product.getId(), product);
    }

    public boolean hasProductWithId(Long id) { return productMap.containsKey(id); }

    public void remove(long fromId, long toId) {
        for (; fromId <= toId; ++fromId) {
            productMap.remove(fromId);
        }
    }

    public Product remove(long id) {
        return productMap.remove(id);
    }

    public List<Product> toList() {
        return new ArrayList<>(productMap.values());
    }

    // endregion
}
