package data_processing;

import models.products.Product;

import java.util.Collection;
import java.util.List;

public interface DataProcessor {
    void save(Collection<Product> products) throws DataProcessingException;
    List<Product> load() throws DataProcessingException;
}
