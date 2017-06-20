package data_processing;

import models.products.Product;

import java.io.*;
import java.util.*;

public class FileProcessor implements DataProcessor {
    private String filePath;

    public FileProcessor(String filePath) {
        this.filePath = filePath;
    }

    public List<Product> load() throws DataProcessingException {
        List<Product> result = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                try {
                    int size = objectInputStream.readInt();
                    for (int i = 1; i <= size; ++i) {
                        Product p = (Product)objectInputStream.readObject();
                        result.add(p);
                    }
                } catch (EOFException e) {
                    return result;
                } finally {
                    objectInputStream.close();
                    fileInputStream.close();
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new DataProcessingException(e.getLocalizedMessage());
        }

        return result;
    }

    public void save(Collection<Product> products) throws DataProcessingException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeInt(products.size());
                for (Product p: products) {
                    objectOutputStream.writeObject(p);
                }
            }
        } catch (IOException e) {
            throw new DataProcessingException(e.getLocalizedMessage());
        }
    }
}