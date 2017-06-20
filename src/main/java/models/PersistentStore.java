package models;

import data_processing.DBProcessor;
import data_processing.DataProcessingException;
import data_processing.DataProcessor;
import data_processing.FileProcessor;
import models.products.Product;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;

public class PersistentStore extends Store {

    // region Private constants

    private static final String defaultFileName = "dump";
    private static final String defaultFileDirectory = "io/";

    // endregion

    // region Public API

    public boolean saveToDB() {
        return saveToPersistentStorage(new DBProcessor());
    }

    public boolean loadFromDB() {
        return loadFromPersistentStorage(new DBProcessor());
    }

    public boolean saveToFile() {
        return saveToPersistentStorage(getFileProcessor());
    }

    public boolean loadFromFile() {
        return loadFromPersistentStorage(getFileProcessor());
    }

    // endregion

    // region Private helpers

    private FileProcessor getFileProcessor() {
        Path path = Paths.get(defaultFileDirectory + defaultFileName);
        return new FileProcessor(path.toString());
    }

    private boolean saveToPersistentStorage(DataProcessor dbp) {
        try {
            dbp.save(productMap.values());
        } catch (DataProcessingException e) {
            return false;
        }
        return true;
    }

    private boolean loadFromPersistentStorage(DataProcessor dp) {
        TreeMap<Long, Product> fetched = new TreeMap<>();

        try {
            dp.load().forEach((p) -> fetched.put(p.getId(), p));
        } catch (DataProcessingException e) {
            return false;
        }

        this.productMap = fetched;
        return true;
    }

    // endregion

}
